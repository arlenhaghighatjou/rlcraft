package com.fantasticsource.setbonus.config;

import com.fantasticsource.mctools.MCTools;
import java.io.File;

public class ConfigHandler {
   public static final String CONFIG_NAME = "setbonus/1.12.2.009+";
   public static final String FULL_CONFIG_NAME = (new File(MCTools.getConfigDir() + "setbonus/1.12.2.009+")).getAbsolutePath();

   public ConfigHandler() {
   }

   public static void init() {
      File file = new File(MCTools.getConfigDir() + "setbonus.cfg");
      if (file.exists()) {
         file.renameTo(new File(MCTools.getConfigDir() + "setbonus/setbonus (old).cfg"));
      }

   }
}
