package me.ichun.mods.ichunutil.client.gui.window;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementMinimize;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTitle;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;

public class Window {
   public final IWorkspace workspace;
   public int posX;
   public int posY;
   public int width;
   public int height;
   public int docked;
   public boolean minimized;
   public int clickX;
   public int clickY;
   public int clickId;
   public int oriWidth;
   public int oriHeight;
   public int minWidth;
   public int minHeight;
   public String titleLocale;
   public boolean hasTitle;
   public boolean isTab;
   public ArrayList<Element> elements = new ArrayList();
   public static final int BORDER_SIZE = 3;

   public Window(IWorkspace parent, int x, int y, int w, int h, int minW, int minH, String title, boolean hasTit) {
      this.workspace = parent;
      this.posX = x;
      this.posY = y;
      this.oriWidth = this.width = w;
      this.oriHeight = this.height = h;
      this.minWidth = minW;
      this.minHeight = minH;
      this.titleLocale = title;
      this.hasTitle = hasTit;
      if (this.hasTitle) {
         this.elements.add(new ElementTitle(this, 0, 0, parent.width - 13, 13, -100));
      }

      if (this.canMinimize()) {
         this.elements.add(new ElementMinimize(this, this.width - 13, 2, 10, 10, -100));
      }

      this.docked = -1;
   }

   public Window putInMiddleOfScreen() {
      this.posX = (this.workspace.width - this.width) / 2;
      this.posY = (this.workspace.height - this.height) / 2;
      this.resized();
      return this;
   }

   public void update() {
      for(Element element : this.elements) {
         element.update();
      }

   }

