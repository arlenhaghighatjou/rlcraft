package rtg.world.biome.realistic.zoesteria;

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
import rtg.api.world.terrain.heighteffect.GroundEffect;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.SpikeEverywhereEffect;
import rtg.api.world.terrain.heighteffect.VoronoiBorderEffect;

public class RealisticBiomeZOEBushlandHills extends RealisticBiomeZOEBase {
   public RealisticBiomeZOEBushlandHills(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_BLOCK).set("");
      this.getConfig().addProperty(this.getConfig().SURFACE_MIX_FILLER_BLOCK).set("");
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeZOEBushlandHills.RidgedExtremeHills(140.0F, 67.0F, 180.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeZOEBushlandHills.SurfaceVanillaExtremeHills(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, this.baseBiome().topBlock, this.baseBiome().fillerBlock, 60.0F, -0.14F, 14.0F, 0.25F);
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

   public static class SurfaceVanillaExtremeHills extends SurfaceBase {
      private IBlockState mixBlockTop;
      private IBlockState mixBlockFill;
      private float width;
      private float height;
      private float smallW;
      private float smallS;

      public SurfaceVanillaExtremeHills(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFill, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
         super(config, top, filler);
         this.mixBlockTop = this.getConfigBlock(config.SURFACE_MIX_BLOCK.get(), mixTop);
         this.mixBlockFill = this.getConfigBlock(config.SURFACE_MIX_FILLER_BLOCK.get(), mixFill);
         this.width = mixWidth;
         this.height = mixHeight;
         this.smallW = smallWidth;
         this.smallS = smallStrength;
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
                  if (simplex.noise2f((float)i / this.width, (float)j / this.width) + simplex.noise2f((float)i / this.smallW, (float)j / this.smallW) * this.smallS > this.height) {
                     primer.setBlockState(x, k, z, this.mixBlockTop);
                     mix = true;
                  } else {
                     primer.setBlockState(x, k, z, this.topBlock);
                  }
               } else if (depth < 4) {
                  if (mix) {
                     primer.setBlockState(x, k, z, this.mixBlockFill);
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
