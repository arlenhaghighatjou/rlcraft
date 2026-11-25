package org.orecruncher.dshuds;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(
   modid = "dshuds"
)
@Config(
   modid = "dshuds",
   type = Type.INSTANCE,
   name = "dshuds",
   category = ""
)
public class ModOptions {
   private static final String PREFIX = "config.dshuds";
   @Name("Logging")
   @Comment({"Options to control logging"})
   @LangKey("config.dshuds.logging")
   public static ModOptions.Logging logging = new ModOptions.Logging();
   @Name("PotionHud")
   @LangKey("config.dshuds.potionhud")
   @Comment({"Options for the Potion HUD overlay"})
   public static ModOptions.PotionHUD potionHUD = new ModOptions.PotionHUD();
   @Name("LightLevelHud")
   @LangKey("config.dshuds.lightlevelhud")
   @Comment({"Options for configuring Light Level HUD"})
   public static ModOptions.LightLevelHUD lightLevelHUD = new ModOptions.LightLevelHUD();
   @Name("CompassHud")
   @LangKey("config.dshuds.compasshud")
   @Comment({"Options for configuring compass HUD"})
   public static ModOptions.CompassHUD compassHUD = new ModOptions.CompassHUD();
   @Name("ClockHud")
   @LangKey("config.dshuds.clockhud")
   @Comment({"Options for configuring clock HUD"})
   public static ModOptions.ClockHUD clockHUD = new ModOptions.ClockHUD();

   public ModOptions() {
   }

   @SubscribeEvent
   public static void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equals("dshuds")) {
         ConfigManager.sync("dshuds", Type.INSTANCE);
         ModBase.log().setDebug(logging.enableLogging);
      }

   }

   public static class ClockHUD {
      private static final String PREFIX = "config.dshuds.clockhud";
      @LangKey("config.dshuds.clockhud.enable")
      @Comment({"Enable/disable clock HUD when clock is held"})
      public boolean enable = true;

      public ClockHUD() {
      }
   }

   public static class CompassHUD {
      private static final String PREFIX = "config.dshuds.compasshud";
      @LangKey("config.dshuds.compasshud.enable")
      @Comment({"Enable/disable compass HUD when compass is held"})
      public boolean enable = true;
      @LangKey("config.dshuds.compasshud.style")
      @Comment({"Style of compass bar"})
      @RangeInt(
         min = 0,
         max = 6
      )
      public int style = 0;
      @LangKey("config.dshuds.compasshud.transparency")
      @Comment({"Compass transparency"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public float transparency = 0.4F;
      @LangKey("config.dshuds.compasshud.format")
      @Comment({"Format string for location coordinates"})
      public String coordFormat = "x: %1$d, z: %3$d";

      public CompassHUD() {
      }
   }

   public static class LightLevelHUD {
      private static final String PREFIX = "config.dshuds.lightlevelhud";
      @LangKey("config.dshuds.lightlevelhud.range")
      @Comment({"Range from player to analyze light levels"})
      @RangeInt(
         min = 16,
         max = 32
      )
      public int range = 24;
      @LangKey("config.dshuds.lightlevelhud.mobspawnthreshold")
      @Comment({"Light level at which mobs can spawn"})
      @RangeInt(
         min = 0,
         max = 15
      )
      public int spawnThreshold = 7;
      @LangKey("config.dshuds.lightlevelhud.displaymode")
      @Comment({"0: Block Light, 1: Block Light + Sky Light"})
      @RangeInt(
         min = 0,
         max = 1
      )
      public int displayMode = 0;
      @LangKey("config.dshuds.lightlevelhud.hidesafe")
      @Comment({"Hide light level information for blocks that are considered safe"})
      public boolean hideSafe = false;
      @LangKey("config.dshuds.lightlevelhud.indicatecaution")
      @Comment({"Indicate current light levels that will change at night which could result in mob spawns"})
      public boolean indicateCaution = true;
      @LangKey("config.dshuds.lightlevelhud.colors")
      @Comment({"Color set: 0 bright, 1 dark"})
      @RangeInt(
         min = 0,
         max = 1
      )
      public int colors = 1;

      public LightLevelHUD() {
      }
   }

   public static class Logging {
      private static final String PREFIX = "config.dshuds.logging";
      @LangKey("config.dshuds.logging.enableLogging")
      @Comment({"Enables debug logging output for diagnostics"})
      public boolean enableLogging = false;
      @LangKey("config.dshuds.logging.enableVersionCheck")
      @Comment({"Enables display of chat messages related to newer versions", "of the mod being available."})
      public boolean enableVersionCheck = true;
      @LangKey("config.dshuds.logging.chatnotices")
      @Comment({"Disable display of hud toggles in chat"})
      public boolean hideChatNotices = false;

      public Logging() {
      }
   }

   public static class PotionHUD {
      private static final String PREFIX = "config.dshuds.potionhud";
      @LangKey("config.dshuds.potionhud.nohud")
      @Comment({"Disables Vanilla and Dynamic Surroundings potion HUD"})
      public boolean none = false;
      @LangKey("config.dshuds.potionhud.enable")
      @Comment({"Enable display of potion icons in display"})
      public boolean enabled = true;
      @LangKey("config.dshuds.potionhud.transparency")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      @Comment({"Transparency factor for icons (higher more solid)"})
      public float transparency = 0.75F;
      @LangKey("config.dshuds.potionhud.leftoffset")
      @RangeInt(
         min = 0
      )
      @Comment({"Offset from left side of screen"})
      public int leftOffset = 5;
      @LangKey("config.dshuds.potionhud.topoffset")
      @RangeInt(
         min = 0
      )
      @Comment({"Offset from top of screen"})
      public int topOffset = 5;
      @LangKey("config.dshuds.potionhud.scale")
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      @Comment({"Size scale of icons (lower is smaller)"})
      public float scale = 0.75F;
      @LangKey("config.dshuds.potionhud.location")
      @RangeInt(
         min = 0,
         max = 1
      )
      @Comment({"Area of the display the Potion HUD is displayed (0 upper left, 1 upper right)"})
      public int anchor = 0;

      public PotionHUD() {
      }
   }
}
