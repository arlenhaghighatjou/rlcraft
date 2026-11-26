package tschipp.callablehorses.common.config;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "callablehorses"
)
public class CallableHorsesConfig {
   @LangKey("callablehorses")
   @Comment({"General Mod Settings"})
   public static Configs.Settings settings = new Configs.Settings();

   public CallableHorsesConfig() {
   }

   public static class EventHandler {
      private static Configuration configuration;

      public EventHandler() {
      }

      public static Configuration getConfiguration() {
         if (configuration == null) {
            configuration = ConfigManager.getConfiguration("callablehorses", null);
         }

         return configuration;
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("callablehorses")) {
            ConfigManager.load("callablehorses", Type.INSTANCE);
         }

      }
   }

   public static void register() {
      ConfigManager.register("callablehorses", "", "general", null,
         ConfigEntry.category("settings",
            ConfigEntry.value("enableDebug", boolean.class, () -> CallableHorsesConfig.settings.enableDebug, v -> CallableHorsesConfig.settings.enableDebug = (Boolean)v).comment("Enable debug mode (Developers only) WARNING! Will override all horse names!"),
            ConfigEntry.value("deathIsPermanent", boolean.class, () -> CallableHorsesConfig.settings.deathIsPermanent, v -> CallableHorsesConfig.settings.deathIsPermanent = (Boolean)v).comment("Personal Horses get deleted if they're killed"),
            ConfigEntry.value("callableInEveryDimension", boolean.class, () -> CallableHorsesConfig.settings.callableInEveryDimension, v -> CallableHorsesConfig.settings.callableInEveryDimension = (Boolean)v).comment("If the horse can be called in every dimension"),
            ConfigEntry.value("callableDimsWhitelist", int[].class, () -> CallableHorsesConfig.settings.callableDimsWhitelist, v -> CallableHorsesConfig.settings.callableDimsWhitelist = (int[])v).comment("Whitelist for dimensions where horses can be called. callableInEveryDimension needs to be false!"),
            ConfigEntry.value("maxCallingDistance", int.class, () -> CallableHorsesConfig.settings.maxCallingDistance, v -> CallableHorsesConfig.settings.maxCallingDistance = (Integer)v).comment("Maximum block distance from last horse where new horse can be called. Set to -1 to disable range.").range(-1, 30000000),
            ConfigEntry.value("enableStatsViewer", boolean.class, () -> CallableHorsesConfig.settings.enableStatsViewer, v -> CallableHorsesConfig.settings.enableStatsViewer = (Boolean)v).comment("Enable/disable the horse stat viewer GUI"),
            ConfigEntry.value("horseWalkRange", double.class, () -> CallableHorsesConfig.settings.horseWalkRange, v -> CallableHorsesConfig.settings.horseWalkRange = (Double)v).comment("Range in which the horse will not teleport, but walk to you. Set to 0 to force the horse to always teleport."),
            ConfigEntry.value("horseSpeed", double.class, () -> CallableHorsesConfig.settings.horseSpeed, v -> CallableHorsesConfig.settings.horseSpeed = (Double)v).comment("Speed with which the horse walks when you call it. Vanilla horse walk speed is 1.2"),
            ConfigEntry.value("continuousAntiDupeChecking", boolean.class, () -> CallableHorsesConfig.settings.continuousAntiDupeChecking, v -> CallableHorsesConfig.settings.continuousAntiDupeChecking = (Boolean)v).comment("Check against duplicate horses every game tick (20 times per second) instead of only on load. Only use this if you're experiencing problem with duplicate horses! THIS COULD CAUSE LAG!"),
            ConfigEntry.value("disableHorseDrops", boolean.class, () -> CallableHorsesConfig.settings.disableHorseDrops, v -> CallableHorsesConfig.settings.disableHorseDrops = (Boolean)v).comment("Prevents personal horses from dropping items (like leather). This won't prevent armor or saddles from dropping. If this is disabled in combination with deathIsPermanent, it can become a really easy way to farm/duplicate leather, so keep that in mind."),
            ConfigEntry.value("checkForSpace", boolean.class, () -> CallableHorsesConfig.settings.checkForSpace, v -> CallableHorsesConfig.settings.checkForSpace = (Boolean)v).comment("If you need a 3x3x3 space to call your horse")).comment("General Mod Settings").langKey("callablehorses"));
   }
}
