package me.ichun.mods.ichunutil.client.gui.window;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementWindow;
import me.ichun.mods.ichunutil.client.gui.window.element.ITextInput;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.translation.I18n;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class IWorkspace extends GuiScreen {
   public int oldWidth;
   public int oldHeight;
   public int liveTime;
   public boolean resize;
   public Element elementHovered;
   public int hoverTime;
   public boolean hovering;
   public int tooltipTime = 20;
   public Element elementDragged;
   public int elementDragX;
   public int elementDragY;
   public Element elementSelected;
   public int selectedMouseX;
   public int selectedMouseY;
   public Window windowDragged;
   public int dragType;
   public boolean mouseLeftDown;
   public boolean mouseRightDown;
   public boolean mouseMiddleDown;
   public int VARIABLE_LEVEL = 4;
   public int TOP_DOCK_HEIGHT = 19;
   public Theme currentTheme = Theme.copyInstance();
   public ArrayList<ArrayList<Window>> levels = new ArrayList();

   public IWorkspace() {
   }

   public abstract boolean canClickOnElement(Window var1, Element var2);

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void initGui() {
      super.initGui();
      this.resize = true;
      this.screenResize();
      Keyboard.enableRepeatEvents(true);
   }

   public void updateScreen() {
      if (this.resize) {
         this.resize = false;
         this.screenResize();
      }

      if (this.elementHovered != null) {
         ++this.hoverTime;
      }

      for(int i = this.levels.size() - 1; i >= 0; --i) {
         if (((ArrayList)this.levels.get(i)).isEmpty() && i >= this.VARIABLE_LEVEL) {
            this.levels.remove(i);
         } else {
            for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
               ((Window)((ArrayList)this.levels.get(i)).get(j)).update();
            }
         }
      }

      ++this.liveTime;
   }

   public boolean drawWindows(int mouseX, int mouseY) {
      this.hovering = false;
      boolean hasClicked = false;
      boolean onWindow = false;
      Element prevElementSelected = this.elementSelected;
      this.elementSelected = null;
      GlStateManager.enableDepth();
      GlStateManager.depthMask(true);
      GlStateManager.translate(0.0F, 0.0F, 100.0F);

      for(int i = this.levels.size() - 1; i >= 0; --i) {
         for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
            Window window = (Window)((ArrayList)this.levels.get(i)).get(j);
            if (mouseX >= window.posX && mouseX <= window.posX + window.getWidth() && mouseY >= window.posY && mouseY <= window.posY + window.getHeight()) {
               onWindow = true;
               if (!hasClicked && this.liveTime > 5) {
                  if (Mouse.isButtonDown(0) && !this.mouseLeftDown) {
                     this.windowDragged = window;
                     this.dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 0);
                     hasClicked = true;
                  }

                  if (Mouse.isButtonDown(1) && !this.mouseRightDown) {
                     this.windowDragged = window;
                     this.dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 1);
                     hasClicked = true;
                  }

                  if (Mouse.isButtonDown(2) && !this.mouseMiddleDown) {
                     this.windowDragged = window;
                     this.dragType = window.onClick(mouseX - window.posX, mouseY - window.posY, 2);
                     hasClicked = true;
                  }
               }
            }

            window.draw(mouseX - window.posX, mouseY - window.posY);
         }

         GlStateManager.translate(0.0F, 0.0F, -10.0F);
      }

      RendererHelper.endGlScissor();
      if (!hasClicked) {
         if ((!Mouse.isButtonDown(0) || this.mouseLeftDown) && (!Mouse.isButtonDown(1) || this.mouseRightDown) && (!Mouse.isButtonDown(2) || this.mouseMiddleDown) || prevElementSelected == null || mouseX >= prevElementSelected.getPosX() && mouseX <= prevElementSelected.getPosX() + prevElementSelected.width && mouseY >= prevElementSelected.getPosY() && mouseY <= prevElementSelected.getPosY() + prevElementSelected.height) {
            this.elementSelected = prevElementSelected;
         } else {
            prevElementSelected.deselected();
         }
      } else if (this.elementSelected != prevElementSelected) {
         if (this.elementSelected != null) {
            this.elementSelected.selected();
            if (this.elementSelected instanceof ITextInput) {
               this.elementSelected.onClick(this.selectedMouseX, this.selectedMouseY, 0);
            }
         }

         if (prevElementSelected != null) {
            prevElementSelected.deselected();
         }
      } else {
         this.elementSelected = prevElementSelected;
      }

      return onWindow;
   }

   public void updateElementHovered(int mouseX, int mouseY, int scroll) {
      if (!this.hovering) {
         this.elementHovered = null;
         this.hoverTime = 0;
      } else if (this.elementHovered != null) {
         boolean activated = false;
         if (scroll != 0) {
            activated = this.elementHovered.mouseScroll(mouseX - this.elementHovered.parent.posX, mouseY - this.elementHovered.parent.posY, Math.round((float)(scroll / 120)));
         }

         if (activated) {
            if (this.elementSelected != null) {
               this.elementSelected.deselected();
            }

            this.elementHovered.onClick(mouseX - this.elementHovered.parent.posX, mouseY - this.elementHovered.parent.posY, 2);
            this.elementSelected = this.elementHovered;
         }

         String tooltip = this.elementHovered.tooltip();
         if (this.hoverTime > this.tooltipTime && tooltip != null) {
            GlStateManager.translate(0.0F, 0.0F, 20.0F * (float)this.levels.size());
            List<String> tips = Splitter.on("\n").splitToList(tooltip);
            List<String> tipss = Splitter.on("\\n").splitToList(tooltip);
            List<String> var14 = new ArrayList(tipss.size() > tips.size() ? tipss : tips);
            if (var14.size() == 1) {
               var14.add(I18n.translateToLocal((String)var14.get(0)));
               var14.remove(0);
            }

            int xOffset = 5;
            int yOffset = 20;
            int longest = 0;

            for(String tip : var14) {
               int length = this.fontRenderer.getStringWidth(tip);
               if (length > longest) {
                  longest = length;
               }
            }

            int size = longest + 4;
            int ySize = 1 + var14.size() * (this.fontRenderer.FONT_HEIGHT + 1);
            if (this.width - mouseX < size) {
               xOffset -= size - (this.width - mouseX) + 20;
            }

            if (this.height - (mouseY + ySize + yOffset) < 0) {
               yOffset = -20;
            }

            RendererHelper.drawColourOnScreen(this.currentTheme.windowBorder[0], this.currentTheme.windowBorder[1], this.currentTheme.windowBorder[2], 255, (double)(mouseX + xOffset), (double)(mouseY + yOffset), (double)(longest + 4), (double)ySize, 0.0D);
            RendererHelper.drawColourOnScreen(this.currentTheme.windowBackground[0], this.currentTheme.windowBackground[1], this.currentTheme.windowBackground[2], 255, (double)(mouseX + xOffset + 1), (double)(mouseY + yOffset + 1), (double)(longest + 4 - 2), (double)(ySize - 2), 0.0D);

            for(int i = 0; i < var14.size(); ++i) {
               FontRenderer var10000 = this.fontRenderer;
               String var10001 = (String)var14.get(i);
               float var10002 = (float)(mouseX + xOffset + 2);
               float var10003 = (float)(mouseY + yOffset + 2 + i * (this.fontRenderer.FONT_HEIGHT + 1));
               Theme var10004 = this.currentTheme;
               var10000.drawString(var10001, var10002, var10003, Theme.getAsHex(this.currentTheme.font), false);
            }
         }
      }

   }

   public void updateWindowDragged(int mouseX, int mouseY) {
      if (this.windowDragged != null) {
         if ((this.windowDragged.clickId != 0 || this.mouseLeftDown) && (this.windowDragged.clickId != 1 || this.mouseRightDown) && (this.windowDragged.clickId != 2 || this.mouseMiddleDown)) {
            this.bringWindowToFront(this.windowDragged);
            if (this.dragType == 1) {
               if (this.windowDragged.canBeDragged()) {
                  int moveX = this.windowDragged.clickX - (mouseX - this.windowDragged.posX);
                  int moveY = this.windowDragged.clickY - (mouseY - this.windowDragged.posY);
                  if (this.windowDragged.docked < 0) {
                     this.windowDragged.posX -= moveX;
                     this.windowDragged.posY -= moveY;
                  } else if (Math.sqrt((double)(moveX * moveX + moveY + moveY)) > 5.0D) {
                     this.removeFromDock(this.windowDragged);
                     this.windowDragged.posX -= moveX;
                     this.windowDragged.posY -= moveY;
                  }

                  boolean tabbed = false;

                  for(int i = this.levels.size() - 1; i >= 0; --i) {
                     for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
                        Window window = (Window)((ArrayList)this.levels.get(i)).get(j);
                        if (!tabbed && window.canBeDragged() && window != this.windowDragged && mouseX - window.posX >= 0 && mouseX - window.posX <= window.getWidth() && mouseY - window.posY >= 0 && mouseY - window.posY <= 12) {
                           WindowTabs tabs;
                           if (window instanceof WindowTabs) {
                              tabs = (WindowTabs)window;
                           } else {
                              tabs = new WindowTabs(this, window);
                           }

                           if (this.windowDragged.minimized) {
                              this.windowDragged.toggleMinimize();
                           }

                           tabs.addWindow(this.windowDragged, true);
                           ((ArrayList)this.levels.get(i)).remove(j);
                           ((ArrayList)this.levels.get(i)).add(j, tabs);
                           if (i < this.VARIABLE_LEVEL) {
                              this.redock(i, (Window)null);
                           }

                           this.removeWindow(this.windowDragged);
                           this.windowDragged = null;
                           tabbed = true;
                        }
                     }
                  }

                  if (mouseX <= 10) {
                     this.addToDock(0, this.windowDragged);
                     this.windowDragged = null;
                  }

                  if (mouseX >= this.width - 10) {
                     this.addToDock(1, this.windowDragged);
                     this.windowDragged = null;
                  }
               }

               if (this.windowDragged != null) {
                  this.windowDragged.resized();
               }
            }

            if (this.dragType >= 2) {
               int bordersClicked = this.dragType - 3;
               if ((bordersClicked & 1) == 1 && (this.windowDragged.docked != 0 && this.windowDragged.docked != 1 || ((ArrayList)this.levels.get(this.windowDragged.docked)).isEmpty() || ((ArrayList)this.levels.get(this.windowDragged.docked)).get(0) != this.windowDragged)) {
                  this.windowDragged.height += this.windowDragged.clickY - (mouseY - this.windowDragged.posY);
                  this.windowDragged.posY -= this.windowDragged.clickY - (mouseY - this.windowDragged.posY);
                  if (this.windowDragged.getHeight() < this.windowDragged.minHeight) {
                     int resize = this.windowDragged.getHeight() - this.windowDragged.minHeight;
                     this.windowDragged.posY += resize;
                     this.windowDragged.height -= resize;
                  } else {
                     this.windowDragged.clickY = mouseY - this.windowDragged.posY;
                  }
               }

               if ((bordersClicked >> 1 & 1) == 1 && this.windowDragged.docked != 0) {
                  this.windowDragged.width += this.windowDragged.clickX - (mouseX - this.windowDragged.posX);
                  this.windowDragged.posX -= this.windowDragged.clickX - (mouseX - this.windowDragged.posX);
                  if (this.windowDragged.getWidth() < this.windowDragged.minWidth) {
                     int resize = this.windowDragged.getWidth() - this.windowDragged.minWidth;
                     this.windowDragged.posX += resize;
                     this.windowDragged.width -= resize;
                  } else {
                     this.windowDragged.clickX = mouseX - this.windowDragged.posX;
                  }
               }

               if ((bordersClicked >> 2 & 1) == 1) {
                  this.windowDragged.height -= this.windowDragged.clickY - (mouseY - this.windowDragged.posY);
                  if (this.windowDragged.getHeight() < this.windowDragged.minHeight) {
                     this.windowDragged.height = this.windowDragged.minHeight;
                  } else {
                     this.windowDragged.clickY = mouseY - this.windowDragged.posY;
                  }
               }

               if ((bordersClicked >> 3 & 1) == 1 && this.windowDragged.docked != 1) {
                  this.windowDragged.width -= this.windowDragged.clickX - (mouseX - this.windowDragged.posX);
                  if (this.windowDragged.getWidth() < this.windowDragged.minWidth) {
                     this.windowDragged.width = this.windowDragged.minWidth;
                  } else {
                     this.windowDragged.clickX = mouseX - this.windowDragged.posX;
                  }
               }

               this.windowDragged.resized();
               if (this.windowDragged.docked >= 0) {
                  this.redock(this.windowDragged.docked, this.windowDragged);
               }
            }
         } else {
            this.windowDragged = null;
         }
      }

   }

   public void updateElementDragged(int mouseX, int mouseY) {
      if (this.elementDragged != null) {
         if (!this.mouseLeftDown) {
            this.elementDragged = null;
         } else if ((mouseX - this.elementDragged.parent.posX < 0 || mouseX - this.elementDragged.parent.posX > this.elementDragged.parent.getWidth() || mouseY - this.elementDragged.parent.posY < 0 || mouseY - this.elementDragged.parent.posY > 12) && this.elementDragged instanceof ElementWindow) {
            WindowTabs var10000 = (WindowTabs)((ElementWindow)this.elementDragged).parent;
            WindowTabs.detach((ElementWindow)this.elementDragged);
            ElementWindow element = (ElementWindow)this.elementDragged;
            this.windowDragged = element.mountedWindow;
            this.windowDragged.docked = -1;
            this.dragType = 1;
            this.windowDragged.width = element.oriWidth;
            this.windowDragged.height = element.oriHeight;
            this.windowDragged.posX = mouseX - this.windowDragged.getWidth() / 2;
            this.windowDragged.posY = mouseY - 6;
            this.windowDragged.resized();
            this.elementDragged = null;
         }
      }

   }

   public void updateKeyStates() {
      this.mouseLeftDown = Mouse.isButtonDown(0);
      this.mouseRightDown = Mouse.isButtonDown(1);
      this.mouseMiddleDown = Mouse.isButtonDown(2);
   }

   public boolean canUseDocks() {
      return true;
   }

   public void addToDock(int dock, Window window) {
      if (this.canUseDocks() && window != null && window.docked < 0) {
         if (window.minimized) {
            window.toggleMinimize();
         }

         ArrayList<Window> docked = (ArrayList)this.levels.get(dock);
         window.docked = dock;
         window.oriHeight = window.height;
         window.oriWidth = window.width;
         docked.add(window);

         for(int i = this.VARIABLE_LEVEL; i < this.levels.size(); ++i) {
            ((ArrayList)this.levels.get(i)).remove(window);
         }

         this.redock(dock, (Window)null);
      }

   }

   public void redock(int dock, Window pref) {
      ArrayList<Window> docked = (ArrayList)this.levels.get(dock);
      int prefInt = -2;
      if (pref != null) {
         for(int j = 0; j < docked.size(); ++j) {
            if (docked.get(j) == pref) {
               prefInt = j;
            }
         }
      }

      for(int j = 0; j < docked.size(); ++j) {
         Window window = (Window)docked.get(j);
         if (dock == 0) {
            window.posX = -1;
         } else if (dock == 1) {
            window.posX = this.width - window.getWidth() + 1;
         }

         if (dock <= 1) {
            if (prefInt != -2) {
               ((Window)docked.get(0)).width = ((Window)docked.get(prefInt)).width;
            } else if (j == 0) {
               window.posY = this.TOP_DOCK_HEIGHT;
            } else {
               window.width = ((Window)docked.get(0)).width;
               window.posY = ((Window)docked.get(j - 1)).posY + (((Window)docked.get(j - 1)).minimized ? 12 : (((Window)docked.get(j - 1)).height + ((Window)docked.get(j - 1)).posY + 2 >= this.height ? ((Window)docked.get(j - 1)).oriHeight : ((Window)docked.get(j - 1)).height));
               ((Window)docked.get(j - 1)).height = window.posY - ((Window)docked.get(j - 1)).posY + 2;
            }

            if (j - 1 == prefInt) {
               window.height += window.posY - (((Window)docked.get(j - 1)).posY + ((Window)docked.get(j - 1)).height) + 2;
               window.posY -= window.posY - (((Window)docked.get(j - 1)).posY + ((Window)docked.get(j - 1)).height) + 2;
            }

            if (j + 1 == prefInt) {
               window.height = ((Window)docked.get(j + 1)).posY - window.posY + 2;
               if (window.height < window.minHeight + 2) {
                  window.height = window.minHeight + 2;
                  ((Window)docked.get(prefInt)).posY = window.posY + window.height - 2;
                  this.windowDragged = null;
                  this.dragType = 0;
               }
            }

            window.width = ((Window)docked.get(0)).width;
            this.redock(2, (Window)null);
         } else if (dock == 2) {
            int pX1 = -1;
            int pX2 = this.width + 1;
            if (!((ArrayList)this.levels.get(0)).isEmpty()) {
               pX1 = ((Window)((ArrayList)this.levels.get(0)).get(0)).width - 2;
            }

            if (!((ArrayList)this.levels.get(1)).isEmpty()) {
               pX2 = ((Window)((ArrayList)this.levels.get(1)).get(0)).posX + 1;
            }

            window.posX = pX1;
            window.width = pX2 - pX1;
            window.posY = this.height - window.getHeight() + 1;
         }

         window.resized();
      }

      this.screenResize();
   }

   public void removeFromDock(Window window) {
      for(int i = 2; i >= 0; --i) {
         ArrayList<Window> docked = (ArrayList)this.levels.get(i);

         for(int j = docked.size() - 1; j >= 0; --j) {
            Window window1 = (Window)docked.get(j);
            if (window1 == window) {
               docked.remove(j);
               this.redock(i, (Window)null);
               break;
            }
         }
      }

      window.docked = -1;
      window.height = window.oriHeight;
      window.width = window.oriWidth;
      this.addWindowOnTop(window);
      window.resized();
      this.redock(2, (Window)null);
   }

   public void removeWindow(Window window) {
      this.removeWindow(window, false);
   }

   public void removeWindow(Window window, boolean checkTab) {
      for(int i = this.levels.size() - 1; i >= 0; --i) {
         for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
            Window window1 = (Window)((ArrayList)this.levels.get(i)).get(j);
            if (window1 instanceof WindowTabs && !(window instanceof WindowTabs) && checkTab) {
               WindowTabs tabs = (WindowTabs)window1;

               for(ElementWindow tab : tabs.tabs) {
                  if (tab.mountedWindow == window) {
                     Window win = WindowTabs.detach(tab);
                     win.docked = -1;
                     win.width = tab.oriWidth;
                     win.height = tab.oriHeight;
                     win.posX = this.width / 2 - win.width / 2;
                     win.posY = this.height / 2 - win.height / 2;
                     win.resized();
                     this.removeWindow(win);
                     return;
                  }
               }
            }

            if (window1 == window) {
               if (i < this.VARIABLE_LEVEL) {
                  this.removeFromDock(window1);
                  this.removeWindow(window1, checkTab);
               } else {
                  ((ArrayList)this.levels.get(i)).remove(j);
                  if (((ArrayList)this.levels.get(i)).isEmpty()) {
                     this.levels.remove(i);
                  }
               }
               break;
            }
         }
      }

   }

   public void addWindowOnTop(Window window) {
      if (!window.allowMultipleInstances() && window.getClass() != Window.class) {
         for(int i = this.levels.size() - 1; i >= 0; --i) {
            for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
               Window window1 = (Window)((ArrayList)this.levels.get(i)).get(j);
               if (window != window1) {
                  if (window1 instanceof WindowTabs) {
                     WindowTabs tabs = (WindowTabs)window1;

                     for(ElementWindow tab : tabs.tabs) {
                        if (tab.mountedWindow.getClass() == window.getClass()) {
                           Window win = WindowTabs.detach(tab);
                           win.docked = -1;
                           win.width = tab.oriWidth;
                           win.height = tab.oriHeight;
                           win.posX = this.width / 2 - win.width / 2;
                           win.posY = this.height / 2 - win.height / 2;
                           win.resized();
                           this.bringWindowToFront(win);
                           return;
                        }
                     }
                  } else if (window1.getClass() == window.getClass()) {
                     if (window1.docked >= 0) {
                        this.removeFromDock(window1);
                     }

                     window1.docked = -1;
                     if (window1.height > this.height) {
                        window1.height = window1.minHeight;
                     }

                     if (window1.width > this.width) {
                        window1.width = window1.minWidth;
                     }

                     window1.posX = this.width / 2 - window1.width / 2;
                     window1.posY = this.height / 2 - window1.height / 2;
                     window1.resized();
                     this.bringWindowToFront(window1);
                     return;
                  }
               }
            }
         }
      }

      ArrayList<Window> topLevel = new ArrayList();
      topLevel.add(window);
      this.levels.add(topLevel);
   }

   public void screenResize() {
      for(int i = 0; i < this.VARIABLE_LEVEL; ++i) {
         ArrayList<Window> docked = (ArrayList)this.levels.get(i);

         for(int j = 0; j < docked.size(); ++j) {
            Window window = (Window)docked.get(j);
            if (i == 0) {
               window.posX = -1;
            } else if (i == 1) {
               window.posX = this.width - window.getWidth() + 1;
            } else if (i == 2) {
               int pX1 = -1;
               int pX2 = this.width + 1;
               if (!((ArrayList)this.levels.get(0)).isEmpty()) {
                  pX1 = ((Window)((ArrayList)this.levels.get(0)).get(0)).width - 2;
               }

               if (!((ArrayList)this.levels.get(1)).isEmpty()) {
                  pX2 = ((Window)((ArrayList)this.levels.get(1)).get(0)).posX + 1;
               }

               window.posX = pX1;
               window.width = pX2 - pX1;
               window.posY = this.height - window.getHeight() + 1;
            }

            if (j == docked.size() - 1 && i != 2) {
               window.height = this.height - window.posY + 1;
            }

            if (window.posX == (this.oldWidth - window.width) / 2 && window.posY == (this.oldHeight - window.height) / 2) {
               window.posX = (this.width - window.width) / 2;
               window.posY = (this.height - window.height) / 2;
            }

            window.resized();
         }
      }

      for(int i = this.VARIABLE_LEVEL; i < this.levels.size(); ++i) {
         ArrayList<Window> docked = (ArrayList)this.levels.get(i);

         for(int j = 0; j < docked.size(); ++j) {
            Window window = (Window)docked.get(j);
            if (window.posX == (this.oldWidth - window.width) / 2 && window.posY == (this.oldHeight - window.height) / 2) {
               window.putInMiddleOfScreen();
            }

            window.resized();
         }
      }

      this.oldWidth = this.width;
      this.oldHeight = this.height;
   }

   public void bringWindowToFront(Window window) {
      if (!window.isStatic()) {
         for(int i = this.levels.size() - 1; i >= 0; --i) {
            for(int j = ((ArrayList)this.levels.get(i)).size() - 1; j >= 0; --j) {
               Window window1 = (Window)((ArrayList)this.levels.get(i)).get(j);
               if (window1 == window && window.docked < 0 && (i != this.levels.size() - 1 || ((ArrayList)this.levels.get(i)).size() != 1)) {
                  ArrayList<Window> topLevel = new ArrayList();
                  topLevel.add(window1);
                  ((ArrayList)this.levels.get(i)).remove(j);
                  if (((ArrayList)this.levels.get(i)).isEmpty() && i >= this.VARIABLE_LEVEL) {
                     this.levels.remove(i);
                  }

                  this.levels.add(topLevel);
               }
            }
         }

      }
   }

   public String reString(String s, int width) {
      while(s.length() > 1 && this.getFontRenderer().getStringWidth(s) > width - 3 && !s.startsWith("...")) {
         if (s.endsWith("...")) {
            s = s.substring(0, s.length() - 4) + "...";
         } else {
            s = s.substring(0, s.length() - 1) + "...";
         }
      }

      return s;
   }
}
