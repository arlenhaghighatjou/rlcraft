package com.realgecko.xpfromharvest;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

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

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("xpfromharvest")) {
            ConfigManager.sync("xpfromharvest", Type.INSTANCE);
            XPFromHarvest.instance.ConfigUpdated();
         }

      }
   }

   public static void register() {
      ConfigManager.register("xpfromharvest", "", "general", null,
         ConfigEntry.value("Chance", int.class, () -> ModConfig.chance, v -> ModConfig.chance = (Integer)v).comment("Chance in % of XP orb spawning after harvest done").langKey("xpfromharvest.general.chance").range(1, 100),
         ConfigEntry.value("XP Amount", int.class, () -> ModConfig.xpAmount, v -> ModConfig.xpAmount = (Integer)v).comment("Amount of XP given").langKey("xpfromharvest.general.xpamount").range(1, Integer.MAX_VALUE),
         ConfigEntry.value("Simple Harvest", boolean.class, () -> ModConfig.simpleHarvest, v -> ModConfig.simpleHarvest = (Boolean)v).comment("Enable simple harvesting and replanting with right click").langKey("xpfromharvest.general.simpleharvest"),
         ConfigEntry.value("Crops List", String[].class, () -> ModConfig.crops, v -> ModConfig.crops = (String[])v).comment("List of crops to process with their ages").langKey("xpfromharvest.general.crops"),
         ConfigEntry.value("Curiosity", boolean.class, () -> ModConfig.curiosity, v -> ModConfig.curiosity = (Boolean)v).comment("Curiosity Mode: sneak + right click with on block to get info in chat").langKey("xpfromharvest.general.curiosity"));
   }
}
