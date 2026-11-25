package com.charles445.rltweaker.asm;

import com.charles445.rltweaker.asm.util.ASMLogger;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class ASMConfig {
   private static boolean setup = false;
   @Nullable
   private static Configuration config = null;
   @Nonnull
   public static Map<String, Property> configMap = new ConcurrentHashMap();

   public ASMConfig() {
   }

   public static boolean getBoolean(String id, boolean _default) {
      if (hasConfig()) {
         Property prop = getProperty(id);
         if (prop == null) {
            ASMLogger.warn("WARNING: unknown config request: " + id);
            return _default;
         } else {
            return prop.getBoolean(_default);
         }
      } else {
         return _default;
      }
   }

   @Nullable
   public static Property getProperty(String s) {
      return (Property)configMap.get(s);
   }

   public static boolean hasConfig() {
      return configMap.size() > 0;
   }

   public static void setup() {
      if (!setup) {
         setup = true;
         ASMLogger.info("ConfigSetup is running.");
         Path path = getConfig("rltweaker");
         config = new Configuration(path.toFile(), true);
         processConfiguration();
      }
   }

   private static void processConfiguration() {
      if (config != null) {
         for(String s : config.getCategoryNames()) {
            loadCategory(config.getCategory(s), s);
         }
      } else {
         ASMLogger.info("Config does not exist, patcher will assume defaults");
      }

   }

   private static void loadCategory(ConfigCategory category, String header) {
      for(Entry<String, Property> entry : category.getValues().entrySet()) {
         String key = header + "." + (String)entry.getKey();
         if (configMap.containsKey(key)) {
            ASMLogger.warn("WARNING: Duplicate key for: " + key);
         }

         configMap.put(key, entry.getValue());
      }

   }

   private static Path getConfig(String modName) {
      return Paths.get("config" + File.separator + modName + ".cfg").toAbsolutePath();
   }
}
