package slimeknights.mantle.pulsar.internal;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraftforge.fml.common.Loader;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import slimeknights.mantle.pulsar.config.IConfiguration;
import slimeknights.mantle.pulsar.pulse.PulseMeta;

@ParametersAreNonnullByDefault
public class Configuration implements IConfiguration {
   private static final int CONFIG_LEVEL = 1;
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
   private final String confPath;
   private final Logger logger;
   private Map<String, Configuration.ConfigEntry> modules;

   public Configuration(String confName, Logger logger) {
      this.confPath = confName + ".json";
      this.logger = logger;
   }

   public void load() {
      this.getModulesFromJson();
   }

   public boolean isModuleEnabled(@Nonnull PulseMeta meta) {
      Configuration.ConfigEntry entry = (Configuration.ConfigEntry)this.modules.get(meta.getId());
      if (entry == null) {
         this.modules.put(meta.getId(), new Configuration.ConfigEntry(meta.isDefaultEnabled(), meta.getDescription()));
         return meta.isEnabled();
      } else {
         return entry.getEnabled().booleanValue();
      }
   }

   public void flush() {
      this.writeModulesToJson();
   }

   private VFile2 configFile() {
      return new VFile2(Loader.instance().getConfigDir(), this.confPath);
   }

   private void getModulesFromJson() {
      VFile2 f = this.configFile();
      if (!f.exists()) {
         this.logger.info("Couldn't find config file; will generate a new one later.");
         this.modules = new HashMap();
      } else {
         try {
            try {
               this.modules = this.parseV1Config(f);
            } catch (Exception var4) {
               this.logger.warn("Failed to parse " + f.getName() + " using the v1 parser; trying the v0 parser.");
               Map<String, Configuration.ConfigEntry> conf = this.parseV0Config(f);
               this.logger.info("Found valid v0 configuration. Upgrading it.");
               this.modules = conf;
               this.writeModulesToJson();
               this.logger.info("Upgrade complete! Config is now in v1 format.");
            }
         } catch (Exception var5) {
            this.logger.warn("Invalid config file. Discarding.");
            var5.printStackTrace();
            this.modules = new HashMap();
         }

      }
   }

   private Map<String, Configuration.ConfigEntry> parseV0Config(VFile2 f) throws Exception {
      JsonElement json = (new JsonParser()).parse(f.getAllChars());
      if (json == null || json.isJsonNull()) {
         throw new NullPointerException("Gson returned null.");
      } else {
         Map<String, Configuration.ConfigEntry> out = new HashMap();

         for(Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
            JsonElement v = (JsonElement)e.getValue();
            out.put(e.getKey(), new Configuration.ConfigEntry(v.isJsonNull() ? null : Boolean.valueOf(v.getAsBoolean())));
         }

         return out;
      }
   }

   private Map<String, Configuration.ConfigEntry> parseV1Config(VFile2 f) throws Exception {
      JsonObject root = (new JsonParser()).parse(f.getAllChars()).getAsJsonObject();
      int version = root.has("CONFIG_VERSION") ? root.get("CONFIG_VERSION").getAsInt() : 0;
      if (version > 1) {
         throw new RuntimeException("Pulsar config is from a newer version! Remove it! " + f.getPath());
      } else if (!root.has("modules") || root.get("modules").isJsonNull()) {
         throw new IllegalArgumentException("Not a valid GsonConfig. Try v0 parsing.");
      } else {
         Map<String, Configuration.ConfigEntry> out = new HashMap();

         for(Entry<String, JsonElement> e : root.getAsJsonObject("modules").entrySet()) {
            JsonObject entry = ((JsonElement)e.getValue()).getAsJsonObject();
            Boolean enabled = entry.has("enabled") && !entry.get("enabled").isJsonNull() ? Boolean.valueOf(entry.get("enabled").getAsBoolean()) : null;
            String description = entry.has("description") && !entry.get("description").isJsonNull() ? entry.get("description").getAsString() : null;
            out.put(e.getKey(), new Configuration.ConfigEntry(enabled, description));
         }

         return out;
      }
   }

   private void writeModulesToJson() {
      try {
         JsonObject modules = new JsonObject();

         for(Entry<String, Configuration.ConfigEntry> e : this.modules.entrySet()) {
            JsonObject entry = new JsonObject();
            Configuration.ConfigEntry value = (Configuration.ConfigEntry)e.getValue();
            if (value.getEnabled() != null) {
               entry.addProperty("enabled", value.getEnabled());
            }

            if (value.getDescription() != null) {
               entry.addProperty("description", value.getDescription());
            }

            modules.add((String)e.getKey(), entry);
         }

         JsonObject root = new JsonObject();
         root.addProperty("CONFIG_VERSION", Integer.valueOf(1));
         root.add("modules", modules);
         this.configFile().setAllChars(gson.toJson(root));
      } catch (Exception var3) {
         this.logger.warn("Could not write config? " + this.confPath);
      }

   }

   private static class ConfigEntry {
      private Boolean enabled;
      private String description;

      public ConfigEntry(Boolean enabled) {
         this.description = null;
         this.enabled = enabled;
      }

      public ConfigEntry(Boolean enabled, String description) {
         this(enabled);
         this.description = description;
      }

      public Boolean getEnabled() {
         return this.enabled;
      }

      public void setEnabled(Boolean enabled) {
         this.enabled = enabled;
      }

      public String getDescription() {
         return this.description;
      }
   }

   private static class FileNotReadWritableException extends RuntimeException {
      public FileNotReadWritableException(String message) {
         super(message);
      }
   }
}
