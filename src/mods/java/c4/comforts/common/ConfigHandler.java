package c4.comforts.common;

import c4.comforts.common.util.ComfortsUtil;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

   @EventBusSubscriber(
      modid = "comforts"
   )
   private static class ConfigEventHandler {
      private ConfigEventHandler() {
      }

      @SubscribeEvent
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
}
