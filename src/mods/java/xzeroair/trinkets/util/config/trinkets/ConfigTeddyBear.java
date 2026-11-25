package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigTeddyBear {
   private final String name = "teddy_bear";
   private final String PREFIX = "xat.config.server.teddy_bear";
   private final String registry = "xat.config.registry";
   @Comment({"give buffs when sleeping"})
   @Name("01. Sleep Bonus")
   @LangKey("xat.config.server.teddy_bear.sleepbonus")
   public boolean sleep_bonus = true;
   @Comment({"Which Buffs to give"})
   @Name("02. Buffs")
   @LangKey("xat.config.server.teddy_bear.sleepbonus.buff")
   public String[] buffs = new String[]{"minecraft:regeneration:300:0", "minecraft:luck:600:0", "minecraft:health_boost:3600:1"};
   @Comment({"If this value is greater then 0, instead of giving every buff in the list, it gives x random buffs"})
   @Name("03. Random Buffs")
   @LangKey("xat.config.server.teddy_bear.sleepbonus.buff.random")
   public int randomBuff = 0;
   @Comment({"Which effects to prevent"})
   @Name("10. Immunities")
   @LangKey("xat.config.immunities.potions")
   public String[] immunities = new String[]{"lycanitesmobs:fear", "lycanitesmobs:insomnia"};
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigTeddyBear.Compatability compat = new ConfigTeddyBear.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigTeddyBear() {
   }

   public class Compatability {
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("trinket");

      public Compatability() {
      }
   }
}
