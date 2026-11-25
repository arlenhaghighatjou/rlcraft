package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigWitherRing {
   private final String name = "wither_ring";
   private final String PREFIX = "xat.config.server.wither_ring";
   private final String registry = "xat.config.registry";
   @Comment({"If an Enemy is has Wither, Should you be able to leech health"})
   @Name("01. Health Leech")
   @LangKey("xat.config.server.wither_ring.leech")
   public boolean leech = true;
   @Comment({"How much damage should you leech per hit in half hearts"})
   @Name("02. Health Leech amount")
   @LangKey("xat.config.server.wither_ring.leech.amount")
   public float leech_amount = 2.0F;
   @Name("03. Can Wither")
   @LangKey("xat.config.server.wither_ring.wither")
   public boolean wither = true;
   @Comment({"1 in X chance to Wither an Enemy on Attack for 2 seconds"})
   @Name("04. Wither Chance")
   @LangKey("xat.config.server.wither_ring.wither.chance")
   public int wither_chance = 5;
   @Comment({""})
   @Name("05. Wither Duration")
   @LangKey("xat.config.server.wither_ring.wither.duration")
   public int wither_duration = 40;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigWitherRing.Compatability compat = new ConfigWitherRing.Compatability();
   @Comment({"For Mor Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0, Operation:0", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigWitherRing() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigWitherRing.Compatability.TANCompat tan = new ConfigWitherRing.Compatability.TANCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("ring");

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
