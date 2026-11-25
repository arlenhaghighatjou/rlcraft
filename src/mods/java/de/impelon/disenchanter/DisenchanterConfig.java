package de.impelon.disenchanter;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

   @EventBusSubscriber
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
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
}
