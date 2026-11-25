package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
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
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.GroundEffect;
import rtg.api.world.terrain.heighteffect.HeightEffect;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.JitterEffect;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.VariableRuggednessEffect;

public class RealisticBiomeBOPXericShrubland extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.xeric_shrubland.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPXericShrubland() {
      super(biome);
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPXericShrubland.TerrainBOPXericShrubland();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPXericShrubland.SurfaceBOPXericShrubland(this.getConfig(), biome.topBlock, biome.fillerBlock, biome.topBlock, biome.fillerBlock, 80.0F, -0.15F, 10.0F, 0.5F);
   }

   public static class SurfaceBOPXericShrubland extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceBOPXericShrubland(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
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
         boolean cliff = c > 2.4F;
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

   public static class TerrainBOPXericShrubland extends TerrainBase {
      private float minHeight = 65.0F;
      private float mesaWavelength = 24.0F;
      private float hillStrength = 5.0F;
      private float topBumpinessHeight = 3.0F;
      private float topBumpinessWavelength = 10.0F;
      private HeightEffect height;
      private HeightEffect groundEffect = new GroundEffect(3.0F);
      private float jitterAmplitude = 4.0F;
      private float jitterWavelength = 15.0F;

      public TerrainBOPXericShrubland() {
         HeightVariation topVariation = new HeightVariation();
         topVariation.height = this.hillStrength;
         topVariation.octave = 1;
         topVariation.wavelength = VariableRuggednessEffect.STANDARD_RUGGEDNESS_WAVELENGTH;
         HeightVariation topBumpiness = new HeightVariation();
         topBumpiness.height = this.topBumpinessHeight;
         topBumpiness.wavelength = this.topBumpinessWavelength;
         topBumpiness.octave = 3;
         this.height = new VariableRuggednessEffect(new RaiseEffect(0.0F), topVariation.plus(topBumpiness).plus(new RaiseEffect(this.hillStrength)), 0.4F, 0.3F, this.mesaWavelength);
         this.height = new JitterEffect(this.jitterAmplitude, this.jitterWavelength, this.height);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.minHeight + this.groundEffect.added(rtgWorld, (float)x, (float)y), river) + this.height.added(rtgWorld, (float)x, (float)y);
      }
   }
}
