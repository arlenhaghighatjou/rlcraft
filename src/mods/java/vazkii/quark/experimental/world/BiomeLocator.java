package vazkii.quark.experimental.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.ChunkGeneratorSettings.Factory;

public class BiomeLocator {
   public BiomeLocator() {
   }

   public static BlockPos spiralOutwardsLookingForBiome(World world, Biome biomeToFind, double startX, double startZ) {
      int sampleSpacing = 4 << getBiomeSize(world);
      int maxDist = sampleSpacing * 100;
      return spiralOutwardsLookingForBiome(world, biomeToFind, startX, startZ, maxDist, sampleSpacing);
   }

   private static BlockPos spiralOutwardsLookingForBiome(World world, Biome biomeToFind, double startX, double startZ, int maxDist, int sampleSpace) {
      if (maxDist > 0 && sampleSpace > 0) {
         if (world.isRemote) {
            return null;
         } else {
            BiomeProvider chunkManager = world.getBiomeProvider();
            double a = (double)sampleSpace / Math.sqrt(3.141592653589793D);
            double b = 2.0D * Math.sqrt(3.141592653589793D);
            double dist = 0.0D;

            for(int n = 0; dist < (double)maxDist; ++n) {
               double rootN = Math.sqrt((double)n);
               dist = a * rootN;
               double x = startX + dist * Math.sin(b * rootN);
               double z = startZ + dist * Math.cos(b * rootN);
               Biome[] biomesAtSample = chunkManager.getBiomes((Biome[])null, (int)x, (int)z, 1, 1, false);
               if (biomesAtSample[0] == biomeToFind) {
                  return new BlockPos((int)x, 0, (int)z);
               }
            }

            return null;
         }
      } else {
         throw new IllegalArgumentException("maxDist and sampleSpace must be positive");
      }
   }

   private static int getBiomeSize(World world) {
      String generatorSettingsJson = world.getWorldInfo().getGeneratorOptions();
      return Factory.jsonToFactory(generatorSettingsJson).build().biomeSize;
   }
}
