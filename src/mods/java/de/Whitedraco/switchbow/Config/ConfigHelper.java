package de.Whitedraco.switchbow.Config;

import java.nio.charset.StandardCharsets;
import java.io.Writer;
import java.io.OutputStreamWriter;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import de.Whitedraco.switchbow.Config.variable.ConfigValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class ConfigHelper {
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   public ConfigHelper() {
   }

   public static void writeConfig(VFile2 ConfigPath, List<ConfigValue> list) throws IOException {
      Writer fw = new OutputStreamWriter(ConfigPath.getOutputStream(), StandardCharsets.UTF_8);
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
