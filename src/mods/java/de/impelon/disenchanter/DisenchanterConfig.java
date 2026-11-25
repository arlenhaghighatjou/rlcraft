package de.impelon.disenchanter;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "disenchanter",
   category = ""
)
public class DisenchanterConfig {
   @Name("experience_jar")
   public static final DisenchanterConfig.ExperienceJarSection experienceJar = new DisenchanterConfig.ExperienceJarSection();
   @RequiresMcRestart
   @Name("crafting")
   public static final DisenchanterConfig.CraftingSection crafting = new DisenchanterConfig.CraftingSection();
   @Name("disenchanting")
   public static final DisenchanterConfig.DisenchantingSection disenchanting = new DisenchanterConfig.DisenchantingSection();
   @Name("visual")
   public static final DisenchanterConfig.VisualSection visual = new DisenchanterConfig.VisualSection();

   public DisenchanterConfig() {
   }

   public static class CraftingSection {
      @Name("EnableDisenchantmentTableRecipe")
      @Comment({"Should the recipe for the normal disenchantment table be available?"})
      public boolean enableDisenchantmentTableRecipe = true;
      @Name("EnableAutomaticTableUpgradeRecipe")
      @Comment({"Should the recipe for the automatic-upgrade (disenchantment table) be available?"})
      public boolean enableAutomaticTableUpgradeRecipe = true;
      @Name("EnableBulkDisenchantingTableUpgradeRecipe")
      @Comment({"Should the recipe for the bulk-disenchanting-upgrade (disenchantment table) be available?"})
      public boolean enableBulkDisenchantingTableUpgradeRecipe = true;
      @Name("EnableCyclingTableUpgradeRecipe")
      @Comment({"Should the recipe for the cycling-upgrade (disenchantment table) be available?"})
      public boolean enableCyclingTableUpgradeRecipe = true;
      @Name("EnableVoidingTableUpgradeRecipe")
      @Comment({"Should the recipe for the voiding-upgrade (disenchantment table) be available?"})
      public boolean enableVoidingTableUpgradeRecipe = true;
      @Name("EnableClearTableRecipe")
      @Comment({"Should the recipe for clearing all upgrades from a disenchantment table be available?"})
      public boolean enableClearTableRecipe = true;
      @Name("EnableJarRecipe")
      @Comment({"Should the recipe for the jar of experience be available?"})
      public boolean enableJarRecipe = true;
      @Name("EnableOverloadJarUpgradeRecipe")
      @Comment({"Should the recipe for the overload-upgrade (jar of experience) be available?"})
      public boolean enableOverloadJarUpgradeRecipe = true;
      @Name("EnableCapacityJarUpgradeRecipe")
      @Comment({"Should the recipe for upgrading the capacity (jar of experience) be available?"})
      public boolean enableCapacityJarUpgradeRecipe = true;
      @Name("EnableClearJarRecipe")
      @Comment({"Should the recipe for clearing all upgrades from a jar of experience be available?"})
      public boolean enableClearJarRecipe = true;

      public CraftingSection() {
      }
   }

