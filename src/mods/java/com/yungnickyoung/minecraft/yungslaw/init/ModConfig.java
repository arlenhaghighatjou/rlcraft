package com.yungnickyoung.minecraft.yungslaw.init;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.fml.common.Loader;

public class ModConfig {
   public ModConfig() {
   }

   public static void preinit() {
      createCustomConfigDir();
      createReadMe();
   }

   private static void createCustomConfigDir() {
      File parentDir = new File(Loader.instance().getConfigDir().toString(), "YungsLaw");
      YungsLaw.customConfigDir = new File(parentDir, "1_12_2");

      try {
         String filePath = YungsLaw.customConfigDir.getCanonicalPath();
         if (YungsLaw.customConfigDir.mkdirs()) {
            YungsLaw.LOGGER.info("Creating directory for dimension-specific YUNG's Law configs at {}", filePath);
         }
      } catch (IOException var2) {
         YungsLaw.LOGGER.error("ERROR creating YUNG's Law config directory.");
      }

   }

   private static void createReadMe() {
      Path path = Paths.get(Loader.instance().getConfigDir().toString(), "YungsLaw", "README.txt");
      File readme = new File(path.toString());
      if (!readme.exists()) {
         String readmeText = "This directory is for adding YUNG's Law configurations specific to certain dimensions.\nFor more information, please see the wiki:\nhttps://github.com/yungnickyoung/YUNGs-Law/wiki";

         try {
            Files.write(path, readmeText.getBytes(), new OpenOption[0]);
         } catch (IOException var4) {
            YungsLaw.LOGGER.error("Unable to create README file!");
         }
      }

   }
}
