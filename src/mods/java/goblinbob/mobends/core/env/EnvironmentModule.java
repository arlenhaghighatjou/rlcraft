package goblinbob.mobends.core.env;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.module.IModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class EnvironmentModule {
   private static EnvironmentModule INSTANCE;
   private static final EnvironmentConfig DEFAULT_CONFIG = new EnvironmentConfig();
   private final File localConfigFile;
   private EnvironmentConfig config;

   public EnvironmentModule(File configDirectory) {
      this.config = DEFAULT_CONFIG;
      File modConfigDirectory = new File(configDirectory, "mobends");
      this.localConfigFile = new File(modConfigDirectory, "env.json");
      this.resolveConfig();
   }

   private void resolveConfig() {
      JsonObject localConfig = getLocalEnvironment(this.localConfigFile);
      if (localConfig != null) {
         Gson gson = new Gson();
         JsonObject propertyMap = gson.toJsonTree(DEFAULT_CONFIG).getAsJsonObject();

         for(Entry<String, JsonElement> entry : localConfig.entrySet()) {
            propertyMap.add((String)entry.getKey(), (JsonElement)entry.getValue());
         }

         this.config = (EnvironmentConfig)gson.fromJson(propertyMap, EnvironmentConfig.class);
      }

   }

   private static JsonObject getLocalEnvironment(File envFile) {
      try {
         if (envFile.isFile()) {
            return (JsonObject)(new Gson()).fromJson(new BufferedReader(new FileReader(envFile)), JsonObject.class);
         }
      } catch (IOException var2) {
         Core.LOG.warning("Couldn't load the local environment configuration.");
      }

      return null;
   }

   public static EnvironmentConfig getConfig() {
      return INSTANCE.config;
   }

   static {
      DEFAULT_CONFIG.apiUrl = "https://mobends.com";
   }

   public static class Factory implements IModule {
      public Factory() {
      }

      public void preInit(FMLPreInitializationEvent event) {
         EnvironmentModule.INSTANCE = new EnvironmentModule(event.getModConfigurationDirectory());
      }

      public void onRefresh() {
         EnvironmentModule.INSTANCE.resolveConfig();
      }
   }
}
