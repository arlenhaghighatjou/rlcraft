package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import biomesoplenty.api.block.BOPBlocks;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.deco.DecoBoulder;
import rtg.api.world.deco.DecoFallenTree;
import rtg.api.world.deco.helper.DecoHelper5050;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBOPShield extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.shield.orNull();
   public static Biome river = Biomes.RIVER;
   private double lakeWaterLevel = 0.04D;
   private double lakeDepressionLevel = 0.3D;

   public RealisticBiomeBOPShield() {
      super(biome);
   }

   public Biome preferredBeach() {
      return (Biome)BOPBiomes.gravel_beach.orNull();
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPShield.TerrainBOPShield();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPShield.SurfaceBOPShield(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, Blocks.STONE.getDefaultState(), 0.1F);
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
      DecoBoulder decoBoulder = new DecoBoulder();
      decoBoulder.setBoulderBlock(Blocks.COBBLESTONE.getDefaultState());
      decoBoulder.setMaxY(80);
      decoBoulder.setChance(16);
      decoBoulder.setStrengthFactor(1.0F);
      this.addDeco(decoBoulder);
      DecoFallenTree decoFallenTree1 = new DecoFallenTree();
      decoFallenTree1.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree1.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree1.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree1.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenTree1.setLogConditionChance(6);
      decoFallenTree1.setMaxY(100);
      decoFallenTree1.setLogBlock(BOPBlocks.log_2.getStateFromMeta(6));
      decoFallenTree1.setLeavesBlock(Blocks.LEAVES.getDefaultState());
      decoFallenTree1.setMinSize(3);
      decoFallenTree1.setMaxSize(4);
      DecoFallenTree decoFallenTree2 = new DecoFallenTree();
      decoFallenTree2.getDistribution().setNoiseDivisor(80.0F);
      decoFallenTree2.getDistribution().setNoiseFactor(60.0F);
      decoFallenTree2.getDistribution().setNoiseAddend(-15.0F);
      decoFallenTree2.setLogCondition(DecoFallenTree.LogCondition.RANDOM_CHANCE);
      decoFallenTree2.setLogConditionChance(6);
      decoFallenTree2.setMaxY(100);
      decoFallenTree2.setLogBlock(BlockUtil.getStateLog(EnumType.SPRUCE));
      decoFallenTree2.setLeavesBlock(BlockUtil.getStateLeaf(EnumType.SPRUCE));
      decoFallenTree2.setMinSize(3);
      decoFallenTree2.setMaxSize(4);
      DecoHelper5050 decoHelperHelper5050 = new DecoHelper5050(decoFallenTree1, decoFallenTree2);
      this.addDeco(decoHelperHelper5050, this.getConfig().ALLOW_LOGS.get());
   }

   public static class SurfaceBOPShield extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;

      public SurfaceBOPShield(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixSize) {
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

   public static class TerrainBOPShield extends TerrainBase {
      public TerrainBOPShield() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 200.0F, 64.0F);
      }
   }
}
