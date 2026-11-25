package com.tmtravlr.potioncore;

import com.tmtravlr.potioncore.potion.PotionArchery;
import com.tmtravlr.potioncore.potion.PotionBrokenArmor;
import com.tmtravlr.potioncore.potion.PotionBrokenMagicShield;
import com.tmtravlr.potioncore.potion.PotionCorePotion;
import com.tmtravlr.potioncore.potion.PotionDiamondSkin;
import com.tmtravlr.potioncore.potion.PotionExplosion;
import com.tmtravlr.potioncore.potion.PotionExplosionSelf;
import com.tmtravlr.potioncore.potion.PotionFire;
import com.tmtravlr.potioncore.potion.PotionIronSkin;
import com.tmtravlr.potioncore.potion.PotionKlutz;
import com.tmtravlr.potioncore.potion.PotionLaunch;
import com.tmtravlr.potioncore.potion.PotionMagicFocus;
import com.tmtravlr.potioncore.potion.PotionMagicInhibition;
import com.tmtravlr.potioncore.potion.PotionMagicShield;
import com.tmtravlr.potioncore.potion.PotionPotionSickness;
import com.tmtravlr.potioncore.potion.PotionReach;
import com.tmtravlr.potioncore.potion.PotionRecoil;
import com.tmtravlr.potioncore.potion.PotionRepair;
import com.tmtravlr.potioncore.potion.PotionRevival;
import com.tmtravlr.potioncore.potion.PotionRust;
import com.tmtravlr.potioncore.potion.PotionSlowfall;
import com.tmtravlr.potioncore.potion.PotionSpin;
import com.tmtravlr.potioncore.potion.PotionStepup;
import com.tmtravlr.potioncore.potion.PotionTeleport;
import com.tmtravlr.potioncore.potion.PotionTeleportSpawn;
import com.tmtravlr.potioncore.potion.PotionVulnerable;
import com.tmtravlr.potioncore.potion.PotionWeight;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

public class ConfigLoader {
   public static Configuration config;
   public static Map<ResourceLocation, Boolean> extraVanillaPotionTypesEnabled = new HashMap();
   public static boolean fixBlindness;
   public static boolean fixSaturation;
   public static boolean fixReach;
   public static boolean fixChangingDimensions;
   public static boolean renderArmorOverlay;
   public static boolean controllableJumpBoost;
   public static boolean separateInvulTimers;
   public static float jumpBoostPerLevel;
   public static int saturationPerLevel;
   public static List<String> chanceBlacklist;

   public ConfigLoader() {
   }

   public static void load() {
      config.load();
      fixBlindness = config.getBoolean("Fix Blindness", "_options", true, "Fixes Blindness so mobs can't see things to attack unless they are really\nclose.");
      fixSaturation = config.getBoolean("Fix Saturation", "_options", true, "Fixes Saturation so it works in potions.");
      fixReach = config.getBoolean("Fix Reach", "_options", true, "Fixes forge's reach attribute so it works with entities as well as blocks.");
      fixChangingDimensions = config.getBoolean("Fix Effects When Changing Dimensions", "_options", true, "Fixes your health resetting when changing dimensions/logging in if you\nhave a health boost potion effect or attribute modifier. Could possibly cause\nissues with entity health, so if you see any issues, try disabling this.");
      renderArmorOverlay = config.getBoolean("Render Armor Bar Overlays", "_options", true, "Renders a few extra things on the armor bar, including damage resistance,\nmagic shielding, armor toughness, and armor values over 20.");
      controllableJumpBoost = config.getBoolean("Controllable Jump Boost", "_options", true, "Makes it so you can control your jump height when jumping with jump boost\nby holding down the space bar as you jump (So you can jump up one block\nwithout shooing into the air...).");
      jumpBoostPerLevel = config.getFloat("Vanilla Jump Boost Height", "_options", PotionCoreHelper.vanillaJumpBoostModifier, 0.0F, Float.POSITIVE_INFINITY, "If 'Controllable Jump Boost' is true, you can modify this to modify how high the\nvanilla jump boost effect lets you jump (so you can set it to 1.0 to\nactually jump up a full extra block!).");
      separateInvulTimers = config.getBoolean("Separate Invulnerabilty Timers", "_options", true, "Moves projectile and magic damage to different invulnerability timers, so you\ncan have a weapon that does multiple of those damage types at once.");
      saturationPerLevel = config.getInt("Vanilla Saturation Per Level", "_options", 4, 1, 20, "If the saturation potion types are added, you can modify this to set how\nmuch saturation the potion types will give you per level.");
      chanceBlacklist = Arrays.asList(config.getStringList("Blacklist for Chance Effects", "_options", new String[0], "Blacklist for effects the chance, bless, and curse potions can give you.\nUseful if you have a mod that adds instakill or overpowered effects that\nyou don't want to randomly get from those potions."));
      loadExtraVanillaPotionTypesEnabled();
      loadPotionConfigs();
      config.save();
   }

