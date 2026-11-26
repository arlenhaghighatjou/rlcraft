package net.theawesomegem.fishingmadebetter.common.configuration;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "fishingmadebetter"
)
public class ConfigurationManager {
   @Comment({"Server Config"})
   @Name("Server")
   public static final ConfigurationManager.ServerConfig server = new ConfigurationManager.ServerConfig();
   @Comment({"Client Config"})
   @Name("Client")
   public static final ConfigurationManager.ClientConfig client = new ConfigurationManager.ClientConfig();

   public ConfigurationManager() {
   }

   public static class ClientConfig {
      @Name("Reeling Hud")
      @Comment({"Enable/Disable the reeling hud. (Why would you disable it?)"})
      public boolean reelingHud = true;
      @Name("Reeling Meter Position")
      @Comment({"Move the Reeling Meter."})
      public ConfigurationManager.ReelingHubPos reelingHubPos = ConfigurationManager.ReelingHubPos.TOP_CENTER;
      @Name("Reeling Meter X-Offset")
      @Comment({"Changes the X-Offset of the reeling meter."})
      public int reelingMeterXOffset = 0;
      @Name("Reeling Meter Y-Offset")
      @Comment({"Changes the Y-Offset of the reeling meter."})
      public int reelingMeterYOffset = 4;

