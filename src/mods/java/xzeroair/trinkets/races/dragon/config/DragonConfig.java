package xzeroair.trinkets.races.dragon.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class DragonConfig {
   private final String name = "dragon";
   private final String PREFIX = "xat.config.races.dragon";
   @RequiresWorldRestart
   @Comment({"Creative Flight. Set to False to Disable. Default True"})
   @Name("00. Creative Flight")
   @LangKey("xat.config.races.dragon.flight")
   public boolean creative_flight = true;
   @Comment({"How much damage per second the dragon breath does"})
   @Name("01. Dragon Breath Damage")
   @LangKey("xat.config.races.dragon.breath.damage")
   public float breath_damage = 1.0F;
   @Comment({"The Mana Cost per tick when using dragons breath"})
   @Name("02. Dragon Breath Cost")
   @LangKey("xat.config.races.dragon.breath.cost")
   public float breath_cost = 10.0F;
   @RequiresWorldRestart
   @Comment({"Change the flight speed from the Vanilla Default of 0.05"})
   @Name("03. Change Flight Speed")
   @LangKey("xat.config.races.dragon.flight.speed")
   public boolean creative_flight_speed = true;
   @RequiresWorldRestart
   @Comment({"How Fast the player moves when in Creative Flight. Vanilla Default 0.05. Default 0.02"})
   @Name("04. Creative Flight Speed")
   @RangeDouble(
      min = 0.01D,
      max = 1.0D
   )
   @LangKey("xat.config.races.dragon.flight.speed.amount")
   public double flight_speed = 0.02D;
   @Comment({"Mana Cost per second while flying"})
   @Name("05. Flight Cost")
   @LangKey("xat.config.races.dragon.flight.cost")
   public float flight_cost = 5.0F;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public DragonConfig.Compatability compat = new DragonConfig.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0.25, Operation:1", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0.5, Operation:1", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0.5, Operation:1", "Name:generic.luck, Amount:0, Operation:0", "Name:generic.reachDistance, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public DragonConfig() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      public DragonConfig.Compatability.TANCompat tan = new DragonConfig.Compatability.TANCompat();
      @Name("Fire Resistance Tiers")
      @LangKey("xat.config.fire_resistance_tiers")
      public DragonConfig.Compatability.FireResistanceTiers FRTiers = new DragonConfig.Compatability.FireResistanceTiers();

      public Compatability() {
      }

      public class FireResistanceTiers {
         public int amplifier = 0;

         public FireResistanceTiers() {
         }
      }

      public class TANCompat {
         @Comment({"If Tough as Nails is Installed, Should the player be immune to Heat"})
         @Name("00. Immune to Heat")
         @LangKey("xat.config.dragons_eye.toughasnails.immunity.heat")
         public boolean immuneToHeat = true;

         public TANCompat() {
         }
      }
   }
}
