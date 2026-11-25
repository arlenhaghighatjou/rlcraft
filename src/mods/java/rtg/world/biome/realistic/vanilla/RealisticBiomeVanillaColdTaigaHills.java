package rtg.world.biome.realistic.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.deco.collection.DecoCollectionTaiga;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaColdTaigaHills extends RealisticBiomeBase {
   public static Biome biome = Biomes.COLD_TAIGA_HILLS;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaColdTaigaHills() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaColdTaigaHills.TerrainVanillaColdTaigaHills();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaColdTaigaHills.SurfaceVanillaColdTaigaHills(this.getConfig(), Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState(), 0.2F);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionTaiga(this.getConfig(), 8.0F));
   }

   public static class SurfaceVanillaColdTaigaHills extends SurfaceBase {
      private float min;
      private float sCliff;
      private float sHeight;
      private float sStrength;
      private float iCliff;
      private float iHeight;
      private float iStrength;
      private float cCliff;

      public SurfaceVanillaColdTaigaHills(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff) {
         super(config, top, fill);
         this.sCliff = 1.5F;
         this.sHeight = 60.0F;
         this.sStrength = 65.0F;
         this.iCliff = 0.3F;
         this.iHeight = 100.0F;
         this.iStrength = 50.0F;
         this.cCliff = 1.5F;
         this.min = minCliff;
      }

      public SurfaceVanillaColdTaigaHills(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float snowCliff, float snowHeight, float snowStrength, float clayCliff) {
         this(config, top, fill, minCliff);
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.iCliff = snowCliff;
         this.iHeight = snowHeight;
         this.iStrength = snowStrength;
         this.cCliff = clayCliff;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
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

                  if ((float)k > 110.0F + p * 4.0F && c < this.iCliff + ((float)k - this.iHeight) / this.iStrength + p) {
                     cliff = 3;
                  }

                  if (cliff == 1) {
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (cliff == 3) {
                     primer.setBlockState(x, k, z, Blocks.SNOW.getDefaultState());
                  } else if (k < 63) {
                     if (k < 62) {
                        primer.setBlockState(x, k, z, this.fillerBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else {
                     primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
                  }
               } else if (depth < 6) {
                  if (cliff == 1) {
                     primer.setBlockState(x, k, z, this.hcStone());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, this.getShadowStoneBlock());
                  } else if (cliff == 3) {
                     primer.setBlockState(x, k, z, Blocks.SNOW.getDefaultState());
                  } else {
                     primer.setBlockState(x, k, z, Blocks.DIRT.getDefaultState());
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaColdTaigaHills extends TerrainBase {
      public TerrainVanillaColdTaigaHills() {
         this.base = 72.0F;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, 10.0F, 68.0F, 35.0F, this.base - 62.0F);
      }
   }
}
