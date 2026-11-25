package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.renderer.GlStateManager;

public class ElementMinimize extends Element {
   public ElementMinimize(Window window, int x, int y, int w, int h, int id) {
      super(window, x, y, w, h, id, true);
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      if (this.parent.docked < 0 || this.parent.docked == 2) {
         if (hover) {
            RendererHelper.drawColourOnScreen(Theme.getAsHex(this.parent.workspace.currentTheme.font), 255, (double)this.getPosX() - 0.5D, (double)this.getPosY(), (double)this.width, 1.0D, 0.0D);
            RendererHelper.drawColourOnScreen(Theme.getAsHex(this.parent.workspace.currentTheme.font), 255, (double)this.getPosX() - 0.5D, (double)this.getPosY(), 1.0D, (double)this.height, 0.0D);
            RendererHelper.drawColourOnScreen(Theme.getAsHex(this.parent.workspace.currentTheme.font), 255, (double)this.getPosX() - 0.5D, (double)(this.getPosY() + this.height - 1), (double)this.width, 1.0D, 0.0D);
            RendererHelper.drawColourOnScreen(Theme.getAsHex(this.parent.workspace.currentTheme.font), 255, (double)(this.getPosX() + this.width - 1) - 0.5D, (double)this.getPosY(), 1.0D, (double)this.height, 0.0D);
         }

         GlStateManager.pushMatrix();
         float scale = 2.0F;
         GlStateManager.scale(scale, scale, scale);
         if ((!this.parent.minimized || this.parent.invertMinimizeSymbol()) && (this.parent.minimized || !this.parent.invertMinimizeSymbol())) {
            this.parent.workspace.getFontRenderer().drawString("▲", (float)((int)((float)(this.getPosX() + 2) / scale)), (float)((int)((float)(this.getPosY() - 2) / scale)), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
         } else {
            this.parent.workspace.getFontRenderer().drawString("▼", (float)((int)((float)(this.getPosX() + 2) / scale)), (float)((int)((float)(this.getPosY() - 2) / scale)), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
         }

         GlStateManager.popMatrix();
      }
   }

   public void resized() {
      this.posX = this.parent.width - 13;
      this.posY = 2;
   }

   public String tooltip() {
      if (this.parent.docked >= 0 && this.parent.docked != 2) {
         return null;
      } else {
         return !this.parent.minimized ? "element.minimize" : "element.expand";
      }
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      if (id == 0 && (this.parent.docked < 0 || this.parent.docked == 2)) {
         this.parent.toggleMinimize();
      }

      return true;
   }
}
