package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.gui.GuiCustom;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;

public class ActionOpenFolder implements IAction {
   String folderName;

   public ActionOpenFolder(String folderName) {
      this.folderName = folderName;
   }

   public void perform(Object source, GuiCustom parent) {
      if (!EagRuntime.openResourceFolder(this.folderName)) {
         EagRuntime.showPopup("Could not open folder " + this.folderName);
      }

   }
}
