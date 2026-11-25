package de.Whitedraco.switchbow.Config;

import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EnchantmentConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static File path = new File("config/SwitchBow/Enchantment Settings.cfg");
   public static ConfigValue enchantPullingSpeedRegistry = new ConfigValue("Registry Enchantment 'Pull-Speed'", true);
   public static ConfigValue enchantPullingSpeedProcent = new ConfigValue("Increase Pulling-Speed by", 30);
   public static ConfigValue enchantPullingSpeedMax = new ConfigValue("Max. Enchantment-Level 'Pull-Speed'", 3);
   public static ConfigValue enchantreducedCooldownRegistry = new ConfigValue("Registry Enchantment 'Reduce Cooldown'", true);
   public static ConfigValue enchantreducedCooldownProcent = new ConfigValue("Decrease Cooldown by", 10);
   public static ConfigValue enchantreducedCooldownMax = new ConfigValue("Max. Enchantment-Level 'Reduce Cooldown'", 5);
   public static ConfigValue enchantActivScopeRegistry = new ConfigValue("Registy Enchantment 'Scope'", true);
   public static List<ConfigValue> configList = Arrays.asList(text("###Enchantment Settings###"), text("##Enchantment 'Pull-Speed'##"), text("#Enable/Disable Registry (Disable at your own risk. Can damage the world.)#"), enchantPullingSpeedRegistry, text("#Enchantment increases the Pulling-Speed by x% per level (default: 30)#"), enchantPullingSpeedProcent, text("#Max. Enchanting-Level (default: 3)#"), enchantPullingSpeedMax, text(LINE_SEPARATOR), text("##Enchantment 'Reduce Cooldown'##"), text("#Enable/Disable Registry (Disable at your own risk. Can damage the world.)#"), enchantreducedCooldownRegistry, text("#Enchantment decrease the Cooldown by x% per level (default: 10)#"), enchantreducedCooldownProcent, text("#Max. Enchanting-Level (default: 5)#"), enchantreducedCooldownMax, text(LINE_SEPARATOR), text("##Enchantment 'Scope'##"), text("#Enable/Disable Registry (Disable at your own risk. Can damage the world.)#"), enchantActivScopeRegistry);

   public EnchantmentConfig() {
   }

   public static void writeConfigEnchantment() throws IOException {
      ConfigHelper.writeConfig(path, configList);
   }

   public static void readConfigEnchantment(List<String[]> readData) {
      ConfigHelper.readConfig(readData, configList);
   }

   private static ConfigValue text(String text) {
      return new ConfigValue(text);
   }

   public static File getPath() {
      return path;
   }
}
