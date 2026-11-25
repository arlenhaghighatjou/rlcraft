package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigDragonsEye {
   private final String name = "dragons_eye";
   private final String PREFIX = "xat.config.server.dragons_eye";
   private final String registry = "xat.config.registry";
   @RequiresWorldRestart
   @Comment({"Dragon's Eye Ore Detection. Set to False to Disable. Default True :Requires Relogin to world"})
   @Name("01. Dragon's Eye Ore Detection")
   @LangKey("xat.config.server.dragons_eye.orefinder")
   public boolean oreFinder = true;
   @Name("Block Settings")
   @LangKey("xat.config.server.dragons_eye.orefinder.blocks")
   public ConfigDragonsEye.Blocks BLOCKS = new ConfigDragonsEye.Blocks();
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigDragonsEye.Compatability compat = new ConfigDragonsEye.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigDragonsEye() {
   }

   public class Blocks {
      @RequiresWorldRestart
      @Comment({"Find the Closest Block to the player. False to Compile a list of all nearby"})
      @Name("Find Closest")
      @LangKey("xat.config.server.dragons_eye.orefinder.blocks.closest")
      public boolean closest = true;
      @RequiresWorldRestart
      @Comment({"Accepts:", "oreDictionary Names, example: oreCoal", "Block Ids, example: minecraft:chest", "Block Id and Meta, example: minecraft:stone[1]"})
      @Name("Ore Blocks The Dragon's Eye can See")
      @LangKey("xat.config.server.dragons_eye.orefinder.blocks.list")
      public String[] Blocks = new String[]{"oreCoal;4605510", "oreIron;16764057", "oreGold;16766720", "oreLapis;2515356", "oreRedstone;11546150", "oreDiamond;59135", "oreEmerald;65357", "oreQuartz;15461355", "minecraft:chest;*;16766720", "minecraft:chest_minecart;16766720"};
      @Comment({"WARNING! SETTING THESE VALUES TOO HIGH WILL CAUSE YOU TO LAG. Try to Keep within a range of 4-16"})
      @Name("Detection Range")
      @LangKey("xat.config.server.dragons_eye.orefinder.range")
      public ConfigDragonsEye.Blocks.Detection_Range DR = new ConfigDragonsEye.Blocks.Detection_Range();

      public Blocks() {
      }

      public class Detection_Range {
         @RequiresMcRestart
         @Comment({"How Far Vertically(Up, Down) in Blocks the Dragon's Eye effect triggers. Default 6, MIN 0, MAX 32"})
         @Name("Vertical Distance")
         @LangKey("xat.config.server.dragons_eye.orefinder.range.vertical")
         @RangeInt(
            min = 0,
            max = 32
         )
         public int C00_VD = 6;
         @RequiresMcRestart
         @Comment({"How Far Horizontally(N, E, S, W) in Blocks the Dragon's Eye effect triggers. Default 12, MIN 0, MAX 32"})
         @Name("Horizontal Distance")
         @LangKey("xat.config.server.dragons_eye.orefinder.range.horizontal")
         @RangeInt(
            min = 0,
            max = 32
         )
         public int C001_HD = 12;

         public Detection_Range() {
         }
      }
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      public ConfigDragonsEye.Compatability.TANCompat tan = new ConfigDragonsEye.Compatability.TANCompat();
      @Name("Ice and Fire Compatability")
      @LangKey("xat.config.iceandfire")
      public ConfigDragonsEye.Compatability.IAFCompat iaf = new ConfigDragonsEye.Compatability.IAFCompat();
      @Name("Fire Resistance Tiers")
      @LangKey("xat.config.fire_resistance_tiers")
      public ConfigDragonsEye.Compatability.FireResistanceTiers FRTiers = new ConfigDragonsEye.Compatability.FireResistanceTiers();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("head");

      public Compatability() {
      }

      public class FireResistanceTiers {
         public int amplifier = 0;

         public FireResistanceTiers() {
         }
      }

      public class IAFCompat {
         @Comment({"If Ice and Fire is Installed, Should the Dragon's Eye Require a Specific Stage of Skull for the recipe. Set to 0 for any stage"})
         @Name("00. Skull Stage")
         @LangKey("xat.config.server.dragons_eye.iceandfire.recipe.stage")
         public int stage = 5;
         @Comment({"Should there be an Ice Variant of the Dragons eye"})
         @Name("01. Ice Variant")
         @LangKey("xat.config.server.dragons_eye.iceandfire.ice.variant")
         public boolean ICE_VARIANT = true;
         @Comment({"Should the Ice Variant have Frost Walker"})
         @Name("02. Frost Walker")
         @LangKey("xat.config.server.dragons_eye.iceandfire.ice.frostwalker")
         public boolean FROST_WALKER = true;

         public IAFCompat() {
         }
      }

      public class TANCompat {
         @Comment({"If Tough as Nails is Installed, Should the player be immune to Heat"})
         @Name("00. Immune to Heat")
         @LangKey("xat.config.server.dragons_eye.toughasnails.immunity.heat")
         public boolean immuneToHeat = true;
         @Comment({"If Tough as Nails is Installed, Should the player be immune to Cold"})
         @Name("01. Immune to Cold")
         @LangKey("xat.config.server.dragons_eye.toughasnails.immunity.cold")
         public boolean immuneToCold = true;

         public TANCompat() {
         }
      }
   }
}