   public static class DisenchantingSection {
      @Name("FlatDamage")
      @Comment({"How much flat damage should be dealt to items when disenchanting?"})
      public int flatDamage = 10;
      @Name("MaxDurabilityDamage")
      @Comment({"How much of the item's maximum durability should be dealt as damage to items when disenchanting?"})
      public double maxDurabilityDamage = 0.025D;
      @Name("MaxDurabilityDamageReducible")
      @Comment({"How much of the item's maximum durability should be dealt as reducible damage to items when disenchanting?", "This can be reduced by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves)."})
      public double maxDurabilityDamageReducible = 0.2D;
      @Name("MachineDamageMultiplier")
      @Comment({"By how much should the damage be multiplied when using an automatic disenchantment table?"})
      public double machineDamageMultiplier = 2.5D;
      @Name("DestroyNonDamageableItems")
      @Comment({"Should non-damageable items like books be destroyed when disenchanted?"})
      public boolean destroyNonDamageableItems = false;
      @Name("EnchantmentLossChance")
      @Comment({"What should the probability be that an enchantment is lost when disenchanting?"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double enchantmentLossChance = 0.0D;
      @Name("RepairCostMultiplier")
      @Comment({"By how much should the repair cost of the item (f.e. used by anvils) be multiplied when disenchanting?", "0.5 is halving the repair cost (using an anvil doubles the repair cost); 1.0 leaves the cost unchanged."})
      @RangeDouble(
         min = 0.0D
      )
      public double repairCostMultiplier = 0.5D;
      @Name("FlatExperience")
      @Comment({"When converting an enchantment to XP, a flat amount of experience points will be added.", "What should that amount be?"})
      public int flatExperience = 0;
      @Name("PowerExperience")
      @Comment({"When converting an enchantment to XP, an amount of experience points multiplied by the power will be added.", "The power can be raised up to 15 by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves).", "What should that amount be?"})
      public double powerExperience = 0.0D;
      @Name("MinEnchantabilityExperience")
      @Comment({"When converting an enchantment to XP, experience points relative to the enchantment's minimum enchantability will be added.", "What percentage of the enchantability should be used?"})
      public double minEnchantabilityExperience = 0.33D;
      @Name("MaxEnchantabilityExperience")
      @Comment({"When converting an enchantment to XP, experience points relative to the enchantment's maximum enchantability will be added.", "What percentage of the enchantability should be used?"})
      public double maxEnchantabilityExperience = 0.15D;
      @Name("PowerEnchantabilityExperienceMultiplier")
      @Comment({"When converting an enchantment to XP, experience points relative to the enchantment's enchantability will be added.", "Additionally a percentage of these experience points will be multiplied by the power and added to the total.", "The power can be raised up to 15 by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves).", "What percentage of the enchantability should be used?"})
      public double powerEnchantabilityExperienceMultiplier = 0.008D;
      @Name("AutomaticDisenchantingProcessTicks")
      @Comment({"How many ticks should the disenchanting process last when using an automatic disenchantment table?"})
      @RangeInt(
         min = 1
      )
      public int ticksAutomaticDisenchantingProcess = 100;
      @Name("CyclingDisenchantingTicks")
      @Comment({"How many ticks should the cycling disenchantment table wait before switching to a new enchantment?"})
      @RangeInt(
         min = 1
      )
      public int ticksCyclingDisenchanting = 60;
      @Name("DisabledItems")
      @Comment({"Which items should not be disenchantable?", "Entries are of the format `modid:itemid`; for example minecraft:dirt", "Java Regex can be used with a `[r]`-prefix; for example [r]minecraft:.* to ban all vanilla items."})
      public String[] disabledItems = new String[0];
      @Name("DisabledEnchantments")
      @Comment({"Which enchantments should be ignored when disenchanting?", "Entries are of the format `modid:enchantid`; for example minecraft:bane_of_arthropods", "Java Regex can be used with a `[r]`-prefix; for example [r]minecraft:.* to ban all vanilla enchantments."})
      public String[] disabledEnchantments = new String[0];
      @Name("DisableCurses")
      @Comment({"Should curse-enchantments -like curse of vanishing- be ignored when disenchanting?"})
      public boolean disableCurses = false;
      @Name("EnableTCBehaviour")
      @Comment({"Should items from Tinkers Construct be handled differently?", "Enchantments will not be able to be removed from these items."})
      public boolean enableTCBehaviour = true;

      public DisenchantingSection() {
      }
   }

   public static class EventHandler {
      private EventHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(OnConfigChangedEvent.class, EventHandler::onConfigChanged);
      }

      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("disenchanter")) {
            ConfigManager.sync("disenchanter", Type.INSTANCE);
         }

      }
   }

   public static class ExperienceJarSection {
      @Name("JarDisenchantingOnly")
      @Comment({"Should disenchanting be only possible with the jar of experience (as opposed to books)?"})
      public boolean jarDisenchantingOnly = false;
      @Name("JarDefaultExperienceCapacity")
      @Comment({"How many experience points should the jar of experience store at most by default?"})
      @RangeInt(
         min = 0
      )
      public int jarDefaultExperienceCapacity = 1024;
      @Name("JarUpgradeCapacityChange")
      @Comment({"By how many experience points should the capacity be changed when upgrading a jar of experience?"})
      public int jarUpgradeCapacityChange = 512;
      @Name("JarUpgradeMaxCapacity")
      @Comment({"What should be the maximal capacity of experience points obtainable via upgrades be for the jar of experience?"})
      @RangeInt(
         min = 0
      )
      public int jarUpgradeMaxCapacity = 2048;
      @Name("PlayerCanInsertXPIntoJar")
      @Comment({"Should players be able to manually insert experience points into the jar of experience by punching while sneaking?"})
      public boolean playerCanInsertXPInJar = true;
      @Name("PlayerCanExtractXPFromJar")
      @Comment({"Should players be able to manually extract experience points from the jar of experience by drinking from it?"})
      public boolean playerCanExtractXPFromJar = true;

      public ExperienceJarSection() {
      }
   }

   public static class VisualSection {
      @Name("BookRendererYOffset")
      @Comment({"How should the book be positioned above the disenchantment table compared to the regular enchanting table?", "0.0 is the same as the enchanting table.", "You will probably want to set this to about 0.1, if you want to disable BookRendererFlipped."})
      public double bookRendererYOffset = 0.4D;
      @Name("BookRendererFlipped")
      @Comment({"Should the book above the disenchantment table be flipped upside-down?"})
      public boolean bookRendererFlipped = true;
      @Name("BookRendererHidden")
      @Comment({"Should the book above the disenchantment table be completely hidden?"})
      public boolean bookRendererHidden = false;
      @Name("ShowUpgradesInGUI")
      @Comment({"Should the different upgrades of a disenchantment table be listed when viewing the GUI of said table?"})
      public boolean showUpgradesInGUI = true;
      @Name("DescriptionInGUIColor")
      @Comment({"What color should be used for additional descriptions (f.e. list of table upgrades) shown in the GUI of a disenchantment table?"})
      public int descriptionInGUIColor = 11184810;

      public VisualSection() {
      }
   }

   public static void register() {
      ConfigManager.register("disenchanter", "", "", null,
         ConfigEntry.category("experience_jar",
            ConfigEntry.value("JarDisenchantingOnly", boolean.class, () -> DisenchanterConfig.experienceJar.jarDisenchantingOnly, v -> DisenchanterConfig.experienceJar.jarDisenchantingOnly = (Boolean)v).comment("Should disenchanting be only possible with the jar of experience (as opposed to books)?").langKey("disenchanter.experience_jar.jardisenchantingonly"),
            ConfigEntry.value("JarDefaultExperienceCapacity", int.class, () -> DisenchanterConfig.experienceJar.jarDefaultExperienceCapacity, v -> DisenchanterConfig.experienceJar.jarDefaultExperienceCapacity = (Integer)v).comment("How many experience points should the jar of experience store at most by default?").langKey("disenchanter.experience_jar.jardefaultexperiencecapacity").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("JarUpgradeCapacityChange", int.class, () -> DisenchanterConfig.experienceJar.jarUpgradeCapacityChange, v -> DisenchanterConfig.experienceJar.jarUpgradeCapacityChange = (Integer)v).comment("By how many experience points should the capacity be changed when upgrading a jar of experience?").langKey("disenchanter.experience_jar.jarupgradecapacitychange"),
            ConfigEntry.value("JarUpgradeMaxCapacity", int.class, () -> DisenchanterConfig.experienceJar.jarUpgradeMaxCapacity, v -> DisenchanterConfig.experienceJar.jarUpgradeMaxCapacity = (Integer)v).comment("What should be the maximal capacity of experience points obtainable via upgrades be for the jar of experience?").langKey("disenchanter.experience_jar.jarupgrademaxcapacity").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("PlayerCanInsertXPIntoJar", boolean.class, () -> DisenchanterConfig.experienceJar.playerCanInsertXPInJar, v -> DisenchanterConfig.experienceJar.playerCanInsertXPInJar = (Boolean)v).comment("Should players be able to manually insert experience points into the jar of experience by punching while sneaking?").langKey("disenchanter.experience_jar.playercaninsertxpinjar"),
            ConfigEntry.value("PlayerCanExtractXPFromJar", boolean.class, () -> DisenchanterConfig.experienceJar.playerCanExtractXPFromJar, v -> DisenchanterConfig.experienceJar.playerCanExtractXPFromJar = (Boolean)v).comment("Should players be able to manually extract experience points from the jar of experience by drinking from it?").langKey("disenchanter.experience_jar.playercanextractxpfromjar")).langKey("disenchanter.experiencejar"),
         ConfigEntry.category("crafting",
            ConfigEntry.value("EnableDisenchantmentTableRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableDisenchantmentTableRecipe, v -> DisenchanterConfig.crafting.enableDisenchantmentTableRecipe = (Boolean)v).comment("Should the recipe for the normal disenchantment table be available?").langKey("disenchanter.crafting.enabledisenchantmenttablerecipe"),
            ConfigEntry.value("EnableAutomaticTableUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableAutomaticTableUpgradeRecipe, v -> DisenchanterConfig.crafting.enableAutomaticTableUpgradeRecipe = (Boolean)v).comment("Should the recipe for the automatic-upgrade (disenchantment table) be available?").langKey("disenchanter.crafting.enableautomatictableupgraderecipe"),
            ConfigEntry.value("EnableBulkDisenchantingTableUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableBulkDisenchantingTableUpgradeRecipe, v -> DisenchanterConfig.crafting.enableBulkDisenchantingTableUpgradeRecipe = (Boolean)v).comment("Should the recipe for the bulk-disenchanting-upgrade (disenchantment table) be available?").langKey("disenchanter.crafting.enablebulkdisenchantingtableupgraderecipe"),
            ConfigEntry.value("EnableCyclingTableUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableCyclingTableUpgradeRecipe, v -> DisenchanterConfig.crafting.enableCyclingTableUpgradeRecipe = (Boolean)v).comment("Should the recipe for the cycling-upgrade (disenchantment table) be available?").langKey("disenchanter.crafting.enablecyclingtableupgraderecipe"),
            ConfigEntry.value("EnableVoidingTableUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableVoidingTableUpgradeRecipe, v -> DisenchanterConfig.crafting.enableVoidingTableUpgradeRecipe = (Boolean)v).comment("Should the recipe for the voiding-upgrade (disenchantment table) be available?").langKey("disenchanter.crafting.enablevoidingtableupgraderecipe"),
            ConfigEntry.value("EnableClearTableRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableClearTableRecipe, v -> DisenchanterConfig.crafting.enableClearTableRecipe = (Boolean)v).comment("Should the recipe for clearing all upgrades from a disenchantment table be available?").langKey("disenchanter.crafting.enablecleartablerecipe"),
            ConfigEntry.value("EnableJarRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableJarRecipe, v -> DisenchanterConfig.crafting.enableJarRecipe = (Boolean)v).comment("Should the recipe for the jar of experience be available?").langKey("disenchanter.crafting.enablejarrecipe"),
            ConfigEntry.value("EnableOverloadJarUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableOverloadJarUpgradeRecipe, v -> DisenchanterConfig.crafting.enableOverloadJarUpgradeRecipe = (Boolean)v).comment("Should the recipe for the overload-upgrade (jar of experience) be available?").langKey("disenchanter.crafting.enableoverloadjarupgraderecipe"),
            ConfigEntry.value("EnableCapacityJarUpgradeRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableCapacityJarUpgradeRecipe, v -> DisenchanterConfig.crafting.enableCapacityJarUpgradeRecipe = (Boolean)v).comment("Should the recipe for upgrading the capacity (jar of experience) be available?").langKey("disenchanter.crafting.enablecapacityjarupgraderecipe"),
            ConfigEntry.value("EnableClearJarRecipe", boolean.class, () -> DisenchanterConfig.crafting.enableClearJarRecipe, v -> DisenchanterConfig.crafting.enableClearJarRecipe = (Boolean)v).comment("Should the recipe for clearing all upgrades from a jar of experience be available?").langKey("disenchanter.crafting.enableclearjarrecipe")).requiresMcRestart(),
         ConfigEntry.category("disenchanting",
            ConfigEntry.value("FlatDamage", int.class, () -> DisenchanterConfig.disenchanting.flatDamage, v -> DisenchanterConfig.disenchanting.flatDamage = (Integer)v).comment("How much flat damage should be dealt to items when disenchanting?").langKey("disenchanter.disenchanting.flatdamage"),
            ConfigEntry.value("MaxDurabilityDamage", double.class, () -> DisenchanterConfig.disenchanting.maxDurabilityDamage, v -> DisenchanterConfig.disenchanting.maxDurabilityDamage = (Double)v).comment("How much of the item's maximum durability should be dealt as damage to items when disenchanting?").langKey("disenchanter.disenchanting.maxdurabilitydamage"),
            ConfigEntry.value("MaxDurabilityDamageReducible", double.class, () -> DisenchanterConfig.disenchanting.maxDurabilityDamageReducible, v -> DisenchanterConfig.disenchanting.maxDurabilityDamageReducible = (Double)v).comment("How much of the item's maximum durability should be dealt as reducible damage to items when disenchanting?", "This can be reduced by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves).").langKey("disenchanter.disenchanting.maxdurabilitydamagereducible"),
            ConfigEntry.value("MachineDamageMultiplier", double.class, () -> DisenchanterConfig.disenchanting.machineDamageMultiplier, v -> DisenchanterConfig.disenchanting.machineDamageMultiplier = (Double)v).comment("By how much should the damage be multiplied when using an automatic disenchantment table?").langKey("disenchanter.disenchanting.machinedamagemultiplier"),
            ConfigEntry.value("DestroyNonDamageableItems", boolean.class, () -> DisenchanterConfig.disenchanting.destroyNonDamageableItems, v -> DisenchanterConfig.disenchanting.destroyNonDamageableItems = (Boolean)v).comment("Should non-damageable items like books be destroyed when disenchanted?").langKey("disenchanter.disenchanting.destroynondamageableitems"),
            ConfigEntry.value("EnchantmentLossChance", double.class, () -> DisenchanterConfig.disenchanting.enchantmentLossChance, v -> DisenchanterConfig.disenchanting.enchantmentLossChance = (Double)v).comment("What should the probability be that an enchantment is lost when disenchanting?").langKey("disenchanter.disenchanting.enchantmentlosschance").range(0.0, 1.0),
            ConfigEntry.value("RepairCostMultiplier", double.class, () -> DisenchanterConfig.disenchanting.repairCostMultiplier, v -> DisenchanterConfig.disenchanting.repairCostMultiplier = (Double)v).comment("By how much should the repair cost of the item (f.e. used by anvils) be multiplied when disenchanting?", "0.5 is halving the repair cost (using an anvil doubles the repair cost); 1.0 leaves the cost unchanged.").langKey("disenchanter.disenchanting.repaircostmultiplier").range(0.0, Double.MAX_VALUE),
            ConfigEntry.value("FlatExperience", int.class, () -> DisenchanterConfig.disenchanting.flatExperience, v -> DisenchanterConfig.disenchanting.flatExperience = (Integer)v).comment("When converting an enchantment to XP, a flat amount of experience points will be added.", "What should that amount be?").langKey("disenchanter.disenchanting.flatexperience"),
            ConfigEntry.value("PowerExperience", double.class, () -> DisenchanterConfig.disenchanting.powerExperience, v -> DisenchanterConfig.disenchanting.powerExperience = (Double)v).comment("When converting an enchantment to XP, an amount of experience points multiplied by the power will be added.", "The power can be raised up to 15 by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves).", "What should that amount be?").langKey("disenchanter.disenchanting.powerexperience"),
            ConfigEntry.value("MinEnchantabilityExperience", double.class, () -> DisenchanterConfig.disenchanting.minEnchantabilityExperience, v -> DisenchanterConfig.disenchanting.minEnchantabilityExperience = (Double)v).comment("When converting an enchantment to XP, experience points relative to the enchantment's minimum enchantability will be added.", "What percentage of the enchantability should be used?").langKey("disenchanter.disenchanting.minenchantabilityexperience"),
            ConfigEntry.value("MaxEnchantabilityExperience", double.class, () -> DisenchanterConfig.disenchanting.maxEnchantabilityExperience, v -> DisenchanterConfig.disenchanting.maxEnchantabilityExperience = (Double)v).comment("When converting an enchantment to XP, experience points relative to the enchantment's maximum enchantability will be added.", "What percentage of the enchantability should be used?").langKey("disenchanter.disenchanting.maxenchantabilityexperience"),
            ConfigEntry.value("PowerEnchantabilityExperienceMultiplier", double.class, () -> DisenchanterConfig.disenchanting.powerEnchantabilityExperienceMultiplier, v -> DisenchanterConfig.disenchanting.powerEnchantabilityExperienceMultiplier = (Double)v).comment("When converting an enchantment to XP, experience points relative to the enchantment's enchantability will be added.", "Additionally a percentage of these experience points will be multiplied by the power and added to the total.", "The power can be raised up to 15 by surrounding the disenchantment table with blocks that increase the enchanting level for the enchanting table (e.g. bookshelves).", "What percentage of the enchantability should be used?").langKey("disenchanter.disenchanting.powerenchantabilityexperiencemultiplier"),
            ConfigEntry.value("AutomaticDisenchantingProcessTicks", int.class, () -> DisenchanterConfig.disenchanting.ticksAutomaticDisenchantingProcess, v -> DisenchanterConfig.disenchanting.ticksAutomaticDisenchantingProcess = (Integer)v).comment("How many ticks should the disenchanting process last when using an automatic disenchantment table?").langKey("disenchanter.disenchanting.ticksautomaticdisenchantingprocess").range(1, Integer.MAX_VALUE),
            ConfigEntry.value("CyclingDisenchantingTicks", int.class, () -> DisenchanterConfig.disenchanting.ticksCyclingDisenchanting, v -> DisenchanterConfig.disenchanting.ticksCyclingDisenchanting = (Integer)v).comment("How many ticks should the cycling disenchantment table wait before switching to a new enchantment?").langKey("disenchanter.disenchanting.tickscyclingdisenchanting").range(1, Integer.MAX_VALUE),
            ConfigEntry.value("DisabledItems", String[].class, () -> DisenchanterConfig.disenchanting.disabledItems, v -> DisenchanterConfig.disenchanting.disabledItems = (String[])v).comment("Which items should not be disenchantable?", "Entries are of the format `modid:itemid`; for example minecraft:dirt", "Java Regex can be used with a `[r]`-prefix; for example [r]minecraft:.* to ban all vanilla items.").langKey("disenchanter.disenchanting.disableditems"),
            ConfigEntry.value("DisabledEnchantments", String[].class, () -> DisenchanterConfig.disenchanting.disabledEnchantments, v -> DisenchanterConfig.disenchanting.disabledEnchantments = (String[])v).comment("Which enchantments should be ignored when disenchanting?", "Entries are of the format `modid:enchantid`; for example minecraft:bane_of_arthropods", "Java Regex can be used with a `[r]`-prefix; for example [r]minecraft:.* to ban all vanilla enchantments.").langKey("disenchanter.disenchanting.disabledenchantments"),
            ConfigEntry.value("DisableCurses", boolean.class, () -> DisenchanterConfig.disenchanting.disableCurses, v -> DisenchanterConfig.disenchanting.disableCurses = (Boolean)v).comment("Should curse-enchantments -like curse of vanishing- be ignored when disenchanting?").langKey("disenchanter.disenchanting.disablecurses"),
            ConfigEntry.value("EnableTCBehaviour", boolean.class, () -> DisenchanterConfig.disenchanting.enableTCBehaviour, v -> DisenchanterConfig.disenchanting.enableTCBehaviour = (Boolean)v).comment("Should items from Tinkers Construct be handled differently?", "Enchantments will not be able to be removed from these items.").langKey("disenchanter.disenchanting.enabletcbehaviour")),
         ConfigEntry.category("visual",
            ConfigEntry.value("BookRendererYOffset", double.class, () -> DisenchanterConfig.visual.bookRendererYOffset, v -> DisenchanterConfig.visual.bookRendererYOffset = (Double)v).comment("How should the book be positioned above the disenchantment table compared to the regular enchanting table?", "0.0 is the same as the enchanting table.", "You will probably want to set this to about 0.1, if you want to disable BookRendererFlipped.").langKey("disenchanter.visual.bookrendereryoffset"),
            ConfigEntry.value("BookRendererFlipped", boolean.class, () -> DisenchanterConfig.visual.bookRendererFlipped, v -> DisenchanterConfig.visual.bookRendererFlipped = (Boolean)v).comment("Should the book above the disenchantment table be flipped upside-down?").langKey("disenchanter.visual.bookrendererflipped"),
            ConfigEntry.value("BookRendererHidden", boolean.class, () -> DisenchanterConfig.visual.bookRendererHidden, v -> DisenchanterConfig.visual.bookRendererHidden = (Boolean)v).comment("Should the book above the disenchantment table be completely hidden?").langKey("disenchanter.visual.bookrendererhidden"),
            ConfigEntry.value("ShowUpgradesInGUI", boolean.class, () -> DisenchanterConfig.visual.showUpgradesInGUI, v -> DisenchanterConfig.visual.showUpgradesInGUI = (Boolean)v).comment("Should the different upgrades of a disenchantment table be listed when viewing the GUI of said table?").langKey("disenchanter.visual.showupgradesingui"),
            ConfigEntry.value("DescriptionInGUIColor", int.class, () -> DisenchanterConfig.visual.descriptionInGUIColor, v -> DisenchanterConfig.visual.descriptionInGUIColor = (Integer)v).comment("What color should be used for additional descriptions (f.e. list of table upgrades) shown in the GUI of a disenchantment table?").langKey("disenchanter.visual.descriptioninguicolor")));
   }
}
