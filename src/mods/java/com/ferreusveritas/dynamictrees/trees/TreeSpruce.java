package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.growthlogic.ConiferLogic;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenConiferTopper;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenPodzol;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeSpruce extends TreeFamilyVanilla {
   Species megaSpecies;

   protected boolean isLocationForMega(World world, BlockPos trunkPos) {
      return Species.isOneOfBiomes(world.getBiome(trunkPos), Biomes.REDWOOD_TAIGA, Biomes.REDWOOD_TAIGA_HILLS);
   }

   public TreeSpruce() {
      super(EnumType.SPRUCE);
      this.hasConiferVariants = true;
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockOldLeaf && state.getValue(BlockOldLeaf.VARIANT) == EnumType.SPRUCE;
      });
      this.addSpeciesLocationOverride((world, trunkPos) -> {
         return this.isLocationForMega(world, trunkPos) ? this.megaSpecies : Species.NULLSPECIES;
      });
   }

   public void createSpecies() {
      this.megaSpecies = new TreeSpruce.SpeciesMegaSpruce(this);
      this.setCommonSpecies(new TreeSpruce.SpeciesSpruce(this));
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      super.registerSpecies(speciesRegistry);
      speciesRegistry.register(this.megaSpecies);
   }

   public boolean isThick() {
      return true;
   }

   public class SpeciesBaseSpruce extends Species {
      SpeciesBaseSpruce(ResourceLocation name, TreeFamily treeFamily) {
         super(name, treeFamily);
         this.setBasicGrowingParameters(0.25F, 16.0F, 3, 3, 0.9F);
         this.setGrowthLogicKit(TreeRegistry.findGrowthLogicKit("conifer"));
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.DRY, 0.25F);
         this.envFactor(Type.WET, 0.75F);
         this.setupStandardSeedDropping();
         this.addGenFeature(new FeatureGenConiferTopper(this.getLeavesProperties()));
         this.addGenFeature(new FeatureGenPodzol());
      }

      public boolean isBiomePerfect(Biome biome) {
         return BiomeDictionary.hasType(biome, Type.CONIFEROUS);
      }
   }

   public class SpeciesMegaSpruce extends TreeSpruce.SpeciesBaseSpruce {
      private static final String speciesName = "megaspruce";

      SpeciesMegaSpruce(TreeFamily treeFamily) {
         super(new ResourceLocation(treeFamily.getName().getResourceDomain(), "megaspruce"), treeFamily);
         this.setBasicGrowingParameters(0.25F, 24.0F, 7, 5, 0.9F);
         this.setGrowthLogicKit(new ConiferLogic(5.0F));
         this.setSoilLongevity(16);
         this.addGenFeature(new FeatureGenClearVolume(8));
         this.addGenFeature(new FeatureGenMound(999));
      }

      public ItemStack getSeedStack(int qty) {
         return TreeSpruce.this.getCommonSpecies().getSeedStack(qty);
      }

      public Seed getSeed() {
         return TreeSpruce.this.getCommonSpecies().getSeed();
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

      public boolean getRequiresTileEntity(World world, BlockPos pos) {
         return !TreeSpruce.this.isLocationForMega(world, pos);
      }
   }

   public class SpeciesSpruce extends TreeSpruce.SpeciesBaseSpruce {
      SpeciesSpruce(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
      }

      public Species getMegaSpecies() {
         return TreeSpruce.this.megaSpecies;
      }
   }
}
