package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenCocoa;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFlareBottom;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenRoots;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenUndergrowth;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenVine;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeJungle extends TreeFamilyVanilla {
   Species megaSpecies;
   BlockSurfaceRoot surfaceRootBlock;

   public TreeJungle() {
      super(EnumType.JUNGLE);
      this.canSupportCocoa = true;
      this.surfaceRootBlock = new BlockSurfaceRoot(Material.WOOD, this.getName() + "root");
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockOldLeaf && state.getValue(BlockOldLeaf.VARIANT) == EnumType.JUNGLE;
      });
   }

   public void createSpecies() {
      this.megaSpecies = new TreeJungle.SpeciesMegaJungle(this);
      this.setCommonSpecies(new TreeJungle.SpeciesJungle(this));
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      super.registerSpecies(speciesRegistry);
      speciesRegistry.register(this.megaSpecies);
   }

   public boolean isThick() {
      return true;
   }

   public List<Block> getRegisterableBlocks(List<Block> blockList) {
      blockList = super.getRegisterableBlocks(blockList);
      blockList.add(this.surfaceRootBlock);
      return blockList;
   }

   public int getMaxSignalDepth() {
      return 64;
   }

   public BlockSurfaceRoot getSurfaceRoots() {
      return this.surfaceRootBlock;
   }

   public boolean onTreeActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (heldItem != null && heldItem.getItem() == Items.DYE && heldItem.getItemDamage() == 3) {
         BlockBranch branch = TreeHelper.getBranch(state);
         if (branch != null && branch.getRadius(state) == 8) {
            if (side != EnumFacing.UP && side != EnumFacing.DOWN) {
               pos = pos.offset(side);
            }

            if (world.isAirBlock(pos)) {
               IBlockState cocoaState = ModBlocks.blockFruitCocoa.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, 0, player);
               EnumFacing facing = (EnumFacing)cocoaState.getValue(BlockHorizontal.FACING);
               world.setBlockState(pos, ModBlocks.blockFruitCocoa.getDefaultState().withProperty(BlockHorizontal.FACING, facing), 2);
               if (!player.capabilities.isCreativeMode) {
                  heldItem.shrink(1);
               }

               return true;
            }
         }
      }

      return super.onTreeActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
   }

   public class SpeciesJungle extends Species {
      SpeciesJungle(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
         this.setBasicGrowingParameters(0.2F, 24.0F, 3, 2, 1.0F);
         this.setGrowthLogicKit(TreeRegistry.findGrowthLogicKit("jungle"));
         this.envFactor(Type.COLD, 0.15F);
         this.envFactor(Type.DRY, 0.2F);
         this.envFactor(Type.HOT, 1.1F);
         this.envFactor(Type.WET, 1.1F);
         this.setupStandardSeedDropping();
         this.addGenFeature(new FeatureGenCocoa());
         this.addGenFeature((new FeatureGenVine()).setQuantity(16).setMaxLength(16));
         this.addGenFeature(new FeatureGenUndergrowth());
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.JUNGLE);
      }

      public Species getMegaSpecies() {
         return TreeJungle.this.megaSpecies;
      }
   }

   public class SpeciesMegaJungle extends Species {
      private static final String speciesName = "megajungle";

      SpeciesMegaJungle(TreeFamily treeFamily) {
         super(new ResourceLocation(treeFamily.getName().getResourceDomain(), "megajungle"), treeFamily);
         this.setRequiresTileEntity(true);
         this.setBasicGrowingParameters(0.17F, 35.0F, 10, 8, 0.9F);
         this.setGrowthLogicKit(TreeRegistry.findGrowthLogicKit("jungle"));
         this.envFactor(Type.COLD, 0.15F);
         this.envFactor(Type.DRY, 0.2F);
         this.envFactor(Type.HOT, 1.1F);
         this.envFactor(Type.WET, 1.1F);
         this.setSoilLongevity(16);
         this.addGenFeature((new FeatureGenVine()).setQuantity(16).setMaxLength(16));
         this.addGenFeature(new FeatureGenFlareBottom());
         this.addGenFeature(new FeatureGenClearVolume(8));
         this.addGenFeature(new FeatureGenMound(999));
         this.addGenFeature((new FeatureGenRoots(9)).setScaler(this.getRootScaler()));
      }

      protected BiFunction<Integer, Integer, Integer> getRootScaler() {
         return (inRadius, trunkRadius) -> {
            float scale = MathHelper.clamp(trunkRadius.intValue() >= 9 ? (float)trunkRadius.intValue() / 20.0F : 0.0F, 0.0F, 1.0F);
            return (int)((float)inRadius.intValue() * scale);
         };
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.JUNGLE);
      }

      public ItemStack getSeedStack(int qty) {
         return TreeJungle.this.getCommonSpecies().getSeedStack(qty);
      }

      public Seed getSeed() {
         return TreeJungle.this.getCommonSpecies().getSeed();
      }

      public int maxBranchRadius() {
         return 24;
      }

      public boolean isThick() {
         return true;
      }

      public boolean isMega() {
         return true;
      }

      public int getWorldGenLeafMapHeight() {
         return 40;
      }
   }
}
