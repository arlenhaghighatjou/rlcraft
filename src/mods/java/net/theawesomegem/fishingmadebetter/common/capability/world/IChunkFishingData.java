package net.theawesomegem.fishingmadebetter.common.capability.world;

import java.util.Map;
import net.minecraft.world.chunk.Chunk;

public interface IChunkFishingData {
   void chunkLoad(Chunk var1);

   void addFishes(Map<String, PopulationData> var1, boolean var2);

   Map<String, PopulationData> getFishes(long var1);

   Map<String, PopulationData> getRawFishMap();

   void reducePopulation(String var1, int var2, long var3, boolean var5);

   boolean hasFishes();

   long getTimeSinceLastRegen();

   long getTimeSinceLastReproduced();

   void setTimeSinceLastRegen(long var1, boolean var3);

   void setTimeSinceLastReproduced(long var1, boolean var3);

   void setPopulationData(String var1, PopulationData var2, boolean var3);
}
