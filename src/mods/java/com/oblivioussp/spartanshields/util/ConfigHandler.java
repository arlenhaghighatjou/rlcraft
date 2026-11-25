package com.oblivioussp.spartanshields.util;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ConfigHandler {
   public static Configuration config;
   public static int durabilityWoodShield = 104;
   public static int durabilityStoneShield = 230;
   public static int durabilityIronShield = 538;
   public static int durabilityGoldShield = 192;
   public static int durabilityDiamondShield = 2560;
   public static int durabilityBronzeShield = 680;
   public static boolean enableBronzeShield = true;
   public static int durabilitySteelShield = 800;
   public static boolean enableSteelShield = true;
   public static int durabilityCopperShield = 400;
   public static boolean enableCopperShield = true;
   public static int durabilityTinShield = 320;
   public static boolean enableTinShield = true;
   public static int durabilitySilverShield = 216;
   public static boolean enableSilverShield = true;
   public static int durabilityObsidianShield = 1600;
   public static boolean enableObsidianShield = true;
   public static int durabilityEnderiumShield = 3120;
   public static boolean enableEnderiumShield = true;
   public static int durabilityInvarShield = 720;
   public static boolean enableInvarShield = true;
   public static int durabilityPlatinumShield = 1536;
   public static boolean enablePlatinumShield = true;
   public static int durabilityElectrumShield = 320;
   public static boolean enableElectrumShield = true;
   public static int durabilityNickelShield = 420;
   public static boolean enableNickelShield = true;
   public static int durabilityLeadShield = 480;
   public static boolean enableLeadShield = true;
   public static int durabilitySignalumShield = 2048;
   public static boolean enableSignalumShield = true;
   public static int durabilityLumiumShield = 1280;
   public static boolean enableLumiumShield = true;
   public static int durabilityManasteelShield = 450;
   public static boolean enableManasteelShield = true;
   public static int durabilityTerrasteelShield = 2920;
   public static boolean enableTerrasteelShield = true;
   public static int durabilityElementiumShield = 1080;
   public static boolean enableElementiumShield = true;
   public static int durabilityConstantanShield = 640;
   public static boolean enableConstantanShield = true;
   public static int durabilityDarkstoneShield = 270;
   public static boolean enableDarkstoneShield = true;
   public static int durabilityAbyssalniteShield = 1800;
   public static boolean enableAbyssalniteShield = true;
   public static int durabilityCoraliumShield = 2700;
   public static boolean enableCoraliumShield = true;
   public static int durabilityDreadiumShield = 3450;
   public static boolean enableDreadiumShield = true;
   public static int durabilityEthaxiumShield = 4200;
   public static boolean enableEthaxiumShield = true;
   public static int durabilitySoulforgedSteelShield = 1024;
   public static boolean enableSoulforgedSteelShield = true;
   public static int durabilityThaumiumShield = 1024;
   public static boolean enableThaumiumShield = true;
   public static int durabilityVoidShield = 225;
   public static boolean enableVoidShield = true;
   public static boolean enableEnderIOShield = true;
   public static boolean enableRFToolsShield = true;
   public static boolean enableRAShield = true;
   public static int damageToRFMultiplier = 50;
   public static int damageToFEMultiplier = 50;
   public static boolean vanillaOnly = false;
   public static boolean forceDisableUncraftableTooltips = false;
   public static boolean enableSpikesEnchantment = true;
   public static boolean disableTowerShields = false;
   public static boolean disableStandardShields = false;
   public static int cooldownShieldBash = 30;
   public static boolean disableShieldBash = false;
   public static final String categoryVanilla = "vanilla";
   public static final String categoryModdedCommon = "moddedCommon";
   public static final String categoryThermalMods = "moddedThermal";
   public static final String categoryModdedBotania = "moddedBotania";
   public static final String categoryModdedAbyssalcraft = "moddedAbyssalcraft";
   public static final String categoryModdedThaumcraft = "moddedThaumcraft";
   public static final String categoryModdedBWM = "moddedBWM";
   public static final String categoryPowered = "moddedPowered";
   public static final String[] categories = new String[]{"general", "vanilla", "moddedCommon", "moddedThermal", "moddedBotania", "moddedAbyssalcraft", "moddedThaumcraft", "moddedBWM", "moddedPowered"};

   public ConfigHandler() {
   }

   public static void init(File configFile) {
      if (config == null) {
         config = new Configuration(configFile, "1");
         loadConfig();

         for(String category : categories) {
            setCategoryLanguageKey(category);
         }
      }

   }

   @SubscribeEvent
   public static void onConfigChangedEvent(OnConfigChangedEvent event) {
      if (event.getModID().equalsIgnoreCase("spartanshields")) {
         loadConfig();
      }

   }

   private static void loadConfig() {
      vanillaOnly = getBoolean("vanillaOnly", "general", false, "Set to true to have only Vanilla-based shields enabled. Note that this completely removes mod-based Shields from the game! You won't be able to see them even in JEI.");
      forceDisableUncraftableTooltips = getBoolean("forceDisableUncraftableTooltips", "general", false, "Will force the uncraftable shield tooltip (highlighted in red) to not appear. Useful for modpack makers who wish to change recipes.");
      enableSpikesEnchantment = getBoolean("enableSpikesEnchantment", "general", true, "Set to false to completely disable the Spikes enchantment.");
      disableTowerShields = getBoolean("disableTowerShields", "general", false, "Set to disable all Tower Shields in the mod.");
      disableStandardShields = getBoolean("disableStandardShields", "general", false, "Set to disable all Standard Shields in the mod. Tower Shields are unaffected.");
      cooldownShieldBash = getInt("cooldownShieldBash", "general", 30, 1, 1000, "Cooldown period for Shield Bashes.");
      disableShieldBash = getBoolean("disableShieldBash", "general", false, "Set to true to disable the Shield Bash feature.");
      durabilityWoodShield = getInt("durabilityWoodShield", "vanilla", 104, 1, 100000, "Tweaks the durability of the Crude Wooden Shield.");
      durabilityStoneShield = getInt("durabilityStoneShield", "vanilla", 230, 1, 100000, "Tweaks the durability of the Stone Braced Shield.");
      durabilityIronShield = getInt("durabilityIronShield", "vanilla", 538, 1, 100000, "Tweaks the durability of the Iron Plated Shield.");
      durabilityGoldShield = getInt("durabilityGoldShield", "vanilla", 192, 1, 100000, "Tweaks the durability of the Gold Gilded Shield.");
      durabilityDiamondShield = getInt("durabilityDiamondShield", "vanilla", 2560, 1, 100000, "Tweaks the durability of the Diamond Reinforced Shield.");
      durabilityObsidianShield = getInt("durabilityObsidianShield", "vanilla", 1600, 1, 100000, "Tweaks the durability of the Bulky Obsidian Shield.");
      enableObsidianShield = getBoolean("enableObsidianShield", "vanilla", true, "Enables or Disables the Bulky Obsidian Shield.");
      durabilityBronzeShield = getInt("durabilityBronzeShield", "moddedCommon", 680, 1, 100000, "Tweaks the durability of the Bronze Plated Shield.");
      enableBronzeShield = getBoolean("enableBronzeShield", "moddedCommon", true, "Enables or Disables the Bronze Plated Shield.");
      durabilitySteelShield = getInt("durabilitySteelShield", "moddedCommon", 800, 1, 100000, "Tweaks the durability of the Steel Plated Shield.");
      enableSteelShield = getBoolean("enableSteelShield", "moddedCommon", true, "Enables or Disables the Steel Plated Shield.");
      durabilityCopperShield = getInt("durabilityCopperShield", "moddedCommon", 400, 1, 100000, "Tweaks the durability of the Copper Braced Shield.");
      enableCopperShield = getBoolean("enableCopperShield", "moddedCommon", true, "Enables or Disables the Copper Braced Shield.");
      durabilityTinShield = getInt("durabilityTinShield", "moddedCommon", 320, 1, 100000, "Tweaks the durability of the Tin Braced Shield.");
      enableTinShield = getBoolean("enableTinShield", "moddedCommon", true, "Enables or Disables the Tin Braced Shield.");
      durabilitySilverShield = getInt("durabilitySilverShield", "moddedCommon", 216, 1, 100000, "Tweaks the durability of the Silver Gilded Shield.");
      enableSilverShield = getBoolean("enableSilverShield", "moddedCommon", true, "Enables or Disables the Silver Gilded Shield.");
      durabilityInvarShield = getInt("durabilityInvarShield", "moddedCommon", 720, 1, 100000, "Tweaks the durability of the Invar Plated Shield.");
      enableInvarShield = getBoolean("enableInvarShield", "moddedCommon", true, "Enables or Disables the Invar Plated Shield.");
      durabilityPlatinumShield = getInt("durabilityPlatinumShield", "moddedCommon", 1536, 1, 100000, "Tweaks the durability of the Platinum Plated Shield.");
      enablePlatinumShield = getBoolean("enablePlatinumShield", "moddedCommon", true, "Enables or Disables the Platinum Plated Shield.");
      durabilityElectrumShield = getInt("durabilityElectrumShield", "moddedCommon", 320, 1, 100000, "Tweaks the durability of the Electrum Plated Shield.");
      enableElectrumShield = getBoolean("enableElectrumShield", "moddedCommon", true, "Enables or Disables the Electrum Plated Shield.");
      durabilityNickelShield = getInt("durabilityNickelShield", "moddedCommon", 420, 1, 100000, "Tweaks the durability of the Nickel Braced Shield.");
      enableNickelShield = getBoolean("enableNickelShield", "moddedCommon", true, "Enables or Disables the Nickel Braced Shield.");
      durabilityLeadShield = getInt("durabilityLeadShield", "moddedCommon", 480, 1, 100000, "Tweaks the durability of the Lead Plated Shield.");
      enableLeadShield = getBoolean("enableLeadShield", "moddedCommon", true, "Enables or Disables the Lead Plated Shield.");
      durabilityEnderiumShield = getInt("durabilityEnderiumShield", "moddedThermal", 3120, 1, 100000, "Tweaks the durability of the Enderium Strengthened Shield.");
      enableEnderiumShield = getBoolean("enableEnderiumShield", "moddedThermal", true, "Enables or Disables the Enderium Strengthened Shield.");
      durabilitySignalumShield = getInt("durabilitySignalumShield", "moddedThermal", 2048, 1, 100000, "Tweaks the durability of the Signalum Strengthened Shield.");
      enableSignalumShield = getBoolean("enableSignalumShield", "moddedThermal", true, "Enables or Disables the Signalum Strengthened Shield.");
      durabilityLumiumShield = getInt("durabilityLumiumShield", "moddedThermal", 1280, 1, 100000, "Tweaks the durability of the Radiant Lumium Shield.");
      enableLumiumShield = getBoolean("enableLumiumShield", "moddedThermal", true, "Enables or Disables the Radiant Lumium Shield.");
      durabilityManasteelShield = getInt("durabilityManasteelShield", "moddedBotania", 450, 1, 100000, "Tweaks the durability of the Manasteel Plated Shield.");
      enableManasteelShield = getBoolean("enableManasteelShield", "moddedBotania", true, "Enables or Disables the Manasteel Plated Shield.");
      durabilityTerrasteelShield = getInt("durabilityTerrasteelShield", "moddedBotania", 2920, 1, 100000, "Tweaks the durability of the Terrasteel Plated Shield.");
      enableTerrasteelShield = getBoolean("enableTerrasteelShield", "moddedBotania", true, "Enables or Disables the Terrasteel Plated Shield.");
      durabilityElementiumShield = getInt("durabilityElementiumShield", "moddedBotania", 1080, 1, 100000, "Tweaks the durability of the Elementium Plated Shield.");
      enableElementiumShield = getBoolean("enableElementiumShield", "moddedBotania", true, "Enables or Disables the Elementium Plated Shield.");
      durabilityConstantanShield = getInt("durabilityConstantanShield", "moddedCommon", 640, 1, 100000, "Tweaks the durability of the Constantan Plated Shield.");
      enableConstantanShield = getBoolean("enableConstantanShield", "moddedCommon", true, "Enables or Disables the Constantan Plated Shield.");
      durabilityDarkstoneShield = getInt("durabilityDarkstoneShield", "moddedAbyssalcraft", 270, 1, 100000, "Tweaks the durability of the Darkstone Braced Shield.");
      enableDarkstoneShield = getBoolean("enableDarkstoneShield", "moddedAbyssalcraft", true, "Enables or Disables the Darkstone Braced Shield.");
      durabilityAbyssalniteShield = getInt("durabilityAbyssalniteShield", "moddedAbyssalcraft", 1800, 1, 100000, "Tweaks the durability of the Abyssalnite Plated Shield.");
      enableAbyssalniteShield = getBoolean("enableAbyssalniteShield", "moddedAbyssalcraft", true, "Enables or Disables the Abyssalnite Plated Shield.");
      durabilityCoraliumShield = getInt("durabilityCoraliumShield", "moddedAbyssalcraft", 2700, 1, 100000, "Tweaks the durability of the Coralium Plated Shield.");
      enableCoraliumShield = getBoolean("enableCoraliumShield", "moddedAbyssalcraft", true, "Enables or Disables the Coralium Plated Shield.");
      durabilityDreadiumShield = getInt("durabilityDreadiumShield", "moddedAbyssalcraft", 3450, 1, 100000, "Tweaks the durability of the Dreadium Reinforced Shield.");
      enableDreadiumShield = getBoolean("enableDreadiumShield", "moddedAbyssalcraft", true, "Enables or Disables the Dreadium Reinforced Shield.");
      durabilityEthaxiumShield = getInt("durabilityEthaxiumShield", "moddedAbyssalcraft", 4200, 1, 100000, "Tweaks the durability of the Ethaxium Strengthened Shield.");
      enableEthaxiumShield = getBoolean("enableEthaxiumShield", "moddedAbyssalcraft", true, "Enables or Disables the Ethaxium Strengthened Shield.");
      durabilityThaumiumShield = getInt("durabilityThaumiumShield", "moddedThaumcraft", 1024, 1, 100000, "Tweaks the durability of the Thaumium Plated Shield.");
      enableThaumiumShield = getBoolean("enableThaumiumShield", "moddedThaumcraft", true, "Enables or Disables the Thaumium Plated Shield.");
      durabilityVoidShield = getInt("durabilityVoidShield", "moddedThaumcraft", 225, 1, 100000, "Tweaks the durability of the Void Plated Shield.");
      enableVoidShield = getBoolean("enableVoidShield", "moddedThaumcraft", true, "Enables or Disables the Void Plated Shield.");
      durabilitySoulforgedSteelShield = getInt("durabilitySoulforgedSteelShield", "moddedBWM", 1024, 1, 100000, "Tweaks the durability of the Soulforged Steel Plated Shield.");
      enableSoulforgedSteelShield = getBoolean("enableSoulforgedSteelShield", "moddedBWM", true, "Enables or Disables the Soulforged Steel Plated Shield.");
      enableEnderIOShield = getBoolean("enableEnderIOShield", "moddedPowered", true, "Enables or Disables the Dark Steel Riot Shield.");
      enableRFToolsShield = getBoolean("enableRFToolsShield", "moddedPowered", true, "Enables or Disables the Infused Riot Shield.");
      enableRAShield = getBoolean("enableRAShield", "moddedPowered", true, "Enables or Disables the Flux-Infused Shield.");
      damageToRFMultiplier = getInt("damageToRFMultiplier", "moddedPowered", 50, 1, 100000, "The amount of RF to use per damage point absorbed with RF-powered Shields.");
      damageToFEMultiplier = getInt("damageToFEMultiplier", "moddedPowered", 50, 1, 100000, "The amount of FE to use per damage point absorbed with FE-powered Shields.");
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static void setCategoryLanguageKey(String category) {
      config.getCategory(category).setLanguageKey(String.format("%s.%s:%s.%s", "config", "spartanshields", "category", category));
   }

   public static int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
      return config.getInt(name, category, defaultValue, minValue, maxValue, comment, String.format("%s.%s:%s", "config", "spartanshields", name));
   }

   public static boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
      return config.getBoolean(name, category, defaultValue, comment, String.format("%s.%s:%s", "config", "spartanshields", name));
   }
}
