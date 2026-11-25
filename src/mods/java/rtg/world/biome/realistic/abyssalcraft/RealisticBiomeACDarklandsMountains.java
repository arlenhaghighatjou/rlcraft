package rtg.world.biome.realistic.abyssalcraft;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.MountainsWithPassesEffect;

public class RealisticBiomeACDarklandsMountains extends RealisticBiomeACBase {
   public static Biome biome = ACBiomes.darklands_mountains;
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeACDarklandsMountains() {
      super(biome);
   }

   public void initConfig() {
      this.getConfig().ALLOW_RIVERS.set(false);
      this.getConfig().ALLOW_SCENIC_LAKES.set(false);
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeACDarklandsMountains.TerrainACDarklandsMountains(120.0F, 100.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeACDarklandsMountains.SurfaceACDarklandsMountains(this.getConfig(), biome.topBlock, biome.fillerBlock, 0.2F);
   }

   public static class SurfaceACDarklandsMountains extends SurfaceACBase {
      private float min;
      private float sCliff;
      private float sHeight;
      private float sStrength;
      private float iCliff;
      private float iHeight;
      private float iStrength;
      private float cCliff;

      public SurfaceACDarklandsMountains(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff) {
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

      public SurfaceACDarklandsMountains(BiomeConfig config, IBlockState top, IBlockState fill, float minCliff, float stoneCliff, float stoneHeight, float stoneStrength, float snowCliff, float snowHeight, float snowStrength, float clayCliff) {
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

                  if (cliff != 1 && cliff != 2) {
                     if (cliff == 3) {
                        primer.setBlockState(x, k, z, this.hcStone());
                     } else if (k < 63) {
                        if (k < 62) {
                           primer.setBlockState(x, k, z, this.fillerBlock);
                        } else {
                           primer.setBlockState(x, k, z, this.topBlock);
                        }
                     } else {
                        primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
                     }
                  } else if (rand.nextInt(3) == 0) {
                     primer.setBlockState(x, k, z, this.hcCobble());
                  } else {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               } else if (depth < 6) {
                  if (cliff != 1 && cliff != 2) {
                     if (cliff == 3) {
                        primer.setBlockState(x, k, z, this.hcStone());
                     } else {
                        primer.setBlockState(x, k, z, Blocks.DIRT.getDefaultState());
                     }
                  } else if (rand.nextInt(3) == 0) {
                     primer.setBlockState(x, k, z, this.hcCobble());
                  } else {
                     primer.setBlockState(x, k, z, this.hcStone());
                  }
               }
            }
         }

      }
   }

   public static class TerrainACDarklandsMountains extends TerrainBase {
      private float width;
      private float strength;
      private float spikeWidth;
      private float spikeHeight;
      protected HeightEffect height;

      public TerrainACDarklandsMountains(float mountainWidth, float mountainStrength) {
         this(mountainWidth, mountainStrength, 90.0F);
      }

      public TerrainACDarklandsMountains(float mountainWidth, float mountainStrength, float baseHeight) {
         this.spikeWidth = 40.0F;
         this.spikeHeight = 60.0F;
         this.width = mountainWidth;
         this.strength = mountainStrength;
         this.base = baseHeight;
         MountainsWithPassesEffect mountainEffect = new MountainsWithPassesEffect();
         mountainEffect.mountainHeight = this.strength;
         mountainEffect.mountainWavelength = this.width;
         mountainEffect.spikeHeight = this.spikeHeight;
         mountainEffect.spikeWavelength = this.spikeWidth;
         this.height = new JitterEffect(6.0F, 10.0F, mountainEffect);
         this.height = new JitterEffect(2.0F, 6.0F, this.height);
         HeightVariation passHeight = new HeightVariation();
         passHeight.height = 15.0F;
         passHeight.octave = 4;
         passHeight.wavelength = 70.0F;
         this.height = this.height.plus(passHeight);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.height.added(rtgWorld, (float)x, (float)y) + this.base, river);
      }
   }
}
