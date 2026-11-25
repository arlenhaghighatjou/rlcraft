package lumien.custommainmenu.configuration;

import java.util.HashMap;
import java.util.function.Consumer;
import lumien.custommainmenu.gui.GuiCustom;

public class Config {
   HashMap<String, GuiEntry> guis = new HashMap();

   public Config() {
   }

   public void addGui(String name, GuiCustom gc) {
      GuiEntry entry = (GuiEntry)this.guis.get(name);
      if (entry == null) {
         entry = new GuiEntry();
         this.guis.put(name, entry);
      }

      int scale = gc.guiConfig.guiScale;
      if (scale == -1) {
         entry.standard = gc;
      } else if (scale == 0) {
         entry.auto = gc;
      } else if (scale == 1) {
         entry.small = gc;
      } else if (scale == 2) {
         entry.normal = gc;
      } else if (scale == 3) {
         entry.large = gc;
      }

   }

   public GuiCustom getGUI(String name) {
      return ((GuiEntry)this.guis.get(name)).getCurrentGUI();
   }

   public void tick() {
      this.guis.values().forEach((ge) -> {
         ge.getCurrentGUI().tick();
      });
   }
}
