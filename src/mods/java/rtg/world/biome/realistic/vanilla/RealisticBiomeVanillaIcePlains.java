package rtg.world.biome.realistic.vanilla;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeVanillaIcePlains extends RealisticBiomeBase {
   public static Biome biome = Biomes.ICE_PLAINS;
   public static Biome river = Biomes.FROZEN_RIVER;

   public RealisticBiomeVanillaIcePlains() {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().USE_ARCTIC_SURFACE).set(true);
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeVanillaIcePlains.TerrainVanillaIcePlains();
   }

   public SurfaceBase initSurface() {
      return (SurfaceBase)(this.getConfig().USE_ARCTIC_SURFACE.get() ? new RealisticBiomeVanillaIcePlains.SurfacePolar(this.getConfig(), Blocks.SNOW.getDefaultState(), biome.fillerBlock, Blocks.SNOW.getDefaultState(), biome.fillerBlock, 80.0F, -0.15F, 10.0F, 0.5F) : new RealisticBiomeVanillaIcePlains.SurfaceVanillaIcePlains(this.getConfig(), biome.topBlock, biome.fillerBlock, biome.topBlock, biome.topBlock));
   }

   public void initDecos() {
   }

   public static class SurfacePolar extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfacePolar(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.blockMixTop = mixTop;
         this.blockMixFiller = mixFiller;
         this.floMixWidth = mixWidth;
         this.floMixHeight = mixHeight;
         this.floSmallWidth = smallWidth;
         this.floSmallStrength = smallStrength;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         EaglercraftRandom rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
         boolean water = false;
         boolean riverPaint = false;
         boolean grass = false;
         if (river > 0.05F && river + simplex.noise2f((float)i / 10.0F, (float)j / 10.0F) * 0.1F > 0.86F) {
            riverPaint = true;
            if (simplex.noise2f((float)i / 12.0F, (float)j / 12.0F) > 0.25F) {
               grass = true;
            }
         }

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (riverPaint) {
                  if (grass && depth < 4) {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  } else if (depth == 0) {
                     if (rand.nextInt(2) == 0) {
                        primer.setBlockState(x, k, z, this.hcStone());
                     } else {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     }
                  }
               } else if (depth > -1 && depth < 9) {
                  primer.setBlockState(x, k, z, this.topBlock);
                  if (depth == 0 && k > 61 && k < 254) {
                     TerrainBase.calcSnowHeight(x, k, z, primer, noise);
                  }
               }
            } else if (!water && b == Blocks.WATER) {
               primer.setBlockState(x, k, z, Blocks.ICE.getDefaultState());
               water = true;
            }
         }

      }
   }

   public static class SurfaceVanillaIcePlains extends SurfaceBase {
      private IBlockState cliffBlock1;
      private IBlockState cliffBlock2;

      public SurfaceVanillaIcePlains(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState cliff1, IBlockState cliff2) {
         super(config, top, filler);
         this.cliffBlock1 = cliff1;
         this.cliffBlock2 = cliff2;
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
                     primer.setBlockState(x, k, z, rand.nextInt(3) == 0 ? this.cliffBlock2 : this.cliffBlock1);
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.cliffBlock1);
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

   public static class TerrainVanillaIcePlains extends TerrainBase {
      public TerrainVanillaIcePlains() {
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainPlains(x, y, rtgWorld, river, 160.0F, 10.0F, 60.0F, 200.0F, 65.0F);
      }
   }
}
