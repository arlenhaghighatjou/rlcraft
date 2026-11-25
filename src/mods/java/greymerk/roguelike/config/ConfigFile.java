package greymerk.roguelike.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class ConfigFile {
   private static final Logger logger = LogManager.getLogger("roguelike");

   public ConfigFile() {
   }

   public static void read(ConfigurationMap configFile, String filename) {
      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(filename))));

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
      FileOutputStream stream = new FileOutputStream(filename, true);
      stream.getChannel().truncate(0L);
      BufferedWriter buffered = new BufferedWriter(new OutputStreamWriter(stream));

      for(Configuration configuration : configFile.asList()) {
         INIParser.write(configuration, buffered);
      }

      buffered.close();
   }
}
