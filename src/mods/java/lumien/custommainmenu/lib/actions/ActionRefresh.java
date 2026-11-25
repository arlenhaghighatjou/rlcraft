package lumien.custommainmenu.lib.actions;

import lumien.custommainmenu.CustomMainMenu;
import lumien.custommainmenu.gui.GuiCustom;
import net.minecraft.client.gui.GuiMainMenu;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class ActionRefresh implements IAction {
   public ActionRefresh() {
   }

   public void perform(Object source, GuiCustom menu) {
      CustomMainMenu.INSTANCE.reload();
      if (Keyboard.isKeyDown(42)) {
         menu.mc.refreshResources();
      }

      menu.mc.displayGuiScreen(new GuiMainMenu());
   }
}
