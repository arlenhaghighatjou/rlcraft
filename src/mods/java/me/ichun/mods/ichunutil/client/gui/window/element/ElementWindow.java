package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.WindowTabs;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.util.text.translation.I18n;

public class ElementWindow extends Element {
   public Window mountedWindow;
   public int oriWidth;
   public int oriHeight;

   public ElementWindow(Window window, int x, int y, int w, int h, int ID, Window mount) {
      super(window, x, y, w, h, ID, true);
      this.mountedWindow = mount;
      this.oriWidth = this.mountedWindow.width;
      this.oriHeight = this.mountedWindow.height;
   }

   public void update() {
      this.mountedWindow.update();
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      String titleToRender = I18n.translateToLocal(this.mountedWindow.titleLocale);

      while(titleToRender.length() > 1 && this.parent.workspace.getFontRenderer().getStringWidth(titleToRender) > this.width && !titleToRender.startsWith("... ")) {
         if (titleToRender.endsWith("... ")) {
            titleToRender = titleToRender.substring(0, titleToRender.length() - 5) + "... ";
         } else {
            titleToRender = titleToRender.substring(0, titleToRender.length() - 1) + "... ";
         }
      }

      this.parent.workspace.getFontRenderer().drawString(titleToRender, (float)(this.parent.posX + this.posX + 4), (float)(this.parent.posY + this.posY + 3), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
      this.mountedWindow.docked = this.parent.docked;
      this.mountedWindow.posX = this.parent.posX;
      this.mountedWindow.posY = this.parent.posY;
      this.mountedWindow.width = this.parent.width;
      this.mountedWindow.height = this.parent.height;
      if (this.parent instanceof WindowTabs) {
         WindowTabs tab = (WindowTabs)this.parent;
         if (tab.minimized && this.id != tab.tabs.size() - 1) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.tabSideInactive[0], this.parent.workspace.currentTheme.tabSideInactive[1], this.parent.workspace.currentTheme.tabSideInactive[2], 255, (double)(this.getPosX() + this.width + 1), (double)(this.getPosY() + 1), 1.0D, (double)this.height, 0.0D);
         }

         if (tab.selectedTab == this.id && !tab.minimized) {
            this.mountedWindow.isTab = true;
            this.mountedWindow.draw(mouseX, mouseY);
            this.mountedWindow.isTab = false;
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.windowBorder[0], this.parent.workspace.currentTheme.windowBorder[1], this.parent.workspace.currentTheme.windowBorder[2], 255, (double)this.getPosX(), (double)(this.getPosY() + 1 + this.height), (double)(this.width + 3), 2.0D, 0.0D);
         } else if (hover) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.tabBorder[0], this.parent.workspace.currentTheme.tabBorder[1], this.parent.workspace.currentTheme.tabBorder[2], 255, (double)(this.getPosX() + 2), (double)(this.getPosY() + 1), (double)this.width, 1.0D, 0.0D);
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.tabBorder[0], this.parent.workspace.currentTheme.tabBorder[1], this.parent.workspace.currentTheme.tabBorder[2], 255, (double)(this.getPosX() + 2), (double)(this.getPosY() + 1), 1.0D, (double)this.height, 0.0D);
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.tabBorder[0], this.parent.workspace.currentTheme.tabBorder[1], this.parent.workspace.currentTheme.tabBorder[2], 255, (double)(this.getPosX() + 2), (double)(this.getPosY() + 1 + this.height - 1), (double)this.width, 1.0D, 0.0D);
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.tabBorder[0], this.parent.workspace.currentTheme.tabBorder[1], this.parent.workspace.currentTheme.tabBorder[2], 255, (double)(this.getPosX() + this.width + 1), (double)(this.getPosY() + 1), 1.0D, (double)this.height, 0.0D);
         }
      }

   }

   public void resized() {
      if (this.parent instanceof WindowTabs) {
         WindowTabs tab = (WindowTabs)this.parent;
         int space = tab.getWidth() - 3 * 2 - tab.workspace.getFontRenderer().getStringWidth("  _");
         int totalSpace = 0;

         for(ElementWindow tab1 : tab.tabs) {
            totalSpace += tab.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(tab1.mountedWindow.titleLocale) + " ");
         }

         if (totalSpace > space) {
            this.posX = 3 + this.id * (space / tab.tabs.size());
            this.posY = 0;
            this.width = space / tab.tabs.size();
            this.height = 12;
         } else {
            this.posX = 3;

            for(int i = 0; i < this.id; ++i) {
               this.posX += tab.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(((ElementWindow)tab.tabs.get(i)).mountedWindow.titleLocale) + " ");
            }

            this.posY = 0;
            this.width = tab.workspace.getFontRenderer().getStringWidth(I18n.translateToLocal(this.mountedWindow.titleLocale) + " ");
            this.height = 12;
         }

         this.mountedWindow.docked = this.parent.docked;
         this.mountedWindow.posX = this.parent.posX;
         this.mountedWindow.posY = this.parent.posY;
         this.mountedWindow.width = this.parent.width;
         this.mountedWindow.height = this.parent.height;
      }

      this.mountedWindow.resized();
   }

   public String tooltip() {
      String titleToRender = I18n.translateToLocal(this.mountedWindow.titleLocale);
      return this.parent.workspace.getFontRenderer().getStringWidth(titleToRender) > this.width ? this.mountedWindow.titleLocale : null;
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      if (id == 0 && this.parent instanceof WindowTabs) {
         ((WindowTabs)this.parent).selectedTab = this.id;
         if (this.parent.minimized) {
            this.parent.toggleMinimize();
         }
      }

      return true;
   }
}
