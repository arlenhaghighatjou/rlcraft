package com.oblivioussp.spartanweaponry.util;

import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.client.gui.AlignmentHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ConfigHandler {
   public static Configuration config;
   public static float damageMaterialCopper = 1.5F;
   public static float damageMaterialTin = 1.75F;
   public static float damageMaterialBronze = 2.0F;
   public static float damageMaterialSteel = 2.5F;
   public static float damageMaterialSilver = 1.5F;
   public static float damageMaterialInvar = 2.2F;
   public static float damageMaterialPlatinum = 3.5F;
   public static float damageMaterialElectrum = 2.0F;
   public static float damageMaterialNickel = 2.0F;
   public static float damageMaterialLead = 2.0F;
   public static int durabilityMaterialCopper = 200;
   public static int durabilityMaterialTin = 180;
   public static int durabilityMaterialBronze = 320;
   public static int durabilityMaterialSteel = 480;
   public static int durabilityMaterialSilver = 48;
   public static int durabilityMaterialInvar = 440;
   public static int durabilityMaterialPlatinum = 1024;
   public static int durabilityMaterialElectrum = 180;
   public static int durabilityMaterialNickel = 200;
   public static int durabilityMaterialLead = 240;
   public static boolean disableMaterialCopper = false;
   public static boolean disableMaterialTin = false;
   public static boolean disableMaterialBronze = false;
   public static boolean disableMaterialSteel = false;
   public static boolean disableMaterialSilver = false;
   public static boolean disableMaterialInvar = false;
   public static boolean disableMaterialPlatinum = false;
   public static boolean disableMaterialElectrum = false;
   public static boolean disableMaterialNickel = false;
   public static boolean disableMaterialLead = false;
   public static int materialPrimaryColourCopper = 16744512;
   public static int materialSecondaryColourCopper = 16760986;
   public static int materialPrimaryColourTin = 12500696;
   public static int materialSecondaryColourTin = 13816575;
   public static int materialPrimaryColourBronze = 11758858;
   public static int materialSecondaryColourBronze = 13407798;
   public static int materialPrimaryColourSteel = 8750469;
   public static int materialSecondaryColourSteel = 12500670;
   public static int materialPrimaryColourSilver = 13487600;
   public static int materialSecondaryColourSilver = 16777215;
   public static int materialPrimaryColourInvar = 11450027;
   public static int materialSecondaryColourInvar = 14607328;
   public static int materialPrimaryColourPlatinum = 6937328;
   public static int materialSecondaryColourPlatinum = 11200511;
   public static int materialPrimaryColourElectrum = 14007119;
   public static int materialSecondaryColourElectrum = 16777109;
   public static int materialPrimaryColourNickel = 14405525;
   public static int materialSecondaryColourNickel = 16250827;
   public static int materialPrimaryColourLead = 5726589;
   public static int materialSecondaryColourLead = 9150162;
   public static float speedDagger = 2.5F;
   public static boolean disableDagger = false;
   public static float damageBaseDagger = 2.5F;
   public static float damageMultiplierDagger = 0.5F;
   public static float speedParryingDagger = 2.5F;
   public static boolean disableParryingDagger = false;
   public static float damageBaseParryingDagger = 2.0F;
   public static float damageMultiplierParryingDagger = 0.5F;
   public static float speedLongsword = 1.4F;
   public static boolean disableLongsword = false;
   public static float damageBaseLongsword = 4.5F;
   public static float damageMultiplierLongsword = 1.5F;
   public static float speedKatana = 2.0F;
   public static boolean disableKatana = false;
   public static float damageBaseKatana = 3.5F;
   public static float damageMultiplierKatana = 0.5F;
   public static float speedSaber = 1.6F;
   public static boolean disableSaber = false;
   public static float damageBaseSaber = 4.5F;
   public static float damageMultiplierSaber = 0.5F;
   public static float speedRapier = 2.4F;
   public static boolean disableRapier = false;
   public static float damageBaseRapier = 2.5F;
   public static float damageMultiplierRapier = 0.5F;
   public static float speedGreatsword = 1.4F;
   public static boolean disableGreatsword = false;
   public static float damageBaseGreatsword = 6.0F;
   public static float damageMultiplierGreatsword = 2.0F;
   public static float speedCaestus = 3.5F;
   public static boolean disableCaestus = false;
   public static float damageBaseCaestus = 2.0F;
   public static float damageMultiplierCaestus = 0.5F;
   public static float speedClub = 1.3F;
   public static boolean disableClub = false;
   public static float damageBaseClub = 4.0F;
   public static float damageMultiplierClub = 1.0F;
   public static float speedHammer = 0.8F;
   public static boolean disableHammer = false;
   public static float damageBaseHammer = 6.0F;
   public static float damageMultiplierHammer = 1.0F;
   public static float speedWarhammer = 1.1F;
   public static boolean disableWarhammer = false;
   public static float damageBaseWarhammer = 4.0F;
   public static float damageMultiplierWarhammer = 1.0F;
   public static float speedSpear = 1.4F;
   public static boolean disableSpear = false;
   public static float damageBaseSpear = 4.5F;
   public static float damageMultiplierSpear = 0.5F;
   public static float speedHalberd = 1.2F;
   public static boolean disableHalberd = false;
   public static float damageBaseHalberd = 6.5F;
   public static float damageMultiplierHalberd = 2.5F;
   public static float speedPike = 1.4F;
   public static boolean disablePike = false;
   public static float damageBasePike = 4.0F;
   public static float damageMultiplierPike = 0.5F;
   public static float speedLance = 1.0F;
   public static boolean disableLance = false;
   public static float damageBaseLance = 4.0F;
   public static float damageMultiplierLance = 1.0F;
   public static boolean disableLongbow = false;
   public static float multiplierLongbow = 1.1F;
   public static boolean woodenLongbowOnly = false;
   public static float materialSpeedModifierLongbow = 0.05F;
   public static int materialDrawModifierLongbow = 1;
   public static boolean disableCrossbow = false;
   public static float crossbowInaccuracyMax = 10.0F;
   public static int crossbowTicksToLoad = 20;
   public static int crossbowTicksCooldown = 8;
   public static float crossbowBaseBoltSpeed = 1.5F;
   public static float crossbowMaterialBoltSpeedModifier = 0.05F;
   public static int crossbowMaterialLoadModifier = 1;
   public static int crossbowMaterialAimModifier = 1;
   public static float boltBaseDamage = 4.0F;
   public static float boltRangeMultiplier = 1.0F;
   public static float boltArmorPiercingFactor = 0.25F;
   public static float boltDiamondBaseDamage = 5.0F;
   public static float boltDiamondRangeMultiplier = 1.25F;
   public static float boltDiamondArmorPiercingFactor = 0.5F;
   public static boolean woodenCrossbowOnly = false;
   public static List<ResourceLocation> enchantmentWhitelistCrossbow = new ArrayList(Defaults.EnchantmentWhitelistCrossbow);
   public static boolean disableThrowingKnife = false;
   public static float meleeSpeedThrowingKnife = 2.5F;
   public static float damageBaseThrowingKnife = 1.5F;
   public static float damageMultiplierThrowingKnife = 0.5F;
   public static int chargeTicksThrowingKnife = 5;
   public static boolean disableThrowingAxe = false;
   public static float meleeSpeedThrowingAxe = 0.9F;
   public static float damageBaseThrowingAxe = 2.0F;
   public static float damageMultiplierThrowingAxe = 1.0F;
   public static int chargeTicksThrowingAxe = 8;
   public static boolean disableJavelin = false;
   public static float meleeSpeedJavelin = 1.2F;
   public static float damageBaseJavelin = 1.5F;
   public static float damageMultiplierJavelin = 0.5F;
   public static int chargeTicksJavelin = 10;
   public static boolean disableBoomerang = false;
   public static boolean woodenBoomerangOnly = false;
   public static float meleeSpeedBoomerang = 1.4F;
   public static float damageBaseBoomerang = 4.0F;
   public static float damageMultiplierBoomerang = 1.0F;
   public static int chargeTicksBoomerang = 5;
   public static float speedBattleaxe = 1.0F;
   public static boolean disableBattleaxe = false;
   public static float damageBaseBattleaxe = 5.0F;
   public static float damageMultiplierBattleaxe = 2.0F;
   public static float speedMace = 1.2F;
   public static boolean disableMace = false;
   public static float damageBaseMace = 4.0F;
   public static float damageMultiplierMace = 1.0F;
   public static float speedGlaive = 1.0F;
   public static boolean disableGlaive = false;
   public static float damageBaseGlaive = 4.0F;
   public static float damageMultiplierGlaive = 1.0F;
   public static float speedQuarterstaff = 2.0F;
   public static boolean disableQuarterstaff = false;
   public static float damageBaseQuarterstaff = 1.5F;
   public static float damageMultiplierQuarterstaff = 1.0F;
   public static boolean disableNewArrows = false;
   public static boolean disableDiamondArrowsAndBolts = false;
   public static boolean disableQuivers = false;
   public static List<String> quiverBowBlacklist = Arrays.asList(Defaults.QuiverArrowBlacklist.clone());
   public static float baseDamageArrowWood = 0.2F;
   public static float rangeMultiplierArrowWood = 1.5F;
   public static float baseDamageArrowIron = 3.0F;
   public static float rangeMultiplierArrowIron = 0.75F;
   public static float baseDamageArrowDiamond = 3.5F;
   public static float rangeMultiplierArrowDiamond = 1.25F;
   public static float explosionStrengthArrowExplosive = 2.0F;
   public static float rangeMultiplierArrowExplosive = 1.0F;
   public static boolean disableExplosives = false;
   public static boolean enableTerrainDamage = true;
   public static float explosionStrengthDynamite = 2.0F;
   public static int fuseTicksDynamite = 60;
   public static float damageBonusChestMultiplier = 2.0F;
   public static float damageBonusRidingMultiplier = 2.0F;
   public static boolean damageBonusRidingCheckSpeed = true;
   public static float damageBonusRidingSpeedMax = 0.225F;
   public static float damageBonusThrowMultiplier = 2.0F;
   public static float damageBonusThrowJavelinMultiplier = 3.0F;
   public static float damageBonusUnarmoredMultiplier = 3.0F;
   public static boolean damageBonusCheckArmorValue = false;
   public static float damageBonusMaxArmorValue = 0.0F;
   public static float damageBonusUndeadMultiplier = 1.5F;
   public static float damageBonusBackstabMultiplier = 3.0F;
   public static float damageAbsorptionFactor = 0.25F;
   public static float twoHanded1Value = 0.5F;
   public static float twoHanded2Value = 0.75F;
   public static float reach1Value = 6.0F;
   public static float reach2Value = 7.0F;
   public static float sweep1Percentage = 50.0F;
   public static float sweep2Percentage = 100.0F;
   public static float armorPiercePercentage = 50.0F;
   public static int quickStrikeHurtResistTicks = 14;
   public static boolean enableExperimentalWeapons = false;
   public static boolean enableModdedMaterialWeapons = true;
   public static boolean forceDisableUncraftableTooltips = false;
   private static final String[] VALID_ALIGNMENT_VALUES = AlignmentHelper.Alignment.getValidConfigValues();
   public static AlignmentHelper.Alignment quiverHudAlignment = AlignmentHelper.Alignment.BOTTOM_CENTER;
   public static int quiverHudOffsetX = 0;
   public static int quiverHudOffsetY = 0;
   public static boolean disableNewCrosshairCrossbow = true;
   public static boolean disableNewCrosshairThrowingWeapon = true;
   public static boolean forceCompatibilityCrosshairs = false;
   public static boolean disableFOVZoomIn = false;
   public static boolean disableSpartanHUDBaublesIntegration = false;
   public static final String categoryMaterials = "materials";
   public static final String categoryDagger = "dagger";
   public static final String categoryParryingDagger = "parrying_dagger";
   public static final String categoryLongsword = "longsword";
   public static final String categoryKatana = "katana";
   public static final String categorySaber = "saber";
   public static final String categoryRapier = "rapier";
   public static final String categoryGreatsword = "greatsword";
   public static final String categoryCaestus = "caestus";
   public static final String categoryClub = "club";
   public static final String categoryHammer = "hammer";
   public static final String categoryWarhammer = "warhammer";
   public static final String categorySpear = "spear";
   public static final String categoryHalberd = "halberd";
   public static final String categoryPike = "pike";
   public static final String categoryLance = "lance";
   public static final String categoryLongbow = "longbow";
   public static final String categoryCrossbow = "crossbow";
   public static final String categoryThrowingKnife = "throwing_knife";
   public static final String categoryThrowingAxe = "throwing_axe";
   public static final String categoryJavelin = "javelin";
   public static final String categoryBoomerang = "boomerang";
   public static final String categoryBattleaxe = "battleaxe";
   public static final String categoryMace = "mace";
   public static final String categoryGlaive = "glaive";
   public static final String categoryQuarterstaff = "quarterstaff";
   public static final String categoryArrows = "arrows";
   public static final String categoryExplosives = "explosives";
   public static final String categoryWeaponProperties = "weapon_properties";
   public static final String[] categories = new String[]{"general", "client", "materials", "dagger", "parrying_dagger", "longsword", "katana", "saber", "rapier", "greatsword", "caestus", "club", "hammer", "warhammer", "spear", "halberd", "pike", "lance", "longbow", "crossbow", "throwing_knife", "throwing_axe", "javelin", "boomerang", "battleaxe", "mace", "glaive", "quarterstaff", "arrows", "explosives", "weapon_properties"};

   public ConfigHandler() {
   }

   public static void init(File configDirectory) {
      File configFile = new File(configDirectory, "spartanweaponry".toLowerCase() + ".cfg");
      if (config == null) {
         config = new Configuration(configFile, "2.0");
         Log.info("Defined config version: " + config.getDefinedConfigVersion() + " - Loaded config version: " + config.getLoadedConfigVersion());
         if (!config.getDefinedConfigVersion().equalsIgnoreCase(config.getLoadedConfigVersion())) {
            String oldVersion = config.getLoadedConfigVersion() == null ? "1.0" : config.getLoadedConfigVersion();
            Log.warn("The loaded config file is from an old version of this mod (beta-1.2.x or older), and is no longer valid! This config will be moved to \"config/backup/spartanweaponry_" + oldVersion + ".cfg\" and a new one will be generated!");
            File backupConfigDir = new File(configDirectory, "backup");
            File backupConfig = new File(backupConfigDir, "spartanweaponry".toLowerCase() + "_" + oldVersion + ".cfg");
            backupConfigDir.mkdirs();
            config.getConfigFile().renameTo(backupConfig);
            config = new Configuration(configFile, "2.0");
         }

         loadConfig();

         for(String category : categories) {
            setCategoryLanguageKey(category);
            Configuration var10001 = config;
            if (category != "general") {
               var10001 = config;
               if (category != "client") {
                  config.setCategoryRequiresMcRestart(category, true);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void onConfigChangedEvent(OnConfigChangedEvent ev) {
      if (ev.getModID().equalsIgnoreCase("spartanweaponry")) {
         loadConfig();
         validateCrossbowEnchantmentWhitelist();
      }

   }

   private static void loadConfig() {
      damageMaterialCopper = getFloat("damageBaseCopper", "materials", 1.5F, 0.0F, 100.0F, "Base material damage for Copper weapons");
      damageMaterialTin = getFloat("damageBaseTin", "materials", 1.75F, 0.0F, 100.0F, "Base material damage for Tin weapons");
      damageMaterialBronze = getFloat("damageBaseBronze", "materials", 2.0F, 0.0F, 100.0F, "Base material damage for Bronze weapons");
      damageMaterialSteel = getFloat("damageBaseSteel", "materials", 2.5F, 0.0F, 100.0F, "Base material damage for Steel weapons");
      damageMaterialSilver = getFloat("damageBaseSilver", "materials", 1.5F, 0.0F, 100.0F, "Base material damage for Silver weapons");
      damageMaterialInvar = getFloat("damageBaseInvar", "materials", 2.2F, 0.0F, 100.0F, "Base material damage for Invar weapons");
      damageMaterialPlatinum = getFloat("damageBasePlatinum", "materials", 3.5F, 0.0F, 100.0F, "Base material damage for Platinum weapons");
      damageMaterialElectrum = getFloat("damageBaseElectrum", "materials", 2.0F, 0.0F, 100.0F, "Base material damage for Electrum weapons");
      damageMaterialNickel = getFloat("damageBaseNickel", "materials", 2.0F, 0.0F, 100.0F, "Base material damage for Nickel weapons");
      damageMaterialLead = getFloat("damageBaseLead", "materials", 2.0F, 0.0F, 100.0F, "Base material damage for Lead weapons");
      durabilityMaterialCopper = getInt("durabilityBaseCopper", "materials", 200, 1, 1000000, "Base durability for Copper weapons");
      durabilityMaterialTin = getInt("durabilityBaseTin", "materials", 180, 1, 1000000, "Base durability for Tin weapons");
      durabilityMaterialBronze = getInt("durabilityBaseBronze", "materials", 320, 1, 1000000, "Base durability for Bronze weapons");
      durabilityMaterialSteel = getInt("durabilityBaseSteel", "materials", 480, 1, 1000000, "Base durability for Steel weapons");
      durabilityMaterialSilver = getInt("durabilityBaseSilver", "materials", 48, 1, 1000000, "Base durability for Silver weapons");
      durabilityMaterialInvar = getInt("durabilityBaseInvar", "materials", 440, 1, 1000000, "Base durability for Invar weapons");
      durabilityMaterialPlatinum = getInt("durabilityBasePlatinum", "materials", 1024, 1, 1000000, "Base durability for Platinum weapons");
      durabilityMaterialElectrum = getInt("durabilityBaseElectrum", "materials", 180, 1, 1000000, "Base durability for Electrum weapons");
      durabilityMaterialNickel = getInt("durabilityBaseNickel", "materials", 200, 1, 1000000, "Base durability for Nickel weapons");
      durabilityMaterialLead = getInt("durabilityBaseLead", "materials", 240, 1, 1000000, "Base durability for Lead weapons");
      disableMaterialCopper = getBoolean("disableCopper", "materials", false, "Disables all Copper-based weapons");
      disableMaterialTin = getBoolean("disableTin", "materials", false, "Disables all Tin-based weapons");
      disableMaterialBronze = getBoolean("disableBronze", "materials", false, "Disables all Bronze-based weapons");
      disableMaterialSteel = getBoolean("disableSteel", "materials", false, "Disables all Steel-based weapons");
      disableMaterialSilver = getBoolean("disableSilver", "materials", false, "Disables all Silver-based weapons");
      disableMaterialInvar = getBoolean("disableInvar", "materials", false, "Disables all Invar-based weapons");
      disableMaterialPlatinum = getBoolean("disablePlatinum", "materials", false, "Disables all Platinum-based weapons");
      disableMaterialElectrum = getBoolean("disableElectrum", "materials", false, "Disables all Electrum-based weapons");
      disableMaterialNickel = getBoolean("disableNickel", "materials", false, "Disables all Nickel-based weapons");
      disableMaterialLead = getBoolean("disableLead", "materials", false, "Disables all Lead-based weapons");
      materialPrimaryColourCopper = getInt("primaryColourCopper", "materials", 16744512, 0, 16777215, "Primary Colour for all Copper-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourCopper = getInt("secondaryColourCopper", "materials", 16760986, 0, 16777215, "Secondary Colour for all Copper-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourTin = getInt("primaryColourTin", "materials", 12500696, 0, 16777215, "Primary Colour for all Tin-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourTin = getInt("secondaryColourTin", "materials", 13816575, 0, 16777215, "Secondary Colour for all Tin-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourBronze = getInt("primaryColourBronze", "materials", 11758858, 0, 16777215, "Primary Colour for all Bronze-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourBronze = getInt("secondaryColourBronze", "materials", 13407798, 0, 16777215, "Secondary Colour for all Bronze-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourSteel = getInt("primaryColourSteel", "materials", 8750469, 0, 16777215, "Primary Colour for all Steel-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourSteel = getInt("secondaryColourSteel", "materials", 12500670, 0, 16777215, "Secondary Colour for all Steel-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourSilver = getInt("primaryColourSilver", "materials", 13487600, 0, 16777215, "Primary Colour for all Silver-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourSilver = getInt("secondaryColourSilver", "materials", 16777215, 0, 16777215, "Secondary Colour for all Silver-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourInvar = getInt("primaryColourInvar", "materials", 11450027, 0, 16777215, "Primary Colour for all Invar-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourInvar = getInt("secondaryColourInvar", "materials", 14607328, 0, 16777215, "Secondary Colour for all Invar-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourPlatinum = getInt("primaryColourPlatinum", "materials", 6937328, 0, 16777215, "Primary Colour for all Platinum-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourPlatinum = getInt("secondaryColourPlatinum", "materials", 11200511, 0, 16777215, "Secondary Colour for all Platinum-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourElectrum = getInt("primaryColourElectrum", "materials", 14007119, 0, 16777215, "Primary Colour for all Electrum-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourElectrum = getInt("secondaryColourElectrum", "materials", 16777109, 0, 16777215, "Secondary Colour for all Electrum-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourNickel = getInt("primaryColourNickel", "materials", 14405525, 0, 16777215, "Primary Colour for all Nickel-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourNickel = getInt("secondaryColourNickel", "materials", 16250827, 0, 16777215, "Secondary Colour for all Nickel-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialPrimaryColourLead = getInt("primaryColourLead", "materials", 5726589, 0, 16777215, "Primary Colour for all Lead-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      materialSecondaryColourLead = getInt("secondaryColourLead", "materials", 9150162, 0, 16777215, "Secondary Colour for all Lead-based weapons.\nColour values can be retrieved using a colour picker and copying its HTML notation value and converting it to decimal");
      disableDagger = getBoolean("disableWeapon", "dagger", false, "Disables all weapons of this type in this mod");
      damageMultiplierDagger = getFloat("damageMultiplier", "dagger", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseDagger = getFloat("damageBase", "dagger", 2.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedDagger = getFloat("speed", "dagger", 2.5F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableParryingDagger = getBoolean("disableWeapon", "parrying_dagger", false, "Disables all weapons of this type in this mod");
      damageMultiplierParryingDagger = getFloat("damageMultiplier", "parrying_dagger", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseParryingDagger = getFloat("damageBase", "parrying_dagger", 2.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedParryingDagger = getFloat("speed", "parrying_dagger", 2.5F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableLongsword = getBoolean("disableWeapon", "longsword", false, "Disables all weapons of this type in this mod");
      damageMultiplierLongsword = getFloat("damageMultiplier", "longsword", 1.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseLongsword = getFloat("damageBase", "longsword", 4.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedLongsword = getFloat("speed", "longsword", 1.4F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableKatana = getBoolean("disableWeapon", "katana", false, "Disables all weapons of this type in this mod");
      damageMultiplierKatana = getFloat("damageMultiplier", "katana", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseKatana = getFloat("damageBase", "katana", 3.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedKatana = getFloat("speed", "katana", 2.0F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableSaber = getBoolean("disableWeapon", "saber", false, "Disables all weapons of this type in this mod");
      damageMultiplierSaber = getFloat("damageMultiplier", "saber", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseSaber = getFloat("damageBase", "saber", 4.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedSaber = getFloat("speed", "saber", 1.6F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableRapier = getBoolean("disableWeapon", "rapier", false, "Disables all weapons of this type in this mod");
      damageMultiplierRapier = getFloat("damageMultiplier", "rapier", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseRapier = getFloat("damageBase", "rapier", 2.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedRapier = getFloat("speed", "rapier", 2.4F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableGreatsword = getBoolean("disableWeapon", "greatsword", false, "Disables all weapons of this type in this mod");
      damageMultiplierGreatsword = getFloat("damageMultiplier", "greatsword", 2.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseGreatsword = getFloat("damageBase", "greatsword", 6.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedGreatsword = getFloat("speed", "greatsword", 1.4F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableCaestus = getBoolean("disableWeapon", "caestus", false, "Disables the Caestus and Studded Caestus");
      damageMultiplierCaestus = getFloat("damageMultiplier", "caestus", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseCaestus = getFloat("damageBase", "caestus", 2.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedCaestus = getFloat("speed", "caestus", 3.5F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableClub = getBoolean("disableWeapon", "club", false, "Disables the Club and Studded Club");
      damageMultiplierClub = getFloat("damageMultiplier", "club", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseClub = getFloat("damageBase", "club", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedClub = getFloat("speed", "club", 1.3F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableHammer = getBoolean("disableWeapon", "hammer", false, "Disables all weapons of this type in this mod");
      damageMultiplierHammer = getFloat("damageMultiplier", "hammer", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseHammer = getFloat("damageBase", "hammer", 6.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedHammer = getFloat("speed", "hammer", 0.8F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableWarhammer = getBoolean("disableWeapon", "warhammer", false, "Disables all weapons of this type in this mod");
      damageMultiplierWarhammer = getFloat("damageMultiplier", "warhammer", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseWarhammer = getFloat("damageBase", "warhammer", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedWarhammer = getFloat("speed", "warhammer", 1.1F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableSpear = getBoolean("disableWeapon", "spear", false, "Disables all weapons of this type in this mod");
      damageMultiplierSpear = getFloat("damageMultiplier", "spear", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseSpear = getFloat("damageBase", "spear", 4.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedSpear = getFloat("speed", "spear", 1.4F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableHalberd = getBoolean("disableWeapon", "halberd", false, "Disables all weapons of this type in this mod");
      damageMultiplierHalberd = getFloat("damageMultiplier", "halberd", 2.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseHalberd = getFloat("damageBase", "halberd", 6.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedHalberd = getFloat("speed", "halberd", 1.2F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disablePike = getBoolean("disableWeapon", "pike", false, "Disables all weapons of this type in this mod");
      damageMultiplierPike = getFloat("damageMultiplier", "pike", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBasePike = getFloat("damageBase", "pike", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedPike = getFloat("speed", "pike", 1.4F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableLance = getBoolean("disableWeapon", "lance", false, "Disables all weapons of this type in this mod");
      damageMultiplierLance = getFloat("damageMultiplier", "lance", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseLance = getFloat("damageBase", "lance", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedLance = getFloat("speed", "lance", 1.0F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableLongbow = getBoolean("disableWeapon", "longbow", false, "Disables all weapons of this type in this mod");
      woodenLongbowOnly = getBoolean("woodenLongbowOnly", "longbow", false, "Disables all Longbows except the Wooden one in this mod");
      multiplierLongbow = getFloat("multiplierLongbow", "longbow", 1.1F, 0.1F, 100.0F, "Default Draw time and Arrow speed multiplier vs. the vanilla bow. Also affects damage of arrows shot. The Wooden Longbow will have these values by default");
      materialSpeedModifierLongbow = getFloat("materialVelocityModifierLongbow", "longbow", 0.05F, 0.01F, 10.0F, "Longbow Arrow speed modifier per material tier (defined by harvest level)");
      materialDrawModifierLongbow = getInt("materialDrawModifierLongbow", "longbow", 1, -10, 10, "Longbow draw tick modifier per material tier (defined by harvest level)");
      disableCrossbow = getBoolean("disableWeapon", "crossbow", false, "Disables all weapons of this type in this mod");
      crossbowBaseBoltSpeed = getFloat("baseBoltSpeedCrossbow", "crossbow", 1.5F, 0.01F, 10.0F, "Base Bolt speed for the Crossbow (Wooden Crossbows will have this as the default value)");
      crossbowMaterialBoltSpeedModifier = getFloat("materialVelocityModifierCrossbow", "crossbow", 0.05F, 0.01F, 10.0F, "Crossbow Bolt speed modifier per material tier (defined by harvest level)");
      crossbowMaterialLoadModifier = getInt("materialLoadModifierCrossbow", "crossbow", 1, -10, 10, "Crossbow load tick modifier per material tier (defined by harvest level)");
      crossbowMaterialAimModifier = getInt("materialAimModifierCrossbow", "crossbow", 1, -10, 10, "Crossbow aiming tick modifier per material tier (defined by harvest level)");
      boltBaseDamage = getFloat("boltDamage", "crossbow", 4.0F, 1.0F, 100.0F, "Base damage of a standard Crossbow Bolt");
      boltRangeMultiplier = getFloat("boltRangeMultiplier", "crossbow", 1.0F, 0.1F, 100.0F, "Range Multiplier for standard and Spectral Crossbow Bolts");
      boltArmorPiercingFactor = getFloat("boltArmorPiercingFactor", "crossbow", 0.25F, 0.01F, 1.0F, "Armour Piercing factor (which determines damage that ignores armour) for standard and Spectral Crossbow Bolts");
      boltDiamondBaseDamage = getFloat("boltDiamondDamage", "crossbow", 5.0F, 1.0F, 100.0F, "Base damage of Diamond Crossbow Bolts");
      boltDiamondRangeMultiplier = getFloat("boltDiamondRangeMultiplier", "crossbow", 1.25F, 0.1F, 100.0F, "Range Multiplier for Diamond Crossbow Bolts");
      boltDiamondArmorPiercingFactor = getFloat("boltDiamondArmorPiercingFactor", "crossbow", 0.5F, 0.01F, 1.0F, "Armour Piercing factor (which determines damage that ignores armour) for Diamond Crossbow Bolts");
      woodenCrossbowOnly = getBoolean("woodenCrossbowOnly", "crossbow", false, "Disables all Crossbows except the Wooden one in this mod.");
      String[] enchWhitelistCrossbowArray = getStringList("enchantmentWhitelist", "crossbow", Defaults.EnchantmentWhitelistArray, "List to allow modded enchantments to be used on the Crossbow. Note that this will allow *any* enchantment to be placed on a Crossbow, even those that might not work properly. Make sure to test the enchantments out first!");
      enchantmentWhitelistCrossbow = new ArrayList();
      Arrays.stream(enchWhitelistCrossbowArray).forEach((str) -> {
         ResourceLocation loc = new ResourceLocation(str);
         enchantmentWhitelistCrossbow.add(loc);
      });
      disableThrowingKnife = getBoolean("disableWeapon", "throwing_knife", false, "Disables all weapons of this type in this mod");
      damageMultiplierThrowingKnife = getFloat("damageMultiplier", "throwing_knife", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseThrowingKnife = getFloat("damageBase", "throwing_knife", 1.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      meleeSpeedThrowingKnife = getFloat("meleeSpeed", "throwing_knife", 2.5F, 0.1F, 20.0F, "Affects the overall speed of melee attacks with this weapon");
      chargeTicksThrowingKnife = getInt("chargeTicks", "throwing_knife", 5, 2, 1000, "Charging time for throwing this weapon. This affects the throwing speed, which affects the throwing range too");
      disableThrowingAxe = getBoolean("disableWeapon", "throwing_axe", false, "Disables all weapons of this type in this mod");
      damageMultiplierThrowingAxe = getFloat("damageMultiplier", "throwing_axe", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseThrowingAxe = getFloat("damageBase", "throwing_axe", 2.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      meleeSpeedThrowingAxe = getFloat("meleeSpeed", "throwing_axe", 0.9F, 0.1F, 20.0F, "Affects the overall speed of melee attacks with this weapon");
      chargeTicksThrowingAxe = getInt("chargeTicks", "throwing_axe", 8, 2, 1000, "Charging time for throwing this weapon. This affects the throwing speed, which affects the throwing range too");
      disableJavelin = getBoolean("disableWeapon", "javelin", false, "Disables all weapons of this type in this mod");
      damageMultiplierJavelin = getFloat("damageMultiplier", "javelin", 0.5F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseJavelin = getFloat("damageBase", "javelin", 1.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      meleeSpeedJavelin = getFloat("meleeSpeed", "javelin", 1.2F, 0.1F, 20.0F, "Affects the overall speed of melee attacks with this weapon");
      chargeTicksJavelin = getInt("chargeTicks", "javelin", 10, 2, 1000, "Charging time for throwing this weapon. This affects the throwing speed, which affects the throwing range too");
      disableBoomerang = getBoolean("disableWeapon", "boomerang", false, "Disables all weapons of this type in this mod");
      woodenBoomerangOnly = getBoolean("woodenBoomerangOnly", "boomerang", false, "Disables all Boomerangs except the Wooden one in this mod.");
      damageMultiplierBoomerang = getFloat("damageMultiplier", "boomerang", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseBoomerang = getFloat("damageBase", "boomerang", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      meleeSpeedBoomerang = getFloat("meleeSpeed", "boomerang", 1.4F, 0.1F, 20.0F, "Affects the overall speed of melee attacks with this weapon");
      chargeTicksBoomerang = getInt("chargeTicks", "boomerang", 5, 2, 1000, "Charging time for throwing this weapon. This affects the throwing speed, which affects the throwing range too");
      disableBattleaxe = getBoolean("disableWeapon", "battleaxe", false, "Disables all weapons of this type in this mod");
      damageMultiplierBattleaxe = getFloat("damageMultiplier", "battleaxe", 2.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseBattleaxe = getFloat("damageBase", "battleaxe", 5.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedBattleaxe = getFloat("speed", "battleaxe", 1.0F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableMace = getBoolean("disableWeapon", "mace", false, "Disables all weapons of this type in this mod");
      damageMultiplierMace = getFloat("damageMultiplier", "mace", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseMace = getFloat("damageBase", "mace", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedMace = getFloat("speed", "mace", 1.2F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableGlaive = getBoolean("disableWeapon", "glaive", false, "Disables all weapons of this type in this mod");
      damageMultiplierGlaive = getFloat("damageMultiplier", "glaive", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseGlaive = getFloat("damageBase", "glaive", 4.0F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedGlaive = getFloat("speed", "glaive", 1.0F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableQuarterstaff = getBoolean("disableWeapon", "quarterstaff", false, "Disables all weapons of this type in this mod");
      damageMultiplierQuarterstaff = getFloat("damageMultiplier", "quarterstaff", 1.0F, 0.1F, 100.0F, "Damage Multiplier. Final damage of weapons are calculated with the formula: ([WeaponMultiplier] * [BaseMaterialDamage]) + [WeaponBaseDamage]");
      damageBaseQuarterstaff = getFloat("damageBase", "quarterstaff", 1.5F, 0.1F, 100.0F, "Base Damage. Will be the same damage as the Wooden version of this weapon");
      speedQuarterstaff = getFloat("speed", "quarterstaff", 2.0F, 0.1F, 20.0F, "Affects the overall speed of attacks with this weapon");
      disableNewArrows = getBoolean("disableNewArrows", "arrows", false, "Disables all new Arrows in this mod");
      disableDiamondArrowsAndBolts = getBoolean("disableDiamondArrowsAndBolts", "arrows", false, "Disables Diamond Arrows and Bolts in this mod");
      disableQuivers = getBoolean("disableQuivers", "arrows", false, "Disables all variants of the Arrow Quiver and the Bolt Quiver in this mod");
      quiverBowBlacklist = Arrays.asList(getStringList("quiverBowBlacklist", "arrows", (String[])Defaults.QuiverArrowBlacklist.clone(), "Bows in this blacklist will not get Arrows pulled out of the Arrow Quiver. Use the ID of the bow to add to this. e.g. \"minecraft:bow\""));
      baseDamageArrowWood = getFloat("baseDamageArrowWood", "arrows", 0.2F, 0.1F, 100.0F, "Base Damage for Wooden Arrows");
      rangeMultiplierArrowWood = getFloat("rangeMultiplierArrowWood", "arrows", 1.5F, 0.1F, 100.0F, "Range Multiplier for Wooden Arrows");
      baseDamageArrowIron = getFloat("baseDamageArrowIron", "arrows", 3.0F, 0.1F, 100.0F, "Base Damage for Iron Arrows");
      rangeMultiplierArrowIron = getFloat("rangeMultiplierArrowIron", "arrows", 0.75F, 0.1F, 100.0F, "Range Multiplier for Iron Arrows");
      baseDamageArrowDiamond = getFloat("baseDamageArrowDiamond", "arrows", 3.5F, 0.1F, 100.0F, "Base Damage for Diamond Arrows");
      rangeMultiplierArrowDiamond = getFloat("rangeMultiplierArrowDiamond", "arrows", 1.25F, 0.1F, 100.0F, "Range Multiplier for Diamond Arrows");
      explosionStrengthArrowExplosive = getFloat("explosionStrengthArrowExplosive", "arrows", 2.0F, 0.1F, 10.0F, "Explosion strength for Explosive Arrows");
      rangeMultiplierArrowExplosive = getFloat("rangeMultiplierArrowExplosive", "arrows", 1.0F, 0.1F, 100.0F, "Range Multiplier for Explosive Arrows");
      disableExplosives = getBoolean("disableExplosives", "explosives", false, "Disables all Explosives in this mod");
      fuseTicksDynamite = getInt("fuseTicksDynamite", "explosives", 60, 1, 600, "Time (in ticks) it takes for Dynamite to explode");
      explosionStrengthDynamite = getFloat("explosionStrengthDynamite", "explosives", 2.0F, 0.1F, 10.0F, "Explosion strength for Dynamite");
      enableTerrainDamage = getBoolean("enableTerrainDamage", "explosives", true, "Enables terrain damage for explosives in this mod such as Dynamite and Explosive Arrows. Respects the 'mobGriefing' gamerule.");
      damageBonusChestMultiplier = getFloat("damageBonusChestMultiplier", "weapon_properties", 2.0F, 1.0F, 50.0F, "Changes the \"Chest Damage Bonus\" Weapon Property multiplier value");
      damageBonusRidingMultiplier = getFloat("damageBonusRidingMultiplier", "weapon_properties", 2.0F, 1.0F, 50.0F, "Changes the \"Riding Damage Bonus\" Weapon Property multiplier value");
      damageBonusRidingCheckSpeed = getBoolean("damageBonusRidingCheckSpeed", "weapon_properties", true, "If true, checks the speed of the player/mount to scale the \"Riding Damage Bonus\" against. Might not be fully compatible with some mods, so it can be disabled if necessary");
      damageBonusRidingSpeedMax = getFloat("damageBonusRidingVelocityMax", "weapon_properties", 0.225F, 0.0F, 10.0F, "Changes the speed required for maximum \"Riding Damage Bonus\" to take effect. Adjusting this value scales how much bonus damage is received");
      damageBonusThrowMultiplier = getFloat("damageBonusThrowMultiplier", "weapon_properties", 2.0F, 1.0F, 50.0F, "Changes the \"Throwing Damage Bonus\" (except for Javelins) Weapon Property multiplier value");
      damageBonusThrowJavelinMultiplier = getFloat("damageBonusThrowJavelinMultiplier", "weapon_properties", 3.0F, 1.0F, 50.0F, "Changes the \"Throwing Damage Bonus\" (for Javelins) Weapon Property multiplier value");
      damageBonusUnarmoredMultiplier = getFloat("damageBonusUnarmoredMultiplier", "weapon_properties", 3.0F, 1.0F, 50.0F, "Changes the \"Unarmored Damage Bonus\" Weapon Property multiplier value");
      damageBonusCheckArmorValue = getBoolean("damageBonusCheckArmorValue", "weapon_properties", false, "If set to true, any damage bonus that checks for armor will only apply if the hit mob has less than the total armor value threshold, while still checking for armor.");
      damageBonusMaxArmorValue = getFloat("damageBonusMaxArmorValue", "weapon_properties", 0.0F, 0.0F, 100.0F, "Max armor value allowed for any damage bonus that checks for armor to apply, without any armor equipped");
      damageBonusUndeadMultiplier = getFloat("damageBonusUndeadMultiplier", "weapon_properties", 1.5F, 1.0F, 50.0F, "Changes the \"Undead Damage Bonus\" Weapon Property damage multiplier value");
      damageBonusBackstabMultiplier = getFloat("damageBonusBackstabMultiplier", "weapon_properties", 3.0F, 1.0F, 50.0F, "Changes the \"Backstab Damage Bonus\" Weapon Property damage multiplier value");
      damageAbsorptionFactor = getFloat("damageAbsorptionFactor", "weapon_properties", 0.25F, 0.0F, 1.0F, "Changes the percentage of damage absorbed by the \"Damage Absorption\" Weapon Property");
      twoHanded1Value = getFloat("twoHanded1Value", "weapon_properties", 0.5F, 0.0F, 1.0F, "Changes the damage reduction applied on weapons with the \"Two-Handed I\" Weapon Property");
      twoHanded2Value = getFloat("twoHanded2Value", "weapon_properties", 0.75F, 0.0F, 1.0F, "Changes the damage reduction applied on weapons with the \"Two-Handed II\" Weapon Property");
      reach1Value = getFloat("reach1Value", "weapon_properties", 6.0F, 5.0F, 15.0F, "Changes the reach of any weapons with the \"Reach I\" Weapon Property");
      reach2Value = getFloat("reach2Value", "weapon_properties", 7.0F, 5.0F, 15.0F, "Changes the reach of any weapons withs the \"Reach II\" Weapon Property");
      sweep1Percentage = getFloat("sweep1Percentage", "weapon_properties", 50.0F, 0.0F, 100.0F, "Changes the percentage of damage inflicted to enemies when sweep attacked on weapons with the \"Sweep I\" Weapon Property");
      sweep2Percentage = getFloat("sweep2Percentage", "weapon_properties", 100.0F, 0.0F, 100.0F, "Changes the percentage of damage inflicted to enemies when sweep attacked on weapons with the \"Sweep II\" Weapon Property");
      armorPiercePercentage = getFloat("armorPiercePercentage", "weapon_properties", 50.0F, 0.0F, 100.0F, "Changes the percentage of damage that ignores armor on weapons with the \"Armor Piercing\" Weapon Property");
      quickStrikeHurtResistTicks = getInt("quickStrikeHurtResistTicks", "weapon_properties", 14, 10, 20, "Tweaks the hurt resistance ticks for weapons that use the \"Quick Strike\" Weapon Property");
      WeaponProperties.EXTRA_DAMAGE_2_CHEST.setMagnitude(damageBonusChestMultiplier);
      WeaponProperties.EXTRA_DAMAGE_2_RIDING.setMagnitude(damageBonusRidingMultiplier);
      WeaponProperties.EXTRA_DAMAGE_2_THROWN.setMagnitude(damageBonusThrowMultiplier);
      WeaponProperties.EXTRA_DAMAGE_3_THROWN.setMagnitude(damageBonusThrowJavelinMultiplier);
      WeaponProperties.EXTRA_DAMAGE_3_NO_ARMOUR.setMagnitude(damageBonusUnarmoredMultiplier);
      WeaponProperties.EXTRA_DAMAGE_50P_UNDEAD.setMagnitude(damageBonusUndeadMultiplier);
      WeaponProperties.EXTRA_DAMAGE_BACKSTAB.setMagnitude(damageBonusBackstabMultiplier);
      WeaponProperties.DAMAGE_ABSORB_25.setMagnitude(damageAbsorptionFactor);
      WeaponProperties.TWO_HANDED_1.setMagnitude(twoHanded1Value);
      WeaponProperties.TWO_HANDED_2.setMagnitude(twoHanded2Value);
      WeaponProperties.REACH_1.setMagnitude(reach1Value);
      WeaponProperties.REACH_2.setMagnitude(reach2Value);
      WeaponProperties.SWEEP_DAMAGE_HALF.setMagnitude(sweep1Percentage);
      WeaponProperties.SWEEP_DAMAGE_FULL.setMagnitude(sweep2Percentage);
      WeaponProperties.ARMOUR_PIERCING_50.setMagnitude(armorPiercePercentage);
      enableExperimentalWeapons = getBoolean("enableExperimentalWeapons", "general", false, "Deprecated! No longers enables Parrying Daggers, as they are not experimental anymore. Addon mods such as Spartan and Fire still use this, and removing it causes a crash. Enable to use addon mods' versions of the Parrying Dagger");
      enableModdedMaterialWeapons = getBoolean("enableModdedMaterialWeapons", "general", true, "Enables weapons made from modded materials. Setting this to false will disable registration of said weapons, meaning only vanilla weapons will be available");
      forceDisableUncraftableTooltips = getBoolean("forceDisableUncraftableTooltips", "general", false, "Setting to true will disable all uncraftable weapon tooltips. Useful for modpack makers, or for those particular mods which recipes work, but the tooltips are inaccurate");
      String quiverHudAlignStr = getStringFromFixedValues("quiverHudAlignment", "client", VALID_ALIGNMENT_VALUES[AlignmentHelper.Alignment.BOTTOM_CENTER.ordinal()], "Sets where the Quiver HUD Element should be aligned", VALID_ALIGNMENT_VALUES);
      quiverHudAlignment = AlignmentHelper.Alignment.valueOf(quiverHudAlignStr.toUpperCase());
      quiverHudOffsetX = getInt("quiverHudOffsetX", "client", -138, -400, 400, "Sets where on the X-axis the Quiver HUD element should be off-set from it's alignment point.");
      quiverHudOffsetY = getInt("quiverHudOffsetY", "client", 65, -400, 400, "Sets where on the Y-axis the Quiver HUD element should be off-set from it's alignment point.");
      disableNewCrosshairCrossbow = getBoolean("disableNewCrosshairCrossbow", "client", false, "Set to true to disable a Crosshair for the Crossbow which visually shows inaccuracy, using the default Crosshair instead; false otherwise");
      disableNewCrosshairThrowingWeapon = getBoolean("disableNewCrosshairThrowingWeapon", "client", false, "Set to true to disable a Crosshair for Throwing Weapons which show the charge for them, using the default Crosshair instead; false otherwise");
      forceCompatibilityCrosshairs = getBoolean("forceCompatibilityCrosshairs", "client", false, "Set to force compatibility crosshairs for Crosshairs and Throwing Weapons. This won't work if the new crosshairs are disabled");
      disableFOVZoomIn = getBoolean("disableFOVZoomIn", "client", false, "Set to disable the FOV zoom in effect when aiming a Longbow, Crossbow or Throwing Weapons");
      disableSpartanHUDBaublesIntegration = getBoolean("disableSpartanHUDBaublesIntegration", "client", false, "Set to disable Spartan HUD: Baubles integration. This will prevent the info the Quiver HUD has from being rendered on the Bauble HUD, and will reenable the Quiver HUD as well");
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static void validateCrossbowEnchantmentWhitelist() {
      List<String> invalidValues = new ArrayList();
      enchantmentWhitelistCrossbow.forEach((rLoc) -> {
         if (!ForgeRegistries.ENCHANTMENTS.containsKey(rLoc)) {
            invalidValues.add(rLoc.toString());
         }

      });
      if (!invalidValues.isEmpty()) {
         Log.warn("Found " + invalidValues.size() + " invalid enchantment whitelist values! Either the mod they come from is not loaded, a typo occurred, or it doesn't exist!");
         invalidValues.forEach((str) -> {
            Log.warn(" - " + str);
         });
      }

   }

   public static void setCategoryLanguageKey(String category) {
      config.getCategory(category).setLanguageKey(String.format("config.%s:%s.%s", "spartanweaponry", "category", category));
   }

   public static float getFloat(String name, String category, float defaultValue, float minValue, float maxValue, String comment) {
      return config.getFloat(name, category, defaultValue, minValue, maxValue, comment, String.format("config.%s:%s", "spartanweaponry", name));
   }

   public static int getInt(String name, String category, int defaultValue, int minValue, int maxValue, String comment) {
      return config.getInt(name, category, defaultValue, minValue, maxValue, comment, String.format("config.%s:%s", "spartanweaponry", name));
   }

   public static boolean getBoolean(String name, String category, boolean defaultValue, String comment) {
      return config.getBoolean(name, category, defaultValue, comment, String.format("config.%s:%s", "spartanweaponry", name));
   }

   public static String getStringFromFixedValues(String name, String category, String defaultValue, String comment, String[] validValues) {
      return config.getString(name, category, defaultValue, comment, validValues, String.format("config.%s:%s", "spartanweaponry", name));
   }

   public static String[] getStringList(String name, String category, String[] defaultValue, String comment) {
      return config.getStringList(name, category, defaultValue, comment, (String[])null, String.format("config.%s:%s", "spartanweaponry", name));
   }
}
