package net.theawesomegem.fishingmadebetter.common.capability.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.util.RandomUtil;
import net.theawesomegem.fishingmadebetter.util.TimeUtil;

public class ChunkFishingData implements IChunkFishingData {
   private final Map<String, PopulationData> fishPopulationMap = new HashMap();
   private final Map<String, Type> byName = new HashMap();
   private Chunk chunk;
   private World world;
   private long timeSinceRegenerated;
   private long timeSinceReproduction;

   public ChunkFishingData() {
   }

   public void chunkLoad(Chunk chunk) {
      this.world = chunk.getWorld();
      this.chunk = chunk;
      this.timeSinceReproduction = this.world.getTotalWorldTime();
      this.checkForRegeneration(this.world.getTotalWorldTime());
   }

   public void addFishes(Map<String, PopulationData> fishMap, boolean markDirty) {
      this.fishPopulationMap.clear();
      this.fishPopulationMap.putAll(fishMap);
      if (markDirty && this.chunk != null) {
         this.chunk.markDirty();
      }

   }

   public Map<String, PopulationData> getFishes(long currentTime) {
      if (ConfigurationManager.server.regenerateEmptyChunks) {
         this.checkForRegeneration(currentTime);
      }

      this.checkForReproduction(currentTime);
      return this.fishPopulationMap;
   }

   public Map<String, PopulationData> getRawFishMap() {
      return this.fishPopulationMap;
   }

   public void reducePopulation(String fishId, int reduce, long currentTime, boolean markDirty) {
      if (this.fishPopulationMap.containsKey(fishId)) {
         PopulationData populationData = (PopulationData)this.fishPopulationMap.get(fishId);
         int quantity = populationData.getQuantity();
         int newPopulation = quantity - reduce;
         if (newPopulation <= 0) {
            this.removeFish(fishId, currentTime, markDirty);
         } else {
            populationData.setQuantity(newPopulation);
         }

         if (markDirty && this.chunk != null) {
            this.chunk.markDirty();
         }

      }
   }

   public boolean hasFishes() {
      return this.fishPopulationMap.size() > 0;
   }

   public long getTimeSinceLastRegen() {
      return this.timeSinceRegenerated;
   }

   public long getTimeSinceLastReproduced() {
      return this.timeSinceReproduction;
   }

   public void setTimeSinceLastRegen(long time, boolean markDirty) {
      this.timeSinceRegenerated = time;
      if (markDirty) {
         this.chunk.markDirty();
      }

   }

   public void setTimeSinceLastReproduced(long time, boolean markDirty) {
      this.timeSinceReproduction = time;
      if (markDirty && this.chunk != null) {
         this.chunk.markDirty();
      }

   }

   public void setPopulationData(String fishId, PopulationData populationData, boolean markDirty) {
      if (this.fishPopulationMap.containsKey(fishId)) {
         this.fishPopulationMap.put(fishId, populationData);
         if (markDirty && this.chunk != null) {
            this.chunk.markDirty();
         }

      }
   }

   private void checkForRegeneration(long currentTime) {
      if (!this.hasFishes()) {
         if (currentTime >= this.timeSinceRegenerated) {
            this.regenerateInitialFishes();
         }

      }
   }

   private void checkForReproduction(long currentTime) {
      boolean updated = false;
      if (this.hasFishes()) {
         long timeDiff = currentTime - this.timeSinceReproduction;
         int steps = (int)(timeDiff / this.getReproductionTime());
         if (steps >= 1) {
            for(int i = 0; i < steps; ++i) {
               for(PopulationData populationData : this.fishPopulationMap.values()) {
                  FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(populationData.getFishType());
                  if (fishData != null && populationData.getQuantity() >= 2 && !populationData.isHungry(currentTime)) {
                     updated = true;
                     this.timeSinceReproduction = currentTime;
                     populationData.setReproductionTick(populationData.getReproductionTick() + 1);
                     if (populationData.getReproductionTick() >= fishData.reproductionTime) {
                        populationData.setReproductionTick(0);
                        populationData.setQuantity(populationData.getQuantity() + 1);
                     }
                  }
               }
            }

            if (updated && this.chunk != null) {
               this.chunk.markDirty();
            }

         }
      }
   }

   private void regenerateInitialFishes() {
      Map<String, PopulationData> fishPopulationMap = new HashMap();
      EaglercraftRandom random = this.chunk.getWorld().rand;

      for(FishData fishData : CustomConfigurationHandler.getAllFishData()) {
         if (fishData.dimensionBlacklist != this.isChunkInDimension(fishData.dimensionList) && fishData.biomeBlacklist != this.isChunkBiomeInBiomeList(fishData.biomeTagList)) {
            int popCount = this.getInitialPopulationCount(random, fishData);
            if (popCount > 0) {
               PopulationData populationData = new PopulationData(fishData.fishId, popCount, 0, this.world.getTotalWorldTime());
               fishPopulationMap.put(fishData.fishId, populationData);
            }
         }
      }

      this.addFishes(fishPopulationMap, true);
   }

   private void removeFish(String fishId, long currentTime, boolean markDirty) {
      if (this.fishPopulationMap.containsKey(fishId)) {
         this.fishPopulationMap.remove(fishId);
         if (!this.hasFishes()) {
            this.timeSinceRegenerated = currentTime + this.getRegenTime();
         }

         if (markDirty && this.chunk != null) {
            this.chunk.markDirty();
         }

      }
   }

   private long getRegenTime() {
      return TimeUtil.minutesToMinecraftTicks(ConfigurationManager.server.fishRegenerationTime);
   }

   private long getReproductionTime() {
      return TimeUtil.minutesToMinecraftTicks(ConfigurationManager.server.fishReproductionTime);
   }

   private int getInitialPopulationCount(EaglercraftRandom random, FishData fishData) {
      float popRarity = (float)fishData.rarity * 0.5F;
      int popFactor = ConfigurationManager.server.randomPopulationFactor;
      return Math.max(0, (int)((double)popRarity * (1.0D + (double)((float)RandomUtil.getRandomInRange(random, -popFactor, popFactor)) / 100.0D) + 0.5D));
   }

   private boolean isChunkBiomeInBiomeList(List<String> biomeTagList) {
      if (this.chunk == null) {
         return false;
      } else if (biomeTagList.isEmpty()) {
         return false;
      } else {
         if (this.byName.isEmpty()) {
            this.initByName();
         }

         ChunkPos chunkPos = this.chunk.getPos();
         BiomeProvider biomeProv = this.world.getBiomeProvider();

         for(String biomeTag : biomeTagList) {
            if (this.byName.get(biomeTag) != null && (BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(0, 60, 0), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(8, 60, 0), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(15, 60, 0), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(0, 60, 0), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(0, 60, 8), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(0, 60, 15), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(8, 60, 15), biomeProv), (Type)this.byName.get(biomeTag)) || BiomeDictionary.hasType(this.chunk.getBiome(chunkPos.getBlock(15, 60, 8), biomeProv), (Type)this.byName.get(biomeTag)))) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isChunkInDimension(List<Integer> dimensionList) {
      if (this.chunk == null) {
         return false;
      } else if (dimensionList.isEmpty()) {
         return false;
      } else {
         return dimensionList.contains(Integer.valueOf(this.chunk.getWorld().provider.getDimension()));
      }
   }

   private void initByName() {
      for(Type type : Type.getAll()) {
         this.byName.put(type.getName(), type);
      }

   }
}
