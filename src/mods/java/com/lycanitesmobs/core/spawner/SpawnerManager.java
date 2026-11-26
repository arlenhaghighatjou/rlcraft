package com.lycanitesmobs.core.spawner;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SpawnerManager extends JSONLoader {
   public static SpawnerManager INSTANCE;
   public Map<String, Spawner> spawners = new HashMap();
   public List<SpawnCondition> globalSpawnConditions = new ArrayList();

   public SpawnerManager() {
   }

   public static SpawnerManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new SpawnerManager();
      }

      return INSTANCE;
   }

   public void loadAllFromJSON() {
      LycanitesMobs.logDebug("JSONSpawner", "Loading JSON Spawners!");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      Map<String, JsonObject> spawnerJSONs = new HashMap();
      String path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.modInfo.modid, "spawners");
      Map<String, JsonObject> defaultSpawnerJSONs = new HashMap();
      this.loadJsonObjects(gson, path, defaultSpawnerJSONs, "name", "spawner");
      path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.modInfo.modid, "mobevents");
      Map<String, JsonObject> defaultMobEventsJSONs = new HashMap();
      this.loadJsonObjects(gson, path, defaultMobEventsJSONs, "name", "spawner");
      String configPath = "config/" + "lycanitesmobs" + "/";
      VFile2 customSpawnersDir = new VFile2(configPath + "spawners");
      Map<String, JsonObject> customSpawnerJSONs = new HashMap();
      this.loadJsonObjects(gson, customSpawnersDir, customSpawnerJSONs, "name", "spawner");
      VFile2 customMobEventsDir = new VFile2(configPath + "mobevents");
      Map<String, JsonObject> customMobEventsJSONs = new HashMap();
      this.loadJsonObjects(gson, customMobEventsDir, customMobEventsJSONs, "name", "spawner");
      this.writeDefaultJSONObjects(gson, defaultSpawnerJSONs, customSpawnerJSONs, spawnerJSONs, true, "spawners");
      this.writeDefaultJSONObjects(gson, defaultMobEventsJSONs, customMobEventsJSONs, spawnerJSONs, true, "mobevents");
      LycanitesMobs.logDebug("", "Loading " + spawnerJSONs.size() + " JSON Spawners...");

      for(String spawnerJSONName : spawnerJSONs.keySet()) {
         try {
            JsonObject spawnerJSON = (JsonObject)spawnerJSONs.get(spawnerJSONName);
            LycanitesMobs.logDebug("JSONSpawner", "Loading Spawner JSON: " + spawnerJSON);
            Spawner spawner = new Spawner();
            spawner.loadFromJSON(spawnerJSON);
            this.addSpawner(spawner);
         } catch (JsonParseException var20) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Spawner: " + spawnerJSONName);
            var20.printStackTrace();
         } catch (Exception var21) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Spawner: " + spawnerJSONName);
            var21.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("", "Complete! " + this.spawners.size() + " JSON Spawners Loaded In Total.");
      this.globalSpawnConditions.clear();
      String defaultGlobalPath = Utilities.getAssetPath(this.getClass(), LycanitesMobs.modInfo.modid, "globalspawner.json");
      JsonObject defaultGlobalJson = this.loadJsonObject(gson, defaultGlobalPath);
      VFile2 customGlobalFile = new VFile2(configPath + "globalspawner.json");
      JsonObject customGlobalJson = null;
      if (customGlobalFile.exists()) {
         customGlobalJson = this.loadJsonObject(gson, customGlobalFile);
      }

      JsonObject globalJson = this.writeDefaultJSONObject(gson, "globalspawner", defaultGlobalJson, customGlobalJson);
      if (globalJson.has("conditions")) {
         JsonArray jsonArray = globalJson.get("conditions").getAsJsonArray();
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            JsonObject spawnConditionJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
            SpawnCondition spawnCondition = SpawnCondition.createFromJSON(spawnConditionJson);
            this.globalSpawnConditions.add(spawnCondition);
         }
      }

      if (this.globalSpawnConditions.size() > 0) {
         LycanitesMobs.logDebug("JSONSpawner", "Loaded " + this.globalSpawnConditions.size() + " Global Spawn Conditions.");
      }

   }

   public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
   }

   public void reload() {
      LycanitesMobs.logDebug("JSONSpawner", "Destroying JSON Spawners!");

      for(Spawner spawner : (Spawner[])this.spawners.values().toArray(new Spawner[this.spawners.size()])) {
         spawner.destroy();
      }

      this.loadAllFromJSON();
   }

   public void addSpawner(Spawner spawner) {
      if (this.spawners.containsKey(spawner.name)) {
         LycanitesMobs.logWarning("", "[Spawner Manager] Tried to add a Spawner with a name that is already in use: " + spawner.name);
      } else if (this.spawners.values().contains(spawner)) {
         LycanitesMobs.logWarning("", "[Spawner Manager] Tried to add a Spawner that is already added: " + spawner.name);
      } else {
         this.spawners.put(spawner.name, spawner);
      }
   }

   public void removeSpawner(Spawner spawner) {
      if (!this.spawners.containsKey(spawner.name)) {
         LycanitesMobs.logWarning("", "[Spawner Manager] Tried to remove a Spawner that hasn't been added: " + spawner.name);
      } else {
         this.spawners.remove(spawner.name);
      }
   }
}
