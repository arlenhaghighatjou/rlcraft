package rtg.world.biome.realistic.explorercraft;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeECSnowdonia extends RealisticBiomeECBase {
   public RealisticBiomeECSnowdonia(Biome biome) {
      super(biome);
   }

   public Biome preferredBeach() {
      return this.baseBiome();
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeECSnowdonia.TerrainBOPOvergrownCliffs(300.0F, 130.0F, 0.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeECSnowdonia.SurfaceBOPOvergrownCliffs(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.95F);
   }

   public static class SurfaceBOPOvergrownCliffs extends SurfaceBase {
      private float min;
      private float sCliff;
      private float sHeight;
      private float sStrength;
      private float cCliff;

      public SurfaceBOPOvergrownCliffs(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff) {
         super(config, top, fill);
         this.sCliff = 3.6F;
         this.sHeight = 60.0F;
         this.sStrength = 65.0F;
         this.cCliff = 3.6F;
         this.min = minCliff;
      }

      public SurfaceBOPOvergrownCliffs(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff) {
         this(config, top, fill, minCliff);
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         float c = TerrainBase.calcCliff(x, z, noise);
         int cliff = 0;

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

   public static class TerrainBOPOvergrownCliffs extends TerrainBase {
      private float width;
      private float strength;
      private float lakeDepth;
      private float lakeWidth;
      private float terrainHeight;

      public TerrainBOPOvergrownCliffs(float mountainWidth, float mountainStrength, float depthLake) {
         this(mountainWidth, mountainStrength, depthLake, 260.0F, 68.0F);
      }

      public TerrainBOPOvergrownCliffs(float mountainWidth, float mountainStrength, float depthLake, float widthLake, float height) {
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.lakeDepth = depthLake;
         this.lakeWidth = widthLake;
         this.terrainHeight = height;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainLonelyMountain(x, y, rtgWorld, river, this.strength, this.width, this.terrainHeight);
      }
   }
}
