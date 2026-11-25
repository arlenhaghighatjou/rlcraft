package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.collection.DecoCollectionSmallPineTreesForest;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusPonderosa;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaFlowerForest extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_FOREST;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaFlowerForest() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaFlowerForest.TerrainVanillaFlowerForest();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaFlowerForest.SurfaceVanillaFlowerForest(this.getConfig(), Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, Blocks.GRASS.getDefaultState(), 0.05F);
   }

   public void initDecos() {
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setMaxY(110);
      decoShrub.setLoopMultiplier(4.0F);
      decoShrub.setChance(3);
      this.addDeco(decoShrub);
      DecoFlowersRTG decoFlowers1 = (new DecoFlowersRTG()).addFlowers(EnumFlowerType.POPPY, EnumFlowerType.BLUE_ORCHID, EnumFlowerType.ALLIUM, EnumFlowerType.HOUSTONIA, EnumFlowerType.RED_TULIP, EnumFlowerType.ORANGE_TULIP, EnumFlowerType.WHITE_TULIP, EnumFlowerType.PINK_TULIP, EnumFlowerType.OXEYE_DAISY, EnumFlowerType.DANDELION).setStrengthFactor(12.0F).setHeightType(DecoFlowersRTG.HeightType.GET_HEIGHT_VALUE);
      this.addDeco(decoFlowers1);
      DecoFlowersRTG decoFlowers2 = new DecoFlowersRTG();
      decoFlowers2.addPlants(EnumPlantType.SUNFLOWER, EnumPlantType.SYRINGA, EnumPlantType.ROSE, EnumPlantType.PAEONIA);
      decoFlowers2.setChance(8);
      decoFlowers2.setHeightType(DecoFlowersRTG.HeightType.GET_HEIGHT_VALUE);
      this.addDeco(decoFlowers2);
      TreeRTG ponderosaOakTree = new TreeRTGPinusPonderosa();
      ponderosaOakTree.setLogBlock(Blocks.LOG.getDefaultState());
      ponderosaOakTree.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      ponderosaOakTree.setMinTrunkSize(11);
      ponderosaOakTree.setMaxTrunkSize(21);
      ponderosaOakTree.setMinCrownSize(15);
      ponderosaOakTree.setMaxCrownSize(29);
      this.addTree(ponderosaOakTree);
      DecoTree oakPines = new DecoTree(ponderosaOakTree);
      oakPines.setStrengthNoiseFactorForLoops(true);
      oakPines.setTreeType(DecoTree.TreeType.RTG_TREE);
      oakPines.getDistribution().setNoiseDivisor(80.0F);
      oakPines.getDistribution().setNoiseFactor(60.0F);
      oakPines.getDistribution().setNoiseAddend(-15.0F);
      oakPines.setTreeCondition(DecoTree.TreeCondition.ALWAYS_GENERATE);
      oakPines.setTreeConditionNoise(0.0F);
      oakPines.setTreeConditionChance(1);
      oakPines.setMaxY(140);
      TreeRTG ponderosaSpruceTree = new TreeRTGPinusPonderosa();
      ponderosaSpruceTree.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      ponderosaSpruceTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      ponderosaSpruceTree.setMinTrunkSize(11);
      ponderosaSpruceTree.setMaxTrunkSize(21);
      ponderosaSpruceTree.setMinCrownSize(15);
      ponderosaSpruceTree.setMaxCrownSize(29);
      this.addTree(ponderosaSpruceTree);
      DecoTree sprucePines = new DecoTree(ponderosaSpruceTree);
      sprucePines.setStrengthNoiseFactorForLoops(true);
      sprucePines.setTreeType(DecoTree.TreeType.RTG_TREE);
      sprucePines.getDistribution().setNoiseDivisor(80.0F);
      sprucePines.getDistribution().setNoiseFactor(60.0F);
      sprucePines.getDistribution().setNoiseAddend(-15.0F);
      sprucePines.setTreeCondition(DecoTree.TreeCondition.ALWAYS_GENERATE);
      sprucePines.setTreeConditionNoise(0.0F);
      sprucePines.setTreeConditionChance(1);
      sprucePines.setMaxY(140);
      DecoHelper5050 decoPines = new DecoHelper5050(oakPines, sprucePines);
      this.addDeco(decoPines);
      this.addDecoCollection(new DecoCollectionSmallPineTreesForest(this.getConfig()));
      DecoFallenTree decoFallenOak = new DecoFallenTree();
      decoFallenOak.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenOak.setLogConditionChance(8);
      decoFallenOak.setMaxY(100);
      decoFallenOak.setLogBlock(Blocks.LOG.getDefaultState());
      decoFallenOak.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      decoFallenOak.setMinSize(3);
      decoFallenOak.setMaxSize(6);
      DecoFallenTree decoFallenSpruce = new DecoFallenTree();
      decoFallenSpruce.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenSpruce.setLogConditionChance(8);
      decoFallenSpruce.setMaxY(100);
      decoFallenSpruce.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      decoFallenSpruce.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      decoFallenSpruce.setMinSize(3);
      decoFallenSpruce.setMaxSize(6);
      DecoHelper5050 decoFallenTree = new DecoHelper5050(decoFallenOak, decoFallenSpruce);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.flowersPerChunk = -999;
   }

   public boolean overridesHardcoded() {
      return true;
   }

   public static class SurfaceVanillaFlowerForest extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceVanillaFlowerForest(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.mixHeight = mixSize;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         int cliff = 0;
         boolean m = false;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (depth == 0) {
                  float p = simplex.noise3f((float)i / 8.0F, (float)j / 8.0F, (float)k / 8.0F) * 0.5F;
                  if (c > this.min && c > this.sCliff - ((float)k - this.sHeight) / this.sStrength + p) {
                     cliff = 1;
                  }

                  if (c > this.cCliff) {
                     cliff = 2;
                  }

                  if (cliff == 1) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (k < 63) {
                     if (k < 62) {
                        primer.setBlockState(x, k, z, this.fillerBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > this.mixHeight) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                     m = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 6) {
                  if (cliff == 1) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaFlowerForest extends TerrainBase {
      public TerrainVanillaFlowerForest() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 80.0F, 65.0F);
      }
   }
}
