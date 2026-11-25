package com.realgecko.xpfromharvest;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "xpfromharvest"
)
public class ModConfig {
   @Comment({"Chance in % of XP orb spawning after harvest done"})
   @Name("Chance")
   @RangeInt(
      min = 1,
      max = 100
   )
   public static int chance = 100;
   @Comment({"Amount of XP given"})
   @Name("XP Amount")
   @RangeInt(
      min = 1
   )
   public static int xpAmount = 1;
   @Comment({"Enable simple harvesting and replanting with right click"})
   @Name("Simple Harvest")
   public static boolean simpleHarvest = false;
   @Comment({"List of crops to process with their ages"})
   @Name("Crops List")
   public static String[] crops = new String[]{"minecraft:potatoes[age=7]", "minecraft:carrots[age=7]", "minecraft:wheat[age=7]", "minecraft:beetroots[age=3]", "minecraft:nether_wart[age=3]"};
   @Comment({"Curiosity Mode: sneak + right click with on block to get info in chat"})
   @Name("Curiosity")
   public static boolean curiosity = false;

   public ModConfig() {
   }

   @EventBusSubscriber
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("xpfromharvest")) {
            ConfigManager.sync("xpfromharvest", Type.INSTANCE);
            XPFromHarvest.instance.ConfigUpdated();
         }

      }
   }
}
