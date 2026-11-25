package xzeroair.trinkets.util.config.trinkets;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import xzeroair.trinkets.util.config.trinkets.shared.BaubleCompat;

public class ConfigGreaterInertia {
   private final String name = "greater_inertia_stone";
   private final String PREFIX = "xat.config.server.greater_inertia_stone";
   private final String registry = "xat.config.registry";
   @Comment({"Should you Take Fall Damage?. Set to true to Enable. Default False"})
   @Name("Modified Fall Damage")
   @LangKey("xat.config.server.greater_inertia_stone.falldamage")
   public boolean fall_damage = true;
   @Name("Modified Fall Damage Amount")
   @RangeDouble(
      min = 0.0D,
      max = 10.0D
   )
   @LangKey("xat.config.server.greater_inertia_stone.falldamage.amount")
   public float falldamage_amount = 0.25F;
   @RequiresMcRestart
   @Comment({"Should this Item Be Registered"})
   @Name("98. Item Enabled")
   @LangKey("xat.config.registry.enabled")
   public boolean enabled = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public ConfigGreaterInertia.Compatability compat = new ConfigGreaterInertia.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0.4; Operation:0", "Name:generic.movementSpeed, Amount:1.0, Operation:2", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:1, Operation:1", "Name:xat.stepheight, Amount:0.6, Operation:0"};

   public ConfigGreaterInertia() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ConfigGreaterInertia.Compatability.TANCompat tan = new ConfigGreaterInertia.Compatability.TANCompat();
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
