package rtg.world.biome.realistic.biomesyougo;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.noise.ISimplexData2D;
import rtg.api.util.noise.SimplexData2D;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;

public class RealisticBiomeBYGChaparralLowlands extends RealisticBiomeBYGBase {
   private static IBlockState bygMixBlock = Blocks.STONE.getDefaultState();

   public RealisticBiomeBYGChaparralLowlands(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGChaparralLowlands.TerrainBOPChaparral();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGChaparralLowlands.SurfaceBOPChaparral(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, bygMixBlock, 26.0F, 0.35F);
   }

   public static class SurfaceBOPChaparral extends SurfaceBase {
      private IBlockState mixBlock;
      private float width;
      private float height;

      public SurfaceBOPChaparral(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mix, float mixWidth, float mixHeight) {
         super(config, top, filler);
         this.mixBlock = mix;
         this.width = mixWidth;
         this.height = mixHeight;
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         SimplexNoise simplex = rtgWorld.simplexInstance(0);
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
                  if (simplex.noise2f((float)i / this.width, (float)j / this.width) > this.height) {
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

   public static class TerrainBOPChaparral extends TerrainBase {
      private float baseHeight = 76.0F;
      private float peakyHillWavelength = 40.0F;
      private float peakyHillStrength = 40.0F;
      private float smoothHillWavelength = 60.0F;
      private float smoothHillStrength = 30.0F;
      private float wavelength = 10.0F;
      private float amplitude = 2.0F;

      public TerrainBOPChaparral() {
      }

      public TerrainBOPChaparral(float bh, float hs) {
         this.baseHeight = bh;
         this.peakyHillStrength = hs;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         this.groundNoise = groundNoise(x, y, this.groundNoiseAmplitudeHills, rtgWorld);
         ISimplexData2D jitterData = SimplexData2D.newDerivative();
         rtgWorld.simplexInstance(1).multiEval2D((double)((float)x / this.wavelength), (double)((float)y / this.wavelength), jitterData);
         int pX = (int)Math.round((double)x + jitterData.getDeltaX() * (double)this.amplitude);
         int pY = (int)Math.round((double)y + jitterData.getDeltaY() * (double)this.amplitude);
         float h = terrainGrasslandHills(pX, pY, rtgWorld, river, this.peakyHillWavelength, this.peakyHillStrength, this.smoothHillWavelength, this.smoothHillStrength, this.baseHeight);
         return this.groundNoise * river + h;
      }
   }
}
