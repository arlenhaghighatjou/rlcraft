package baubles.common;

import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config {
   public static Configuration config;
   public static boolean renderBaubles = true;

   public Config() {
   }

   public static void initialize(File file) {
      config = new Configuration(file);
      config.load();
      load();
      MinecraftForge.EVENT_BUS.register(Config.ConfigChangeListener.class);
   }

   public static void load() {
      String desc = "Set this to false to disable rendering of baubles in the player.";
      renderBaubles = config.getBoolean("baubleRender.enabled", "client", renderBaubles, desc);
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static void save() {
      config.save();
   }

   public static class ConfigChangeListener {
      public ConfigChangeListener() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent eventArgs) {
         if (eventArgs.getModID().equals("baubles")) {
            Config.load();
         }

      }
   }
}
