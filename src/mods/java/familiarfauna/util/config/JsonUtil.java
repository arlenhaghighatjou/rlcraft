package familiarfauna.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import familiarfauna.core.FamiliarFauna;
import java.io.File;
import java.lang.reflect.Type;
import org.apache.commons.io.FileUtils;

public class JsonUtil {
   public static final Gson SERIALIZER = (new GsonBuilder()).setPrettyPrinting().create();

   public JsonUtil() {
   }

   public static <T> T getOrCreateConfigFile(File configDir, String configName, T defaults, Type type) {
      File configFile = new File(configDir, configName);
      if (!configFile.exists()) {
         writeFile(configFile, defaults);
      }

      try {
         return (T)SERIALIZER.fromJson(FileUtils.readFileToString(configFile), type);
      } catch (Exception var6) {
         FamiliarFauna.logger.error("Error parsing config from json: " + configFile.toString(), var6);
         return (T)null;
      }
   }

   protected static boolean writeFile(File outputFile, Object obj) {
      try {
         FileUtils.write(outputFile, SERIALIZER.toJson(obj));
         return true;
      } catch (Exception var3) {
         FamiliarFauna.logger.error("Error writing config file " + outputFile.getAbsolutePath() + ": " + var3.getMessage());
         return false;
      }
   }
}
