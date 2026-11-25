package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.info.ItemDrop;
import com.lycanitesmobs.core.spawner.MobSpawn;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class DungeonSchematic {
   public String name = "";
   public boolean enabled = true;
   public int sectorCountMin = 30;
   public int sectorCountMax = 50;
   public double corridorToCorridorChance = 0.1D;
   public double roomToRoomChance = 0.1D;
   public List<SpawnCondition> conditions = new ArrayList();
   public List<Biome> biomes = new ArrayList();
   public List<String> themes = new ArrayList();
   public List<String> entrances = new ArrayList();
   public List<String> rooms = new ArrayList();
   public List<String> corridors = new ArrayList();
   public List<String> stairs = new ArrayList();
   public List<String> towers = new ArrayList();
   public List<String> bossRooms = new ArrayList();
   public List<String> finishes = new ArrayList();
   public List<MobSpawn> mobSpawns = new ArrayList();
   public Map<Integer, List<String>> lootTables = new HashMap();
   public List<ItemDrop> loot = new ArrayList();
   public DungeonPlacer placer = new DungeonPlacer.RandomPlacer();
   public boolean canGenerateAsTower = true;

   public DungeonSchematic() {
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString().toLowerCase();
      if (json.has("enabled")) {
         this.enabled = json.get("enabled").getAsBoolean();
      }

      if (json.has("sectorCountMin")) {
         this.sectorCountMin = json.get("sectorCountMin").getAsInt();
      }

      if (json.has("sectorCountMax")) {
         this.sectorCountMax = json.get("sectorCountMax").getAsInt();
      }

      if (json.has("corridorToCorridorChance")) {
         this.corridorToCorridorChance = json.get("corridorToCorridorChance").getAsDouble();
      }

      if (json.has("roomToRoomChance")) {
         this.roomToRoomChance = json.get("roomToRoomChance").getAsDouble();
      }

      if (json.has("lootTables")) {
         JsonArray jsonArray = json.get("lootTables").getAsJsonArray();

         JsonObject lootTableJson;
         int level;
         for(Iterator<JsonElement> jsonIterator = jsonArray.iterator(); jsonIterator.hasNext(); ((List)this.lootTables.get(Integer.valueOf(level))).add(lootTableJson.get("id").getAsString())) {
            lootTableJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            level = -1;
            if (lootTableJson.has("level")) {
               level = lootTableJson.get("level").getAsInt();
            }

            if (!this.lootTables.containsKey(Integer.valueOf(level))) {
               this.lootTables.put(Integer.valueOf(level), new ArrayList());
            }
         }
      }

      if (json.has("conditions")) {
         JsonArray jsonArray = json.get("conditions").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject conditionJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
            this.conditions.add(spawnCondition);
         }
      }

      if (json.has("biomes")) {
         this.biomes = JSONHelper.getBiomesFromTags(JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray()));
      }

      if (json.has("themes")) {
         for(JsonElement jsonElement : json.get("themes").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.themes.contains(jsonString)) {
               this.themes.add(jsonString);
            }
         }
      }

      if (json.has("entrances")) {
         for(JsonElement jsonElement : json.get("entrances").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.entrances.contains(jsonString)) {
               this.entrances.add(jsonString);
            }
         }
      }

      if (json.has("rooms")) {
         for(JsonElement jsonElement : json.get("rooms").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.rooms.contains(jsonString)) {
               this.rooms.add(jsonString);
            }
         }
      }

      if (json.has("corridors")) {
         for(JsonElement jsonElement : json.get("corridors").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.corridors.contains(jsonString)) {
               this.corridors.add(jsonString);
            }
         }
      }

      if (json.has("stairs")) {
         for(JsonElement jsonElement : json.get("stairs").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.stairs.contains(jsonString)) {
               this.stairs.add(jsonString);
            }
         }
      }

      if (json.has("towers")) {
         for(JsonElement jsonElement : json.get("towers").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.towers.contains(jsonString)) {
               this.towers.add(jsonString);
            }
         }
      }

      if (json.has("bossRooms")) {
         for(JsonElement jsonElement : json.get("bossRooms").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.bossRooms.contains(jsonString)) {
               this.bossRooms.add(jsonString);
            }
         }
      } else {
         this.bossRooms.addAll(this.rooms);
      }

      if (json.has("finishes")) {
         for(JsonElement jsonElement : json.get("finishes").getAsJsonArray()) {
            String jsonString = jsonElement.getAsString().toLowerCase();
            if (!this.finishes.contains(jsonString)) {
               this.finishes.add(jsonString);
            }
         }
      }

      if (json.has("mobSpawns")) {
         JsonArray jsonArray = json.get("mobSpawns").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject mobSpawnJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            MobSpawn mobSpawn = MobSpawn.createFromJSON(mobSpawnJson);
            if (mobSpawn != null) {
               this.mobSpawns.add(mobSpawn);
            }
         }
      }

      if (json.has("loot")) {
         for(JsonElement mobDropJson : json.getAsJsonArray("loot")) {
            ItemDrop itemDrop = ItemDrop.createFromJSON(mobDropJson.getAsJsonObject());
            if (itemDrop != null) {
               this.loot.add(itemDrop);
            }
         }
      }

      if (json.has("placer")) {
         this.placer = DungeonPlacer.createFromJson(json.get("placer").getAsJsonObject());
      }

      if (json.has("canGenerateAsTower")) {
         this.canGenerateAsTower = json.get("canGenerateAsTower").getAsBoolean();
      }

   }

   public boolean canBuild(World world, BlockPos pos) {
      if (!this.enabled) {
         return false;
      } else {
         for(SpawnCondition condition : this.conditions) {
            if (!condition.isMet(world, (EntityPlayer)null, pos)) {
               return false;
            }
         }

         if (!this.isValidBiome(world.getBiome(pos))) {
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean isValidBiome(Biome biome) {
      if (this.biomes.isEmpty()) {
         return true;
      } else {
         return this.biomes.contains(biome);
      }
   }

   public int getRandomSectorCount(EaglercraftRandom random) {
      return this.sectorCountMax <= this.sectorCountMin ? this.sectorCountMin : this.sectorCountMin + random.nextInt(this.sectorCountMax - this.sectorCountMin + 1);
   }

   public DungeonSector getRandomSector(String type, EaglercraftRandom random) {
      List<String> sectorList;
      if ("entrance".equalsIgnoreCase(type)) {
         sectorList = this.entrances;
      } else if ("corridor".equalsIgnoreCase(type)) {
         sectorList = this.corridors;
      } else if ("stairs".equalsIgnoreCase(type)) {
         sectorList = this.stairs;
      } else if ("tower".equalsIgnoreCase(type)) {
         sectorList = this.towers;
      } else if ("bossRoom".equalsIgnoreCase(type)) {
         sectorList = this.bossRooms;
      } else if ("finish".equalsIgnoreCase(type)) {
         sectorList = this.finishes;
      } else {
         sectorList = this.rooms;
      }

      List<DungeonSector> sectors = new ArrayList();
      int totalWeights = 0;

      for(String sectorName : sectorList) {
         DungeonSector sector = DungeonManager.getInstance().getSector(sectorName);
         if (sector != null && sector.weight > 0) {
            sectors.add(sector);
            totalWeights += sector.weight;
         }
      }

      if (sectors.isEmpty()) {
         LycanitesMobs.logWarning("Dungeon", "Unable to find any " + type + " sectors for the dungeon: " + this.name);
         return null;
      } else if (sectors.size() == 1) {
         return (DungeonSector)sectors.get(0);
      } else {
         int randomWeight = random.nextInt(totalWeights) + 1;
         int searchedWeight = 0;

         for(DungeonSector sector : sectors) {
            if (randomWeight <= sector.weight + searchedWeight) {
               return sector;
            }

            searchedWeight += sector.weight;
         }

         return (DungeonSector)sectors.get(sectors.size() - 1);
      }
   }

   public String getNextConnectingSector(String parentType, EaglercraftRandom random) {
      if ("room".equalsIgnoreCase(parentType)) {
         return random.nextDouble() <= this.roomToRoomChance ? "room" : "corridor";
      } else if ("corridor".equalsIgnoreCase(parentType)) {
         return random.nextDouble() <= this.corridorToCorridorChance ? "corridor" : "room";
      } else if ("entrance".equalsIgnoreCase(parentType)) {
         return random.nextBoolean() ? "corridor" : "room";
      } else {
         return "room";
      }
   }

   public DungeonTheme getRandomTheme(EaglercraftRandom random) {
      List<DungeonTheme> themes = new ArrayList();

      for(String themeName : this.themes) {
         DungeonTheme dungeonTheme = DungeonManager.getInstance().getTheme(themeName);
         if (dungeonTheme != null) {
            themes.add(dungeonTheme);
         }
      }

      if (themes.isEmpty()) {
         LycanitesMobs.logWarning("Dungeon", "No Dungeon Themes Found For " + this.name);
         return null;
      } else if (themes.size() == 1) {
         return (DungeonTheme)themes.get(0);
      } else {
         return (DungeonTheme)themes.get(random.nextInt(themes.size()));
      }
   }

   public MobSpawn getRandomMobSpawn(int level, boolean boss, EaglercraftRandom random) {
      int levelMin = level;
      int levelMax = level;
      if (level < 0) {
         levelMax = -level * 2;
         levelMin = levelMax - 1;
      }

      int totalWeights = 0;
      List<MobSpawn> mobSpawns = new ArrayList();

      for(MobSpawn mobSpawn : this.mobSpawns) {
         if (mobSpawn.dungeonBoss == boss && (mobSpawn.dungeonLevelMin < 0 || mobSpawn.dungeonLevelMin <= levelMax) && (mobSpawn.dungeonLevelMax < 0 || mobSpawn.dungeonLevelMax >= levelMin) && mobSpawn.getWeight() > 0) {
            mobSpawns.add(mobSpawn);
            totalWeights += mobSpawn.getWeight();
         }
      }

      if (totalWeights <= 0) {
         return null;
      } else {
         int randomWeight = 1;
         if (totalWeights > 1) {
            randomWeight = random.nextInt(totalWeights);
         }

         int searchWeight = 0;
         MobSpawn chosenMobSpawn = null;

         for(MobSpawn mobSpawn : mobSpawns) {
            chosenMobSpawn = mobSpawn;
            if (mobSpawn.getWeight() + searchWeight > randomWeight) {
               break;
            }

            searchWeight += mobSpawn.getWeight();
         }

         return chosenMobSpawn;
      }
   }

   public ResourceLocation getRandomLootTable(int level, EaglercraftRandom random) {
      if (level < 0) {
         level = -level * 2;
      }

      List<String> possibleLootTables = new ArrayList();
      if (this.lootTables.containsKey(Integer.valueOf(-1))) {
         possibleLootTables.addAll((Collection)this.lootTables.get(Integer.valueOf(-1)));
      }

      for(int i = 0; i <= level; ++i) {
         if (this.lootTables.containsKey(Integer.valueOf(i))) {
            possibleLootTables.addAll((Collection)this.lootTables.get(Integer.valueOf(i)));
         }
      }

      if (possibleLootTables.isEmpty()) {
         return null;
      } else if (possibleLootTables.size() == 1) {
         return new ResourceLocation((String)possibleLootTables.get(0));
      } else {
         return new ResourceLocation((String)possibleLootTables.get(random.nextInt(possibleLootTables.size())));
      }
   }

   public List<ItemStack> getRandomLoot(EaglercraftRandom random) {
      List<ItemStack> loot = new ArrayList();

      for(ItemDrop itemDrop : this.loot) {
         if (itemDrop.chance > 0.0F) {
            boolean addLoot = itemDrop.chance >= 1.0F;
            if (!addLoot) {
               addLoot = (double)itemDrop.chance <= random.nextDouble();
            }

            if (addLoot) {
               int quantity = itemDrop.getQuantity(random, 0, 1);
               if (quantity > 0) {
                  loot.add(new ItemStack(itemDrop.getItemStack().getItem(), itemDrop.getItemStack().getMetadata(), quantity));
               }
            }
         }
      }

      return loot;
   }
}
