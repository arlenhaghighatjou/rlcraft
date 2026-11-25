package lumien.custommainmenu.configuration;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import org.apache.commons.io.IOUtils;

public class ConfigurationLoader {
   Config config;

   public ConfigurationLoader(Config config) {
      this.config = config;
   }

   public void load() throws Exception {
      JsonParser jsonParser = new JsonParser();
      File configFolder = new File(CustomMainMenu.INSTANCE.configFolder, "CustomMainMenu");
      if (!configFolder.exists()) {
         configFolder.mkdir();
      }

      File mainmenuConfig = new File(configFolder, "mainmenu.json");
      if (!mainmenuConfig.exists()) {
         InputStream input = null;
         OutputStream output = null;

         try {
            output = new FileOutputStream(mainmenuConfig);
            input = this.getClass().getResourceAsStream("/assets/custommainmenu/mainmenu_default.json");
            ByteStreams.copy(input, output);
         } catch (FileNotFoundException var33) {
            var33.printStackTrace();
         } catch (IOException var34) {
            var34.printStackTrace();
         } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);
         }
      }

      File[] jsonFiles = configFolder.listFiles();

      for(File guiFile : jsonFiles) {
         if (guiFile.getName().equals("mainmenu.json")) {
            GuiConfig guiConfig = new GuiConfig();
            String name = guiFile.getName().replace(".json", "");
            JsonReader reader = null;

            try {
               reader = new JsonReader(new FileReader(guiFile));
            } catch (FileNotFoundException var32) {
               var32.printStackTrace();
            }

            try {
               JsonElement jsonElement = jsonParser.parse(reader);
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               guiConfig.load(name, jsonObject);
            } catch (Exception var31) {
               try {
                  reader.close();
               } catch (IOException var26) {
                  var26.printStackTrace();
               }

               throw var31;
            }

            try {
               reader.close();
            } catch (IOException var30) {
               var30.printStackTrace();
            }

            this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
         }
      }

      for(File guiFile : jsonFiles) {
         if (!guiFile.getName().equals("mainmenu.json") && guiFile.getName().endsWith(".json")) {
            GuiConfig guiConfig = new GuiConfig();
            String name = guiFile.getName().replace(".json", "");
            JsonReader reader = null;

            try {
               reader = new JsonReader(new FileReader(guiFile));
            } catch (FileNotFoundException var29) {
               var29.printStackTrace();
            }

            try {
               JsonElement jsonElement = jsonParser.parse(reader);
               JsonObject jsonObject = jsonElement.getAsJsonObject();
               guiConfig.load(name, jsonObject);
            } catch (Exception var28) {
               try {
                  reader.close();
               } catch (IOException var25) {
                  var25.printStackTrace();
               }

               throw var28;
            }

            try {
               reader.close();
            } catch (IOException var27) {
               var27.printStackTrace();
            }

            this.config.addGui(guiConfig.name, new GuiCustom(guiConfig));
         }
      }

   }
}
