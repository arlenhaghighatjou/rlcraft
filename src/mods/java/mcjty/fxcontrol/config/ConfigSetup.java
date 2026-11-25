package mcjty.fxcontrol.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ConfigSetup {
   public static File modConfigDir;
   private static Configuration mainConfig;

   public ConfigSetup() {
   }

   public static void init(FMLPreInitializationEvent e) {
      modConfigDir = e.getModConfigurationDirectory();
      mainConfig = new Configuration(new File(modConfigDir.getPath() + File.separator + "fxcontrol", "main.cfg"));
      readMainConfig();
   }

   private static void readMainConfig() {
      Configuration cfg = mainConfig;

      try {
         cfg.load();
         cfg.addCustomCategoryComment("general", "General settings");
         GeneralConfiguration.init(cfg);
      } catch (Exception var5) {
         FMLLog.log(Level.ERROR, var5, "Problem loading config file!", new Object[0]);
      } finally {
         if (mainConfig.hasChanged()) {
            mainConfig.save();
         }

      }

   }

   public static void postInit() {
      if (mainConfig.hasChanged()) {
         mainConfig.save();
      }

   }
}
