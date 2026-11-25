package me.ichun.mods.ichunutil.client.gui.window;

import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.render.RendererHelper;

public class WindowTopDockBase extends Window {
   public WindowTopDockBase(IWorkspace parent, int w, int h) {
      super(parent, 0, 0, w, h, 20, 20, "", false);
   }

   public int clickedOnBorder(int mouseX, int mouseY, int id) {
      return 0;
   }

   public boolean clickedOnTitle(int mouseX, int mouseY, int id) {
      return false;
   }

   public void resized() {
      this.posX = 0;
      this.posY = 0;
      this.width = this.workspace.width;
      this.height = 20;

      for(Element element : this.elements) {
         element.resized();
      }

   }

   public boolean canBeDragged() {
      return false;
   }

   public boolean canMinimize() {
      return false;
   }

   public void toggleMinimize() {
   }

   public boolean isStatic() {
      return true;
   }

   public void setScissor() {
      RendererHelper.startGlScissor(this.posX, this.posY, this.getWidth(), this.getHeight());
   }

   public void drawBackground() {
      RendererHelper.drawColourOnScreen(this.workspace.currentTheme.windowBackground[0], this.workspace.currentTheme.windowBackground[1], this.workspace.currentTheme.windowBackground[2], 255, (double)this.posX, (double)this.posY, (double)this.getWidth(), (double)this.getHeight(), 0.0D);
   }

   public int getHeight() {
      return 20;
   }
}
