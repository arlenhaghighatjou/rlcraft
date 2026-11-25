package xzeroair.trinkets.util.config.mana;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;

public class EntityManaConfig {
   private final String PREFIX = "xat.config.magic";
   @Comment({"should certain Trinkets and Race abilities require Mana?"})
   @Name("01. Mana Enabled")
   @LangKey("xat.config.magic.mana.enabled")
   public boolean mana_enabled = true;
   @Comment({"how often in ticks mana regens"})
   @Name("02. Mana Regen Rate")
   @LangKey("xat.config.magic.mana.regen.ticks")
   public int mana_update_ticks = 20;
   @Comment({"after using an ability, how long before starting to regen mana again?"})
   @Name("03. Mana Regen Timeout")
   @LangKey("xat.config.magic.mana.regen.timeout")
   public int mana_regen_timeout = 60;
   @Comment({"Should Max Mana Be Capped"})
   @Name("04. Mana Cap")
   @LangKey("xat.config.magic.mana.cap")
   public boolean mana_cap = false;
   @Comment({"Total Max Mana before affinity bonus"})
   @Name("05. Max Mana")
   @LangKey("xat.config.magic.mana.cap.max")
   public float mana_max = 1000.0F;
   @Comment({"How much Essence does the Moon Rose have"})
   @Name("06. Moon Rose Essence")
   @LangKey("xat.config.magic.moon_rose.essence")
   public int essence_amount = 10;
   @Comment({"How long in ticks before the next essence can be absorbed"})
   @Name("07. Moon Rose Essence Cooldown")
   @LangKey("xat.config.magic.moon_rose.cooldown")
   public int essence_cooldown = 300;
   @Comment({"Allow Max Mana cap to effect Magic Affinity"})
   @Name("08. Cap Affinity")
   @LangKey("xat.config.magic.mana.cap.affinity")
   public boolean cap_affinity = false;
   @Comment({"Mana Crystals explode when smashed against stone producing a Mana Reagent"})
   @Name("09. Mana Crystals Explode")
   @LangKey("xat.config.magic.mana_crystal.explodes")
   public boolean crystalExplodes = false;
   @Comment({"Do Mana Reagents Hurt when you consume them"})
   @Name("10. Mana Reagent Harmful")
   @LangKey("xat.config.magic.mana_reagent.harmful")
   public boolean reagentHarmful = true;
   @Comment({"Cap Bonus Mana Obtained from Mana Crystals and Moon Rose, set to 0 to disable"})
   @Name("11. Cap Bonus Mana")
   @LangKey("xat.config.magic.mana.cap.bonus")
   @RangeDouble(
      min = 0.0D,
      max = Double.MAX_VALUE
   )
   public float cap_bonus = 0.0F;
   @Comment({"Bonus Mana Obtained from Mana Crystals and Moon Rose"})
   @Name("12. Bonus Mana")
   @LangKey("xat.config.magic.mana.bonus")
   @RangeDouble(
      min = 0.0D,
      max = Double.MAX_VALUE
   )
   public float bonus = 10.0F;
   @Comment({"modid:item_name;meta;amount"})
   @Name("97. MP Recovery Items")
   @LangKey("xat.config.magic.mana.recovery.list")
   public String[] recovery = new String[]{"xat:dwarf_stout;0;100%", "xat:elf_sap;0;100%", "xat:faelis_food;0;100%", "xat:fairy_dew;0;100%", "xat:goblin_soup;0;100%", "xat:titan_spirit;0;100%", "xat:dragon_gem;0;100%", "xat:mana_crystal;0;100%", "xat:mana_reagent;0;100%", "xat:mana_candy;0;50", "minecraft:golden_apple;0;20", "minecraft:golden_apple;1;50", "simpledifficulty:juice;0;10%", "simpledifficulty:juice;5;50%", "botania:manacookie;0;10%"};

   public EntityManaConfig() {
   }
}
