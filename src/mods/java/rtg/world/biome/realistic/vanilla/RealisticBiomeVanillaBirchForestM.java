package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
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
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.deco.DecoTree;
import rtg.api.world.gen.feature.tree.rtg.TreeRTG;
import rtg.api.world.gen.feature.tree.rtg.TreeRTGBetulaPapyrifera;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaBirchForestM extends RealisticBiomeBase {
   public static Biome biome = Biomes.MUTATED_BIRCH_FOREST;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaBirchForestM() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaBirchForestM.TerrainVanillaBirchForestM();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaBirchForestM.SurfaceVanillaBirchForestM(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, BlockUtil.getStateDirt(DirtType.COARSE_DIRT), 0.15F);
   }

   public void initDecos() {
      TreeRTG tallBirch = new TreeRTGBetulaPapyrifera();
      tallBirch.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      tallBirch.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      tallBirch.setMinTrunkSize(16);
      tallBirch.setMaxTrunkSize(23);
      tallBirch.setMinCrownSize(4);
      tallBirch.setMaxCrownSize(11);
      this.addTree(tallBirch);
      DecoTree superTallBirch = new DecoTree(tallBirch);
      superTallBirch.setStrengthFactorForLoops(16.0F);
      superTallBirch.setStrengthNoiseFactorForLoops(true);
      superTallBirch.setTreeType(DecoTree.TreeType.RTG_TREE);
      superTallBirch.getDistribution().setNoiseDivisor(80.0F);
      superTallBirch.getDistribution().setNoiseFactor(60.0F);
      superTallBirch.getDistribution().setNoiseAddend(-15.0F);
      superTallBirch.setTreeCondition(DecoTree.TreeCondition.ALWAYS_GENERATE);
      superTallBirch.setMaxY(100);
      this.addDeco(superTallBirch);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenTree.setLogConditionChance(20);
      decoFallenTree.setLogBlock(BlockUtil.getStateLog(EnumType.BIRCH));
      decoFallenTree.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.BIRCH));
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(6);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoShrub decoShrub = new DecoShrub();
      decoShrub.setMaxY(110);
      decoShrub.setLoopMultiplier(2.0F);
      this.addDeco(decoShrub);
   }

   public static class SurfaceVanillaBirchForestM extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceVanillaBirchForestM(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
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

   public static class TerrainVanillaBirchForestM extends TerrainBase {
      public TerrainVanillaBirchForestM() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 80.0F, 65.0F);
      }
   }
}
