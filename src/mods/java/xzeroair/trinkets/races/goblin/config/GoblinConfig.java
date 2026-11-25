package xzeroair.trinkets.races.goblin.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

public class GoblinConfig {
   private final String name = "goblin";
   private final String PREFIX = "xat.config.races.goblin";
   @Comment({""})
   @Name("01. Resistances")
   @LangKey("xat.config.races.goblin.natural_resistance")
   public boolean natural_resistance = true;
   @Comment({""})
   @Name("02. Goblin Rider")
   @LangKey("xat.config.races.goblin.rider")
   public boolean rider = true;
   @Comment({"Should Creepers Ignore Goblin Players"})
   @Name("03. Friendly Creepers")
   @LangKey("xat.config.races.goblin.creeper")
   public boolean friendly_creepers = true;
   @Comment({"Should Creepers Explode when Attacked by Goblin Players"})
   @Name("03. Creepers Explode")
   @LangKey("xat.config.races.goblin.creeper.explode")
   public boolean creepers_explode = true;
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   private GoblinConfig.Compatability compat = new GoblinConfig.Compatability();
   @Comment({"For More Information on Attributes", "https://minecraft.gamepedia.com/Attribute"})
   @Name("Attributes")
   @LangKey("xat.config.attributes")
   public String[] attributes = new String[]{"Name:generic.maxHealth, Amount:-0.4, Operation:2", "Name:generic.knockbackResistance, Amount:0, Operation:0", "Name:generic.movementSpeed, Amount:0.2, Operation:1", "Name:generic.attackDamage, Amount:0.5, Operation:1", "Name:generic.attackSpeed, Amount:0, Operation:0", "Name:generic.armor, Amount:0, Operation:0", "Name:generic.armorToughness, Amount:0, Operation:0", "Name:generic.luck, Amount:1, Operation:0", "Name:generic.reachDistance, Amount:0, Operation:0", "Name:forge.swimSpeed, Amount:0.1, Operation:1", "Name:xat.entityMagic.regen, Amount:0, Operation:0", "Name:xat.entityMagic.regen.cooldown, Amount:0, Operation:0", "Name:xat.entityMagic.affinity, Amount:0, Operation:0", "Name:xat.jump, Amount:0, Operation:0", "Name:xat.stepheight, Amount:0, Operation:0"};

   public GoblinConfig() {
   }

   public class Compatability {
      @Name("Tough as Nails Compatability")
      @LangKey("xat.config.toughasnails")
      private GoblinConfig.Compatability.TANCompat tan = new GoblinConfig.Compatability.TANCompat();

      public Compatability() {
      }

      public class TANCompat {
         public TANCompat() {
         }
      }
   }
}
