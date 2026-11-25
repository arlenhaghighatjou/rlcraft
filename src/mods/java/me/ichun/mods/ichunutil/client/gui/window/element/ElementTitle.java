package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;
import net.minecraft.util.text.translation.I18n;

public class ElementTitle extends Element {
   public ElementTitle(Window window, int x, int y, int w, int h, int ID) {
      super(window, x, y, w, h, ID, true);
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
   }

   public void resized() {
      this.posX = 0;
      this.posY = 0;
      this.width = this.parent.width - 13;
      this.height = 13;
   }

   public String tooltip() {
      String titleToRender = I18n.translateToLocal(this.parent.titleLocale);
      int var10000 = this.parent.workspace.getFontRenderer().getStringWidth(titleToRender);
      int var10001 = this.parent.getWidth();
      Window var10002 = this.parent;
      return var10000 > var10001 - 3 * 2 - this.parent.workspace.getFontRenderer().getStringWidth("  _") ? this.parent.titleLocale : null;
   }
}
