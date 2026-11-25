package rtg.api.world.surface;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import rtg.api.config.BiomeConfig;
import rtg.api.world.RTGWorld;

public class SurfaceRiverOasis extends SurfaceBase {
   public SurfaceRiverOasis(BiomeConfig config) {
      super(config, Blocks.GRASS.getDefaultState(), Blocks.DIRT.getDefaultState());
   }

   public void paintTerrain(ChunkPrimer primer, int i, int j, int x, int z, int depth, RTGWorld rtgWorld, float[] noise, float river, Biome[] base) {
      float cutOffScale = rtgWorld.getGeneratorSettings().riverCutOffScale;
      float cutOffAmplitude = rtgWorld.getGeneratorSettings().riverCutOffAmpl;

      int highestY;
      for(highestY = 255; highestY > 0; --highestY) {
         IBlockState blockState = primer.getBlockState(x, highestY, z);
         if (blockState.getBlock() != Blocks.AIR) {
            break;
         }
      }

      float amplitude = 0.05F;
      float noiseValue = rtgWorld.simplexInstance(0).noise2f((float)i / 8.0F, (float)j / 8.0F) * amplitude / 1.0F + rtgWorld.simplexInstance(1).noise2f((float)i / 3.0F, (float)j / 3.0F) * amplitude / 2.0F;
      float noiseNeg = rtgWorld.simplexInstance(2).noise2f((float)i / cutOffScale, (float)j / cutOffScale) * cutOffAmplitude;
      noiseValue = noiseValue - noiseNeg;
      if (highestY > 62) {
         noiseValue -= (float)(highestY - 62) * 0.083333336F;
      }

      if ((double)river > 0.7D && (double)(river + noiseValue) > 0.85D) {
         for(int k = 255; k > -1; --k) {
            IBlockState blockState = primer.getBlockState(x, k, z);
            if (blockState.getBlock() == Blocks.AIR) {
               depth = -1;
            } else if (blockState.getMaterial() != Material.WATER) {
               ++depth;
               if (depth == 0 && k > 61) {
                  primer.setBlockState(x, k, z, Blocks.GRASS.getDefaultState());
               } else if (depth < 4) {
                  primer.setBlockState(x, k, z, Blocks.DIRT.getDefaultState());
               } else if (depth > 4) {
                  return;
               }
            }
         }
      }

   }
}
