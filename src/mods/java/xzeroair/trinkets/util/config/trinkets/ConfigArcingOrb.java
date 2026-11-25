package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigArcingOrb {
   private final String name = "arcing_orb";
   private final String PREFIX = "xat.config.server.arcing_orb";
   private final String registry = "xat.config.registry";
   @Name("Bolt Attack")
   @LangKey("xat.config.server.arcing_orb.bold.enabled")
   public boolean attackAbility = true;
   @Name("Bolt Attack Damage")
   @LangKey("xat.config.server.arcing_orb.bolt.damage")
   public float attackDmg = 40.0F;
   @Name("Bolt Attack Cost")
   @LangKey("xat.config.server.arcing_orb.bolt.cost")
   public float attackCost = 300.0F;
   @Name("Dodge")
   @LangKey("xat.config.server.arcing_orb.dodge.enabled")
   public boolean dodgeAbility = true;
   @Name("Dodge Cost")
   @LangKey("xat.config.server.arcing_orb.dodge.cost")
   public float dodgeCost = 30.0F;
   @Name("Dodge Stuns")
   @LangKey("xat.config.server.arcing_orbdodge.stuns.enabled")
   public boolean dodgeStuns = true;
   @Name("Stun Radius")
   @LangKey("xat.config.server.arcing_orbdodge.stuns.radius")
   public double stunDistance = 2.0D;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigArcingOrb.Compatability compat = new ConfigArcingOrb.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0.25, Operation:1", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ConfigArcingOrb() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigArcingOrb.Compatability.TANCompat tan = new ConfigArcingOrb.Compatability.TANCompat();
      @Name("Baubles Compatability")
      @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
      @LangKey("xat.config.baubles")
      public BaubleCompat baubles = new BaubleCompat("belt");

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
