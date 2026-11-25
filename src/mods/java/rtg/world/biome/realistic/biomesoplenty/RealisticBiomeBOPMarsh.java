package rtg.world.biome.realistic.biomesoplenty;

import biomesoplenty.api.biome.BOPBiomes;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;
import rtg.api.world.surface.SurfaceBase;
import rtg.api.world.terrain.TerrainBase;
import rtg.api.world.terrain.heighteffect.HeightVariation;

public class RealisticBiomeBOPMarsh extends RealisticBiomeBOPBase {
   public static Biome biome = (Biome)BOPBiomes.marsh.orNull();
   public static Biome river = Biomes.RIVER;

   public RealisticBiomeBOPMarsh() {
      super(biome);
   }

   public Biome preferredBeach() {
      return biome;
   }

   public void initConfig() {
   }

   public TerrainBase initTerrain() {
      return new RealisticBiomeBOPMarsh.TerrainBOPMarsh();
   }

   public SurfaceBase initSurface() {
      return new RealisticBiomeBOPMarsh.SurfaceBOPMarsh(this.getConfig(), biome.topBlock, biome.fillerBlock);
   }

   public static class SurfaceBOPMarsh extends SurfaceBase {
      public SurfaceBOPMarsh(BiomeConfig config, IBlockState top, IBlockState filler) {
         super(config, top, filler);
      }

      public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
         Random rand = rtgWorld.rand();
         float c = TerrainBase.calcCliff(x, z, noise);
         boolean cliff = c > 1.4F;

         for(int k = 255; k > -1; --k) {
            Block b = primer.getBlockState(x, k, z).getBlock();
            if (b == Blocks.AIR) {
               depth = -1;
            } else if (b == Blocks.STONE) {
               ++depth;
               if (cliff && k > 64) {
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

   public static class TerrainBOPMarsh extends TerrainBase {
      private float baseHeight = 62.0F;
      private HeightVariation variation = new HeightVariation();
      private HeightVariation smallVariation;

      public TerrainBOPMarsh() {
         this.variation.height = 1.5F;
         this.variation.wavelength = 20.0F;
         this.variation.octave = 0;
         this.smallVariation = new HeightVariation();
         this.smallVariation.height = 1.5F;
         this.smallVariation.wavelength = 10.0F;
         this.smallVariation.octave = 0;
      }

      public float generateNoise(RTGWorld rtgWorld, int x, int y, float border, float river) {
         return this.baseHeight + this.variation.added(rtgWorld, (float)x, (float)y) + this.smallVariation.added(rtgWorld, (float)x, (float)y);
      }
   }
}