      public ClientConfig() {
      }
   }

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent e) {
         if (e.getModID().equals("fishingmadebetter")) {
            ConfigManager.sync("fishingmadebetter", Type.INSTANCE);
         }

      }
   }

   public static enum ReelingHubPos {
      TOP_LEFT,
      TOP_CENTER,
      TOP_RIGHT,
      BOTTOM_LEFT,
      BOTTOM_CENTER,
      BOTTOM_RIGHT;

      private ReelingHubPos() {
      }
   }

   public static class ServerConfig {
      @Name("Fish Bucket")
      @Comment({"Enable the fish bucket?"})
      public boolean enableFishBucket = true;
      @Name("Automatically Catch Fish When Ready")
      @Comment({"When the distance left is 0m and you can catch the fish, automatically force the player to right-click to reel in?"})
      public boolean autoReel = true;
      @Name("Magnetic Fishing")
      @Comment({"Do fish drop on your player when you successfully reel them in?"})
      public boolean magneticFishing = true;
      @Name("Lose Bait")
      @Comment({"Lose the bait whenever a fish bites your hook."})
      public boolean looseBait = true;
      @Name("Simple Bait")
      @Comment({"All bait listed in the fish configs work for all fish, baited rods catch fish faster and all bait can be used in the bait box."})
      public boolean simpleBait = true;
      @Name("Regenerate Empty Chunks")
      @Comment({"Attempt to regenerate fish if a chunk is empty?"})
      public boolean regenerateEmptyChunks = false;
      @Name("Disable Minigame")
      @Comment({"Skips the minigame when a fish bites the hook and just reels it in instead."})
      public boolean skipMinigame = false;
      @Name("Aquaculture Recipe Override")
      @Comment({"Replace/remove/override/add Aquaculture recipes to work better with this mod? (Does nothing if Aquaculture is not installed)"})
      public boolean aquacultureRecipeOverride = true;
      @Name("AdvancedFishing Recipe Override")
      @Comment({"Replace/remove/override/add AdvancedFishing recipes to work better with this mod? (Does nothing if AdvancedFishing is not installed)"})
      public boolean advancedFishingRecipeOverride = true;
      @Name("Charm Salvage Patch")
      @Comment({"Attempt to patch the dupe bug caused by Charm's Salvage enchant on rods with attachments? (Will cause attachments to be voided if you drop a rod at 0 durability remaining)"})
      public boolean charmSalvagePatch = true;
      @Name("LevelUpReloaded Patch")
      @Comment({"Attempt to patch LevelUpReloaded using the wrong event for fishing loot. (Built for version 1.1.23, likely won't work with newer rewrite versions currently)"})
      public boolean levelUpPatch = true;
      @Name("Fish Regeneration Time")
      @RangeInt(
         min = 0
      )
      @Comment({"After a chunk is emptied, how long until it attempts to regenerate the fish population. (Minutes)"})
      public int fishRegenerationTime = 180;
      @Name("Fish Reproduction Time")
      @RangeInt(
         min = 0
      )
      @Comment({"How often the fish population reproduction time should be ticked up. (Minutes) (Ticks required per fish set in Fish-Config)"})
      public int fishReproductionTime = 1;
      @Name("Bait Box Update Interval")
      @RangeInt(
         min = 0
      )
      @Comment({"How often a bait-box checks for hungry fish. (Minutes)"})
      public int baitBoxUpdateInterval = 5;
      @Name("Tracking Time")
      @RangeInt(
         min = 0
      )
      @Comment({"Time it takes to probe for fish with the tracker in seconds."})
      public int trackingTime = 5;
      @Name("Random Population Factor")
      @RangeInt(
         min = 0
      )
      @Comment({"When populating chunks, each population is increased or decreased by a random percent chosen between 0 and this."})
      public int randomPopulationFactor = 50;
      @Name("Base Treasure Chance")
      @Comment({"Base chance for a fishing rod to get extra treasure. (Out of 100)"})
      public int baseTreasureChance = 15;
      @Name("Base Fishing Time-To-Bobber")
      @Comment({"How many ticks should the time-to-bobber take before applying modifiers. (Ticks)"})
      public float baseTimeToBobber = 900.0F;

      public ServerConfig() {
      }
   }

   public static void register() {
      ConfigManager.register("fishingmadebetter", "", "general", null,
         ConfigEntry.category("Server",
            ConfigEntry.value("Fish Bucket", boolean.class, () -> ConfigurationManager.server.enableFishBucket, v -> ConfigurationManager.server.enableFishBucket = (Boolean)v).comment("Enable the fish bucket?").langKey("fishingmadebetter.general.server.enablefishbucket"),
            ConfigEntry.value("Automatically Catch Fish When Ready", boolean.class, () -> ConfigurationManager.server.autoReel, v -> ConfigurationManager.server.autoReel = (Boolean)v).comment("When the distance left is 0m and you can catch the fish, automatically force the player to right-click to reel in?").langKey("fishingmadebetter.general.server.autoreel"),
            ConfigEntry.value("Magnetic Fishing", boolean.class, () -> ConfigurationManager.server.magneticFishing, v -> ConfigurationManager.server.magneticFishing = (Boolean)v).comment("Do fish drop on your player when you successfully reel them in?").langKey("fishingmadebetter.general.server.magneticfishing"),
            ConfigEntry.value("Lose Bait", boolean.class, () -> ConfigurationManager.server.looseBait, v -> ConfigurationManager.server.looseBait = (Boolean)v).comment("Lose the bait whenever a fish bites your hook.").langKey("fishingmadebetter.general.server.loosebait"),
            ConfigEntry.value("Simple Bait", boolean.class, () -> ConfigurationManager.server.simpleBait, v -> ConfigurationManager.server.simpleBait = (Boolean)v).comment("All bait listed in the fish configs work for all fish, baited rods catch fish faster and all bait can be used in the bait box.").langKey("fishingmadebetter.general.server.simplebait"),
            ConfigEntry.value("Regenerate Empty Chunks", boolean.class, () -> ConfigurationManager.server.regenerateEmptyChunks, v -> ConfigurationManager.server.regenerateEmptyChunks = (Boolean)v).comment("Attempt to regenerate fish if a chunk is empty?").langKey("fishingmadebetter.general.server.regenerateemptychunks"),
            ConfigEntry.value("Disable Minigame", boolean.class, () -> ConfigurationManager.server.skipMinigame, v -> ConfigurationManager.server.skipMinigame = (Boolean)v).comment("Skips the minigame when a fish bites the hook and just reels it in instead.").langKey("fishingmadebetter.general.server.skipminigame"),
            ConfigEntry.value("Aquaculture Recipe Override", boolean.class, () -> ConfigurationManager.server.aquacultureRecipeOverride, v -> ConfigurationManager.server.aquacultureRecipeOverride = (Boolean)v).comment("Replace/remove/override/add Aquaculture recipes to work better with this mod? (Does nothing if Aquaculture is not installed)").langKey("fishingmadebetter.general.server.aquaculturerecipeoverride"),
            ConfigEntry.value("AdvancedFishing Recipe Override", boolean.class, () -> ConfigurationManager.server.advancedFishingRecipeOverride, v -> ConfigurationManager.server.advancedFishingRecipeOverride = (Boolean)v).comment("Replace/remove/override/add AdvancedFishing recipes to work better with this mod? (Does nothing if AdvancedFishing is not installed)").langKey("fishingmadebetter.general.server.advancedfishingrecipeoverride"),
            ConfigEntry.value("Charm Salvage Patch", boolean.class, () -> ConfigurationManager.server.charmSalvagePatch, v -> ConfigurationManager.server.charmSalvagePatch = (Boolean)v).comment("Attempt to patch the dupe bug caused by Charm's Salvage enchant on rods with attachments? (Will cause attachments to be voided if you drop a rod at 0 durability remaining)").langKey("fishingmadebetter.general.server.charmsalvagepatch"),
            ConfigEntry.value("LevelUpReloaded Patch", boolean.class, () -> ConfigurationManager.server.levelUpPatch, v -> ConfigurationManager.server.levelUpPatch = (Boolean)v).comment("Attempt to patch LevelUpReloaded using the wrong event for fishing loot. (Built for version 1.1.23, likely won't work with newer rewrite versions currently)").langKey("fishingmadebetter.general.server.leveluppatch"),
            ConfigEntry.value("Fish Regeneration Time", int.class, () -> ConfigurationManager.server.fishRegenerationTime, v -> ConfigurationManager.server.fishRegenerationTime = (Integer)v).comment("After a chunk is emptied, how long until it attempts to regenerate the fish population. (Minutes)").langKey("fishingmadebetter.general.server.fishregenerationtime").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("Fish Reproduction Time", int.class, () -> ConfigurationManager.server.fishReproductionTime, v -> ConfigurationManager.server.fishReproductionTime = (Integer)v).comment("How often the fish population reproduction time should be ticked up. (Minutes) (Ticks required per fish set in Fish-Config)").langKey("fishingmadebetter.general.server.fishreproductiontime").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("Bait Box Update Interval", int.class, () -> ConfigurationManager.server.baitBoxUpdateInterval, v -> ConfigurationManager.server.baitBoxUpdateInterval = (Integer)v).comment("How often a bait-box checks for hungry fish. (Minutes)").langKey("fishingmadebetter.general.server.baitboxupdateinterval").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("Tracking Time", int.class, () -> ConfigurationManager.server.trackingTime, v -> ConfigurationManager.server.trackingTime = (Integer)v).comment("Time it takes to probe for fish with the tracker in seconds.").langKey("fishingmadebetter.general.server.trackingtime").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("Random Population Factor", int.class, () -> ConfigurationManager.server.randomPopulationFactor, v -> ConfigurationManager.server.randomPopulationFactor = (Integer)v).comment("When populating chunks, each population is increased or decreased by a random percent chosen between 0 and this.").langKey("fishingmadebetter.general.server.randompopulationfactor").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("Base Treasure Chance", int.class, () -> ConfigurationManager.server.baseTreasureChance, v -> ConfigurationManager.server.baseTreasureChance = (Integer)v).comment("Base chance for a fishing rod to get extra treasure. (Out of 100)").langKey("fishingmadebetter.general.server.basetreasurechance"),
            ConfigEntry.value("Base Fishing Time-To-Bobber", float.class, () -> ConfigurationManager.server.baseTimeToBobber, v -> ConfigurationManager.server.baseTimeToBobber = (Float)v).comment("How many ticks should the time-to-bobber take before applying modifiers. (Ticks)").langKey("fishingmadebetter.general.server.basetimetobobber")).comment("Server Config").langKey("fishingmadebetter.general.server"),
         ConfigEntry.category("Client",
            ConfigEntry.value("Reeling Hud", boolean.class, () -> ConfigurationManager.client.reelingHud, v -> ConfigurationManager.client.reelingHud = (Boolean)v).comment("Enable/Disable the reeling hud. (Why would you disable it?)").langKey("fishingmadebetter.general.client.reelinghud"),
            ConfigEntry.enumValue("Reeling Meter Position", net.theawesomegem.fishingmadebetter.common.configuration.ConfigurationManager.ReelingHubPos.values(), () -> ConfigurationManager.client.reelingHubPos, v -> ConfigurationManager.client.reelingHubPos = v).comment("Move the Reeling Meter.").langKey("fishingmadebetter.general.client.reelinghubpos"),
            ConfigEntry.value("Reeling Meter X-Offset", int.class, () -> ConfigurationManager.client.reelingMeterXOffset, v -> ConfigurationManager.client.reelingMeterXOffset = (Integer)v).comment("Changes the X-Offset of the reeling meter.").langKey("fishingmadebetter.general.client.reelingmeterxoffset"),
            ConfigEntry.value("Reeling Meter Y-Offset", int.class, () -> ConfigurationManager.client.reelingMeterYOffset, v -> ConfigurationManager.client.reelingMeterYOffset = (Integer)v).comment("Changes the Y-Offset of the reeling meter.").langKey("fishingmadebetter.general.client.reelingmeteryoffset")).comment("Client Config").langKey("fishingmadebetter.general.client"));
   }
}
