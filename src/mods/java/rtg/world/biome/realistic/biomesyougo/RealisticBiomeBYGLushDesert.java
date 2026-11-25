package rtg.world.biome.realistic.biomesyougo;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks.EnumType;
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
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.RaiseEffect;
import rtg.api.world.terrain.heighteffect.VariableRuggednessEffect;

public class RealisticBiomeBYGLushDesert extends RealisticBiomeBYGBase {
   private static IBlockState bygMixBlock = Blocks.HARDENED_CLAY.getDefaultState();

   public RealisticBiomeBYGLushDesert(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      this.fallenTrees(new IBlockState[]{BlockUtil.getBlockStateFromCfgString("byg:ironwoodlog", BlockUtil.getStateLog(EnumType.BIRCH)), BlockUtil.getBlockStateFromCfgString("byg:ironwoodlog", BlockUtil.getStateLog(EnumType.BIRCH))}, new int[]{4, 4});
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGLushDesert.TerrainBOPLushDesert(65.0F, 40.0F, 10.0F);
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGLushDesert.SurfaceBOPLushDesert(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock, bygMixBlock, this.baseBiome().fillerBlock, 40.0F, 0.5F, 10.0F, 0.5F);
   }

   public static class SurfaceBOPLushDesert extends SurfaceBase {
      private IBlockState blockMixTop;
      private IBlockState blockMixFiller;
      private float floMixWidth;
      private float floMixHeight;
      private float floSmallWidth;
      private float floSmallStrength;

      public SurfaceBOPLushDesert(BiomeConfig config, IBlockState top, IBlockState filler, IBlockState mixTop, IBlockState mixFiller, float mixWidth, float mixHeight, float smallWidth, float smallStrength) {
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
         boolean cliff = c > 3.4F;
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

   public static class TerrainBOPLushDesert extends TerrainBase {
      private float minHeight;
      private float mesaWavelength;
      private float hillStrength;
      private float topBumpinessHeight = 2.0F;
      private float topBumpinessWavelength = 15.0F;
      private HeightEffect height;
      private HeightEffect groundEffect;

      public TerrainBOPLushDesert(float minHeight, float maxHeight, float hillStrength) {
         this.minHeight = minHeight;
         this.mesaWavelength = maxHeight;
         this.hillStrength = hillStrength;
         this.groundEffect = new GroundEffect(3.0F);
         HeightVariation topVariation = new HeightVariation();
         topVariation.height = hillStrength;
         topVariation.octave = 1;
         topVariation.wavelength = VariableRuggednessEffect.STANDARD_RUGGEDNESS_WAVELENGTH;
         HeightVariation topBumpiness = new HeightVariation();
         topBumpiness.height = this.topBumpinessHeight;
         topBumpiness.wavelength = this.topBumpinessWavelength;
         topBumpiness.octave = 3;
         this.height = new VariableRuggednessEffect(new RaiseEffect(0.0F), topVariation.plus(topBumpiness).plus(new RaiseEffect(hillStrength)), 0.3F, 0.15F, this.mesaWavelength);
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return riverized(this.minHeight + this.groundEffect.added(rtgWorld, (float)x, (float)y), river) + this.height.added(rtgWorld, (float)x, (float)y);
      }
   }
}
