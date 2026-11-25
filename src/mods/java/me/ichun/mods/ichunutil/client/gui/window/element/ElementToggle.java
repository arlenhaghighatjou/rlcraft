package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.util.text.translation.I18n;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class ElementToggle extends Element {
   public int anchorH;
   public int spaceH;
   public int anchorV;
   public int spaceV;
   public String text;
   public String tooltip;
   public boolean toggledState;

   public ElementToggle(Window window, int x, int y, int w, int h, int ID, boolean igMin, int sideH, int sideV, String Text, String Tooltip, boolean state) {
      super(window, x, y, w, h, ID, igMin);
      this.anchorH = sideH;
      switch(this.anchorH) {
      case 0:
         this.spaceH = this.posX;
         break;
      case 1:
         this.spaceH = this.parent.width - this.posX - this.width;
      }

      this.anchorV = sideV;
      switch(this.anchorV) {
      case 0:
         this.spaceV = this.posY;
         break;
      case 1:
         this.spaceV = this.parent.height - this.posY - this.height;
      }

      this.text = Text;
      this.tooltip = Tooltip;
      this.toggledState = state;
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

      this.parent.workspace.getFontRenderer().drawString(I18n.translateToLocal(this.text), (float)(this.getPosX() + this.width / 2 - this.parent.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(this.text)) / 2), (float)(this.getPosY() + this.height - this.height / 2 - this.parent.workspace.getFontRenderer().FONT_HEIGHT / 2), Theme.getAsHex(!this.toggledState ? this.parent.workspace.currentTheme.elementButtonToggleHover : this.parent.workspace.currentTheme.font), false);
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      this.toggledState = !this.toggledState;
      this.parent.elementTriggered(this);
      return true;
   }

   public void resized() {
      switch(this.anchorH) {
      case 0:
         this.posX = this.spaceH;
         break;
      case 1:
         this.posX = this.parent.width - this.spaceH - this.width;
         break;
      case 2:
         this.posX = this.parent.width / 2 - this.width / 2;
      }

      switch(this.anchorV) {
      case 0:
         this.posY = this.spaceV;
         break;
      case 1:
         this.posY = this.parent.height - this.spaceV - this.height;
         break;
      case 2:
         this.posY = this.parent.height / 2 - this.height / 2;
      }

   }

   public String tooltip() {
      return this.tooltip;
   }
}
