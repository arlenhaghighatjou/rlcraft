package leviathan143.loottweaker.common;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Config(
   modid = "loottweaker"
)
@EventBusSubscriber(
   modid = "loottweaker"
)
public class LTConfig {
   private static final Logger LOGGER = LogManager.getLogger("LootTweaker");
   @LangKey("loottweaker.config.warnings.category")
   public static LTConfig.Warnings warnings = new LTConfig.Warnings();
   @LangKey("loottweaker.config.workarounds.category")
   public static LTConfig.Workarounds workarounds = new LTConfig.Workarounds();
   @Comment({"Do not touch!"})
   @LangKey("loottweaker.config.lastCfgVersion")
   public static String lastCfgVersion = "0.3.1";

   public LTConfig() {
   }

   public static void onLoad() {
      if (!lastCfgVersion.equals("0.3.1")) {
         lastCfgVersion = "0.3.1";
         warnings.deprecation = true;
      }

      forceInitClasses();
      ConfigManager.sync("loottweaker", Type.INSTANCE);
   }

   @SubscribeEvent
   public static void syncConfig(OnConfigChangedEvent e) {
      if (e.getModID().equals("loottweaker")) {
         forceInitClasses();
         ConfigManager.sync("loottweaker", Type.INSTANCE);
      }

   }

   private static void forceInitClasses() {
      for(String className : workarounds.forceInitClasses) {
         try {
            Class.forName(className);
            LOGGER.info("{} initialisation ensured", className);
         } catch (ClassNotFoundException var5) {
            LOGGER.error("Could not ensure initialisation of {}", className, var5);
         }
      }

   }

   public static class Warnings {
      @RequiresMcRestart
      @Comment({"Warns about deprecated methods on world load. Resets if the version of LootTweaker is changed."})
      @LangKey("loottweaker.config.warnings.deprecation")
      public boolean deprecation = true;
      @RequiresMcRestart
      @Comment({"Warns when newTable() is passed a table id that implictly or explicitly uses the 'minecraft' namescape."})
      @LangKey("loottweaker.config.warnings.newTableMinecraftNamespace")
      public boolean newTableMinecraftNamespace = true;

      public Warnings() {
      }
   }

   public static class Workarounds {
      @Comment({"Classes to force initialise during pre-init. Use only if directed to by LootTweaker author."})
      @LangKey("loottweaker.config.workarounds.forceInitClasses")
      public String[] forceInitClasses = new String[0];

      public Workarounds() {
      }
   }
}
