package rtg.world.biome.realistic.vanilla;

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
import rtg.api.world.deco.collection.DecoCollectionDesert;
import rtg.api.world.deco.collection.DecoCollectionDesertRiver;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaDesertHills extends RealisticBiomeBase {
   public static Biome biome = Biomes.DESERT_HILLS;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeVanillaDesertHills() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().ALLOW_CACTUS).set(true);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaDesertHills.TerrainVanillaDesertHills(10.0F, 80.0F, 68.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeVanillaDesertHills.SurfaceVanillaDesertHills(this.getConfig(), Blocks.SAND.getDefaultState(), Blocks.SANDSTONE.getDefaultState(), 0.0F, 1.5F, 60.0F, 65.0F, 1.5F);
   }

   public void rReplace(ChunkPrimer primer, int i, int j, int x, int y, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      this.rReplaceWithRiver(primer, i, j, x, y, depth, rtgWorld, noise, river, base);
   }

   public void initDecos() {
      this.addDecoCollection(new DecoCollectionDesertRiver(this.getConfig()));
      this.addDecoCollection(new DecoCollectionDesert(this.getConfig()));
   }

   public void overrideDecorations() {
      this.baseBiome().decorator.cactiPerChunk = -999;
   }

   public static class SurfaceVanillaDesertHills extends SurfaceBase {
      private float min;
      private float sCliff;
      private float sHeight;
      private float sStrength;
      private float cCliff;

      public SurfaceVanillaDesertHills(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff) {
         super(config, top, fill);
         this.sCliff = 1.5F;
         this.sHeight = 60.0F;
         this.sStrength = 65.0F;
         this.cCliff = 1.5F;
         this.min = minCliff;
      }

      public SurfaceVanillaDesertHills(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff) {
         this(config, top, fill, minCliff);
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
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
                     primer.setBlockState(x, k, z, Blocks.SANDSTONE.getDefaultState());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, Blocks.SANDSTONE.getDefaultState());
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
                     primer.setBlockState(x, k, z, Blocks.SANDSTONE.getDefaultState());
                  } else if (cliff == 2) {
                     primer.setBlockState(x, k, z, Blocks.SANDSTONE.getDefaultState());
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainVanillaDesertHills extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainVanillaDesertHills(float hillStart, float landHeight, float baseHeight, float hillWidth) {
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
