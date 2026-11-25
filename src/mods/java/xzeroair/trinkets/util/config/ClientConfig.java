package xzeroair.trinkets.util.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import xzeroair.trinkets.util.config.gui.ClientConfigPropertiesGui;
import xzeroair.trinkets.util.config.gui.ClientConfigTrinketsContainer;

public class ClientConfig {
   private static final String cfgPrefix = "xat.config.client";
   @Name("Debug Settings")
   @Comment({""})
   @LangKey("xat.config.client.debug.settings")
   public ClientConfig.DebugConfig debug = new ClientConfig.DebugConfig();
   @Name("Trinkets Container Settings")
   @Comment({""})
   @LangKey("xat.config.client.gui.settings")
   public ClientConfigTrinketsContainer GUI = new ClientConfigTrinketsContainer();
   @Name("Mana Bar Settings")
   @Comment({""})
   @LangKey("xat.config.client.magic.hud.settings")
   public ClientConfig.ClientConfigMPBar MPBar = new ClientConfig.ClientConfigMPBar();
   @Name("Race Properties Gui")
   @Comment({""})
   @LangKey("xat.config.client.race.properties.gui.settings")
   public ClientConfigPropertiesGui raceProperties = new ClientConfigPropertiesGui();
   @Comment({"Adjusts players eye height when transformed"})
   @Name("POV Adjustments")
   @LangKey("xat.config.client.camera.adjustments")
   public boolean cameraHeight = true;
   @Comment({"Main Render Control for all Trinkets"})
   @Name("Render Trinkets")
   @LangKey("xat.config.client.render.trinkets.all")
   public boolean rendering = true;
   @Name("Item Settings")
   @Comment({""})
   @LangKey("xat.config.items")
   public ClientConfig.ClientConfigItems items = new ClientConfig.ClientConfigItems();

   public ClientConfig() {
   }

   public class ClientConfigItems {
      @Name("Dragon's Eye Settings")
      @Comment({""})
      @LangKey("xat.config.dragons_eye")
      public ClientConfig.ClientConfigItems.ClientConfigDragonsEye DRAGON_EYE = new ClientConfig.ClientConfigItems.ClientConfigDragonsEye();
      @Name("Ring of the Fairies Settings")
      @Comment({""})
      @LangKey("xat.config.fairy_ring")
      public ClientConfig.ClientConfigItems.ClientConfigFairyRing FAIRY_RING = new ClientConfig.ClientConfigItems.ClientConfigFairyRing();
      @Name("Shield of Honor Settings")
      @Comment({""})
      @LangKey("xat.config.damage_shield")
      public ClientConfig.ClientConfigItems.ClientConfigDamageShield DAMAGE_SHIELD = new ClientConfig.ClientConfigItems.ClientConfigDamageShield();
      @Name("Ender Queen's Crown Settings")
      @Comment({""})
      @LangKey("xat.config.ender_tiara")
      public ClientConfig.ClientConfigItems.ClientConfigEnderCrown ENDER_CROWN = new ClientConfig.ClientConfigItems.ClientConfigEnderCrown();
      @Name("Stone of the Sea Settings")
      @Comment({""})
      @LangKey("xat.config.sea_stone")
      public ClientConfig.ClientConfigItems.ClientConfigSeaStone SEA_STONE = new ClientConfig.ClientConfigItems.ClientConfigSeaStone();
      @Name("Faelis Claw Settings")
      @Comment({""})
      @LangKey("xat.config.faelis_claw")
      public ClientConfig.ClientConfigItems.ClientConfigFaelisClaw FAELIS_CLAW = new ClientConfig.ClientConfigItems.ClientConfigFaelisClaw();

      public ClientConfigItems() {
      }

      public class ClientConfigDamageShield {
         @Name("Render Trinket")
         @Comment({""})
         @LangKey("xat.config.client.render.trinket")
         public boolean doRender = true;
         @Name("Block Damage Volume")
         @Comment({""})
         @LangKey("xat.config.client.damage_shield.volume")
         @RangeDouble(
            min = 0.0D
         )
         public double effectVolume = 0.2D;

