package com.lycanitesmobs.core.dungeon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonStructure;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.info.ModInfo;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DungeonManager extends JSONLoader {
   public static DungeonManager INSTANCE;
   public Map<String, DungeonTheme> themes = new HashMap();
   public Map<String, DungeonStructure> structures = new HashMap();
   public Map<String, DungeonSector> sectors = new HashMap();
   public Map<String, DungeonSchematic> schematics = new HashMap();

   public DungeonManager() {
   }

   public static DungeonManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new DungeonManager();
      }

      return INSTANCE;
   }

   public void loadAllFromJSON() {
      Map<String, JsonObject> themeJSONs = this.loadDungeonsFromJSON("themes");
      LycanitesMobs.logDebug("", "Loading " + themeJSONs.size() + " JSON Dungeon Themes...");

      for(String jsonName : themeJSONs.keySet()) {
         try {
            JsonObject json = (JsonObject)themeJSONs.get(jsonName);
            LycanitesMobs.logDebug("Dungeon", "Loading Dungeon Themes JSON: " + json);
            DungeonTheme theme = new DungeonTheme();
            theme.loadFromJSON(json);
            this.addTheme(theme);
         } catch (JsonParseException var15) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Dungeon Theme: " + jsonName);
            var15.printStackTrace();
         } catch (Exception var16) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Dungeon Theme: " + jsonName);
            var16.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("", "Complete! " + this.themes.size() + " JSON Dungeon Themes Loaded In Total.");
      Map<String, JsonObject> structureJSONs = this.loadDungeonsFromJSON("structures");
      LycanitesMobs.logDebug("", "Loading " + structureJSONs.size() + " JSON Dungeon Structures...");

      for(String jsonName : structureJSONs.keySet()) {
         try {
            JsonObject json = (JsonObject)structureJSONs.get(jsonName);
            LycanitesMobs.logDebug("Dungeon", "Loading Dungeon Structures JSON: " + json);
            DungeonStructure structure = new DungeonStructure();
            structure.loadFromJSON(json);
            this.addStructure(structure);
         } catch (JsonParseException var13) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Dungeon Structure: " + jsonName);
            var13.printStackTrace();
         } catch (Exception var14) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Dungeon Structure: " + jsonName);
            var14.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("", "Complete! " + this.structures.size() + " JSON Dungeon Structures Loaded In Total.");
      Map<String, JsonObject> sectorJSONs = this.loadDungeonsFromJSON("sectors");
      LycanitesMobs.logDebug("", "Loading " + sectorJSONs.size() + " JSON Dungeon Sectors...");

      for(String jsonName : sectorJSONs.keySet()) {
         try {
            JsonObject json = (JsonObject)sectorJSONs.get(jsonName);
            LycanitesMobs.logDebug("Dungeon", "Loading Dungeon Sectors JSON: " + json);
            DungeonSector sector = new DungeonSector();
            sector.loadFromJSON(json);
            this.addSector(sector);
         } catch (JsonParseException var11) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Dungeon Sector: " + jsonName);
            var11.printStackTrace();
         } catch (Exception var12) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Dungeon Sector: " + jsonName);
            var12.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("", "Complete! " + this.sectors.size() + " JSON Dungeon Sectors Loaded In Total.");
      Map<String, JsonObject> schematicJSONs = this.loadDungeonsFromJSON("schematics");
      LycanitesMobs.logDebug("", "Loading " + schematicJSONs.size() + " JSON Dungeon Schematics...");

      for(String jsonName : schematicJSONs.keySet()) {
         try {
            JsonObject json = (JsonObject)schematicJSONs.get(jsonName);
            LycanitesMobs.logDebug("Dungeon", "Loading Dungeon Schematics JSON: " + json);
            DungeonSchematic schematic = new DungeonSchematic();
            schematic.loadFromJSON(json);
            this.addSchematic(schematic);
         } catch (JsonParseException var9) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON Dungeon Schematics: " + jsonName);
            var9.printStackTrace();
         } catch (Exception var10) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON Dungeon Schematics: " + jsonName);
            var10.printStackTrace();
         }
      }

      LycanitesMobs.logDebug("", "Complete! " + this.schematics.size() + " JSON Dungeon Schematics Loaded In Total.");
   }

   public void parseJson(ModInfo groupInfo, String name, JsonObject json) {
   }

   public Map<String, JsonObject> loadDungeonsFromJSON(String type) {
      LycanitesMobs.logDebug("Dungeon", "Loading JSON Dungeons!");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      Map<String, JsonObject> spawnerJSONs = new HashMap();
      Path path = Utilities.getAssetPath(this.getClass(), LycanitesMobs.modInfo.modid, "dungeons/" + type);
      Map<String, JsonObject> defaultJSONs = new HashMap();
      this.loadJsonObjects(gson, path, defaultJSONs, "name", (String)null);
      String configPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + "lycanitesmobs" + "/";
      File customDir = new File(configPath + "dungeons/" + type);
      customDir.mkdirs();
      path = customDir.toPath();
      Map<String, JsonObject> customJSONs = new HashMap();
      this.loadJsonObjects(gson, path, customJSONs, "name", (String)null);
      this.writeDefaultJSONObjects(gson, defaultJSONs, customJSONs, spawnerJSONs, true, "dungeons/" + type);
      return spawnerJSONs;
   }

   public void reload() {
      LycanitesMobs.logDebug("Dungeon", "Destroying JSON Dungeons!");
      this.themes.clear();
      this.structures.clear();
      this.sectors.clear();
      this.schematics.clear();
      this.loadAllFromJSON();
   }

   public void addTheme(DungeonTheme theme) {
      if (this.themes.containsKey(theme.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Theme with a name that is already in use: " + theme.name);
      } else if (this.themes.values().contains(theme)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Theme that is already added: " + theme.name);
      } else {
         this.themes.put(theme.name, theme);
      }
   }

   public void removeTheme(DungeonTheme theme) {
      if (!this.themes.containsKey(theme.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to remove a Dungeon Theme that hasn't been added: " + theme.name);
      } else {
         this.themes.remove(theme.name);
      }
   }

   public DungeonTheme getTheme(String name) {
      if (!this.themes.containsKey(name)) {
         LycanitesMobs.logWarning("Dungeon", "Unable to find a theme called " + name);
         return null;
      } else {
         return (DungeonTheme)this.themes.get(name);
      }
   }

   public void addStructure(DungeonStructure structure) {
      if (this.structures.containsKey(structure.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Structure with a name that is already in use: " + structure.name);
      } else if (this.structures.values().contains(structure)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Structure that is already added: " + structure.name);
      } else {
         this.structures.put(structure.name, structure);
      }
   }

   public void removeStructure(DungeonStructure structure) {
      if (!this.structures.containsKey(structure.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to remove a Dungeon Structure that hasn't been added: " + structure.name);
      } else {
         this.structures.remove(structure.name);
      }
   }

   public DungeonStructure getStructure(String name) {
      return !this.structures.containsKey(name) ? null : (DungeonStructure)this.structures.get(name);
   }

   public void addSector(DungeonSector sector) {
      if (this.sectors.containsKey(sector.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Sector with a name that is already in use: " + sector.name);
      } else if (this.sectors.values().contains(sector)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Sector that is already added: " + sector.name);
      } else {
         this.sectors.put(sector.name, sector);
      }
   }

   public void removeSector(DungeonSector sector) {
      if (!this.sectors.containsKey(sector.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to remove a Dungeon Sector that hasn't been added: " + sector.name);
      } else {
         this.sectors.remove(sector.name);
      }
   }

   public DungeonSector getSector(String name) {
      if (!this.sectors.containsKey(name)) {
         LycanitesMobs.logWarning("Dungeon", "Unable to find a Dungeon Sector by the name: " + name);
         return null;
      } else {
         return (DungeonSector)this.sectors.get(name);
      }
   }

   public void addSchematic(DungeonSchematic schematic) {
      if (this.schematics.containsKey(schematic.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Schematic with a name that is already in use: " + schematic.name);
      } else if (this.schematics.values().contains(schematic)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to add a Dungeon Schematic that is already added: " + schematic.name);
      } else {
         this.schematics.put(schematic.name, schematic);
      }
   }

   public void removeSchematic(DungeonSchematic schematic) {
      if (!this.schematics.containsKey(schematic.name)) {
         LycanitesMobs.logWarning("", "[Dungeon Manager] Tried to remove a Dungeon Schematic that hasn't been added: " + schematic.name);
      } else {
         this.schematics.remove(schematic.name);
      }
   }

   public DungeonSchematic getSchematic(String name) {
      return !this.schematics.containsKey(name) ? null : (DungeonSchematic)this.schematics.get(name);
   }
}
