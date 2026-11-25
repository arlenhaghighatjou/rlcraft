package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoFlowersRTG;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.deco.collection.DecoCollectionBase;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPiceaSitchensis;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGPinusPonderosa;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPWoodland extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.woodland.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPWoodland() {
      super(biome);
   }

   public Biome preferredBeach() {
      return (Biome)BOPBiomes.gravel_beach.orNull();
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPWoodland.TerrainBOPWoodland(10.0F, 25.0F, 72.0F, 120.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPWoodland.SurfaceBOPWoodland(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public void initDecos() {
      this.addDecoCollection(new RealisticBiomeBOPWoodland.DecoCollectionWoodland(this.getConfig()));
   }

   private static class DecoCollectionWoodland extends DecoCollectionBase {
      private DecoTree.Distribution forestDistribution = new DecoTree.Distribution(100.0F, 6.0F, 0.8F);
      private float tallMin = -1.0F;
      private float tallMax = 3.0F;
      private float short1Min = -3.0F;
      private float short1Max = -1.0F;

      DecoCollectionWoodland(BiomeConfig config) {
         super(config);
         this.addDeco(this.tallTrees(this.tallMin, this.tallMax)).addDeco(this.shortTrees(this.short1Min, this.short1Max)).addDeco(this.logs(), config.ALLOW_LOGS.get()).addDeco(this.shrubsOak()).addDeco(this.shrubsSpruce()).addDeco(this.flowers());
      }

      private DecoHelper5050 tallTrees(float noiseMin, float noiseMax) {
         return new DecoHelper5050(this.tallPineTrees(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), noiseMin, noiseMax), this.tallPineTrees(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE), noiseMin, noiseMax));
      }

      private DecoTree tallPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
         TreeRTG pinusPonderosa = new TreeRTGPinusPonderosa();
         pinusPonderosa.setLogBlock(log);
         pinusPonderosa.setLeavesBlock(leaves);
         pinusPonderosa.setMinTrunkSize(6);
         pinusPonderosa.setMaxTrunkSize(10);
         pinusPonderosa.setMinCrownSize(8);
         pinusPonderosa.setMaxCrownSize(20);
         this.addTree(pinusPonderosa);
         return (new DecoTree(pinusPonderosa)).setStrengthFactorForLoops(6.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(1).setMaxY(85);
      }

      private DecoHelper5050 shortTrees(float noiseMin, float noiseMax) {
         return new DecoHelper5050(this.shortPineTrees(Blocks.LOG.getDefaultState(), Blocks.LEAVES.getDefaultState(), noiseMin, noiseMax), this.shortPineTrees(BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLeaf(EnumType.SPRUCE), noiseMin, noiseMax));
      }

      private DecoTree shortPineTrees(IBlockState log, IBlockState leaves, float noiseMin, float noiseMax) {
         TreeRTG piceaSitchensis = (new TreeRTGPiceaSitchensis()).setLogBlock(log).setLeavesBlock(leaves).setMinTrunkSize(4).setMaxTrunkSize(9).setMinCrownSize(6).setMaxCrownSize(13);
         this.addTree(piceaSitchensis);
         return (new DecoTree(piceaSitchensis)).setStrengthFactorForLoops(6.0F).setTreeType(DecoTree.TreeType.RTG_TREE).setDistribution(this.forestDistribution).setTreeCondition(DecoTree.TreeCondition.NOISE_BETWEEN_AND_RANDOM_CHANCE).setTreeConditionNoise(noiseMin).setTreeConditionNoise2(noiseMax).setTreeConditionChance(1).setMaxY(85);
      }

      private DecoHelper5050 logs() {
         return new DecoHelper5050(this.oakLogs(), this.spruceLogs());
      }

      private DecoFallenTree oakLogs() {
         return (new DecoFallenTree()).setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE).setLogConditionChance(10).setMaxY(80).setLogBlock(Blocks.LOG.getDefaultState()).setLeavesBlock(Blocks.LEAVES.getDefaultState()).setMinSize(3).setMaxSize(6);
      }

      private DecoFallenTree spruceLogs() {
         return (new DecoFallenTree()).setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE).setLogConditionChance(24).setMaxY(80).setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE)).setMinSize(3).setMaxSize(6);
      }

      private DecoShrub shrubsOak() {
         return (new DecoShrub()).setMaxY(140).setLoopMultiplier(4.0F).setChance(3);
      }

      private DecoShrub shrubsSpruce() {
         return (new DecoShrub()).setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE)).setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE)).setMaxY(140).setLoopMultiplier(4.0F).setChance(9);
      }

      private DecoFlowersRTG flowers() {
         return (new DecoFlowersRTG()).addFlowers(EnumFlowerType.POPPY, EnumFlowerType.BLUE_ORCHID, EnumFlowerType.ALLIUM, EnumFlowerType.HOUSTONIA, EnumFlowerType.RED_TULIP, EnumFlowerType.ORANGE_TULIP, EnumFlowerType.WHITE_TULIP, EnumFlowerType.PINK_TULIP, EnumFlowerType.OXEYE_DAISY, EnumFlowerType.DANDELION).setMaxY(128).setStrengthFactor(6.0F);
      }
   }

   public static class SurfaceBOPWoodland extends SurfaceBase {
      public SurfaceBOPWoodland(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff) {
                  if (depth > -1 && depth < 2) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, this.topBlock);
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainBOPWoodland extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainBOPWoodland(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base - 62.0F);
      }
   }
}
