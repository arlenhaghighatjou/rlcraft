package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorApple;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFruit;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenVine;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.registries.IForgeRegistry;

public class TreeOak extends TreeFamilyVanilla {
   Species swampSpecies;
   Species appleSpecies;

   protected boolean isLocationSwampy(World world, BlockPos pos) {
      return BiomeDictionary.hasType(world.getBiome(pos), Type.SWAMP);
   }

   public TreeOak() {
      super(EnumType.OAK);
      this.hasConiferVariants = true;
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockOldLeaf && state.getValue(BlockOldLeaf.VARIANT) == EnumType.OAK;
      });
      this.addSpeciesLocationOverride((world, trunkPos) -> {
         return this.isLocationSwampy(world, trunkPos) ? this.swampSpecies : Species.NULLSPECIES;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeOak.SpeciesOak(this));
      this.swampSpecies = new TreeOak.SpeciesSwampOak(this);
      this.appleSpecies = new TreeOak.SpeciesAppleOak(this);
   }

   public void registerSpecies(IForgeRegistry<Species> speciesRegistry) {
      super.registerSpecies(speciesRegistry);
      speciesRegistry.register(this.swampSpecies);
      speciesRegistry.register(this.appleSpecies);
   }

   public List<Item> getRegisterableItems(List<Item> itemList) {
      itemList.add(this.appleSpecies.getSeed());
      return super.getRegisterableItems(itemList);
   }

   public boolean isThick() {
      return true;
   }

   public class SpeciesAppleOak extends Species {
      private static final String speciesName = "apple";

      public SpeciesAppleOak(TreeFamily treeFamily) {
         super(new ResourceLocation(treeFamily.getName().getResourceDomain(), "apple"), treeFamily);
         this.setRequiresTileEntity(true);
         this.setBasicGrowingParameters(0.4F, 10.0F, 1, 4, 0.7F);
         this.envFactor(Type.COLD, 0.75F);
         this.envFactor(Type.HOT, 0.75F);
         this.envFactor(Type.DRY, 0.25F);
         this.generateSeed();
         ModBlocks.blockApple.setSpecies(this);
         this.addGenFeature((new FeatureGenFruit(ModBlocks.blockApple)).setRayDistance(4.0F));
      }

      public boolean isBiomePerfect(Biome biome) {
         return biome == Biomes.PLAINS;
      }
   }

   public class SpeciesOak extends Species {
      SpeciesOak(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
         this.setBasicGrowingParameters(0.3F, 12.0F, this.upProbability, this.lowestBranchHeight, 0.8F);
         this.envFactor(Type.COLD, 0.75F);
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.DRY, 0.5F);
         this.envFactor(Type.FOREST, 1.05F);
         if (ModConfigs.worldGen && !ModConfigs.enableAppleTrees) {
            this.addDropCreator(new DropCreatorApple());
         }

         this.setupStandardSeedDropping();
      }

      public boolean isBiomePerfect(Biome biome) {
         return isOneOfBiomes(biome, new Biome[]{Biomes.FOREST, Biomes.FOREST_HILLS});
      }

      public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
         if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
            if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
               world.setBlockState(pos, random.nextInt(3) == 0 ? ModBlocks.blockStates.redMushroom : ModBlocks.blockStates.brownMushroom);
               world.setBlockState(pos.down(), ModBlocks.blockStates.podzol);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public class SpeciesSwampOak extends Species {
      SpeciesSwampOak(TreeFamily treeFamily) {
         super(new ResourceLocation(treeFamily.getName().getResourceDomain(), treeFamily.getName().getResourcePath() + "swamp"), treeFamily);
         this.setBasicGrowingParameters(0.3F, 12.0F, this.upProbability, this.lowestBranchHeight, 0.8F);
         this.envFactor(Type.COLD, 0.5F);
         this.envFactor(Type.DRY, 0.5F);
         this.setupStandardSeedDropping();
         this.addGenFeature((new FeatureGenVine()).setMaxLength(7).setVerSpread(30.0F).setRayDistance(6.0F).setQuantity(24));
      }

      public boolean isBiomePerfect(Biome biome) {
         return isOneOfBiomes(biome, new Biome[]{Biomes.SWAMPLAND});
      }

      public ItemStack getSeedStack(int qty) {
         return TreeOak.this.getCommonSpecies().getSeedStack(qty);
      }

      public Seed getSeed() {
         return TreeOak.this.getCommonSpecies().getSeed();
      }

      public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
         if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
            if (radius > 4 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 4) {
               world.setBlockState(pos, random.nextInt(3) == 0 ? ModBlocks.blockStates.redMushroom : ModBlocks.blockStates.brownMushroom);
               world.setBlockState(pos.down(), ModBlocks.blockStates.podzol);
            }

            return true;
         } else {
            return false;
         }
      }

      private boolean isGeneratingInWater(World world, BlockPos pos, IBlockState soilBlockState) {
         if (soilBlockState.getBlock() == Blocks.WATER) {
            Biome biome = world.getBiome(pos);
            if (BiomeDictionary.hasType(biome, Type.SWAMP)) {
               BlockPos down = pos.down();
               return this.isAcceptableSoil(world, down, world.getBlockState(down));
            }
         }

         return false;
      }

      public boolean isAcceptableSoilForWorldgen(World world, BlockPos pos, IBlockState soilBlockState) {
         return this.isGeneratingInWater(world, pos, soilBlockState) ? true : super.isAcceptableSoilForWorldgen(world, pos, soilBlockState);
      }

      public boolean generate(World world, BlockPos rootPos, Biome biome, Random random, int radius, SafeChunkBounds safeBounds) {
         if (this.isGeneratingInWater(world, rootPos, world.getBlockState(rootPos))) {
            return radius >= 5 ? super.generate(world, rootPos.down(), biome, random, radius, safeBounds) : false;
         } else {
            return super.generate(world, rootPos, biome, random, radius, safeBounds);
         }
      }
   }
}
