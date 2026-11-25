package svenhjol.charm.tweaks.feature;

import svenhjol.charm.base.CharmLoadingPlugin;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ConfigHelper;

public class GoldToolImprovements extends Feature {
   public static int durability;
   public static double efficiency;
   public static double attackDamage;
   public static int harvestLevel;
   public static int enchantability;
   public static final String MATERIAL_NAME = "GOLD";
   public static final String CONFIG_FEATURE_CATEGORY = "CharmTweaks";
   public static final String CONFIG_FEATURE_NAME = "GoldToolImprovements";
   public static final String CONFIG_VALUES_CATEGORY = "CharmTweaks.GoldToolImprovements";
   public static final String CONFIG_DURABILITY = "Gold tool durability";
   public static final String CONFIG_EFFICIENCY = "Gold tool efficiency";
   public static final String CONFIG_ATTACK_DAMAGE = "Gold tool damage";
   public static final String CONFIG_HARVEST_LEVEL = "Gold tool harvest level";
   public static final String CONFIG_ENCHANTABILITY = "Gold tool enchantability";
   public static final int CUSTOM_DURABILITY = 512;
   public static final float CUSTOM_EFFICIENCY = 17.0F;
   public static final float CUSTOM_ATTACK_DAMAGE = 0.0F;
   public static final int CUSTOM_HARVEST_LEVEL = 0;
   public static final int CUSTOM_ENCHANTABILITY = 22;

   public GoldToolImprovements() {
   }

   public String getDescription() {
      return "Improves the base durability and efficiency of gold tools.";
   }

   public void configure() {
      super.configure();
      durability = this.propInt("Gold tool durability", "Default durability of gold tools. Vanilla is 32.", 512);
      efficiency = this.propDouble("Gold tool efficiency", "Default efficiency of gold tools. Vanilla is 12.0.", 17.0D);
      attackDamage = this.propDouble("Gold tool damage", "Default attack of gold tools. Vanilla is 0.0.", 0.0D);
      harvestLevel = this.propInt("Gold tool harvest level", "Default harvest level of gold tools. Vanilla is 0.", 0);
      enchantability = this.propInt("Gold tool enchantability", "Default enchantability of gold tools. Vanilla is 22.", 22);
   }

   public static boolean enabled() {
      return ConfigHelper.propBoolean(CharmLoadingPlugin.config, "GoldToolImprovements", "CharmTweaks", "", true);
   }

   public static int getMaxUses(int itemDefault) {
      if (!enabled()) {
         return itemDefault;
      } else {
         durability = ConfigHelper.propInt(CharmLoadingPlugin.config, "Gold tool durability", "CharmTweaks.GoldToolImprovements", "", 512);
         return durability;
      }
   }

   public static double getEfficiency(double itemDefault) {
      if (!enabled()) {
         return itemDefault;
      } else {
         efficiency = ConfigHelper.propDouble(CharmLoadingPlugin.config, "Gold tool efficiency", "CharmTweaks.GoldToolImprovements", "", 17.0D);
         return efficiency;
      }
   }

   public static double getAttackDamage(double itemDefault) {
      if (!enabled()) {
         return itemDefault;
      } else {
         attackDamage = ConfigHelper.propDouble(CharmLoadingPlugin.config, "Gold tool damage", "CharmTweaks.GoldToolImprovements", "", 0.0D);
         return attackDamage;
      }
   }

   public static int getHarvestLevel(int itemDefault) {
      if (!enabled()) {
         return itemDefault;
      } else {
         harvestLevel = ConfigHelper.propInt(CharmLoadingPlugin.config, "Gold tool harvest level", "CharmTweaks.GoldToolImprovements", "", 0);
         return harvestLevel;
      }
   }

   public static int getEnchantability(int itemDefault) {
      if (!enabled()) {
         return itemDefault;
      } else {
         enchantability = ConfigHelper.propInt(CharmLoadingPlugin.config, "Gold tool enchantability", "CharmTweaks.GoldToolImprovements", "", 22);
         return enchantability;
      }
   }
}
