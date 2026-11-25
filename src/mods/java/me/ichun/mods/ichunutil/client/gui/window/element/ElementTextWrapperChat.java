package me.ichun.mods.ichunutil.client.gui.window.element;

import java.util.List;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class ElementTextWrapperChat extends ElementTextWrapper {
   public ElementTextWrapperChat(Window window, int x, int y, int w, int h, int ID, boolean igMin) {
      super(window, x, y, w, h, ID, igMin, true);
   }

   public void drawText(int mouseX, int mouseY, boolean hover) {
      GlStateManager.pushMatrix();

      for(int i = 0; i < this.text.size(); ++i) {
         String msg = (String)this.text.get(i);
         List list = this.parent.workspace.getFontRenderer().listFormattedStringToWidth(msg, this.width - 12);
         boolean onName = true;

         for(int j = 0; j < list.size(); ++j) {
            String[] strings = ((String)list.get(j)).split(": ");
            this.parent.workspace.getFontRenderer().drawString(strings[0], (float)(this.getPosX() + (j == 0 ? 4 : 12)), (float)(this.getPosY() + 4), onName ? this.getRandomColourForName(strings[0]) : Theme.getAsHex(this.parent.workspace.currentTheme.fontChat), false);
            if (strings.length > 1) {
               onName = false;
               this.parent.workspace.getFontRenderer().drawString(": " + strings[1], (float)(this.getPosX() + this.parent.workspace.getFontRenderer().getStringWidth(strings[0]) + 4), (float)(this.getPosY() + 4), Theme.getAsHex(this.parent.workspace.currentTheme.fontChat), false);
            }

            GlStateManager.translate(0.0F, (float)(this.parent.workspace.getFontRenderer().FONT_HEIGHT + 2), 0.0F);
         }
      }

      GlStateManager.popMatrix();
   }

   public int getRandomColourForName(String s) {
      return s.equalsIgnoreCase("System") ? 16763904 : Math.abs(s.hashCode()) & 16777215;
   }
}
