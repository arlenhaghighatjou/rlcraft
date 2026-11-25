package rtg.world.biome.realistic.projectvibrantjourneys;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.util.BlockUtil;
import rtg.api.util.noise.SimplexNoise;
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.SpikeEverywhereEffect;
import rtg.api.world.terrain.heighteffect.VoronoiBorderEffect;

public class RealisticBiomePVJRedwoodPeaks extends RealisticBiomePVJBase {
   public RealisticBiomePVJRedwoodPeaks(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_2_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomePVJRedwoodPeaks.RidgedExtremeHills(150.0F, 67.0F, 200.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomePVJRedwoodPeaks.SurfaceVanillaForest(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, 0.0F, 1.5F, 60.0F, 65.0F, 1.5F, BlockUtil.getStateDirt(DirtType.PODZOL), 0.4F, BlockUtil.getStateDirt(DirtType.COARSE_DIRT), -0.5F);
   }

   public static class RidgedExtremeHills extends TerrainBase {
      private final HeightEffect heightIncrease;
      private final HeightEffect multiplier;
      private final HeightEffect groundEffect;
      private float height;
      private float width;
      private float ridgeWidth = 300.0F;
      private float valleyFloor = -0.2F;

      public RidgedExtremeHills(float landHeight, float baseHeight, float hillWidth) {
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
         SpikeEverywhereEffect baseHills = new SpikeEverywhereEffect();
         baseHills.spiked = new RaiseEffect(this.height * 2.0F / 3.0F);
         baseHills.wavelength = this.width;
         baseHills.minimumSimplex = -0.2F;
         baseHills.octave = 3;
         baseHills.power = 1.9F;
         SpikeEverywhereEffect additionalHeightSpikes = new SpikeEverywhereEffect();
         additionalHeightSpikes.spiked = new RaiseEffect(this.height / 3.0F);
         additionalHeightSpikes.wavelength = this.width / 3.0F;
         additionalHeightSpikes.minimumSimplex = -0.2F;
         additionalHeightSpikes.octave = 4;
         additionalHeightSpikes.power = 1.9F;
         JitterEffect additionalHeight = new JitterEffect();
         additionalHeight.amplitude = additionalHeightSpikes.wavelength / 3.0F;
         additionalHeight.wavelength = additionalHeightSpikes.wavelength / 2.0F;
         additionalHeight.jittered = additionalHeightSpikes;
         SpikeEverywhereEffect rougheningSpikes = new SpikeEverywhereEffect();
         rougheningSpikes.spiked = new RaiseEffect(this.height / 8.0F);
         rougheningSpikes.wavelength = this.width / 10.0F;
         rougheningSpikes.minimumSimplex = -0.2F;
         rougheningSpikes.octave = 5;
         rougheningSpikes.power = 1.9F;
         JitterEffect roughening = new JitterEffect();
         roughening.amplitude = rougheningSpikes.wavelength / 3.0F;
         roughening.wavelength = rougheningSpikes.wavelength / 2.0F;
         roughening.jittered = rougheningSpikes;
         JitterEffect hillJitter = new JitterEffect();
         hillJitter.amplitude = 15.0F;
         hillJitter.wavelength = 50.0F;
         hillJitter.jittered = baseHills.plus(additionalHeight).plus(roughening);
         this.heightIncrease = hillJitter;
         VoronoiBorderEffect ridging = new VoronoiBorderEffect();
         ridging.pointWavelength = this.ridgeWidth;
         ridging.floor = this.valleyFloor;
         ridging.minimumDivisor = 0.2F;
         JitterEffect ridgeJitter = new JitterEffect();
         ridgeJitter.amplitude = 15.0F;
         ridgeJitter.wavelength = 50.0F;
         ridgeJitter.jittered = ridging;
         JitterEffect ridgeJitterrette = new JitterEffect();
         ridgeJitterrette.amplitude = 5.0F;
         ridgeJitterrette.wavelength = 20.0F;
         ridgeJitterrette.jittered = ridgeJitter;
         this.multiplier = ridgeJitterrette;
         this.groundEffect = new GroundEffect(6.0F);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         float groundEffectLevel = this.groundEffect.added(rtgWorld, (float)x, (float)y);
         float ridging = this.multiplier.added(rtgWorld, (float)x, (float)y);
         ridging = TerrainBase.bayesianAdjustment(ridging, 2.0F);
         float result = this.base + ridging * (groundEffectLevel + this.heightIncrease.added(rtgWorld, (float)x, (float)y)) + groundEffectLevel;
         return TerrainBase.mountainCap(result);
      }
   }

   public static class SurfaceVanillaForest extends SurfaceBase {
      private float min;
      private float sCliff = 1.5F;
      private float sHeight = 60.0F;
      private float sStrength = 65.0F;
      private float cCliff = 1.5F;
      private IBlockState mixBlock;
      private float mixHeight;
      private IBlockState mix2Block;
      private float mix2Height;

      public SurfaceVanillaForest(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float clayCliff, IBlockState mix, float mixHeight, IBlockState mix2, float mix2Height) {
         super(config, top, fill);
         this.min = minCliff;
         this.sCliff = stoneCliff;
         this.sHeight = stoneHeight;
         this.sStrength = stoneStrength;
         this.cCliff = clayCliff;
         this.mixBlock = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mix);
         this.mixHeight = mixHeight;
         this.mix2Block = this.getConfigBlock(config.SURFACE_MIX_2_BLOCK.get(), mix2);
         this.mix2Height = mix2Height;
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
                  } else {
                     float mixNoise = simplex.noise2f((float)i / 12.0F, (float)j / 12.0F);
                     if (mixNoise < this.mix2Height) {
                        primer.setBlockState(x, k, z, this.mix2Block);
                        m = true;
                     } else if (mixNoise > this.mixHeight) {
                        primer.setBlockState(x, k, z, this.mixBlock);
                        m = true;
                     } else {
                        primer.setBlockState(x, k, z, this.topBlock);
                     }
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

   public static class TerrainVanillaExtremeHills extends TerrainBase {
      private float start;
      private float height;
      private float width;

      public TerrainVanillaExtremeHills(float hillStart, float landHeight, float baseHeight, float hillWidth) {
         this.start = hillStart;
         this.height = landHeight;
         this.base = baseHeight;
         this.width = hillWidth;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return terrainHighland((float)x, (float)y, rtgWorld, river, this.start, this.width, this.height, this.base);
      }
   }
}
