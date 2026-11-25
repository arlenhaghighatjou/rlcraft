package lumien.custommainmenu.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public class ConfigurationLoader {
   private final Config config;

   public ConfigurationLoader(Config config) {
      this.config = config;
   }

   public void load() throws IOException {
      VFile2 folder = new VFile2(CustomMainMenu.INSTANCE.configFolder, "CustomMainMenu");
      VFile2 main = new VFile2(folder, "mainmenu.json");
      String menu = main.getAllChars();
      if (menu == null) {
         menu = EagRuntime.getResourceString("/pack-defaults/config/CustomMainMenu/mainmenu.json");
      }
      if (menu == null) {
         return;
      }

      loadGui("mainmenu", menu);
      String prefix = folder.getPath() + "/";
      for (VFile2 file : folder.listFiles(false)) {
         String name = file.getPath().substring(prefix.length());
         if (!name.equals("mainmenu.json") && name.endsWith(".json")) {
            String contents = file.getAllChars();
            if (contents == null) {
               throw new IOException("Could not read Custom Main Menu config " + file.getPath());
            }
            loadGui(name.substring(0, name.length() - 5), contents);
         }
      }
   }

   private void loadGui(String name, String source) {
      JsonObject json = new JsonParser().parse(source).getAsJsonObject();
      GuiConfig gui = new GuiConfig();
      gui.load(name, json);
      this.config.addGui(gui.name, new GuiCustom(gui));
      CustomMainMenu.INSTANCE.logger.debug("Loaded custom menu {}", name);
   }
}
