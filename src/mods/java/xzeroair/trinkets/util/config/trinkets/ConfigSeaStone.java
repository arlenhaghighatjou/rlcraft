package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigSeaStone {
   private final String name = "sea_stone";
   private final String PREFIX = "xat.config.server.sea_stone";
   private final String registry = "xat.config.registry";
   @Comment({"Sea Stone's Ability to float in water. Set to False to Disable. Default True"})
   @Name("01. Sea Stone's Swimming Tweaks")
   @LangKey("xat.config.server.sea_stone.betterswimming")
   public boolean Swim_Tweaks = true;
   @Comment({"Use old version of Swimming Tweaks. Default True"})
   @Name("04. Sea Stone's Swimming Tweaks")
   @LangKey("xat.config.server.sea_stone.betterswimming.old")
   public boolean old_tweaks = false;
   @Name("02. Infinite Water Breathing")
   @LangKey("xat.config.server.sea_stone.waterbreathing")
   public boolean underwater_breathing = true;
   @Comment({"Should the player always have full bubbles, or stop at 1"})
   @Name("03. Full Bubbles")
   @LangKey("xat.config.server.sea_stone.waterbreathing.bubbles")
   public boolean always_full = true;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigSeaStone.Compatability compat = new ConfigSeaStone.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:4, Operation:2", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigSeaStone() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      public ConfigSeaStone.Compatability.TANCompat tan = new ConfigSeaStone.Compatability.TANCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("amulet");

      public Compatability() {
      }

      public class TANCompat {
         @Comment({"If Tough as Nails is installed should the Stone of the Sea Prevent thirst Poisoning"})
         @Name("00. Prevent TAN Thirst Poisoning")
         @LangKey("xat.config.server.sea_stone.toughasnails.thirst")
         public boolean prevent_thirst = true;

         public TANCompat() {
         }
      }
   }
}
