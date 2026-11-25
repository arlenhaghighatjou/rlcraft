package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import biomesoplenty.api.enums.BOPTrees;
import biomesoplenty.api.enums.BOPWoods;
import biomesoplenty.common.block.BlockBOPLeaves;
import biomesoplenty.common.block.BlockBOPLog;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.DecoMushrooms;
import rtg.api.world.deco.DecoShrub;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPBayou extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.bayou.orNull();
   public static Biome river = Biomes.RIVER;
   private static IBlockState mudBlock = BOPBlocks.mud.getDefaultState();
   private static IBlockState logBlock = BlockBOPLog.paging.getVariantState(BOPWoods.WILLOW).withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
   private static IBlockState leavesBlock = BlockBOPLeaves.paging.getVariantState(BOPTrees.WILLOW).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(false));
   private double lakeWaterLevel = 0.04D;
   private double lakeDepressionLevel = 0.3D;

   public RealisticBiomeBOPBayou() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPBayou.TerrainBOPBayou();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPBayou.SurfaceBOPBayou(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, biome.topBlock, 0.1F);
   }

   public float lakePressure(RTGWorld rtgWorld, int x, int y, float border, float lakeInterval, float largeBendSize, float mediumBendSize, float smallBendSize) {
      double pX = (double)x;
      double pY = (double)y;
      ISimplexData2D jitterData = SimplexData2D.newDisk();
      rtgWorld.simplexInstance(1).multiEval2D((double)x / 40.0D, (double)y / 40.0D, jitterData);
      pX = pX + jitterData.getDeltaX() * 35.0D;
      pY = pY + jitterData.getDeltaY() * 35.0D;
      return TerrainBase.bayesianAdjustment((float)rtgWorld.cellularInstance(0).eval2D(pX / 150.0D, pY / 150.0D).interiorValue(), 0.25F);
   }

   public float erodedNoise(RTGWorld rtgWorld, int x, int y, float river, float border, float biomeHeight) {
      float riverFlattening = 1.0F - river;
      riverFlattening = riverFlattening - 0.90625F;
      if (riverFlattening < 0.0F) {
         return biomeHeight;
      } else {
         riverFlattening = riverFlattening / 0.09375F;
         float r = 1.0F - riverFlattening;
         riverFlattening = riverFlattening * 1.4F - 0.4F;
         if (riverFlattening < 0.0F) {
            riverFlattening = 0.0F;
         }

         if (r < 1.0F && biomeHeight > 57.0F) {
            float irregularity = rtgWorld.simplexInstance(0).noise2f((float)x / 12.0F, (float)y / 12.0F) * 2.0F + rtgWorld.simplexInstance(0).noise2f((float)x / 8.0F, (float)y / 8.0F);
            irregularity = irregularity * (1.0F + r);
            return biomeHeight * r + (57.0F + irregularity) * 1.0F * (1.0F - r);
         } else {
            return biomeHeight;
         }
      }
   }

   public float lakeFlattening(float pressure, float bottomLevel, float topLevel) {
      return pressure;
   }

   public void initDecos() {
      DecoShrub decoShrubOak = new DecoShrub();
      decoShrubOak.setMaxY(90);
      decoShrubOak.setLoopMultiplier(4.0F);
      decoShrubOak.setChance(3);
      this.addDeco(decoShrubOak);
      DecoFallenTree decoFallenTree = new DecoFallenTree();
      decoFallenTree.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree.setLogConditionChance(4);
      decoFallenTree.setLogBlock(logBlock);
      decoFallenTree.setLeavesBlock(leavesBlock);
      decoFallenTree.setMinSize(3);
      decoFallenTree.setMaxSize(6);
      this.addDeco(decoFallenTree, this.getConfig().ALLOW_LOGS.get());
      DecoMushrooms decoMushrooms = new DecoMushrooms();
      decoMushrooms.setMaxY(90);
      decoMushrooms.setRandomType(DecoMushrooms.RandomType.ALWAYS_GENERATE);
      this.addDeco(decoMushrooms);
   }

   public static class SurfaceBOPBayou extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceBOPBayou(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
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
         Random rand = rtgWorld.rand();
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

   public static class TerrainBOPBayou extends TerrainBase {
      public TerrainBOPBayou() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 80.0F, 1.0F, 40.0F, 20.0F, 62.0F);
      }
   }
}
