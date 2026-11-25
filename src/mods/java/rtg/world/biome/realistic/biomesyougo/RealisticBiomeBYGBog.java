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
import rtg.api.world.RTGWorld;
import rtg.api.world.biome.RealisticBiomeBase;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightVariation;
import rtg.api.world.terrain.heighteffect.HillockEffect;

public class RealisticBiomeBYGBog extends RealisticBiomeBYGBase {
   public RealisticBiomeBYGBog(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.NORMAL, RealisticBiomeBase.BeachType.NORMAL);
   }

   public void initConfig() {
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.BIRCH), BlockUtil.getStateLog(EnumType.DARK_OAK)}, new int[]{2, 2});
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBYGBog.TerrainBYGBiome();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBYGBog.SurfaceBYGBiome(this.getConfig(), this.baseBiome().topBlock, this.baseBiome().fillerBlock);
   }

   public static class SurfaceBYGBiome extends SurfaceBase {
      public SurfaceBYGBiome(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
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
                     if (rand.nextInt(3) == 0) {
                        primer.setBlockState(x, k, z, this.hcCobble());
                     } else {
                        primer.setBlockState(x, k, z, this.hcStone());
                     }
                  } else if (depth < 10) {
                     primer.setBlockState(x, k, z, this.hcStone());
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

   public static class TerrainBYGBiome extends TerrainBase {
      private final float bottom = 62.0F;
      private final HeightVariation bottomVariation = new HeightVariation();
      private final HillockEffect smallHills;
      private final HillockEffect mediumHills;

      public TerrainBYGBiome() {
         this.bottomVariation.height = 2.0F;
         this.bottomVariation.octave = 0;
         this.bottomVariation.wavelength = 40.0F;
         this.smallHills = new HillockEffect();
         this.smallHills.height = 5.0F;
         this.smallHills.wavelength = 25.0F;
         this.smallHills.minimumSimplex = 0.2F;
         this.smallHills.octave = 1;
         this.mediumHills = new HillockEffect();
         this.mediumHills.height = 10.0F;
         this.mediumHills.wavelength = 40.0F;
         this.mediumHills.minimumSimplex = 0.2F;
         this.mediumHills.octave = 2;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         float increment = this.bottomVariation.added(rtgWorld, (float)x, (float)y) + this.smallHills.added(rtgWorld, (float)x, (float)y);
         increment = increment + this.mediumHills.added(rtgWorld, (float)x, (float)y);
         return riverized(62.0F + increment, river);
      }
   }
}
