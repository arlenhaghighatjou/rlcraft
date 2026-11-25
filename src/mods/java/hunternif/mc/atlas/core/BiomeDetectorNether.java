package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeDetectorNether extends BiomeDetectorBase implements IBiomeDetector {
   private static final int airProbeLevel = 50;
   private static final int lavaSeaLevel = 31;
   private static final int priorityLava = 1;

   public BiomeDetectorNether() {
   }

   public int getBiomeID(Chunk chunk) {
      int biomesCount = Biome.REGISTRY.getKeys().size();

      int[] chunkBiomes;
      try {
         chunkBiomes = ByteUtil.unsignedByteToIntArray(biomeArrayMethod.invoke(chunk));
      } catch (InvocationTargetException | IllegalAccessException var12) {
         throw new RuntimeException(var12);
      }

      int[] biomeOccurrences = new int[biomesCount];
      int lavaOccurences = 0;
      int groundOccurences = 0;
      int hellID = Biome.getIdForBiome(Biomes.HELL);

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int biomeID = chunkBiomes[x << 4 | z];
            if (biomeID == hellID) {
               Block netherBlock = chunk.getBlockState(x, 31, z).getBlock();
               if (netherBlock == Blocks.LAVA) {
                  ++lavaOccurences;
               } else {
                  netherBlock = chunk.getBlockState(x, 50, z).getBlock();
                  if (netherBlock != null && netherBlock != Blocks.AIR) {
                     ++biomeOccurrences[biomeID];
                  } else {
                     ++groundOccurences;
                  }
               }
            } else if (biomeID >= 0 && biomeID < biomesCount && Biome.getBiomeForId(biomeID) != null) {
               biomeOccurrences[biomeID] += this.priorityForBiome(Biome.getBiomeForId(biomeID));
            }
         }
      }

      int meanBiomeId = -1;
      int meanBiomeOccurences = 0;

      for(int i = 0; i < biomeOccurrences.length; ++i) {
         if (biomeOccurrences[i] > meanBiomeOccurences) {
            meanBiomeId = i;
            meanBiomeOccurences = biomeOccurrences[i];
         }
      }

      if (meanBiomeOccurences < lavaOccurences) {
         meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID("lava");
      } else if (meanBiomeOccurences < groundOccurences) {
         meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID("lavaShore");
      }

      return meanBiomeId;
   }
}
