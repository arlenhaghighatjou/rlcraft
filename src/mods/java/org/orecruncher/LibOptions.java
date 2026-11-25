package org.orecruncher;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(
   modid = "orelib"
)
@Config(
   modid = "orelib",
   type = Type.INSTANCE,
   name = "orelib",
   category = ""
)
public class LibOptions {
   private static final String PREFIX = "config.orelib";
   @Name("Logging")
   @Comment({"Options to control logging"})
   @LangKey("config.orelib.logging")
   public static LibOptions.Logging logging = new LibOptions.Logging();

   public LibOptions() {
   }

   @SubscribeEvent
   public static void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("orelib")) {
         ConfigManager.sync("orelib", Type.INSTANCE);
         LibBase.log().setDebug(logging.enableLogging);
      }

   }

   public static class Logging {
      private static final String PREFIX = "config.orelib.logging";
      @LangKey("config.orelib.logging.enableLogging")
      @Comment({"Enables debug logging output for diagnostics"})
      public boolean enableLogging = false;
      @LangKey("config.orelib.logging.enableVersionCheck")
      @Comment({"Enables display of chat messages related to newer versions", "of the mod being available."})
      public boolean enableVersionCheck = true;

      public Logging() {
      }
   }
}
