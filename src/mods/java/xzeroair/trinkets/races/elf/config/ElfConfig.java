package xzeroair.trinkets.races.elf.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;

public class ElfConfig {
   private final String name = "elf";
   private final String PREFIX = "xat.config.races.elf";
   @Comment({""})
   @Name("01. Charge Shot")
   @LangKey("xat.config.races.elf.chargeshot.enabled")
   public boolean charge_shot = true;
   @Comment({"how much Mana should this ability initially cost"})
   @Name("02. Charge Shot Cost")
   @LangKey("xat.config.races.elf.chargeshot.cost")
   public float CS_Cost = 20.0F;
   @Comment({"If a fully charged shot should explode on impact causing splash damage"})
   @Name("03. Full Charge Explodes")
   @LangKey("xat.config.races.elf.chargeshot.explode")
   public boolean explode = true;
   @Comment({""})
   @Name("04. Max Charge Time")
   @LangKey("xat.config.races.elf.chargeshot.max.time")
   @RangeInt(
      min = 12,
      max = 72000
   )
   public int ChargeTime = 120;
   @Comment({""})
   @Name("05. Damage Base Multiplier")
   @LangKey("xat.config.races.elf.chargeshot.base.multiplier")
   public float baseMultiplier = 1.0F;
   @Comment({""})
   @Name("06. Minimum damage Multiplier")
   @LangKey("xat.config.races.elf.chargeshot.minimum.damage.multiplier")
   public float minimumDamageMultiplier = 10.0F;
   @Comment({"Used for when a mod bow is not working properly with Charged Shot"})
   @Name("7. Bow Blacklist")
   @LangKey("xat.config.races.elf.chargeshot.bow.blacklist")
   public String[] bowBlacklist = new String[]{"*:*crossbow*"};
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   private ElfConfig.Compatability compat = new ElfConfig.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:0, Operation:0", "Name:generic.knockbackResistance; Amount:0; Operation:0", "Name:generic.movementSpeed, Amount:0.1, Operation:1", "Name:generic.attackDamage, Amount:0, Operation:0", "Name:generic.attackSpeed, Amount:0.3, Operation:1", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:0, Operation:0", "Name:generic.reachDistance, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0, Operation:0", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public ElfConfig() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private ElfConfig.Compatability.TANCompat tan = new ElfConfig.Compatability.TANCompat();

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
