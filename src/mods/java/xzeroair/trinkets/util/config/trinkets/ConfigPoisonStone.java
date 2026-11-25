package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigPoisonStone {
   private final String name = "poison_stone";
   private final String PREFIX = "xat.config.server.poison_stone";
   private final String registry = "xat.config.registry";
   @Comment({"If an Enemy is Poisoned, Should you be able to do Extra damage"})
   @Name("01. Bonus Damage")
   @LangKey("xat.config.server.poison_stone.damagebonus")
   public boolean bonus_damage = true;
   @Comment({"How much damage should you do when the enemy is poisoned. Default Damage x this"})
   @Name("02. Bonus Damage amount")
   @LangKey("xat.config.server.poison_stone.damagebonus.amount")
   public float bonus_damage_amount = 2.0F;
   @Comment({"Can the Poison Stone Poison Enemies?"})
   @Name("03. Can Poison")
   @LangKey("xat.config.server.poison_stone.poison")
   public boolean poison = true;
   @Comment({"1 in X chance to Poison an Enemy on Attack for 2 seconds, Settings this value to 0 always poisons"})
   @Name("04. Poison Chance")
   @LangKey("xat.config.server.poison_stone.poison.chance")
   public int poison_chance = 5;
   @Comment({"How Long should the poison effect last in ticks"})
   @Name("05. Poison Duration")
   @LangKey("xat.config.server.poison_stone.poison.duration")
   public int poison_duration = 40;
   @Comment({"Which effects to prevent"})
   @Name("81. Immunities")
   @LangKey("xat.config.immunities.potions")
   public String[] immunities = new String[]{"minecraft:poison", "minecraft:hunger", "lycanitesmobs:plague"};
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigPoisonStone.Compatability compat = new ConfigPoisonStone.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigPoisonStone() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigPoisonStone.Compatability.TANCompat tan = new ConfigPoisonStone.Compatability.TANCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("trinket");

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
