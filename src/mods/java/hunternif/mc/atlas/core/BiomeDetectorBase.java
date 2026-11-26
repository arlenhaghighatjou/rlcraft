package hunternif.mc.atlas.core;

import hunternif.mc.atlas.ext.ExtTileIdMap;
import hunternif.mc.atlas.util.ByteUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.ToIntFunction;
import net.minecraft.block.Block;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class BiomeDetectorBase implements IBiomeDetector {
   private boolean doScanPonds = true;
   private boolean doScanRavines = true;
   private static final int waterPoolBiomeID = Biome.getIdForBiome(Biomes.RIVER);
   private static final int priorityRavine = 12;
   private static final int priorityWaterPool = 3;
   private static final int prioritylavaPool = 6;
   private static final int ravineMinDepth = 7;
   private static final Set<Biome> waterBiomes = new HashSet();
   private static final Set<Biome> beachBiomes = new HashSet();
   private static final Set<Biome> swampBiomes = new HashSet();

   public BiomeDetectorBase() {
   }

   public static void scanBiomeTypes() {
      waterBiomes.addAll(BiomeDictionary.getBiomes(Type.WATER));
      beachBiomes.addAll(BiomeDictionary.getBiomes(Type.BEACH));
      swampBiomes.addAll(BiomeDictionary.getBiomes(Type.SWAMP));
   }

   public void setScanPonds(boolean value) {
      this.doScanPonds = value;
   }

   public void setScanRavines(boolean value) {
      this.doScanRavines = value;
   }

   int priorityForBiome(Biome biome) {
      if (waterBiomes.contains(biome)) {
         return 4;
      } else {
         return beachBiomes.contains(biome) ? 3 : 1;
      }
   }

   public int getBiomeID(Chunk chunk) {
      int biomeCount = Biome.REGISTRY.getKeys().size();

      int[] chunkBiomes;
      chunkBiomes = ByteUtil.unsignedByteToIntArray(chunk.getBiomeArray());

      Map<Integer, Integer> biomeOccurrences = new HashMap(biomeCount);
      int lavaOccurrences = 0;
      int ravineOccurences = 0;

      for(int x = 0; x < 16; ++x) {
         for(int z = 0; z < 16; ++z) {
            int biomeID = chunkBiomes[x << 4 | z];
            if (this.doScanPonds) {
               int y = chunk.getHeightValue(x, z);
               if (y > 0) {
                  Block topBlock = chunk.getBlockState(x, y - 1, z).getBlock();
                  Block topBlock2 = chunk.getBlockState(x, y, z).getBlock();
                  if (topBlock == Blocks.WATER && !swampBiomes.contains(Biome.getBiomeForId(biomeID))) {
                     int occurrence = ((Integer)biomeOccurrences.getOrDefault(Integer.valueOf(waterPoolBiomeID), Integer.valueOf(0))).intValue() + 3;
                     biomeOccurrences.put(Integer.valueOf(waterPoolBiomeID), Integer.valueOf(occurrence));
                  } else if (topBlock2 == Blocks.LAVA) {
                     lavaOccurrences += 6;
                  }
               }
            }

            if (this.doScanRavines && chunk.getHeightValue(x, z) < chunk.getWorld().provider.getAverageGroundLevel() - 7) {
               ravineOccurences += 12;
            }

            if (biomeID >= 0 && Biome.getBiomeForId(biomeID) != null) {
               int occurrence = ((Integer)biomeOccurrences.getOrDefault(Integer.valueOf(biomeID), Integer.valueOf(0))).intValue() + this.priorityForBiome(Biome.getBiomeForId(biomeID));
               biomeOccurrences.put(Integer.valueOf(biomeID), Integer.valueOf(occurrence));
            }
         }
      }

      try {
         Entry<Integer, Integer> meanBiome = (Entry)Collections.max(biomeOccurrences.entrySet(), Comparator.comparingInt(Entry::getValue));
         int meanBiomeId = ((Integer)meanBiome.getKey()).intValue();
         int meanBiomeOccurrences = ((Integer)meanBiome.getValue()).intValue();
         if (meanBiomeOccurrences < ravineOccurences) {
            return ExtTileIdMap.instance().getPseudoBiomeID("ravine");
         } else if (meanBiomeOccurrences < lavaOccurrences) {
            return ExtTileIdMap.instance().getPseudoBiomeID("lava");
         } else {
            return meanBiomeId;
         }
      } catch (NoSuchElementException var14) {
         return Biome.getIdForBiome(Biomes.DEFAULT);
      }
   }
}
