package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.api.cells.ICellKit;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.blocks.LeavesPaging;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeBuilder {
   private ResourceLocation name;
   private int seqNum = -1;
   private IBlockState primitiveLeavesBlockState = Blocks.LEAVES.getDefaultState();
   private IBlockState primitiveLogBlockState = Blocks.LOG.getDefaultState();
   private ItemStack stickItemStack;
   private ILeavesProperties dynamicLeavesProperties;
   private int dynamicLeavesSmotherMax = 4;
   private int dynamicLeavesLightRequirement = 13;
   private ResourceLocation dynamicLeavesCellKit;
   private TreeBuilder.ISpeciesCreator speciesCreator;
   private boolean speciesCreateSeed = true;
   private final List<TreeBuilder.ISpeciesCreator> extraSpeciesCreators = new ArrayList(0);
   private final Map<String, Species> extraSpecies = new HashMap();

   public TreeBuilder() {
   }

   public TreeBuilder setName(ResourceLocation name) {
      this.name = name;
      return this;
   }

   public TreeBuilder setName(String domain, String path) {
      return this.setName(new ResourceLocation(domain, path));
   }

   public TreeBuilder setDynamicLeavesSequence(int seqNum) {
      if (this.dynamicLeavesProperties == null) {
         this.seqNum = seqNum;
      }

      return this;
   }

   public TreeBuilder setDynamicLeavesProperties(ILeavesProperties leavesProperties) {
      this.seqNum = -1;
      this.dynamicLeavesProperties = leavesProperties;
      return this;
   }

   public TreeBuilder setPrimitiveLeaves(IBlockState primLeaves) {
      this.primitiveLeavesBlockState = primLeaves;
      return this;
   }

   public TreeBuilder setPrimitiveLog(IBlockState primLog) {
      this.primitiveLogBlockState = primLog;
      return this;
   }

   public TreeBuilder setStick(ItemStack stick) {
      this.stickItemStack = stick;
      return this;
   }

   public TreeBuilder setSmotherLeavesMax(int smotherMax) {
      this.dynamicLeavesSmotherMax = smotherMax;
      return this;
   }

   public TreeBuilder setLightRequirement(int light) {
      this.dynamicLeavesLightRequirement = light;
      return this;
   }

   public TreeBuilder setCellKit(ResourceLocation kit) {
      this.dynamicLeavesCellKit = kit;
      return this;
   }

   public TreeBuilder setCommonSpecies(TreeBuilder.ISpeciesCreator speciesCreator) {
      this.speciesCreator = speciesCreator;
      return this;
   }

   public TreeBuilder addExtraSpecies(TreeBuilder.ISpeciesCreator speciesCreator) {
      this.extraSpeciesCreators.add(speciesCreator);
      return this;
   }

   public TreeBuilder setCreateSeed(boolean isStandard) {
      this.speciesCreateSeed = isStandard;
      return this;
   }

   public TreeFamily build() {
      if (this.name == null) {
         System.err.println("Error: Attempted to build an nameless tree");
         return TreeFamily.NULLFAMILY;
      } else if (this.seqNum == -1 && this.dynamicLeavesProperties == null) {
         System.err.println("Error: Attempted to build an unsequenced tree(or a tree without dynamic leaves properties)");
         return TreeFamily.NULLFAMILY;
      } else {
         TreeFamily treeFamily = new TreeFamily(this.name) {
            {
               if (TreeBuilder.this.dynamicLeavesProperties == null) {
                  TreeBuilder.this.dynamicLeavesProperties = new LeavesProperties(TreeBuilder.this.primitiveLeavesBlockState) {
                     public int getLightRequirement() {
                        return TreeBuilder.this.dynamicLeavesLightRequirement;
                     }

                     public int getSmotherLeavesMax() {
                        return TreeBuilder.this.dynamicLeavesSmotherMax;
                     }

                     public ICellKit getCellKit() {
                        return TreeRegistry.findCellKit(TreeBuilder.this.dynamicLeavesCellKit);
                     }
                  };
                  LeavesPaging.getLeavesBlockForSequence("dynamictrees", TreeBuilder.this.seqNum, TreeBuilder.this.dynamicLeavesProperties);
               }

               this.setPrimitiveLog(TreeBuilder.this.primitiveLogBlockState);
               TreeBuilder.this.dynamicLeavesProperties.setTree(this);
               if (TreeBuilder.this.stickItemStack != null) {
                  this.setStick(TreeBuilder.this.stickItemStack);
               }

            }

            public void createSpecies() {
               this.setCommonSpecies(TreeBuilder.this.speciesCreator != null ? TreeBuilder.this.speciesCreator.create(this) : new Species(TreeBuilder.this.name, this, TreeBuilder.this.dynamicLeavesProperties));
               if (TreeBuilder.this.speciesCreateSeed) {
                  this.getCommonSpecies().generateSeed();
                  this.getCommonSpecies().setupStandardSeedDropping();
               }

               for(TreeBuilder.ISpeciesCreator creator : TreeBuilder.this.extraSpeciesCreators) {
                  Species species = creator.create(this);
                  TreeBuilder.this.extraSpecies.put(species.getRegistryName().getResourcePath(), species);
               }

            }

            public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
               super.registerSpecies(speciesRegistry);
               TreeBuilder.this.extraSpecies.values().forEach((s) -> {
                  speciesRegistry.register(s);
               });
            }

            public List<Item> getRegisterableItems(List<Item> itemList) {
               for(Species species : TreeBuilder.this.extraSpecies.values()) {
                  Seed seed = species.getSeed();
                  if (seed != Seed.NULLSEED) {
                     itemList.add(seed);
                  }
               }

               return super.getRegisterableItems(itemList);
            }
         };
         return treeFamily;
      }
   }

   public interface ISpeciesCreator {
      Species create(TreeFamily var1);
   }
}
