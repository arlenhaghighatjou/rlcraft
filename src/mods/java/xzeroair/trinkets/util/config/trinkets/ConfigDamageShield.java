package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigDamageShield {
   private final String name = "damage_shield";
   private final String PREFIX = "xat.config.server.damage_shield";
   private final String registry = "xat.config.registry";
   @Name("01. ignore damage System")
   @LangKey("xat.config.server.damage_shield.ignore.damage")
   public boolean damage_ignore = true;
   @Comment({"How many hits required before you ignore the next hit. Hits only count if the damage is at least 1 whole heart"})
   @Name("02. Requred Hits")
   @LangKey("xat.config.server.damage_shield.ignore.damage.hits")
   public int hits = 3;
   @Comment({"Do you Take less damage from Explosions. IE Creeper Explosions"})
   @Name("03. Explosion Resistance")
   @LangKey("xat.config.server.damage_shield.explosion.resist")
   public boolean explosion_resist = true;
   @Comment({"1 Means you Take Full Damage, 0.25 Means you take 1/4 damage"})
   @Name("04. Explosion Reistance Amount")
   @RangeDouble(
      min = 0.0D,
      max = 1.0D
   )
   @LangKey("xat.config.server.damage_shield.explosion.resist.amount")
   public float explosion_amount = 0.25F;
   @Comment({"The Resistance level given when worn"})
   @Name("05. Resistance Level")
   @RangeInt(
      min = 0
   )
   @LangKey("xat.config.server.damage_shield.resistance.level")
   public int resistance_level = 0;
   @Comment({"Does Resistance stack with Resistance from other sources"})
   @Name("06. Resistance stacks")
   @LangKey("xat.config.server.damage_shield.resistance.stacks")
   public boolean resistance_stacks = true;
   @Comment({"Max level Resistance can stack"})
   @Name("07. Max Stackable")
   @RangeInt(
      min = 0
   )
   @LangKey("xat.config.server.damage_shield.resistance.stacks.max")
   public int resistance_stacks_max = 3;
   @Comment({"Option to change what 'Resistance' is"})
   @Name("08. Resistance potion")
   @LangKey("xat.config.server.damage_shield.resistance")
   public String potionEffect = "minecraft:resistance";
   @Comment({"Only Disable this if You're the Epic Pro Gamer"})
   @Name("69. Epic Pro Gamer")
   @LangKey("xat.config.server.damage_shield.epic.gamer")
   public boolean special = true;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigDamageShield.Compatability compat = new ConfigDamageShield.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigDamageShield() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigDamageShield.Compatability.TANCompat tan = new ConfigDamageShield.Compatability.TANCompat();
      @Name("First Aid Compatability")
      @LangKey("xat.config.firstaid")
      public ConfigDamageShield.Compatability.FirstAidCompat firstaid = new ConfigDamageShield.Compatability.FirstAidCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("body");

      public Compatability() {
      }

      public class FirstAidCompat {
         @Comment({"If First Aid is Installed. This ONLY Triggers if the Next headshot was supposed to kill you"})
         @Name("01. Chance to Ignore Headshots")
         @LangKey("xat.config.server.damage_shield.firstaid.ignore.headshots")
         public boolean chance_ignore = true;
         @Comment({"If First Aid is Installed. 1 in How many Chance to Trigger Ignore Headshot"})
         @Name("02. Headshots Ignore Chance")
         @LangKey("xat.config.server.damage_shield.firstaid.ignore.headshots.chance")
         public int chance_headshots = 100;

         public FirstAidCompat() {
         }
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
