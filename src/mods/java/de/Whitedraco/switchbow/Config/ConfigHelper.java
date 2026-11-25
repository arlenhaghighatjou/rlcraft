package de.Whitedraco.switchbow.Config;

import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ConfigHelper {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   public ConfigHelper() {
   }

   public static void writeConfig(File ConfigPath, List<ConfigValue> list) throws IOException {
      FileWriter fw = new FileWriter(ConfigPath);
      BufferedWriter bw = new BufferedWriter(fw);

      for(ConfigValue text : list) {
         bw.write(text.getString());
      }

      bw.close();
      fw.close();
   }

   public static void readConfig(List<String[]> readData, List<ConfigValue> configData) {
      for(String[] data : readData) {
         for(ConfigValue configInf : configData) {
            if (configInf.checkandreplaceData(data)) {
               break;
            }
         }
      }

   }
}
