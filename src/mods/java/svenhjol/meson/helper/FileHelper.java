package svenhjol.meson.helper;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import org.apache.commons.io.FileUtils;
import svenhjol.meson.Meson;

public class FileHelper {
   public FileHelper() {
   }

   public static void backupConfigFile(File configFile) {
      Configuration testConfig = new Configuration(configFile);
      testConfig.load();
      String version = testConfig.getLoadedConfigVersion();
      if (version != null && !version.isEmpty()) {
         Meson.log("Config version " + version + ", no backup required");
      } else {
         String backupName = configFile.getAbsolutePath().replaceFirst(".cfg", ".backup.cfg");
         File backupFile = new File(backupName);

         try {
            FileUtils.copyFile(configFile, backupFile);
            Meson.log("Backed up config");
         } catch (Exception var6) {
            Meson.log("Could not backup config file - will be overwritten");
         }

         FileUtils.deleteQuietly(configFile);
      }

   }
}
