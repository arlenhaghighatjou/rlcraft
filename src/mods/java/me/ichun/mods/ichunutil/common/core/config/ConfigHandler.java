package me.ichun.mods.ichunutil.common.core.config;

import com.google.common.collect.Ordering;
import java.util.TreeSet;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
   public static TreeSet<ConfigBase> configs = new TreeSet(Ordering.natural());
   public static Configuration configKeybind;

   public ConfigHandler() {
   }

   public static <T extends ConfigBase> T registerConfig(T config) {
      configs.add(config);
      config.read();
      config.storeSession();
      return config;
   }
}