   public void draw(int mouseX, int mouseY) {
      this.setScissor();
      this.drawBackground();
      this.drawTitle();

      for(Element element : this.elements) {
         if (element.ignoreMinimized && this.minimized || !this.minimized) {
            boolean boundary = element.mouseInBoundary(mouseX, mouseY);
            boolean obstructed = false;
            if (boundary) {
               boolean found = false;

               for(int i = 0; i < this.workspace.levels.size(); ++i) {
                  for(int j = 0; j < ((ArrayList)this.workspace.levels.get(i)).size(); ++j) {
                     Window window = (Window)((ArrayList)this.workspace.levels.get(i)).get(j);
                     if (!found) {
                        if (window == this) {
                           found = true;
                        }
                     } else if (this.posX + mouseX >= window.posX && this.posX + mouseX <= window.posX + window.getWidth() && this.posY + mouseY >= window.posY && this.posY + mouseY <= window.posY + window.getHeight()) {
                        obstructed = true;
                        break;
                     }
                  }
               }
            }

            if (boundary && !obstructed) {
               this.workspace.hovering = true;
               if (this.workspace.elementHovered != element) {
                  this.workspace.elementHovered = element;
                  this.workspace.hoverTime = 0;
               }
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            element.draw(mouseX, mouseY, boundary && !obstructed);
         }
      }

      RendererHelper.endGlScissor();
   }

   public void setScissor() {
      if (this.isTab) {
         RendererHelper.startGlScissor(this.posX + 1, this.posY + 1 + 12, this.getWidth() - 2, this.getHeight() - 2 - 12);
      } else {
         RendererHelper.startGlScissor(this.posX + 1, this.posY + 1, this.getWidth() - 2, this.getHeight() - 2);
      }

   }

   public void drawBackground() {
      if (!this.minimized) {
         if (this.docked >= 0) {
            RendererHelper.drawColourOnScreen(this.workspace.currentTheme.windowBackground[0], this.workspace.currentTheme.windowBackground[1], this.workspace.currentTheme.windowBackground[2], 255, (double)(this.posX + 1), (double)(this.posY + 1), (double)(this.getWidth() - 2), (double)(this.getHeight() - 2), 0.0D);
         } else {
            RendererHelper.drawColourOnScreen(this.workspace.currentTheme.windowBorder[0], this.workspace.currentTheme.windowBorder[1], this.workspace.currentTheme.windowBorder[2], 255, (double)(this.posX + 1), (double)(this.posY + 1), (double)(this.getWidth() - 2), (double)(this.getHeight() - 2), 0.0D);
            RendererHelper.drawColourOnScreen(this.workspace.currentTheme.windowBackground[0], this.workspace.currentTheme.windowBackground[1], this.workspace.currentTheme.windowBackground[2], 255, (double)(this.posX + 3), (double)(this.posY + 3), (double)(this.getWidth() - 6), (double)(this.getHeight() - 6), 0.0D);
         }
      }

   }

   public void drawTitle() {
      if (this.hasTitle) {
         RendererHelper.drawColourOnScreen(this.workspace.currentTheme.windowBorder[0], this.workspace.currentTheme.windowBorder[1], this.workspace.currentTheme.windowBorder[2], 255, (double)(this.posX + 1), (double)(this.posY + 1), (double)(this.getWidth() - 2), 12.0D, 0.0D);
         String titleToRender = I18n.translateToLocal(this.titleLocale);

         while(titleToRender.length() > 1 && this.workspace.getFontRenderer().getStringWidth(titleToRender) > this.getWidth() - 6 - this.workspace.getFontRenderer().getStringWidth("  _") && !titleToRender.startsWith("...")) {
            if (titleToRender.endsWith("...")) {
               titleToRender = titleToRender.substring(0, titleToRender.length() - 4) + "...";
            } else {
               titleToRender = titleToRender.substring(0, titleToRender.length() - 1) + "...";
            }
         }

         this.workspace.getFontRenderer().drawString(titleToRender, (float)(this.posX + 4), (float)(this.posY + 3), Theme.getAsHex(this.workspace.currentTheme.font), false);
      }

   }

   public int onClick(int mouseX, int mouseY, int id) {
      this.clickX = mouseX;
      this.clickY = mouseY;
      this.clickId = id;
      boolean clickedElement = false;
      ArrayList<Element> els = new ArrayList(this.elements);

      for(int k = els.size() - 1; k >= 0; --k) {
         Element element = (Element)els.get(k);
         if (element.mouseInBoundary(mouseX, mouseY) && (this.minimized && element.ignoreMinimized || !this.minimized) && this.workspace.canClickOnElement(this, element) && element.onClick(mouseX, mouseY, id)) {
            if (id == 0) {
               this.workspace.elementDragged = element;
               this.workspace.elementDragX = this.posX + mouseX;
               this.workspace.elementDragY = this.posY + mouseY;
            }

            this.workspace.elementSelected = element;
            this.workspace.selectedMouseX = mouseX;
            this.workspace.selectedMouseY = mouseY;
            clickedElement = true;
         }
      }

      if (!clickedElement) {
         int borderClick = this.clickedOnBorder(mouseX, mouseY, id);
         if (borderClick > 1) {
            return borderClick + 2;
         }

         if (this.clickedOnTitle(mouseX, mouseY, id)) {
            return 1;
         }
      }

      return 0;
   }

   public int clickedOnBorder(int mouseX, int mouseY, int id) {
      return id == 0 && !this.minimized ? (mouseY <= 4 ? 1 : 0) + ((mouseX <= 4 ? 1 : 0) << 1) + ((mouseY >= this.getHeight() - 3 - 1 ? 1 : 0) << 2) + ((mouseX >= this.getWidth() - 3 - 1 ? 1 : 0) << 3) + 1 : 0;
   }

   public boolean clickedOnTitle(int mouseX, int mouseY, int id) {
      return mouseX >= 0 && mouseX <= this.getWidth() && mouseY >= 0 && mouseY <= 12;
   }

   public boolean allowMultipleInstances() {
      return false;
   }

   public boolean interactableWhileNoProjects() {
      return true;
   }

   public void elementTriggered(Element element) {
   }

   public void resized() {
      for(Element element : this.elements) {
         element.resized();
      }

   }

   public boolean canBeDragged() {
      return true;
   }

   public void shutdown() {
   }

   public void toggleMinimize() {
      this.minimized = !this.minimized;
      if (this.docked >= 0) {
         this.workspace.redock(this.docked, (Window)null);
      }

   }

   public boolean isStatic() {
      return false;
   }

   public boolean canMinimize() {
      return true;
   }

   public boolean invertMinimizeSymbol() {
      return false;
   }

   public int getHeight() {
      return this.minimized ? 13 : this.height;
   }

   public int getWidth() {
      return this.width;
   }
}
