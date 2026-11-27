package familiarfauna.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import familiarfauna.core.FamiliarFauna;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class JsonUtil {
   public static final Gson SERIALIZER = (new GsonBuilder()).setPrettyPrinting().create();

   public JsonUtil() {
   }

   public static List<String> getOrCreateStringList(VFile2 configDir, String configName, List<String> defaults) {
      VFile2 configFile = new VFile2(configDir, configName);
      if (!configFile.exists()) {
         writeFile(configFile, defaults);
      }

      try {
         JsonElement json = (new JsonParser()).parse(configFile.getAllChars());
         if (json == null || json.isJsonNull()) {
            return null;
         }

         JsonArray array = json.getAsJsonArray();
         List<String> list = new ArrayList<String>(array.size());

         for(int i = 0; i < array.size(); ++i) {
            JsonElement element = array.get(i);
            list.add(element.isJsonNull() ? null : element.getAsString());
         }

         return list;
      } catch (Exception var6) {
         FamiliarFauna.logger.error("Error parsing config from json: " + configFile.toString(), var6);
         return null;
      }
   }

   protected static boolean writeFile(VFile2 outputFile, List<String> obj) {
      try {
         JsonArray array = new JsonArray();

         for(int i = 0; i < obj.size(); ++i) {
            array.add(obj.get(i));
         }

         outputFile.setAllChars(SERIALIZER.toJson(array));
         return true;
      } catch (Exception var3) {
         FamiliarFauna.logger.error("Error writing config file " + outputFile.getPath() + ": " + var3.getMessage());
         return false;
      }
   }
}
