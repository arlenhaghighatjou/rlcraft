package me.jellysquid.mods.phosphor.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public class PhosphorConfig {
   private static final Gson gson = createGson();
   private static PhosphorConfig INSTANCE;
   @SerializedName("enable_illegal_thread_access_warnings")
   public boolean enableIllegalThreadAccessWarnings = true;
   @SerializedName("enable_phosphor")
   public boolean enablePhosphor = true;
   @SerializedName("show_patreon_message")
   public boolean showPatreonMessage = true;

   public PhosphorConfig() {
   }

   public static PhosphorConfig instance() {
      return INSTANCE;
   }

   public static PhosphorConfig loadConfig() {
      if (INSTANCE != null) {
         return INSTANCE;
      } else {
         File file = getConfigFile();
         PhosphorConfig config;
         if (!file.exists()) {
            config = new PhosphorConfig();
            config.saveConfig();
         } else {
            try {
               Reader reader = new FileReader(file);
               Throwable var3 = null;

               try {
                  config = (PhosphorConfig)gson.fromJson(reader, PhosphorConfig.class);
               } catch (Throwable var13) {
                  var3 = var13;
                  throw var13;
               } finally {
                  if (reader != null) {
                     if (var3 != null) {
                        try {
                           reader.close();
                        } catch (Throwable var12) {
                           var3.addSuppressed(var12);
                        }
                     } else {
                        reader.close();
                     }
                  }

               }
            } catch (IOException var15) {
               throw new RuntimeException("Failed to deserialize config from disk", var15);
            }
         }

         INSTANCE = config;
         return config;
      }
   }

   public void saveConfig() {
      File dir = getConfigDirectory();
      if (!dir.exists()) {
         if (!dir.mkdirs()) {
            throw new RuntimeException("Could not create configuration directory at '" + dir.getAbsolutePath() + "'");
         }
      } else if (!dir.isDirectory()) {
         throw new RuntimeException("Configuration directory at '" + dir.getAbsolutePath() + "' is not a directory");
      }

      try {
         Writer writer = new FileWriter(getConfigFile());
         Throwable var3 = null;

         try {
            gson.toJson(this, writer);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (writer != null) {
               if (var3 != null) {
                  try {
                     writer.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  writer.close();
               }
            }

         }

      } catch (IOException var15) {
         throw new RuntimeException("Failed to serialize config to disk", var15);
      }
   }

   private static File getConfigDirectory() {
      return new File("config");
   }

   private static File getConfigFile() {
      return new File(getConfigDirectory(), "phosphor.json");
   }

   private static Gson createGson() {
      return (new GsonBuilder()).setPrettyPrinting().create();
   }
}
