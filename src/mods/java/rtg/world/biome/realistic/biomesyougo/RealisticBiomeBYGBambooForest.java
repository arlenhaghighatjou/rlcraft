package rtg.world.biome.realistic.biomesyougo;

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
import rtg.api.world.terrain.heighteffect.BumpyHillsEffect;
import rtg.api.world.terrain.heighteffect.GroundEffect;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.VariableRuggednessEffect;

public class RealisticBiomeBYGBambooForest extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGBambooForest(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGBambooForest.TerrainBYGBiome();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGBambooForest.SurfaceBYGBiome(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, this.baseBiome().topBlock, 0.15F);
   }

   public static class SurfaceBYGBiome extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mix;
      private float mixHeight;

      public SurfaceBYGBiome(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mixBlock, float mixSize) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mix = mixBlock;
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
                     primer.setBlockState(x, k, z, this.mix);
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

   public static class TerrainBYGBiome extends TerrainBase {
      private float hillockWavelength = 30.0F;
      private float hillockBoost = 5.0F;
      private float hillockVariance = 10.0F;
      private float hillockSpikeWidth = 8.0F;
      private float hillockSpikeHeight = 4.0F;
      private float terrainBase = 68.0F;
      private JitterEffect biomeHeight;

      public TerrainBYGBiome() {
         BumpyHillsEffect onTop = new BumpyHillsEffect();
         onTop.hillHeight = this.hillockVariance;
         onTop.hillWavelength = this.hillockWavelength;
         onTop.spikeHeight = this.hillockSpikeHeight;
         onTop.spikeWavelength = this.hillockSpikeWidth;
         onTop.hillOctave = 1;
         HeightEffect hillLevel = onTop.plus(new RaiseEffect(this.hillockBoost + this.terrainBase));
         VariableRuggednessEffect hills = new VariableRuggednessEffect();
         hills.ruggedTerrain = hillLevel;
         hills.smoothTerrain = new RaiseEffect(this.terrainBase);
         hills.octave = 1;
         hills.startTransition = 0.1F;
         hills.transitionWidth = 0.35F;
         hills.wavelength = this.hillockWavelength;
         HeightEffect unJittered = hills.plus(new GroundEffect(6.0F));
         this.biomeHeight = new JitterEffect();
         this.biomeHeight.amplitude = 2.0F;
         this.biomeHeight.wavelength = 5.0F;
         this.biomeHeight.jittered = unJittered;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         float result = this.biomeHeight.added(rtgWorld, (float)x, (float)y);
         return riverized(result, river);
      }
   }
}
