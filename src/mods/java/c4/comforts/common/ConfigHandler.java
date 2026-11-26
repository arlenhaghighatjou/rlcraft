package c4.comforts.common;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import c4.comforts.common.util.ComfortsUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "comforts"
)
public class ConfigHandler {
   @Name("Auto Use Sleeping Bags")
   @Comment({"Set to true to automatically use sleeping bags when placed"})
   @RequiresMcRestart
   public static boolean autoUse = true;
   @Name("Well-Rested")
   @Comment({"Set to true to prevent sleeping depending on how long you previously slept"})
   @RequiresMcRestart
   public static boolean wellRested = false;
   @Name("Sleepy Factor")
   @Comment({"If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)"})
   @RangeDouble(
      min = 1.0D,
      max = 20.0D
   )
   public static float sleepyRatio = 2.0F;
   @Name("Leisure Hammocks")
   @Comment({"Set to true to enable relaxing in hammocks without sleeping"})
   public static boolean restHammocks = false;
   @Name("Nighttime Hammocks")
   @Comment({"Set to true to enable sleeping in hammocks at night"})
   public static boolean nightHammocks = false;
   @Name("Sleeping Bag Debuffs")
   @Comment({"List of debuffs to apply to players after using the sleeping bag", "Format: [effect] [duration(secs)] [power]"})
   public static String[] sleepingBagDebuffs = new String[0];
   @Name("Sleeping Bag Break Chance")
   @Comment({"The chance that a sleeping bag will break upon usage"})
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   public static double bagBreakPerc = 0.0D;
   public static final ConfigHandler.ToughAsNails toughasnails = new ConfigHandler.ToughAsNails();

   public ConfigHandler() {
   }

   public static class ConfigEventHandler {
      private ConfigEventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, ConfigEventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent evt) {
         if (evt.getModID().equals("comforts")) {
            ConfigManager.sync("comforts", Type.INSTANCE);
            ComfortsUtil.parseDebuffs();
         }

      }
   }

   public static class ToughAsNails {
      @Name("Insulated Sleeping Bags")
      @Comment({"Set to true to have sleeping bags slightly warm your body if you're cold"})
      @RequiresMcRestart
      public boolean warmBody = false;

      public ToughAsNails() {
      }
   }

   public static void register() {
      ConfigManager.register("comforts", "", "general", null,
         ConfigEntry.value("Auto Use Sleeping Bags", boolean.class, () -> ConfigHandler.autoUse, v -> ConfigHandler.autoUse = (Boolean)v).comment("Set to true to automatically use sleeping bags when placed").langKey("comforts.general.autouse").requiresMcRestart(),
         ConfigEntry.value("Well-Rested", boolean.class, () -> ConfigHandler.wellRested, v -> ConfigHandler.wellRested = (Boolean)v).comment("Set to true to prevent sleeping depending on how long you previously slept").langKey("comforts.general.wellrested").requiresMcRestart(),
         ConfigEntry.value("Sleepy Factor", float.class, () -> ConfigHandler.sleepyRatio, v -> ConfigHandler.sleepyRatio = (Float)v).comment("If well rested is true, this value is used to determine how long you need before being able to sleep again (larger numbers = can sleep sooner)").langKey("comforts.general.sleepyratio").range(1.0, 20.0),
         ConfigEntry.value("Leisure Hammocks", boolean.class, () -> ConfigHandler.restHammocks, v -> ConfigHandler.restHammocks = (Boolean)v).comment("Set to true to enable relaxing in hammocks without sleeping").langKey("comforts.general.resthammocks"),
         ConfigEntry.value("Nighttime Hammocks", boolean.class, () -> ConfigHandler.nightHammocks, v -> ConfigHandler.nightHammocks = (Boolean)v).comment("Set to true to enable sleeping in hammocks at night").langKey("comforts.general.nighthammocks"),
         ConfigEntry.value("Sleeping Bag Debuffs", String[].class, () -> ConfigHandler.sleepingBagDebuffs, v -> ConfigHandler.sleepingBagDebuffs = (String[])v).comment("List of debuffs to apply to players after using the sleeping bag", "Format: [effect] [duration(secs)] [power]").langKey("comforts.general.sleepingbagdebuffs"),
         ConfigEntry.value("Sleeping Bag Break Chance", double.class, () -> ConfigHandler.bagBreakPerc, v -> ConfigHandler.bagBreakPerc = (Double)v).comment("The chance that a sleeping bag will break upon usage").langKey("comforts.general.bagbreakperc").range(0.0, 1.0),
         ConfigEntry.category("toughasnails",
            ConfigEntry.value("Insulated Sleeping Bags", boolean.class, () -> ConfigHandler.toughasnails.warmBody, v -> ConfigHandler.toughasnails.warmBody = (Boolean)v).comment("Set to true to have sleeping bags slightly warm your body if you're cold").langKey("comforts.general.toughasnails.warmbody").requiresMcRestart()));
   }
}
