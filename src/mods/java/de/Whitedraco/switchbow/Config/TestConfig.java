package de.Whitedraco.switchbow.Config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TestConfig {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static VFile2 path = new VFile2("config/SwitchBow/test.cfg");
   public static ConfigValue bool = new ConfigValue("bool", true);
   public static ConfigValue doub = new ConfigValue("doub", 0.15D);
   public static ConfigValue floa = new ConfigValue("floa", 1.29F);
   public static ConfigValue integer = new ConfigValue("integer", 103);
   public static List<ConfigValue> configList = Arrays.asList(text("test"), bool, doub, floa, integer);

   public TestConfig() {
   }

   public static void writeConfigTest() throws IOException {
      ConfigHelper.writeConfig(path, configList);
   }

   public static void readConfigTest(List<String[]> readData) {
      ConfigHelper.readConfig(readData, configList);
   }

   private static ConfigValue text(String text) {
      return new ConfigValue(text);
   }

   public static VFile2 getPath() {
      return path;
   }
}
