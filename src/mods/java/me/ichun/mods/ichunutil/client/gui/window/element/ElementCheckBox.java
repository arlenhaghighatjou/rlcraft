package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import org.lwjgl.input.Mouse;

public class ElementCheckBox extends ElementToggle {
   public ElementCheckBox(Window window, int x, int y, int ID, boolean igMin, int sideH, int sideV, String Tooltip, boolean state) {
      super(window, x, y, 9, 9, ID, igMin, sideH, sideV, "", Tooltip, state);
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementButtonBorder[0], this.parent.workspace.currentTheme.elementButtonBorder[1], this.parent.workspace.currentTheme.elementButtonBorder[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)this.width, (double)this.height, 0.0D);
      if (hover) {
         if (Mouse.isButtonDown(0)) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementButtonClick[0], this.parent.workspace.currentTheme.elementButtonClick[1], this.parent.workspace.currentTheme.elementButtonClick[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
         } else {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementButtonBackgroundHover[0], this.parent.workspace.currentTheme.elementButtonBackgroundHover[1], this.parent.workspace.currentTheme.elementButtonBackgroundHover[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
         }
      } else if (this.toggledState) {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementButtonToggle[0], this.parent.workspace.currentTheme.elementButtonToggle[1], this.parent.workspace.currentTheme.elementButtonToggle[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
      } else {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementButtonBackgroundInactive[0], this.parent.workspace.currentTheme.elementButtonBackgroundInactive[1], this.parent.workspace.currentTheme.elementButtonBackgroundInactive[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2), (double)(this.height - 2), 0.0D);
      }

      if (this.toggledState) {
         this.parent.workspace.getFontRenderer().drawString("X", (float)(this.getPosX() + 2), (float)(this.getPosY() + this.height - this.height / 2 - this.parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(!this.toggledState ? this.parent.workspace.currentTheme.elementButtonToggleHover : this.parent.workspace.currentTheme.font), false);
      }

   }
}
