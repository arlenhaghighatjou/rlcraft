package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public class BiomeDetectorEnd extends BiomeDetectorBase implements IBiomeDetector {
   public BiomeDetectorEnd() {
   }

   public int getBiomeID(Chunk chunk) {
      int biomesCount = Biome.REGISTRY.getKeys().size();

      int[] chunkBiomes;
      chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());

      int[] biomeOccurrences = new int[biomesCount];
      int islandOccurences = 0;
      int plantOccurences = 0;
      int voidOccurences = 0;
      int endID = Biome.getIdForBiome(Biomes.SKY);

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int biomeID = chunkBiomes[x << 4 | z];
            if (biomeID != endID) {
               if (biomeID >= 0 && biomeID < biomesCount && Biome.getBiomeForId(biomeID) != null) {
                  biomeOccurrences[biomeID] += this.priorityForBiome(Biome.getBiomeForId(biomeID));
               }
            } else {
               int top = chunk.getHeightValue(x, z);
               Block topBlock = chunk.getBlockState(x, top - 1, z).getBlock();
               if (topBlock == Blocks.END_STONE) {
                  ++islandOccurences;
                  Block rootBlock = chunk.getBlockState(x, top, z).getBlock();
                  if (rootBlock == Blocks.CHORUS_FLOWER || rootBlock == Blocks.CHORUS_PLANT) {
                     ++plantOccurences;
                  }
               }

               if (topBlock == Blocks.AIR) {
                  ++voidOccurences;
               }
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

      if (meanBiomeOccurences < islandOccurences) {
         if (plantOccurences == 0) {
            meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID("endIsland");
         } else {
            meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID("endIslandPlants");
         }
      } else if (meanBiomeOccurences < voidOccurences) {
         meanBiomeId = ExtTileIdMap.instance().getPseudoBiomeID("endVoid");
      }

      return meanBiomeId;
   }
}
