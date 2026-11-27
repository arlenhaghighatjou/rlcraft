package greymerk.roguelike.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.nio.charset.StandardCharsets;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ConfigFile {
   private static final Logger logger = LogManager.getLogger("roguelike");

   public ConfigFile() {
   }

   public static void read(ConfigurationMap configFile, String filename) {
      try {
         BufferedReader reader = new BufferedReader(new StringReader((new VFile2(filename)).getAllChars()));

         while(true) {
            Configuration config = INIParser.readLine(reader);
            if (config == null) {
               break;
            }

            configFile.put(config.key, config.value);
         }
      } catch (Exception var4) {
         logger.error("Error while reading config file. : ", var4);
      }

   }

   public static void write(ConfigurationMap configFile, String filename) throws Exception {
      BufferedWriter buffered = new BufferedWriter(new OutputStreamWriter((new VFile2(filename)).getOutputStream(), StandardCharsets.UTF_8));

      for(Configuration configuration : configFile.asList()) {
         INIParser.write(configuration, buffered);
      }

      buffered.close();
   }
}
