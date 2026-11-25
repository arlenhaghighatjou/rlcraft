package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigFaelisClaw {
   private final String name = "faelis_claw";
   private final String PREFIX = "xat.config.server.faelis_claw";
   private final String registry = "xat.config.registry";
   @Name("01. Bleeding")
   @LangKey("xat.config.server.faelis_claw.bleeding")
   public boolean bleed = true;
   @Name("02. Bleed Duration")
   @LangKey("xat.config.server.faelis_claw.bleeding.duration")
   public int bleedDuration = 300;
   @Name("03. Bleed chance")
   @LangKey("xat.config.server.faelis_claw.bleeding.chance")
   public int chance = 4;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigFaelisClaw.Compatability compat = new ConfigFaelisClaw.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0.25, Operation:1", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigFaelisClaw() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigFaelisClaw.Compatability.TANCompat tan = new ConfigFaelisClaw.Compatability.TANCompat();
      public ConfigFaelisClaw.Compatability.LycanitesCompat lycanites = new ConfigFaelisClaw.Compatability.LycanitesCompat();
      public ConfigFaelisClaw.Compatability.DefiledLandsCompat defiledlands = new ConfigFaelisClaw.Compatability.DefiledLandsCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("Trinket");

      public Compatability() {
      }

      public class DefiledLandsCompat {
         public boolean useDefiledLandsBleed = true;

         public DefiledLandsCompat() {
         }
      }

      public class LycanitesCompat {
         public boolean useLycaniteBleed = true;

         public LycanitesCompat() {
         }
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
