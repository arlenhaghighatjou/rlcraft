package de.Whitedraco.switchbow.Config;

import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ArrowConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File path = new File("config/SwitchBow/Arrow Settings.cfg");
   public static ConfigValue DamageArrowLighningBolt = new ConfigValue("Damage Thunder-Arrow", 2.0D);
   public static ConfigValue InfiArrowLightningBolt = new ConfigValue("Infi. Thunder-Arrow", false);
   public static ConfigValue DamageArrowTNT = new ConfigValue("Damage TNT-Arrow", 2.0D);
   public static ConfigValue ExplodeRadiusArrowTNT = new ConfigValue("Explode-Radius TNT-Arrow", 2.0F);
   public static ConfigValue InfiArrowTNT = new ConfigValue("Infi. TNT-Arrow", false);
   public static ConfigValue DamageArrowFrost = new ConfigValue("Damage Frost-Arrow", 2.0D);
   public static ConfigValue InfiArrowFrost = new ConfigValue("Infi. Frost-Arrow", false);
   public static ConfigValue DamageArrowAir = new ConfigValue("Damage Air-Arrow", 2.0D);
   public static ConfigValue InfiArrowAir = new ConfigValue("Infi. Air-Arrow", false);
   public static ConfigValue DamageArrowEnderperle = new ConfigValue("Damage Teleport-Arrow", 2.0D);
   public static ConfigValue InfiArrowEnderperle = new ConfigValue("Infi. Teleport-Arrow", false);
   public static ConfigValue DamageArrowVampier = new ConfigValue("Damage Vampire-Arrow", 2.0D);
   public static ConfigValue InfiArrowVampier = new ConfigValue("Infi. Vampire-Arrow", false);
   public static ConfigValue DamageArrowWither = new ConfigValue("Damage Wither-Arrow", 2.0D);
   public static ConfigValue InfiArrowWither = new ConfigValue("Infi. Wither-Arrow", false);
   public static ConfigValue DamageArrowTorch = new ConfigValue("Damage Torch-Arrow", 2.0D);
   public static ConfigValue InfiArrowTorch = new ConfigValue("Infi. Torch-Arrow", false);
   public static ConfigValue DamageArrowSplit = new ConfigValue("Damage Sliver-Arrow", 2.0D);
   public static ConfigValue InfiArrowSplit = new ConfigValue("Infi. Sliver-Arrow", false);
   public static ConfigValue DamageArrowUnderwater = new ConfigValue("Damage Underwater-Arrow", 2.0D);
   public static ConfigValue InfiArrowUnderwater = new ConfigValue("Infi. Underwater-Arrow", false);
   public static ConfigValue DamageArrowDiamond = new ConfigValue("Damage Reinforced-Arrow", 6.0D);
   public static ConfigValue InfiArrowDiamond = new ConfigValue("Infi. Reinforced Arrow", false);
   public static ConfigValue InfiArrowLove = new ConfigValue("Infi. Love-Arrow", false);
   public static ConfigValue CraftingArrowLove = new ConfigValue("Crafting Love-Arrow", true);
   public static ConfigValue DamageArrowFire = new ConfigValue("Damage Fire-Arrow", 2.0D);
   public static ConfigValue InfiArrowFire = new ConfigValue("Infi. Fire-Arrow", false);
   public static ConfigValue DamageArrowBone = new ConfigValue("Damage Bone-Arrow", 2.0D);
   public static ConfigValue InfiArrowBone = new ConfigValue("Infi. Bone-Arrow", false);
   public static ConfigValue DamageArrowFirework = new ConfigValue("Damage Rocket-Arrow", 2.0D);
   public static ConfigValue InfiArrowFirework = new ConfigValue("Infi. Rocket-Arrow", false);
   public static ConfigValue CraftingArrowFirework = new ConfigValue("Crafting Rocket-Arrow", true);
   public static ConfigValue InfiArrowSplitLove = new ConfigValue("Infi. Love-Arrow(AOE)", false);
   public static ConfigValue CraftingArrowSplitLove = new ConfigValue("Crafting Love-Arrow(AOE)", true);
   public static ConfigValue DamageArrowLuck = new ConfigValue("Damage Luck-Arrow", 3.0D);
   public static ConfigValue LootingArrowLuck = new ConfigValue("Looting-Effect Luck-Arrow", 3);
   public static ConfigValue InfiArrowLuck = new ConfigValue("Infi. Luck-Arrow", false);
   public static ConfigValue DamageArrowRedstone = new ConfigValue("Damage Redstone-Arrow", 2.0D);
   public static ConfigValue InfiArrowRedstone = new ConfigValue("Infi. Redstone-Arrow", false);
   public static ConfigValue DamageArrowBurial = new ConfigValue("Damage Burial-Arrow", 2.0D);
   public static ConfigValue InfiArrowBurial = new ConfigValue("Infi. Burial-Arrow", false);
   public static ConfigValue DamageArrowChorus = new ConfigValue("Damage Chorus-Arrow", 2.0D);
   public static ConfigValue InfiArrowChorus = new ConfigValue("Infi. Chorus-Arrow", false);
   public static ConfigValue DamageArrowSprinkler = new ConfigValue("Damage Sprinkler-Arrow", 2.0D);
   public static ConfigValue InfiArrowSprinkler = new ConfigValue("Infi. Sprinkler-Arrow", false);
   public static ConfigValue DamageArrowDragonBreath = new ConfigValue("Damage Dragon-Arrow", 2.0D);
   public static ConfigValue InfiArrowDragonBreath = new ConfigValue("Infi. Dragon-Arrow", false);
   public static ConfigValue DamageArrowTriple = new ConfigValue("Damage Triple-Arrow", 2.0D);
   public static ConfigValue InfiArrowTriple = new ConfigValue("Infi. Triple-Arrow", false);
   public static ConfigValue DamageArrowProtector = new ConfigValue("Damage Protector-Arrow", 2.0D);
   public static ConfigValue InfiArrowProtector = new ConfigValue("Infi. Protector-Arrow", false);
   public static ConfigValue DamageArrowKnockback = new ConfigValue("Damage Knockback-Arrow", 2.0D);
   public static ConfigValue InfiArrowKnockback = new ConfigValue("Infi. Knockback-Arrow", false);
   public static ConfigValue DamageArrowBouncy = new ConfigValue("Damage Bouncy-Arrow", 2.0D);
   public static ConfigValue InfiArrowBouncy = new ConfigValue("Infi. Bouncy-Arrow", false);
   public static ConfigValue DamageArrowoftheSearcher = new ConfigValue("Damage Arrow of the Searcher", 2.0D);
   public static ConfigValue InfiArrowoftheSearcher = new ConfigValue("Infi. Arrow of the Searcher", false);
   public static ConfigValue DamageArrowPiercing = new ConfigValue("Damage Piercing-Arrow", 2.0D);
   public static ConfigValue InfiArrowPiercing = new ConfigValue("Infi. Piercing-Arrow", false);
   public static ConfigValue DamageArrowLightningStrom = new ConfigValue("Damage Thunderstorm-Arrow", 2.0D);
   public static ConfigValue InfiArrowLightningStorm = new ConfigValue("Infi. Thunderstorm-Arrow", false);
   public static ConfigValue CraftingArrowLightningStorm = new ConfigValue("Crafting Thunderstorm-Arrow", true);
   public static ConfigValue DamageArrowBoneAOE = new ConfigValue("Damage Bone-Arrow(AOE)", 2.0D);
   public static ConfigValue InfiArrowBoneAOE = new ConfigValue("Infi. Bone-Arrow(AOE)", false);
   public static ConfigValue CraftingArrowBoneAOE = new ConfigValue("Crafting Bone-Arrow(AOE)", true);
   public static ConfigValue DamageArrowSplitUpgrade = new ConfigValue("Damage Transforming Sliver-Arrow", 2.0D);
   public static ConfigValue InfiArrowSplitUpgrade = new ConfigValue("Infi. Transforming Sliver-Arrow", false);
   public static ConfigValue CraftingArrowSplitUpgrade = new ConfigValue("Crafting Transforming Sliver-Arrow", true);
   public static ConfigValue DamageArrowBurialAOE = new ConfigValue("Damage Burial-Arrow(AOE)", 2.0D);
   public static ConfigValue InfiArrowBurialAOE = new ConfigValue("Infi. Burial-Arrow(AOE)", false);
   public static ConfigValue CraftingArrowBurialAOE = new ConfigValue("Crafting Burial-Arrow(AOE)", true);
   public static ConfigValue DamageArrowFireUpgrade = new ConfigValue("Damage FireStrom-Arrow", 2.0D);
   public static ConfigValue InfiArrowFireUpgrade = new ConfigValue("Infi. FireStorm-Arrow", false);
   public static ConfigValue CraftingArrowFireUpgrade = new ConfigValue("Crafting FireStorm-Arrow", true);
   public static ConfigValue DamageArrowLuckUpgrade = new ConfigValue("Damage Transforming Luck-Arrow", 4.0D);
   public static ConfigValue LootingArrowLuckUpgrade = new ConfigValue("Looting-Effect Transforming Luck-Arrow", 4);
   public static ConfigValue InfiArrowLuckUpgrade = new ConfigValue("Infi. Transforming Luck-Arrow", false);
   public static ConfigValue CraftingArrowLuckUpgrade = new ConfigValue("Crafting Transforming Luck-Arrow", true);
   public static ConfigValue DamageArrowAirUpgrade = new ConfigValue("Damage Air-Arrow(AOE)", 2.0D);
   public static ConfigValue InfiArrowAirUpgrade = new ConfigValue("Infi. Air-Arrow(AOE)", false);
   public static ConfigValue CraftingArrowAirUpgrade = new ConfigValue("Crafting Air-Arrow(AOE)", true);
   public static ConfigValue InfiArrowSplitLoveUpgrade = new ConfigValue("Infi. Transforming Love-Arrow(AOE)", false);
   public static ConfigValue CraftingArrowSplitLoveUpgrade = new ConfigValue("Crafting Transforming Love-Arrow(AOE)", true);
   public static List<ConfigValue> configList = Arrays.asList(text("###Arrow-Settigs###"), text("#Thunder-Arrow#"), DamageArrowLighningBolt, InfiArrowLightningBolt, text(LINE_SEPARATOR), text("#TNT-Arrow#"), DamageArrowTNT, ExplodeRadiusArrowTNT, InfiArrowTNT, text(LINE_SEPARATOR), text("#Frost-Arrow#"), DamageArrowFrost, InfiArrowFrost, text(LINE_SEPARATOR), text("#Air-Arrow#"), DamageArrowAir, InfiArrowAir, text(LINE_SEPARATOR), text("#Teleport-Arrow#"), DamageArrowEnderperle, InfiArrowEnderperle, text(LINE_SEPARATOR), text("#Vampier-Arrow#"), DamageArrowVampier, InfiArrowVampier, text(LINE_SEPARATOR), text("#Wither-Arrow#"), DamageArrowWither, InfiArrowWither, text(LINE_SEPARATOR), text("#Torch-Arrow#"), DamageArrowTorch, InfiArrowTorch, text(LINE_SEPARATOR), text("#Sliver-Arrow#"), DamageArrowSplit, InfiArrowSplit, text(LINE_SEPARATOR), text("#Underwater-Arrow#"), DamageArrowUnderwater, InfiArrowUnderwater, text(LINE_SEPARATOR), text("#Reinforced-Arrow#"), DamageArrowDiamond, InfiArrowDiamond, text(LINE_SEPARATOR), text("#Love-Arrow#"), InfiArrowLove, CraftingArrowLove, text(LINE_SEPARATOR), text("#Fire-Arrow#"), DamageArrowFire, InfiArrowFire, text(LINE_SEPARATOR), text("#Bone-Arrow#"), DamageArrowBone, InfiArrowBone, text(LINE_SEPARATOR), text("#Rocket-Arrow#"), DamageArrowFirework, InfiArrowFirework, CraftingArrowFirework, text(LINE_SEPARATOR), text("#Love-Arrow(AOE)#"), InfiArrowSplitLove, CraftingArrowSplitLove, text(LINE_SEPARATOR), text("#Luck-Arrow#"), DamageArrowLuck, LootingArrowLuck, InfiArrowLuck, text(LINE_SEPARATOR), text("#Redstone-Arrow#"), DamageArrowRedstone, InfiArrowRedstone, text(LINE_SEPARATOR), text("#Burial-Arrow#"), DamageArrowBurial, InfiArrowBurial, text(LINE_SEPARATOR), text("#Chorus-Arrow#"), DamageArrowChorus, InfiArrowChorus, text(LINE_SEPARATOR), text("#Sprinkler-Arrow#"), DamageArrowSprinkler, InfiArrowSprinkler, text(LINE_SEPARATOR), text("#Dragon-Arrow#"), DamageArrowDragonBreath, InfiArrowDragonBreath, text(LINE_SEPARATOR), text("#Triple-Arrow#"), DamageArrowTriple, InfiArrowTriple, text(LINE_SEPARATOR), text("#Protector-Arrow#"), DamageArrowProtector, InfiArrowProtector, text(LINE_SEPARATOR), text("#Knockback-Arrow#"), DamageArrowKnockback, InfiArrowKnockback, text(LINE_SEPARATOR), text("#Bouncy-Arrow#"), DamageArrowBouncy, InfiArrowBouncy, text(LINE_SEPARATOR), text("#Arrow of the Searcher#"), DamageArrowoftheSearcher, InfiArrowoftheSearcher, text(LINE_SEPARATOR), text("#Piercing-Arrow#"), DamageArrowPiercing, InfiArrowPiercing, text(LINE_SEPARATOR), text("#Thunderstrom-Arrow#"), DamageArrowLightningStrom, InfiArrowLightningStorm, CraftingArrowLightningStorm, text(LINE_SEPARATOR), text("#Bone-Arrow(AOE)#"), DamageArrowBoneAOE, InfiArrowBoneAOE, CraftingArrowBoneAOE, text(LINE_SEPARATOR), text("#Transfroming Sliver-Arrow#"), DamageArrowSplitUpgrade, InfiArrowSplitUpgrade, CraftingArrowSplitUpgrade, text(LINE_SEPARATOR), text("#Burial-Arrow(AOE)#"), DamageArrowBurialAOE, InfiArrowBurialAOE, CraftingArrowBurialAOE, text(LINE_SEPARATOR), text("#FireStrom-Arrow#"), DamageArrowFireUpgrade, InfiArrowFireUpgrade, CraftingArrowFireUpgrade, text(LINE_SEPARATOR), text("#Transforming Luck-Arrow#"), DamageArrowLuckUpgrade, LootingArrowLuckUpgrade, InfiArrowLuckUpgrade, CraftingArrowLuckUpgrade, text(LINE_SEPARATOR), text("#Air-Arrow(AOE)#"), DamageArrowAirUpgrade, InfiArrowAirUpgrade, CraftingArrowAirUpgrade, text(LINE_SEPARATOR), text("#Transforming Love-Arrow(AOE)#"), InfiArrowSplitLoveUpgrade, CraftingArrowSplitLoveUpgrade);

   public ArrowConfig() {
   }

   public static void writeConfigArrow() throws IOException {
      ConfigHelper.writeConfig(path, configList);
   }

   public static void readConfigArrow(List<String[]> readData) {
      ConfigHelper.readConfig(readData, configList);
   }

   private static ConfigValue text(String text) {
      return new ConfigValue(text);
   }

   public static File getPath() {
      return path;
   }
}
