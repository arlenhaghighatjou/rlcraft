package me.ichun.mods.ichunutil.client.gui.window;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import net.minecraft.util.text.translation.I18n;

public class WindowPopup extends Window {
   public String message;

   public WindowPopup(IWorkspace parent, int x, int y, int w, int h, int minW, int minH, String msg) {
      super(parent, 0, 0, w, h, minW, minH, "window.popup.title", true);
      this.message = msg;
      this.elements.add(new ElementButton(this, this.width / 2 - 30, this.height - 25, 60, 16, 3, false, 2, 1, "element.button.ok"));
   }

   public void draw(int mouseX, int mouseY) {
      super.draw(mouseX, mouseY);
      if (!this.minimized) {
         this.workspace.getFontRenderer().drawString(I18n.translateToLocal(this.message), (float)(this.posX + 11), (float)(this.posY + 20), Theme.getAsHex(this.workspace.currentTheme.font), false);
      }

   }

   public void elementTriggered(Element element) {
      this.workspace.removeWindow(this, true);
   }

   public boolean allowMultipleInstances() {
      return true;
   }
}