   private static void loadExtraVanillaPotionTypesEnabled() {
      PotionCoreTypes.EXTRA_VANILLA_TYPES.forEach((potionType) -> {
         String name = potionType.getRegistryName().getResourcePath();
         boolean enabled = config.get("_vanilla_potion_types_to_add", name, true).getBoolean();
         config.addCustomCategoryComment("_vanilla_potion_types_to_add", "Adds potion types for the missing vanilla potion effects.");
         extraVanillaPotionTypesEnabled.put(potionType.getRegistryName(), Boolean.valueOf(enabled));
      });
   }

   private static void loadPotionConfigs() {
      PotionCoreEffects.POTIONS.keySet().forEach((name) -> {
         PotionCorePotion potion = (PotionCorePotion)PotionCoreEffects.POTIONS.get(name);
         potion.setEnabled(config.getBoolean("Enabled", name, potion.isEnabled(), "Is the " + name + " potion enabled?"));
      });
      PotionArchery.damageModifier = config.getFloat("Damage Modifier", "archery", PotionArchery.damageModifier, 0.0F, Float.POSITIVE_INFINITY, "The projectile damage increase per level.\n");
      PotionArchery.addModifier = config.getBoolean("Add Modifier", "archery", PotionArchery.addModifier, "If true, will add the damage modifier instead of multiplying (like how strength is now).\n");
      PotionBrokenArmor.armorModifier = config.getFloat("Armor Modifier", "broken_armor", PotionBrokenArmor.armorModifier, Float.NEGATIVE_INFINITY, 0.0F, "The armor percent reduced per level.\n");
      PotionBrokenMagicShield.magicShieldModifier = config.getFloat("Magic Shield Modifier", "broken_magic_shield", PotionBrokenMagicShield.magicShieldModifier, Float.NEGATIVE_INFINITY, 0.0F, "The magic shield percent reduced per level.\n");
      PotionDiamondSkin.armorModifier = config.getFloat("Armor Toughness Modifier", "diamond_skin", PotionDiamondSkin.armorModifier, 0.0F, 20.0F, "The armor toughness added per level.\n");
      PotionExplosion.explosionSize = config.getFloat("Explosion Size", "explode", PotionExplosion.explosionSize, 0.0F, Float.POSITIVE_INFINITY, "The base explosion size.\n");
      PotionExplosionSelf.explosionSize = config.getFloat("Explosion Size", "burst", PotionExplosionSelf.explosionSize, 0.0F, Float.POSITIVE_INFINITY, "The base explosion size (Note it won't do block damage).\n");
      PotionFire.fireDuration = config.getFloat("Fire Duration", "fire", PotionFire.fireDuration, 0.0F, Float.POSITIVE_INFINITY, "The fire duration in seconds per level.\n");
      PotionIronSkin.armorModifier = config.getFloat("Armor Modifier", "iron_skin", PotionIronSkin.armorModifier, 0.0F, 30.0F, "The armor added per level.\n");
      PotionKlutz.damageModifier = config.getFloat("Damage Modifier", "klutz", PotionKlutz.damageModifier, Float.NEGATIVE_INFINITY, 0.0F, "The projectile damage decreased per level.\n");
      PotionKlutz.addModifier = config.getBoolean("Add Modifier", "klutz", PotionKlutz.addModifier, "If true, will add the damage modifier instead of multiplying (like how strength is now).\n");
      PotionLaunch.launchSpeed = config.getFloat("Launch Speed", "launch", PotionLaunch.launchSpeed, 0.0F, Float.POSITIVE_INFINITY, "The upwards launch speed per level.\n");
      PotionMagicFocus.damageModifier = config.getFloat("Damage Modifier", "magic_focus", PotionMagicFocus.damageModifier, 0.0F, Float.POSITIVE_INFINITY, "The magic damage percent increased per level.\n");
      PotionMagicFocus.addModifier = config.getBoolean("Add Modifier", "magic_focus", PotionMagicFocus.addModifier, "If true, will add the damage modifier instead of multiplying (like how strength is now).\n");
      PotionMagicInhibition.damageModifier = config.getFloat("Damage Modifier", "magic_inhibition", PotionMagicInhibition.damageModifier, Float.NEGATIVE_INFINITY, 0.0F, "The magic damage percent decreased per level.\n");
      PotionMagicInhibition.addModifier = config.getBoolean("Add Modifier", "magic_inhibition", PotionMagicInhibition.addModifier, "If true, will add the damage modifier instead of multiplying (like how strength is now).\n");
      PotionMagicShield.armorModifier = config.getFloat("Armor Toughness Modifier", "magic_shield", PotionMagicShield.armorModifier, 0.0F, 20.0F, "The magic shielding added per level.\n");
      PotionPotionSickness.potionsForSickness = config.getInt("Number of Good Potions to Activate Potion Sickness", "potion_sickness", PotionPotionSickness.potionsForSickness, 0, Integer.MAX_VALUE, "Number of good potion effects you must have to get potion sickness. Will increase per level\nwith each additional good potion effect. (So if it's 8 and you have 10 good potion effects,\nyou will get potion sickness 3).\n");
      PotionPotionSickness.chanceForSickness = config.getInt("Bad Effect Chance", "potion_sickness", PotionPotionSickness.chanceForSickness, 0, Integer.MAX_VALUE, "The 1/(this number) chance for you to get a bad potion effect each game tick per level.\n");
      PotionReach.rangeModifier = (double)config.getFloat("Reach Increase", "reach", 1.0F, 0.0F, 2024.0F, "The increase in your reach in blocks per level.\n");
      PotionRecoil.reflectDamage = config.getFloat("Damage Reflection", "recoil", PotionRecoil.reflectDamage, 0.0F, 0.9F, "The percent of damage reflected per level.\n");
      PotionRepair.repairTime = config.getInt("Repair Time", "repair", PotionRepair.repairTime, 0, Integer.MAX_VALUE, "The game ticks (20/second) between each repair per level.\n");
      PotionRevival.reviveHealth = config.getFloat("Revive Health", "revival", PotionRevival.reviveHealth, 0.0F, Float.POSITIVE_INFINITY, "The health recovered in half hearts per level when reviving from the revive potion.\n");
      PotionRust.damageTime = config.getInt("Damage Time", "rust", PotionRust.damageTime, 0, Integer.MAX_VALUE, "The game ticks (20/second) between each durability reduction from the rust potion.\n");
      PotionSlowfall.maxSpeed = config.getFloat("Max Fall Speed", "slow_fall", PotionSlowfall.maxSpeed, 0.0F, Float.POSITIVE_INFINITY, "The maximum fall speed per level.\n");
      PotionStepup.increase = config.getFloat("Step Height Increase", "step_up", PotionStepup.increase, 0.0F, Float.POSITIVE_INFINITY, "The step height increase per level.\n");
      PotionTeleport.teleportRange = config.getFloat("Teleport Radius", "teleport", PotionTeleport.teleportRange, 1.0F, Float.POSITIVE_INFINITY, "The teleport radius per level.\n");
      PotionTeleportSpawn.teleportDelay = config.getInt("Teleport Delay", "teleport_spawn", PotionTeleportSpawn.teleportDelay, 0, Integer.MAX_VALUE, "The number of seconds you must stay still before teleporting to your spawnpoint.\n");
      PotionVulnerable.damageMultiplier = config.getFloat("Damage Multiplier", "vulnerable", PotionVulnerable.damageMultiplier, Float.NEGATIVE_INFINITY, 0.0F, "The damage resistance decreased per level.\n");
      PotionWeight.heightReduction = config.getFloat("Jump Height Reduction", "weight", PotionWeight.heightReduction, Float.NEGATIVE_INFINITY, 0.0F, "The jump height percent reduction per level.\n");
      PotionSpin.rotationSpeed = config.getFloat("Rotation Speed", "spin", PotionSpin.rotationSpeed, 0.0F, Float.POSITIVE_INFINITY, "The speed at which you spin per level.\n");
   }
}
