package com.endertech.minecraft.forge.configs;

import com.endertech.minecraft.forge.ForgeMain;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class UnitConfig extends ForgeConfig {
   public UnitConfig(Path baseDir, String folderName, String configName) {
      this(buildPath(baseDir, folderName, configName).toFile());
   }

   public UnitConfig(Path baseDir, String configName) {
      this(buildPath(baseDir, configName).toFile());
   }

   public UnitConfig(File file) {
      super(file);
   }

   public static Path buildPath(Path baseDir, String folderName, String configName) {
      baseDir = Paths.get(String.valueOf(baseDir), folderName);
      return buildPath(baseDir, configName);
   }

   public static Path buildPath(Path baseDir, String configName) {
      return Paths.get(String.valueOf(baseDir), configName + ".cfg");
   }

   public static boolean alreadyExists(Path baseDir, String configName) {
      Path path = buildPath(baseDir, configName);
      return path.toFile().isFile();
   }

   public static List<Path> listCustomConfigs(Path searchDir, @Nullable Class<? extends IForgeEnum> exclude) {
      List<String> excludeList = new ArrayList();
      if (exclude != null) {
         for(IForgeEnum e : (IForgeEnum[])exclude.getEnumConstants()) {
            excludeList.add(e.getName() + ".cfg");
         }
      }

      List<Path> configList = new ArrayList();

      try {
         DirectoryStream<Path> dirStream = Files.newDirectoryStream(searchDir, "*.cfg");
         Throwable var21 = null;

         try {
            for(Path path : dirStream) {
               if (!excludeList.contains(String.valueOf(path.getFileName()))) {
                  configList.add(path);
               }
            }
         } catch (Throwable var16) {
            var21 = var16;
            throw var16;
         } finally {
            if (dirStream != null) {
               if (var21 != null) {
                  try {
                     dirStream.close();
                  } catch (Throwable var15) {
                     var21.addSuppressed(var15);
                  }
               } else {
                  dirStream.close();
               }
            }

         }
      } catch (Exception var18) {
         ForgeMain.instance.getLogger().error(var18.toString());
      }

      return configList;
   }
}
