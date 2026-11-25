package sereneseasons.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import sereneseasons.config.json.BiomeData;
import sereneseasons.util.config.JsonUtil;

public class BiomeConfig {
   public static final Map<ResourceLocation, BiomeData> biomeDataMap = Maps.newHashMap();

   public BiomeConfig() {
   }

   public static void init(VFile2 configDir) {
      Map<String, BiomeData> defaultBiomeData = Maps.newHashMap();
      addBlacklistedBiomes(defaultBiomeData);
      addTropicalBiomes(defaultBiomeData);
      addDisabledCropBiomes(defaultBiomeData);
      biomeDataMap.clear();
      JsonObject defaults = new JsonObject();

      for(Entry<String, BiomeData> entry : defaultBiomeData.entrySet()) {
         JsonObject data = new JsonObject();
         data.addProperty("enable_seasonal_effects", Boolean.valueOf(entry.getValue().enableSeasonalEffects));
         data.addProperty("use_tropical_seasons", Boolean.valueOf(entry.getValue().useTropicalSeasons));
         data.addProperty("disable_crops", Boolean.valueOf(entry.getValue().disableCrops));
         defaults.add(entry.getKey(), data);
      }

      JsonObject loaded = JsonUtil.getOrCreateConfigFile(configDir, "biome_info.json", defaults);
      if (loaded != null) {
         for(Entry<String, JsonElement> entry : loaded.entrySet()) {
            JsonObject data = entry.getValue().getAsJsonObject();
            biomeDataMap.put(new ResourceLocation(entry.getKey()), new BiomeData(readFlag(data, "enable_seasonal_effects"), readFlag(data, "use_tropical_seasons"), readFlag(data, "disable_crops")));
         }
      }

   }

   private static boolean readFlag(JsonObject data, String name) {
      JsonElement flag = data.get(name);
      return flag != null && flag.getAsBoolean();
   }

   public static boolean enablesSeasonalEffects(Biome biome) {
      ResourceLocation name = biome.getRegistryName();
      return biomeDataMap.containsKey(name) ? ((BiomeData)biomeDataMap.get(name)).enableSeasonalEffects : true;
   }

   public static boolean usesTropicalSeasons(Biome biome) {
      ResourceLocation name = biome.getRegistryName();
      return biomeDataMap.containsKey(name) ? ((BiomeData)biomeDataMap.get(name)).useTropicalSeasons : false;
   }

   public static boolean disablesCrops(Biome biome) {
      ResourceLocation name = biome.getRegistryName();
      return biomeDataMap.containsKey(name) ? ((BiomeData)biomeDataMap.get(name)).disableCrops : false;
   }

   private static void addBlacklistedBiomes(Map<String, BiomeData> map) {
      for(String biomeName : Lists.newArrayList(new String[]{"minecraft:mushroom_island", "minecraft:mushroom_island_shore", "minecraft:ocean", "minecraft:deep_ocean", "minecraft:river", "biomesoplenty:mystic_grove", "biomesoplenty:ominous_woods", "biomesoplenty:wasteland", "biomesoplenty:flower_island", "biomesoplenty:coral_reef", "biomesoplenty:kelp_forest", "thaumcraft:magical_forest", "integrateddynamics:biome_meneglin", "abyssalcraft:darklands", "abyssalcraft:darklands_forest", "abyssalcraft:darklands_plains", "abyssalcraft:darklands_hills", "abyssalcraft:darklands_mountains", "abyssalcraft:coralium_infested_swamp"})) {
         if (!map.containsKey(biomeName)) {
            map.put(biomeName, new BiomeData(false, false, false));
         } else {
            ((BiomeData)map.get(biomeName)).enableSeasonalEffects = false;
         }
      }

   }

   private static void addTropicalBiomes(Map<String, BiomeData> map) {
      for(String biomeName : Lists.newArrayList(new String[]{"minecraft:desert", "minecraft:desert_hills", "minecraft:mutated_desert", "minecraft:jungle", "minecraft:jungle_hills", "minecraft:jungle_edge", "minecraft:mutated_jungle", "minecraft:mutated_jungle_edge", "minecraft:mesa", "minecraft:mesa_rock", "minecraft:mesa_clear_rock", "minecraft:mutated_mesa", "minecraft:mutated_mesa_rock", "minecraft:mutated_mesa_clear_rock", "minecraft:savanna", "minecraft:savanna_rock", "minecraft:mutated_savanna", "minecraft:mutated_savanna_rock", "minecraft:mushroom_island", "minecraft:mushroom_island_shore", "biomesoplenty:bamboo_forest", "biomesoplenty:bayou", "biomesoplenty:brushland", "biomesoplenty:eucalyptus_forest", "biomesoplenty:floodplains", "biomesoplenty:lush_desert", "biomesoplenty:mangrove", "biomesoplenty:outback", "biomesoplenty:overgrown_cliffs", "biomesoplenty:rainforest", "biomesoplenty:sacred_springs", "biomesoplenty:scrubland", "biomesoplenty:tropical_rainforest", "biomesoplenty:wasteland", "biomesoplenty:xeric_shrubland", "biomesoplenty:flower_island", "biomesoplenty:tropical_island", "biomesoplenty:volcanic_island", "biomesoplenty:oasis", "biomesoplenty:white_beach", "traverse:arid_highland", "traverse:badlands", "traverse:canyon", "traverse:desert_shrubland", "traverse:mini_jungle", "traverse:mountainous_desert", "traverse:red_desert", "conquest:bamboo_forest", "conquest:desert_mod", "conquest:jungle_mod", "conquest:mesa_extreme_mod", "conquest:red_desert", "climaticbiomesjbg:subtropical_forest", "climaticbiomesjbg:subtropical_forest_hills", "climaticbiomesjbg:tropical_forest", "climaticbiomesjbg:tropical_forest_hills", "climaticbiomesjbg:pine_swamp", "climaticbiomesjbg:dense_scrub", "climaticbiomesjbg:dense_scrub_hills", "climaticbiomesjbg:dense_scrub_hills", "climaticbiomesjbg:dry_scrub", "climaticbiomesjbg:dry_scrub_hills", "climaticbiomesjbg:hot_mountain", "climaticbiomesjbg:hot_mountain_trees"})) {
         if (!map.containsKey(biomeName)) {
            map.put(biomeName, new BiomeData(true, true, false));
         } else {
            ((BiomeData)map.get(biomeName)).useTropicalSeasons = true;
         }
      }

   }

   private static void addDisabledCropBiomes(Map<String, BiomeData> map) {
      for(String biomeName : Lists.newArrayList(new String[]{"biomesoplenty:crag", "biomesoplenty:wasteland", "biomesoplenty:volcanic_island"})) {
         if (!map.containsKey(biomeName)) {
            map.put(biomeName, new BiomeData(false, false, true));
         } else {
            ((BiomeData)map.get(biomeName)).disableCrops = true;
         }
      }

   }
}
