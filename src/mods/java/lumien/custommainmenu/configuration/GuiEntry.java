package lumien.custommainmenu.configuration;

import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.Minecraft;

public class GuiEntry {
   GuiCustom standard;
   GuiCustom auto;
   GuiCustom small;
   GuiCustom normal;
   GuiCustom large;

   public GuiEntry() {
   }

   public GuiCustom getCurrentGUI() {
      int guiScale = Minecraft.getMinecraft().gameSettings.guiScale;
      if (guiScale == 0 && this.auto != null) {
         return this.auto;
      } else if (guiScale == 1 && this.small != null) {
         return this.small;
      } else if (guiScale == 2 && this.normal != null) {
         return this.normal;
      } else {
         return guiScale == 3 && this.large != null ? this.large : this.standard;
      }
   }
}
