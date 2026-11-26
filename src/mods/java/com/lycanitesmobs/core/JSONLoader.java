package com.lycanitesmobs.core;

import java.io.StringReader;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.core.info.ModInfo;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.FilenameUtils;

public abstract class JSONLoader {
   public JSONLoader() {
   }

   public void loadAllJson(ModInfo groupInfo, String name, String assetPath, String mapKey, boolean loadCustom) {
      LycanitesMobs.logDebug(name, "Loading JSON " + name + "...");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
      Map<String, JsonObject> jsons = new HashMap();
      String path = Utilities.getAssetPath(groupInfo.getClass(), groupInfo.modid, assetPath);
      Map<String, JsonObject> defaultJsons = new HashMap();
      this.loadJsonObjects(gson, path, defaultJsons, mapKey, (String)null);
      String configPath = "config/" + "lycanitesmobs" + "/";
      VFile2 customDir = new VFile2(configPath + assetPath);
      Map<String, JsonObject> customJsons = new HashMap();
      this.loadJsonObjects(gson, customDir, customJsons, mapKey, (String)null);
      this.writeDefaultJSONObjects(gson, defaultJsons, customJsons, jsons, loadCustom, assetPath);
      LycanitesMobs.logDebug(name, "Loading " + jsons.size() + " " + name + "...");

      for(String jsonName : jsons.keySet()) {
         try {
            JsonObject json = (JsonObject)jsons.get(jsonName);
            LycanitesMobs.logDebug(name, "Loading " + name + " JSON: " + json);
            this.parseJson(groupInfo, name, json);
         } catch (JsonParseException var16) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON " + name + ": " + jsonName);
            var16.printStackTrace();
         } catch (Exception var17) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON " + name + ": " + jsonName);
            var17.printStackTrace();
         }
      }

   }

   public abstract void parseJson(ModInfo var1, String var2, JsonObject var3);

   public void loadJsonObjects(Gson gson, String path, Map<String, JsonObject> jsonObjectMap, String mapKey, String jsonType) {
      if (path != null) {
         String prefix = path.endsWith("/") ? path : path + "/";
         List<String> files = EagRuntime.listResources(prefix);

         for(int i = 0, l = files.size(); i < l; ++i) {
            String filePath = files.get(i);
            if ("json".equals(FilenameUtils.getExtension(filePath))) {
               this.loadJsonObject(gson, EagRuntime.getResourceString("/" + filePath), filePath.substring(prefix.length()), jsonObjectMap, mapKey, jsonType);
            }
         }

      }
   }

   public void loadJsonObjects(Gson gson, VFile2 path, Map<String, JsonObject> jsonObjectMap, String mapKey, String jsonType) {
      if (path != null) {
         List<VFile2> files = path.listFiles(true);

         for(int i = 0, l = files.size(); i < l; ++i) {
            VFile2 file = files.get(i);
            if ("json".equals(FilenameUtils.getExtension(file.getName()))) {
               this.loadJsonObject(gson, file.getAllChars(), file.getPath().substring(path.getPath().length() + 1), jsonObjectMap, mapKey, jsonType);
            }
         }

      }
   }

   private void loadJsonObject(Gson gson, String content, String relativePath, Map<String, JsonObject> jsonObjectMap, String mapKey, String jsonType) {
      try {
         JsonObject json = (JsonObject)JsonUtils.fromJson(gson, new StringReader(content), JsonObject.class);
         boolean validJSON = true;
         if (jsonType != null) {
            if (!json.has("type")) {
               validJSON = false;
            } else {
               validJSON = jsonType.equalsIgnoreCase(json.get("type").getAsString());
            }
         }

         if (validJSON) {
            jsonObjectMap.put(json.get(mapKey).getAsString(), json);
         }
      } catch (JsonParseException var17) {
         LycanitesMobs.logWarning("", "Parsing error loading JSON " + relativePath + "\n" + var17.toString());
         var17.printStackTrace();
      } catch (Exception var18) {
         LycanitesMobs.logWarning("", "There was a problem loading JSON " + relativePath + "\n" + var18.toString());
         var18.printStackTrace();
      }

   }

   public void writeDefaultJSONObjects(Gson gson, Map<String, JsonObject> defaultJSONs, Map<String, JsonObject> customJSONs, Map<String, JsonObject> mixedJSONs, boolean custom, String assetPath) {
      for(String jsonName : defaultJSONs.keySet()) {
         try {
            JsonObject defaultJSON = (JsonObject)defaultJSONs.get(jsonName);
            boolean loadDefault = true;
            JsonObject customJSON = null;
            if (customJSONs.containsKey(jsonName)) {
               loadDefault = false;
               customJSON = (JsonObject)customJSONs.get(jsonName);
               if (customJSON.has("loadDefault")) {
                  loadDefault = customJSON.get("loadDefault").getAsBoolean();
               }
            }

            if (loadDefault) {
               this.saveJsonObject(gson, defaultJSON, jsonName, assetPath);
               mixedJSONs.put(jsonName, defaultJSON);
            } else if (customJSON != null) {
               mixedJSONs.put(jsonName, customJSON);
            }
         } catch (JsonParseException var12) {
            LycanitesMobs.logWarning("", "Parsing error loading JSON: " + jsonName);
            var12.printStackTrace();
         } catch (Exception var13) {
            LycanitesMobs.logWarning("", "There was a problem loading JSON: " + jsonName);
            var13.printStackTrace();
         }
      }

      if (custom) {
         for(String jsonName : customJSONs.keySet()) {
            if (!defaultJSONs.containsKey(jsonName)) {
               mixedJSONs.put(jsonName, customJSONs.get(jsonName));
            }
         }
      }

   }

   public JsonObject loadJsonObject(Gson gson, String path) {
      return path == null ? null : this.loadJsonObject(gson, EagRuntime.getResourceString("/" + path), path);
   }

   public JsonObject loadJsonObject(Gson gson, VFile2 path) {
      return path == null ? null : this.loadJsonObject(gson, path.getAllChars(), path.getPath());
   }

   private JsonObject loadJsonObject(Gson gson, String content, String relativePath) {
      try {
         return (JsonObject)JsonUtils.fromJson(gson, new StringReader(content), JsonObject.class);
      } catch (JsonParseException var12) {
         LycanitesMobs.logWarning("", "Parsing error loading JSON " + relativePath + "\n" + var12.toString());
         var12.printStackTrace();
         return null;
      } catch (Exception var13) {
         LycanitesMobs.logWarning("", "There was a problem loading JSON " + relativePath + "\n" + var13.toString());
         var13.printStackTrace();
         return null;
      }
   }

   public JsonObject writeDefaultJSONObject(Gson gson, String jsonName, JsonObject defaultJSON, JsonObject customJSON) {
      try {
         boolean loadDefault = true;
         if (customJSON != null) {
            loadDefault = false;
            if (customJSON.has("loadDefault")) {
               loadDefault = customJSON.get("loadDefault").getAsBoolean();
            }
         }

         if (loadDefault) {
            this.saveJsonObject(gson, defaultJSON, jsonName, "");
            return defaultJSON;
         }

         if (customJSON != null) {
            return customJSON;
         }
      } catch (JsonParseException var6) {
         LycanitesMobs.logWarning("", "Parsing error loading JSON: " + jsonName);
         var6.printStackTrace();
      } catch (Exception var7) {
         LycanitesMobs.logWarning("", "There was a problem loading JSON: " + jsonName);
         var7.printStackTrace();
      }

      return null;
   }

   public void saveJsonObject(Gson gson, JsonObject jsonObject, String name, String assetPath) {
      String configPath = "config/" + "lycanitesmobs" + "/";

      try {
         VFile2 jsonFile = new VFile2(configPath + (!"".equals(assetPath) ? assetPath + "/" : "") + name + ".json");
         jsonFile.setAllChars(gson.toJson(jsonObject));
      } catch (Exception var9) {
         LycanitesMobs.logWarning("", "Unable to save JSON into the config folder.");
         var9.printStackTrace();
      }

   }
}
