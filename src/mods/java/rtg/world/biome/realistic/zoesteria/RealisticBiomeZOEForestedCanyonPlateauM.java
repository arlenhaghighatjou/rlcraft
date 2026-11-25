package rtg.world.biome.realistic.zoesteria;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeZOEForestedCanyonPlateauM extends RealisticBiomeZOEBase {
   public RealisticBiomeZOEForestedCanyonPlateauM(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().SURFACE_WATER_LAKE_MULT.set(0.0F);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeZOEForestedCanyonPlateauM.TerrainVanillaSavannaPlateau(true, 35.0F, 160.0F, 60.0F, 40.0F, 69.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeZOEForestedCanyonPlateauM.SurfaceVanillaSavannaPlateau(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0);
   }

   public class SurfaceVanillaSavannaPlateau extends SurfaceBase {
      private int grassRaise = 0;
      private IBlockState mixBlock;

      public SurfaceVanillaSavannaPlateau(BiomeConfig config, IBlockState top, IBlockState fill, int grassHeight) {
         super(config, top, fill);
         this.grassRaise = grassHeight;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), top);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.3F;

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
               } else if (k > 74 + this.grassRaise) {
                  if (depth == 0) {
                     if (rand.nextInt(5) == 0) {
                        primer.setBlockState(x, k, z, this.mixBlock);
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
                  } else if (depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               } else if (depth == 0 && k > 61) {
                  int r = (int)((float)(k - (62 + this.grassRaise)) / 2.0F);
                  if (rand.nextInt(r + 2) == 0) {
                     primer.setBlockState(x, k, z, this.topBlock);
                  } else if (rand.nextInt((int)((float)r / 2.0F) + 2) == 0) {
                     primer.setBlockState(x, k, z, this.mixBlock);
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, this.fillerBlock);
               }
            }
         }

      }
   }

   public static class TerrainVanillaSavannaPlateau extends TerrainBase {
      private boolean booRiver = true;
      private float[] height = new float[]{12.0F, 0.5F, 6.0F, 0.7F};
      private int heightLength;
      private float strength = 10.0F;
      private float smooth;
      private float cWidth;
      private float cHeigth;
      private float cStrength;
      private float base;

      public TerrainVanillaSavannaPlateau(boolean riverGen, float heightStrength, float canyonWidth, float canyonHeight, float canyonStrength, float baseHeight) {
         this.heightLength = this.height.length;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlateau((float)x, (float)y, rtgWorld, river, this.height, border, this.strength, this.heightLength, 50.0F, true);
      }
   }
}
