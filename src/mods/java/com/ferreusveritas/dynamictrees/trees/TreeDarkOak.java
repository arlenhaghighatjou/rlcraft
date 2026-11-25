package com.ferreusveritas.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreatorApple;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFlareBottom;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenHugeMushrooms;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenPredicate;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenRoots;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;

public class TreeDarkOak extends TreeFamilyVanilla {
   BlockSurfaceRoot surfaceRootBlock;

   public TreeDarkOak() {
      super(EnumType.DARK_OAK);
      this.hasConiferVariants = true;
      this.surfaceRootBlock = new BlockSurfaceRoot(Material.WOOD, this.getName() + "root");
      this.addConnectableVanillaLeaves((state) -> {
         return state.getBlock() instanceof BlockNewLeaf && state.getValue(BlockNewLeaf.VARIANT) == EnumType.DARK_OAK;
      });
   }

   public void createSpecies() {
      this.setCommonSpecies(new TreeDarkOak.SpeciesDarkOak(this));
   }

   public boolean isThick() {
      return true;
   }

   public List<Block> getRegisterableBlocks(List<Block> blockList) {
      blockList = super.getRegisterableBlocks(blockList);
      blockList.add(this.surfaceRootBlock);
      return blockList;
   }

   public BlockSurfaceRoot getSurfaceRoots() {
      return this.surfaceRootBlock;
   }

   public class SpeciesDarkOak extends Species {
      protected FeatureGenHugeMushrooms underGen;
      protected FeatureGenFlareBottom flareBottomGen;
      protected FeatureGenRoots rootGen;
      protected FeatureGenMound moundGen;

      SpeciesDarkOak(TreeFamily treeFamily) {
         super(treeFamily.getName(), treeFamily);
         this.setBasicGrowingParameters(0.3F, 18.0F, 4, 6, 0.8F);
         this.setGrowthLogicKit(TreeRegistry.findGrowthLogicKit("darkoak"));
         this.setSoilLongevity(14);
         this.envFactor(Type.COLD, 0.75F);
         this.envFactor(Type.HOT, 0.5F);
         this.envFactor(Type.DRY, 0.25F);
         this.envFactor(Type.MUSHROOM, 1.25F);
         if (ModConfigs.worldGen && !ModConfigs.enableAppleTrees) {
            this.addDropCreator(new DropCreatorApple());
         }

         this.setupStandardSeedDropping();
         this.addGenFeature(new FeatureGenClearVolume(6));
         this.addGenFeature(new FeatureGenFlareBottom());
         this.addGenFeature(new FeatureGenMound(5));
         if (ModConfigs.roofedForestMushroomGen) {
            this.addGenFeature((new FeatureGenPredicate((new FeatureGenHugeMushrooms()).setMaxShrooms(1).setMaxAttempts(3))).setBiomePredicate((biome) -> {
               return biome == Biomes.ROOFED_FOREST;
            }));
         }

         this.addGenFeature((new FeatureGenRoots(13)).setScaler(this.getRootScaler()));
      }

      protected BiFunction<Integer, Integer, Integer> getRootScaler() {
         return (inRadius, trunkRadius) -> {
            float scale = MathHelper.clamp(trunkRadius.intValue() >= 13 ? (float)trunkRadius.intValue() / 24.0F : 0.0F, 0.0F, 1.0F);
            return (int)((float)inRadius.intValue() * scale);
         };
      }

      public boolean isBiomePerfect(Biome biome) {
         return isOneOfBiomes(biome, new Biome[]{Biomes.ROOFED_FOREST});
      }

      public int getLowestBranchHeight(World world, BlockPos pos) {
         return (int)((float)super.getLowestBranchHeight(world, pos) * this.biomeSuitability(world, pos));
      }

      public float getEnergy(World world, BlockPos pos) {
         return super.getEnergy(world, pos) * this.biomeSuitability(world, pos);
      }

      public float getGrowthRate(World world, BlockPos pos) {
         return super.getGrowthRate(world, pos) * this.biomeSuitability(world, pos);
      }

      public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random, boolean rapid) {
         if (super.rot(world, pos, neighborCount, radius, random, rapid)) {
            if (radius > 2 && TreeHelper.isRooty(world.getBlockState(pos.down())) && world.getLightFor(EnumSkyBlock.SKY, pos) < 6) {
               world.setBlockState(pos, ModBlocks.blockStates.redMushroom);
               world.setBlockState(pos.down(), ModBlocks.blockStates.podzol);
            }

            return true;
         } else {
            return false;
         }
      }

      public boolean isThick() {
         return true;
      }
   }
}
