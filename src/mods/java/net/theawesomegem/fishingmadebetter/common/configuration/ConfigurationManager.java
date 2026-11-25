package net.theawesomegem.fishingmadebetter.common.configuration;

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

   @EventBusSubscriber(
      modid = "fishingmadebetter"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
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
}
