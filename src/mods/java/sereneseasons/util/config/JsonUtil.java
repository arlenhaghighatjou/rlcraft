package sereneseasons.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import sereneseasons.core.SereneSeasons;

public class JsonUtil {
   public static final Gson SERIALIZER = (new GsonBuilder()).setPrettyPrinting().create();

   public JsonUtil() {
   }

   public static JsonObject getOrCreateConfigFile(VFile2 configDir, String configName, JsonObject defaults) {
      VFile2 configFile = new VFile2(configDir, configName);
      if (!configFile.exists()) {
         writeFile(configFile, defaults);
      }

      try {
         return JsonParser.parseString(configFile.getAllChars()).getAsJsonObject();
      } catch (Exception var6) {
         SereneSeasons.logger.error("Error parsing config from json: " + configFile.toString(), var6);
         return null;
      }
   }

   protected static boolean writeFile(VFile2 outputFile, JsonObject obj) {
      outputFile.setAllChars(SERIALIZER.toJson(obj));
      return true;
   }
}
