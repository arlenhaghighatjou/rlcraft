package com.teammetallurgy.aquaculture.loot;

import com.teammetallurgy.aquaculture.handlers.Config;
import com.teammetallurgy.aquaculture.items.AquacultureItems;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class FishLoot {
   public static FishLoot instance;
   Map<BiomeType, WeightedLootSet> fishBiomeMap = new HashMap();
   Map<BiomeType, WeightedLootSet> junkBiomeMap = new HashMap();

   private FishLoot() {
   }

   public static FishLoot instance() {
      if (instance == null) {
         instance = new FishLoot();
      }

      return instance;
   }

   public void addBiome(BiomeType biome) {
      this.junkBiomeMap.put(biome, new WeightedLootSet());
      this.fishBiomeMap.put(biome, new WeightedLootSet());
   }

   public void addFish(ItemStack fish, BiomeType biome, int rarity) {
      if (!this.fishBiomeMap.containsKey(biome)) {
         this.fishBiomeMap.put(biome, new WeightedLootSet());
      }

      ((WeightedLootSet)this.fishBiomeMap.get(biome)).addLoot(fish, rarity, 1, 1);
   }

   public void addJunkLoot(ItemStack junk, int rarity) {
      for(WeightedLootSet lootSet : this.junkBiomeMap.values()) {
         lootSet.addLoot(junk, rarity, 1, 1);
      }

   }

   public void addJunkLoot(ItemStack junk, BiomeType[] biomes, int rarity) {
      for(BiomeType biome : biomes) {
         this.addJunkLoot(junk, biome, rarity);
      }

   }

   public void addJunkLoot(ItemStack fish, BiomeType biome, int rarity) {
      if (!this.junkBiomeMap.containsKey(biome)) {
         this.junkBiomeMap.put(biome, new WeightedLootSet());
      }

      ((WeightedLootSet)this.junkBiomeMap.get(biome)).addLoot(fish, rarity, 1, 1);
   }

   @Nonnull
   public ItemStack getRandomFish(int biomeID) {
      BiomeType biome = BiomeType.getBiomeType(biomeID);
      ItemStack fishStack;
      if (biome != null && this.fishBiomeMap.containsKey(biome)) {
         fishStack = ((WeightedLootSet)this.fishBiomeMap.get(biome)).getRandomLoot();
      } else {
         fishStack = ((WeightedLootSet)this.fishBiomeMap.get(BiomeType.freshwater)).getRandomLoot();
      }

      return fishStack;
   }

   @Nonnull
   public ItemStack getRandomFish(int biomeID, int heavyLineLvl) {
      BiomeType biome = BiomeType.getBiomeType(biomeID);
      ItemStack fishStack;
      if (biome != null && this.fishBiomeMap.containsKey(biome)) {
         fishStack = ((WeightedLootSet)this.fishBiomeMap.get(biome)).getRandomLoot();
      } else {
         fishStack = ((WeightedLootSet)this.fishBiomeMap.get(BiomeType.freshwater)).getRandomLoot();
      }

      if (Config.assignRandomWeight) {
         AquacultureItems.fish.assignRandomWeight(fishStack, heavyLineLvl);
      }

      return fishStack;
   }

   @Nonnull
   public ItemStack getRandomJunk(int biomeID) {
      BiomeType biome = BiomeType.getBiomeType(biomeID);
      return biome != null && this.junkBiomeMap.containsKey(biome) ? ((WeightedLootSet)this.junkBiomeMap.get(biome)).getRandomLoot() : ((WeightedLootSet)this.junkBiomeMap.get(BiomeType.freshwater)).getRandomLoot();
   }
}
