package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigEnderCrown {
   private final String name = "ender_tiara";
   private final String PREFIX = "xat.config.server.ender_tiara";
   private final String registry = "xat.config.registry";
   @Comment({"If while wearing this should the player take damage while wet"})
   @Name("01. Water Hurts")
   @LangKey("xat.config.server.ender_tiara.waterhurts")
   public boolean water_hurts = false;
   @Comment({"If while wearing this you should have a chance of Ignoring Damage"})
   @Name("02. Chance to Ignore Damage")
   @LangKey("xat.config.server.ender_tiara.ignoredamage")
   public boolean dmgChance = true;
   @Comment({"If while wearing this you should have a chance of Summoning an Enderman to protect you"})
   @Name("03. Chance for Enderman")
   @LangKey("xat.config.server.ender_tiara.ignoredamage.spawn")
   public boolean spawnChance = true;
   @Comment({"1 in 'num' chance to ignore damage and summon an Enderman to protect you"})
   @Name("04. Chance")
   @LangKey("xat.config.server.ender_tiara.ignoredamage.spawn.chance")
   public int chance = 50;
   @Comment({"Nearby Endermen Follow you if enabled"})
   @Name("05. Endermen Follow")
   @LangKey("xat.config.server.ender_tiara.endermen.follow")
   public boolean Follow = true;
   @Comment({"When Attacking an Enderman should it fight back?"})
   @Name("06. Enderman Retaliate")
   @LangKey("xat.config.server.ender_tiara.endermen.retaliate")
   public boolean attackBack = false;
   @Comment({"When Killing an Enderman Should it drop Experience?"})
   @Name("07. Enderman drop exp")
   @LangKey("xat.config.server.ender_tiara.endermen.exp")
   public boolean expDrop = false;
   @Comment({"When Killing an Enderman Should it drop Items?"})
   @Name("08. Enderman drop Items")
   @LangKey("xat.config.server.ender_tiara.endermen.items")
   public boolean itemDrop = false;
   @Comment({"Allow Enderman to teleport"})
   @Name("09. teleport")
   @LangKey("xat.config.server.ender_tiara.endermen.teleport")
   public boolean teleport = false;
   @Comment({"Teleport when damaged by indirect attacks"})
   @Name("10. teleport on hurt")
   @LangKey("xat.config.server.ender_tiara.ignoredamage.teleport")
   public boolean teleportOnHurt = true;
   @Comment({"1 in 'num' chance to teleport when taking indirect damage"})
   @Name("11. Teleport Chance")
   @LangKey("xat.config.server.ender_tiara.ignoredamage.teleport.chance")
   public int teleportChance = 1;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigEnderCrown.Compatability compat = new ConfigEnderCrown.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigEnderCrown() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      public ConfigEnderCrown.Compatability.TANCompat tan = new ConfigEnderCrown.Compatability.TANCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("head");

      public Compatability() {
      }

      public class TANCompat {
         @Comment({"If Tough as Nails is Installed, Should the player be immune to Cold"})
         @Name("00. Immune to Cold")
         @LangKey("xat.config.server.ender_tiara.toughasnails.immunity.cold")
         public boolean immuneToCold = true;

         public TANCompat() {
         }
      }
   }
}