         public ClientConfigDamageShield() {
         }
      }

      public class ClientConfigDragonsEye {
         @RangeInt(
            min = 20,
            max = 360
         )
         @Name("Refresh Rate")
         @Comment({"Determines of Often the Treasure Finder Pings for Treasure"})
         @LangKey("xat.config.client.dragons_eye.orefinder.refresh.rate")
         public int Render_Cooldown = 79;
         @Name("Dragon's Growl")
         @Comment({"When The Treasure Finder is Enabled, Should There be a Growl the Treasure Finder Pings Treasure"})
         @LangKey("xat.config.client.dragons_eye.growl.enabled")
         public boolean Dragon_Growl = true;
         @Name("Growl Trigger")
         @Comment({"Determines When the Dragon's Growl Happens\\nOptions:\\nSNEAK\\nSTAND\\nBOTH"})
         @LangKey("xat.config.client.dragons_eye.growl.activation")
         public String Dragon_Growl_Sneak = "SNEAK";
         @Name("Growl Volume")
         @Comment({"Dragon's Growl Volume Control"})
         @RangeInt(
            min = 0,
            max = 300
         )
         @LangKey("xat.config.client.dragons_eye.growl.volume")
         public int Dragon_Growl_Volume = 100;

         public ClientConfigDragonsEye() {
         }
      }

      public class ClientConfigEnderCrown {
         @Name("Render Trinket")
         @Comment({""})
         @LangKey("xat.config.client.render.trinket")
         public boolean doRender = true;

         public ClientConfigEnderCrown() {
         }
      }

      public class ClientConfigFaelisClaw {
         @Name("Render Trinket")
         @Comment({""})
         @LangKey("xat.config.client.render.trinket")
         public boolean doRender = true;

         public ClientConfigFaelisClaw() {
         }
      }

      public class ClientConfigFairyRing {
         @Name("Render Trinket effects on the player")
         @Comment({""})
         @LangKey("xat.config.client.render.trinket")
         public boolean doRender = true;

         public ClientConfigFairyRing() {
         }
      }

      public class ClientConfigSeaStone {
         @Name("Render Trinket")
         @Comment({""})
         @LangKey("xat.config.client.render.trinket")
         public boolean doRender = true;

         public ClientConfigSeaStone() {
         }
      }
   }

   public class ClientConfigMPBar {
      private final String PREFIX = "xat.config.client.magic.hud";
      @Name("01. Show Mana")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.shown")
      public boolean shown = true;
      @Name("02. Show when Full")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.shown.always")
      public boolean always_shown = false;
      @Name("03. Mana Bar Horizontal")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.horizontal")
      public boolean mana_horizontal = true;
      @Name("04. Hide Text")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.text.shown")
      public boolean hide_text = false;
      @Name("05. Location X")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.location.x")
      public double translatedX = 0.0D;
      @Name("06. Location Y")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.location.y")
      public double translatedY = 0.0D;
      @Name("09. Width")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.width")
      public int width = 106;
      @Name("10. Height")
      @Comment({""})
      @LangKey("xat.config.client.magic.hud.height")
      public int height = 16;

      public ClientConfigMPBar() {
      }
   }

   public class DebugConfig {
      @Name("Show Armor Materials")
      @Comment({""})
      @LangKey("xat.config.client.debug.materials")
      public boolean debugArmorMaterials = false;
      @Name("Show Item Slot IDs")
      @Comment({""})
      @LangKey("xat.config.client.debug.slots")
      public boolean showID = false;
      @Name("Show OreDict Entries")
      @Comment({""})
      @LangKey("xat.config.client.debug.oredict")
      public boolean showOreDictEntries = false;
      @Name("Show Movement Speed")
      @Comment({""})
      @LangKey("xat.config.client.debug.speed")
      public boolean showMovementSpeed = false;

      public DebugConfig() {
      }
   }
}
