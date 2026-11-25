package rtg.world.biome.realistic.projectvibrantjourneys;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomePVJAspenGrove extends RealisticBiomePVJBase {
   public RealisticBiomePVJAspenGrove(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomePVJAspenGrove.TerrainBOPCherryBlossomGrove(58.0F, 76.0F, 20.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomePVJAspenGrove.SurfaceBOPCherryBlossomGrove(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, this.baseBiome().topBlock, this.baseBiome().fillerBlock, 80.0F, -0.15F, 10.0F, 0.5F);
   }

   public static class SurfaceBOPCherryBlossomGrove extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceBOPCherryBlossomGrove(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
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
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;
         boolean mix = false;

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
                  if (simplex.noise2f((float)i / this.floMixWidth, (float)j / this.floMixWidth) + simplex.noise2f((float)i / this.floSmallWidth, (float)j / this.floSmallWidth) * this.floSmallStrength > this.floMixHeight) {
                     primer.setBlockState(x, k, z, this.blockMixTop);
                     mix = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  if (mix) {
                     primer.setBlockState(x, k, z, this.blockMixFiller);
                  } else {
                     primer.setBlockState(x, k, z, this.fillerBlock);
                  }
               }
            }
         }

      }
   }

   public static class TerrainBOPCherryBlossomGrove extends TerrainBase {
      private float minHeight;
      private float maxHeight;
      private float hillStrength;

      public TerrainBOPCherryBlossomGrove(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.maxHeight = maxHeight > this.rollingHillsMaxHeight ? this.rollingHillsMaxHeight : (maxHeight < this.minHeight ? this.rollingHillsMaxHeight : maxHeight);
         this.hillStrength = hillStrength;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainRollingHills(x, y, rtgWorld, river, this.hillStrength, this.maxHeight, this.groundNoiseAmplitudeHills, 0.0F);
      }
   }
}
