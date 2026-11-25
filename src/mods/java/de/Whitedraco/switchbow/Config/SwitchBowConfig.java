package de.Whitedraco.switchbow.Config;

import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SwitchBowConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File path = new File("config/SwitchBow/General Settings.cfg");
   public static ConfigValue showScope = new ConfigValue("Show Scope when Enchant", true);
   public static ConfigValue scopeZoom = new ConfigValue("Zoom strength", 0.65F);
   public static ConfigValue arrowPicPosX = new ConfigValue("Pos.X from Image", -99);
   public static ConfigValue arrowPicPosY = new ConfigValue("Pos.Y from Image", -99);
   public static ConfigValue arrowStringPosX = new ConfigValue("Pos.x from String", -99);
   public static ConfigValue arrowStringPosY = new ConfigValue("Pos.Y from String", -99);
   public static ConfigValue spawnFirewithLightning = new ConfigValue("Spawn Fire", true);
   public static ConfigValue manySoundByThunder_Arrow = new ConfigValue("extra Sound", 2);
   public static ConfigValue destroyBlockswithTNTArrow = new ConfigValue("Destroy Blocks", true);
   public static ConfigValue maxTicksTransformationproItem = new ConfigValue("max Ticks Transformation pro Arrow", 100);
   public static ConfigValue teleportdamage = new ConfigValue("Damage by Arrow Teleporting", true);
   public static ConfigValue replaceTorchwithMinilight = new ConfigValue("Replace Torch with Minilight", false);
   public static ConfigValue whichBurialEffect = new ConfigValue("Which Burial Effect use", 2);
   public static ConfigValue durabilitySwitchBow = new ConfigValue("Durability Switch-Bow", 500);
   public static ConfigValue enchantabilitySwitchBow = new ConfigValue("Enchantability Switch-Bow", 10);
   public static ConfigValue drawSpeedSwitchBow = new ConfigValue("Basic Pulling-Speed", 1.0F);
   public static ConfigValue extraDamageSwitchBow = new ConfigValue("Extra Damage Switch-Bow", 0.0D);
   public static ConfigValue didDamageSwitchBow = new ConfigValue("Did Switch-Bow Damage", true);
   public static ConfigValue durabilitySwitchCrossBow = new ConfigValue("Durability Switch-CrossBow", 500);
   public static ConfigValue enchantabilitySwitchCrossBow = new ConfigValue("Enchantability Switch-CrossBow", 10);
   public static ConfigValue cooldownSwitchCrossBow = new ConfigValue("Basic Cooldown", 40);
   public static ConfigValue extraDamageSwitchCrossBow = new ConfigValue("Extra Damage Switch-CrossBow", 0.0D);
   public static ConfigValue didDamageSwitchCrossBow = new ConfigValue("Did Switch-CrossBow Damage", true);
   public static ConfigValue MagicQuiver = new ConfigValue("Big-Quiver", true);
   public static List<ConfigValue> configList = Arrays.asList(text("###General Settings###"), text("#Spawn Fire from the Lightning-Arrow#"), spawnFirewithLightning, text("#How much extra sound with the Lightning Storm-Arrow#"), manySoundByThunder_Arrow, text("#Destroy Blocks on Explosion from TNT-Arrow#"), destroyBlockswithTNTArrow, text("#Time to Transformation an Arrow in Ticks (20 Ticks = 1 sec)#"), maxTicksTransformationproItem, text("#Get Damage from Teleporting by Teleport-Arrow#"), teleportdamage, text("#Replacing the Torch to an Minilight (This has no Drops, is passable, can spawn under a block and can be replaced by other blocks)#"), replaceTorchwithMinilight, text(LINE_SEPARATOR), text("##Burial-Effect##"), text("#There are 3 different types of the burial effect                                     #"), text("# 1: The Blocks under the Entity are removed and gradually placed back directly       #"), text("# 2: The Blocks under the Entity are removed and fall back onto the Entity from above #"), text("# 3: The entity is sucked directly into the ground (Use it if you have extremely lags)#"), whichBurialEffect, text(LINE_SEPARATOR), text("###Client Settings###"), text("##Scope-Settings (Client-Side)##"), showScope, text("#Zoom strength from Scope when Enchant(The higher the further, 0 to turn it off)#"), scopeZoom, text("#Positions from Image and String#"), arrowPicPosX, arrowPicPosY, arrowStringPosX, arrowStringPosY, text(LINE_SEPARATOR), text("###Switch-Bow Attribute###"), durabilitySwitchBow, enchantabilitySwitchBow, didDamageSwitchBow, text("#Increase the value to accelerate the basic pulling-speed (default: 1.0)#"), drawSpeedSwitchBow, text("#Add extra basic damage (default: 0.0)#"), extraDamageSwitchBow, text(LINE_SEPARATOR), text("###Switch-CrossBow Attribute###"), durabilitySwitchCrossBow, enchantabilitySwitchCrossBow, didDamageSwitchCrossBow, text("#Decrease the value to reduced the basic cooldown (default: 40)#"), cooldownSwitchCrossBow, text("#Add extra basic damage (default: 0.0)#"), extraDamageSwitchCrossBow, text(LINE_SEPARATOR), text("###Crafting-Recipes (default: true)###"), text("##Use the .json-Recipes to activate or deactivate the recipes##"), text("#Items#"), MagicQuiver);

   public SwitchBowConfig() {
   }

   public static void writeConfigSwitchBow() throws IOException {
      ConfigHelper.writeConfig(path, configList);
   }

   public static void readConfigSwitchBow(List<String[]> readData) {
      ConfigHelper.readConfig(readData, configList);
   }

   private static ConfigValue text(String text) {
      return new ConfigValue(text);
   }

   public static File getPath() {
      return path;
   }
}
