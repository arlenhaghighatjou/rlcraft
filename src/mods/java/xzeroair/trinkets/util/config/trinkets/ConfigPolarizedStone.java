package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigPolarizedStone {
   private final String name = "polarized_stone";
   private final String PREFIX = "xat.config.server.polarized_stone";
   private final String registry = "xat.config.registry";
   @Comment({"Should the Polarized Stone Instantly pickup Items, or Pull them toward you. Default True"})
   @Name("01. Instant Pickup")
   @LangKey("xat.config.server.polarized_stone.collection.instant")
   public boolean instant_pickup = true;
   @Comment({"Should the Polarized Stone Effect Nearby Exp"})
   @Name("02. Collect nearby exp")
   @LangKey("xat.config.server.polarized_stone.collection.xp")
   public boolean collectXP = true;
   @Comment({"Should the Polarized Stone Repel Incoming projectiles. Default True"})
   @Name("03. Repel Projectiles")
   @LangKey("xat.config.server.polarized_stone.repel")
   public boolean repell = true;
   @Comment({"Should The Polarized Stone Cost Mana to Repel Projectiles. Default True"})
   @Name("04. Repel costs Mana")
   @LangKey("xat.config.server.polarized_stone.repel.exhaust")
   public boolean exhaustion = true;
   @Comment({"Mana Cost Per tickrate."})
   @Name("05. Mana Cost")
   @RangeDouble(
      min = 0.0D
   )
   @LangKey("xat.config.server.polarized_stone.repel.exhaust.amount")
   public float exhaust_rate = 10.0F;
   @Comment({"How often in Ticks Mana is reduced while Repel is Active"})
   @Name("06. Mana Cost Rate")
   @RangeInt(
      min = 0
   )
   @LangKey("xat.config.server.polarized_stone.repel.exhaust.speed")
   public int exhaust_ticks = 20;
   @Comment({"If Instant Pickup is Disabled, Polarized Stone's Push and pull speed. Default 0.1, 0.1 MIN, 1.0 MAX"})
   @Name("07. Polarized Stone Push and Pull Speed")
   @RangeDouble(
      min = 0.1D,
      max = 1.0D
   )
   @LangKey("xat.config.server.polarized_stone.collection.speed")
   public double Polarized_Stone_Speed = 0.1D;
   @Name("09. Repelled Entities")
   @LangKey("xat.config.server.polarized_stone.repel.entity.whitelist")
   public String[] repelledEntities = new String[]{"minecraft:arrow", "minecraft:fireball"};
   @Comment({"The Distance from the player to repel entities"})
   @Name("10. Polarized Repel Range")
   @RangeDouble(
      min = 1.0D,
      max = 8.0D
   )
   @LangKey("xat.config.server.polarized_stone.repel.range")
   public double repelRange = 2.0D;
   @Comment({"Should the Polarized Stone Instantly pickup XP, or Pull them toward you. Default True"})
   @Name("11. Instant XP Pickup")
   @LangKey("xat.config.server.polarized_stone.collection.instant.xp")
   public boolean instant_xp = true;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigPolarizedStone.Compatability compat = new ConfigPolarizedStone.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};
   @Comment({"WARNING! SETTING THESE VALUES TOO HIGH WILL CAUSE YOU TO LAG. Try to Keep within a range of 4-16"})
   @Name("Pickup Range")
   @LangKey("xat.config.server.polarized_stone.collection.range")
   public ConfigPolarizedStone.Pickup_Range PR = new ConfigPolarizedStone.Pickup_Range();

   public ConfigPolarizedStone() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigPolarizedStone.Compatability.TANCompat tan = new ConfigPolarizedStone.Compatability.TANCompat();
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

   public class Pickup_Range {
      @Comment({"How Far Vertically(Up, Down) in Blocks the Polarized Stone collects Items and XP. Default 6, MIN 0, MAX 32"})
      @Name("Vertical Distance")
      @RangeInt(
         min = 0,
         max = 32
      )
      @LangKey("xat.config.server.polarized_stone.collection.range.vertical")
      public int VD = 6;
      @Comment({"How Far Horizontally(N, E, S, W) in Blocks the Polarized Stone collects Items and XP. Default 12, MIN 0, MAX 32"})
      @Name("Horizontal Distance")
      @RangeInt(
         min = 0,
         max = 32
      )
      @LangKey("xat.config.server.polarized_stone.collection.range.horizontal")
      public int HD = 12;

      public Pickup_Range() {
      }
   }
}
