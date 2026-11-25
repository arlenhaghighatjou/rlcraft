package prospector.traverse.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import prospector.traverse.core.TraverseConstants;
import prospector.traverse.shadow.shootingstar.version.Version;

public class TraverseWorldVersion {
   public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   public File versionConfig;
   public Version version;

   public TraverseWorldVersion(File worldDir) {
      File traverseDir = new File(worldDir, "traverse");
      if (!traverseDir.exists()) {
         traverseDir.mkdir();
      }

      this.versionConfig = new File(traverseDir, "instance_version.json");
      if ("1".equals("@major@")) {
         this.version = TraverseConstants.DEV_VERSION;
      } else {
         this.version = new Version(Integer.parseInt("1"), Integer.parseInt("6"), Integer.parseInt("0"));
      }

      this.reloadVersionFile();
   }

   public void reloadVersionFile() {
      if (!this.versionConfig.exists()) {
         this.writeVersionFile(new TraverseWorldVersion.VersionConfig(this.version));
      }

      if (this.versionConfig.exists()) {
         TraverseWorldVersion.VersionConfig config = null;

         try {
            Reader reader = new FileReader(this.versionConfig);
            Throwable var3 = null;

            try {
               config = (TraverseWorldVersion.VersionConfig)GSON.fromJson(reader, TraverseWorldVersion.VersionConfig.class);
               this.version = config.version;
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
         } catch (Exception var15) {
            var15.printStackTrace();
         }

         if (config == null) {
            config = new TraverseWorldVersion.VersionConfig(this.version);
            this.writeVersionFile(config);
         }
      }

   }

   public void writeVersionFile(TraverseWorldVersion.VersionConfig config) {
      try {
         Writer writer = new FileWriter(this.versionConfig);
         Throwable var3 = null;

         try {
            GSON.toJson(config, writer);
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
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      this.reloadVersionFile();
   }

   public static class VersionConfig {
      public Version version;

      public VersionConfig(Version version) {
         this.version = version;
      }

      public Version getVersion() {
         return this.version;
      }

      public void setVersion(Version version) {
         this.version = version;
      }
   }
}
