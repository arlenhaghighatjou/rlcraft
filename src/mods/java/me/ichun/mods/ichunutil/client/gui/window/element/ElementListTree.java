package me.ichun.mods.ichunutil.client.gui.window.element;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.WindowTopDockBase;
import me.ichun.mods.ichunutil.client.module.tabula.model.ModelInfo;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.util.IOUtil;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.Animation;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeGroup;
import me.ichun.mods.ichunutil.common.module.tabula.project.components.CubeInfo;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class ElementListTree extends Element {
   public int spacerL;
   public int spacerR;
   public int spacerU;
   public int spacerD;
   public double sliderProg = 0.0D;
   public ArrayList<ElementListTree.Tree> trees = new ArrayList();
   public boolean canDrag;
   public ElementListTree.Tree treeDragged;
   public int dragX;
   public int dragY;
   public double dragScroll;
   public ElementListTree.Tree treeClicked;
   public int clickTimeout;
   public boolean lmbDown;
   public boolean rmbDown;
   public String selectedIdentifier;
   public static final ResourceLocation txModel = new ResourceLocation("tabula", "textures/icon/model.png");
   public static final ResourceLocation txGroup = new ResourceLocation("tabula", "textures/icon/group.png");

   public ElementListTree(Window window, int x, int y, int w, int h, int ID, boolean igMin, boolean drag) {
      super(window, x, y, w, h, ID, igMin);
      this.spacerL = x;
      this.spacerR = this.parent.width - x - this.width;
      this.spacerU = y;
      this.spacerD = this.parent.height - y - this.height;
      this.selectedIdentifier = "";
      this.canDrag = drag;
      this.lmbDown = true;
      this.rmbDown = true;
   }

   public void update() {
      if (this.clickTimeout > 0) {
         --this.clickTimeout;
      }

   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      int x1 = this.getPosX();
      int x2 = this.getPosX() + this.width;
      int y1 = this.getPosY();
      int y2 = this.getPosY() + this.height;
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
      }

      RendererHelper.endGlScissor();
      RendererHelper.startGlScissor(this.getPosX(), this.getPosY() - 1, this.width + 2, this.height + 3);
      if (treeHeight1 > this.height) {
         x2 -= 10;
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBarBorder[0], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[1], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, (double)(x2 + 5), (double)(this.getPosY() + this.height / 40), 2.0D, (double)(this.height - this.height / 40 * 2), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBarBorder[0], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[1], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, (double)(x2 + 1), (double)(this.getPosY() - 1) + (double)(this.height - this.height / 11) * this.sliderProg, 10.0D, (double)(this.height / 10), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBar[0], this.parent.workspace.currentTheme.elementTreeScrollBar[1], this.parent.workspace.currentTheme.elementTreeScrollBar[2], 255, (double)(x2 + 2), (double)this.getPosY() + (double)(this.height - this.height / 11) * this.sliderProg, 8.0D, (double)(this.height / 10 - 2), 0.0D);
         int sbx1 = x2 + 1 - this.parent.posX;
         int sbx2 = sbx1 + 10;
         int sby1 = this.getPosY() - 1 - this.parent.posY;
         int sby2 = this.getPosY() + this.height - this.parent.posY;
         if (Mouse.isButtonDown(0) && mouseX >= sbx1 && mouseX <= sbx2 && mouseY >= sby1 && mouseY <= sby2) {
            sby1 = sby1 + 10;
            sby2 = sby2 - 10;
            this.sliderProg = 1.0D - MathHelper.clamp((double)(sby2 - mouseY) / (double)(sby2 - sby1), 0.0D, 1.0D);
         }
      }

      RendererHelper.startGlScissor(this.getPosX(), this.getPosY(), this.width + 2, this.height + 2);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, -((double)(treeHeight1 - this.height) * this.sliderProg), 0.0D);
      int treeHeight = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
         tree.draw(mouseX, mouseY, hover, x2 - x1, treeHeight, treeHeight1 > this.height, treeHeight1, Mouse.isButtonDown(0) && !this.lmbDown, Mouse.isButtonDown(1) && !this.rmbDown);
         treeHeight += tree.getHeight();
      }

      GlStateManager.popMatrix();
      RendererHelper.endGlScissor();
      if (this.treeDragged != null && (this.dragX != mouseX || this.dragY != mouseY)) {
         treeHeight = 0;

         for(int i = 0; i < this.trees.size(); ++i) {
            ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
            if (tree == this.treeDragged) {
               break;
            }

            treeHeight += tree.getHeight();
         }

         this.treeDragged.dragDraw = true;
         this.treeDragged.draw(mouseX, mouseY, hover, x2 - x1, treeHeight, treeHeight1 > this.height, treeHeight1, Mouse.isButtonDown(0) && !this.lmbDown, Mouse.isButtonDown(1) && !this.rmbDown);
         this.treeDragged.dragDraw = false;
      }

      if (this.parent.isTab) {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1 + 12, this.parent.getWidth() - 2, this.parent.getHeight() - 2 - 12);
      } else {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1, this.parent.getWidth() - 2, this.parent.getHeight() - 2);
      }

      if (this.parent.docked < 0) {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y1 - 1), (double)(x2 - x1 + 1), 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y1 - 1), 1.0D, (double)(this.height + 2), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y2 + 1), (double)(x2 - x1 + 2), 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)x2, (double)(y1 - 1), 1.0D, (double)(this.height + 2), 0.0D);
      }

      if (!Mouse.isButtonDown(0) && this.lmbDown) {
         if (this.treeDragged != null && (this.dragX != mouseX || this.dragY != mouseY)) {
            boolean unHook = true;
            treeHeight = 0;

            for(int i = 0; i < this.trees.size(); ++i) {
               ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
               double scrollHeight = 0.0D;
               if (treeHeight1 > this.height) {
                  scrollHeight = (double)(this.height - treeHeight1) * this.sliderProg;
               }

               boolean realBorder = mouseX >= this.posX && mouseX < this.posX + this.width && (double)mouseY >= (double)(this.posY + treeHeight) + scrollHeight && (double)mouseY < (double)(this.posY + treeHeight) + scrollHeight + (double)tree.theHeight;
               if (realBorder) {
                  if (treeHeight1 > this.height && (mouseY > this.height + this.posY || mouseY <= this.posY)) {
                     break;
                  }

                  unHook = false;
                  if (tree != this.treeDragged) {
                     this.dragOnto(tree.attachedObject, this.treeDragged.attachedObject);
                     break;
                  }

                  treeHeight += tree.getHeight();
               } else {
                  treeHeight += tree.getHeight();
               }
            }

            if (unHook) {
               this.dragOnto((Object)null, this.treeDragged.attachedObject);
            }
         }

         this.treeDragged = null;
      }

      this.lmbDown = Mouse.isButtonDown(0);
      this.rmbDown = Mouse.isButtonDown(1);
   }

   public void dragOnto(Object draggedOn, Object dragged) {
   }

   public void resized() {
      this.posX = this.spacerL;
      this.width = this.parent.width - this.posX - this.spacerR;
      this.posY = this.spacerU;
      this.height = this.parent.height - this.posY - this.spacerD;
      this.sliderProg = 0.0D;
   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementListTree.Tree tree = (ElementListTree.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
      }

      if (treeHeight1 > this.height) {
         this.sliderProg += 0.05D * (double)(-k);
         this.sliderProg = MathHelper.clamp(this.sliderProg, 0.0D, 1.0D);
      }

      return false;
   }

   public String tooltip() {
      return null;
   }

   public void createTree(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse) {
      this.trees.add(new ElementListTree.Tree(loc, obj, h, attach, expandable, collapse));
   }

   public void clickElement(Object obj) {
      if (obj instanceof Animation) {
         this.selectedIdentifier = ((Animation)obj).identifier;
      } else if (obj instanceof String) {
         this.selectedIdentifier = (String)obj;
      } else if (obj instanceof IIdentifiable) {
         this.selectedIdentifier = ((IIdentifiable)obj).getIdentifier();
      }

   }

   public void rightClickElement(Object obj) {
   }

   public Object getObjectByIdentifier(String s) {
      for(ElementListTree.Tree tree : this.trees) {
         if (tree.attachedObject instanceof CubeInfo && ((CubeInfo)tree.attachedObject).identifier.equals(s) || tree.attachedObject instanceof CubeGroup && ((CubeGroup)tree.attachedObject).identifier.equals(s) || tree.attachedObject instanceof Animation && ((Animation)tree.attachedObject).identifier.equals(s)) {
            return tree.attachedObject;
         }
      }

      return null;
   }

   public void triggerParent() {
      this.parent.elementTriggered(this);
   }

   public class Tree {
      public ResourceLocation txLoc;
      public Object attachedObject;
      private int theHeight;
      public boolean canExpand;
      public boolean collapsed;
      public int attached;
      public boolean selected;
      public boolean dragDraw;

      public Tree(ResourceLocation loc, Object obj, int h, int attach, boolean expandable, boolean collapse) {
         this.txLoc = loc;
         this.attachedObject = obj;
         this.theHeight = h;
         this.attached = attach;
         this.canExpand = expandable;
         this.collapsed = collapse;
      }

      public int getHeight() {
         return this.theHeight;
      }

      public ElementListTree.Tree draw(int mouseX, int mouseY, boolean hover, int width, int treeHeight, boolean hasScroll, int totalHeight, boolean clicking, boolean rClicking) {
         if (ElementListTree.this.treeDragged != this || ElementListTree.this.dragX == mouseX && ElementListTree.this.dragY == mouseY || this.dragDraw) {
            double scrollHeight = 0.0D;
            if (hasScroll) {
               scrollHeight = (double)(ElementListTree.this.height - totalHeight) * ElementListTree.this.sliderProg;
            }

            boolean realBorder = mouseX >= ElementListTree.this.posX && mouseX < ElementListTree.this.posX + width && (double)mouseY >= (double)(ElementListTree.this.posY + treeHeight) + scrollHeight && (double)mouseY < (double)(ElementListTree.this.posY + treeHeight) + scrollHeight + (double)this.theHeight;
            boolean found = false;
            boolean obstructed = false;

            for(int i = ElementListTree.this.parent.workspace.levels.size() - 1; i >= 0 && !found; --i) {
               for(int j = 0; j < ((ArrayList)ElementListTree.this.parent.workspace.levels.get(i)).size(); ++j) {
                  Window window = (Window)((ArrayList)ElementListTree.this.parent.workspace.levels.get(i)).get(j);
                  if (window == ElementListTree.this.parent) {
                     found = true;
                     break;
                  }

                  if (ElementListTree.this.parent.posX + mouseX >= window.posX && ElementListTree.this.parent.posX + mouseX <= window.posX + window.getWidth() && ElementListTree.this.parent.posY + mouseY >= window.posY && ElementListTree.this.parent.posY + mouseY <= window.posY + window.getHeight() && !(window instanceof WindowTopDockBase)) {
                     obstructed = true;
                     break;
                  }
               }
            }

            int offX = 0;
            int offY = 0;
            if (this.dragDraw) {
               scrollHeight = scrollHeight - (double)(ElementListTree.this.height - totalHeight) * (ElementListTree.this.sliderProg - ElementListTree.this.dragScroll);
               offX = mouseX - ElementListTree.this.dragX;
               offY = mouseY - ElementListTree.this.dragY + (int)scrollHeight;
            }

            RendererHelper.drawColourOnScreen(ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBorder[0], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBorder[1], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBorder[2], 255, (double)(ElementListTree.this.getPosX() + offX), (double)(ElementListTree.this.getPosY() + offY + treeHeight), (double)width, (double)this.theHeight, 0.0D);
            if (this.selected) {
               RendererHelper.drawColourOnScreen(ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgSelect[0], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgSelect[1], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgSelect[2], 255, (double)(ElementListTree.this.getPosX() + offX + 1), (double)(ElementListTree.this.getPosY() + offY + treeHeight + 1), (double)(width - 2), (double)(this.theHeight - 2), 0.0D);
            } else if (realBorder && !obstructed) {
               RendererHelper.drawColourOnScreen(ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgHover[0], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgHover[1], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBgHover[2], 255, (double)(ElementListTree.this.getPosX() + offX + 1), (double)(ElementListTree.this.getPosY() + offY + treeHeight + 1), (double)(width - 2), (double)(this.theHeight - 2), 0.0D);
            } else {
               RendererHelper.drawColourOnScreen(ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBg[0], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBg[1], ElementListTree.this.parent.workspace.currentTheme.elementTreeItemBg[2], 255, (double)(ElementListTree.this.getPosX() + offX + 1), (double)(ElementListTree.this.getPosY() + offY + treeHeight + 1), (double)(width - 2), (double)(this.theHeight - 2), 0.0D);
            }

            if (realBorder && hasScroll && (mouseY > ElementListTree.this.height + ElementListTree.this.posY || mouseY <= ElementListTree.this.posY)) {
               clicking = false;
               rClicking = false;
            }

            if (this.attachedObject instanceof CubeInfo) {
               CubeInfo info = (CubeInfo)this.attachedObject;
               boolean hide = info.hidden;
               int level = this.attached;
               int index = -1;

               for(int i = 0; i < ElementListTree.this.trees.size(); ++i) {
                  ElementListTree.Tree tree = (ElementListTree.Tree)ElementListTree.this.trees.get(i);
                  if (tree == this) {
                     index = i;
                     break;
                  }
               }

               while(level > 0 && !hide && index != -1) {
                  --index;
                  ElementListTree.Tree tree = (ElementListTree.Tree)ElementListTree.this.trees.get(index);
                  if (tree.attached == level - 1) {
                     level = tree.attached;
                     if (tree.attachedObject instanceof CubeInfo) {
                        hide = ((CubeInfo)tree.attachedObject).hidden;
                     } else if (tree.attachedObject instanceof CubeGroup) {
                        hide = ((CubeGroup)tree.attachedObject).hidden;
                     }
                  }
               }

               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               RendererHelper.drawTextureOnScreen(ElementListTree.txModel, (double)(ElementListTree.this.getPosX() + offX) + 1.5D + (double)(this.attached * 5), (double)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), (double)(this.theHeight - 4), (double)(this.theHeight - 4), 0.0D);
               ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(info.name, width - 8), (float)(ElementListTree.this.getPosX() + offX + 4 + 8 + this.attached * 5), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), hide ? Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.fontDim) : Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               if (info.parentIdentifier == null && realBorder && rClicking) {
                  ElementListTree.this.rightClickElement(this.attachedObject);
               }
            } else if (!(this.attachedObject instanceof CubeGroup)) {
               if (this.attachedObject instanceof ModelInfo) {
                  ModelInfo info = (ModelInfo)this.attachedObject;
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(info.modelParent.getClass().getSimpleName() + " - " + info.clz.getSimpleName(), width), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               } else if (this.attachedObject instanceof File) {
                  File info = (File)this.attachedObject;
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(info.getName(), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + 3 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString((new SimpleDateFormat()).format(new Date(info.lastModified())), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + 14 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(IOUtil.readableFileSize(info.length()), (float)(ElementListTree.this.getPosX() + offX + width - 4 - ElementListTree.this.parent.workspace.getFontRenderer().getStringWidth(IOUtil.readableFileSize(info.length()))), (float)(ElementListTree.this.getPosY() + offY + 3 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               } else if (this.attachedObject instanceof Theme) {
                  Theme theme = (Theme)this.attachedObject;
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(theme.name + " - " + theme.author, width), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               } else if (this.attachedObject instanceof Animation) {
                  Animation anim = (Animation)this.attachedObject;
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(anim.name, width), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               } else if (this.attachedObject instanceof String) {
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString((String)this.attachedObject, width), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               } else if (this.attachedObject instanceof IListable) {
                  IListable info = (IListable)this.attachedObject;
                  ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(info.localizable() ? I18n.translateToLocal(info.getName()) : info.getName(), width), (float)(ElementListTree.this.getPosX() + offX + 4), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               }
            } else {
               CubeGroup info = (CubeGroup)this.attachedObject;
               boolean hide = info.hidden;
               int level = this.attached;
               int index = -1;

               for(int i = 0; i < ElementListTree.this.trees.size(); ++i) {
                  ElementListTree.Tree tree = (ElementListTree.Tree)ElementListTree.this.trees.get(i);
                  if (tree == this) {
                     index = i;
                     break;
                  }
               }

               while(level > 0 && !hide && index != -1) {
                  --index;
                  ElementListTree.Tree tree = (ElementListTree.Tree)ElementListTree.this.trees.get(index);
                  if (tree.attached == level - 1) {
                     level = tree.attached;
                     if (tree.attachedObject instanceof CubeInfo) {
                        hide = ((CubeInfo)tree.attachedObject).hidden;
                     } else if (tree.attachedObject instanceof CubeGroup) {
                        hide = ((CubeGroup)tree.attachedObject).hidden;
                     }
                  }
               }

               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               RendererHelper.drawTextureOnScreen(ElementListTree.txGroup, (double)(ElementListTree.this.getPosX() + offX) + 1.5D + (double)(this.attached * 5), (double)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), (double)(this.theHeight - 4), (double)(this.theHeight - 4), 0.0D);
               ElementListTree.this.parent.workspace.getFontRenderer().drawString(ElementListTree.this.parent.workspace.reString(info.name, width - 8), (float)(ElementListTree.this.getPosX() + offX + 4 + 8 + this.attached * 5), (float)(ElementListTree.this.getPosY() + offY + (this.theHeight - ElementListTree.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), hide ? Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.fontDim) : Theme.getAsHex(ElementListTree.this.parent.workspace.currentTheme.font), false);
               if (realBorder && rClicking) {
                  ElementListTree.this.rightClickElement(this.attachedObject);
               }
            }

            if (realBorder && clicking && !obstructed) {
               this.selected = true;
               this.deselectOthers(ElementListTree.this.trees);
               ElementListTree.this.clickElement(this.attachedObject);
               if (ElementListTree.this.clickTimeout > 0 && ElementListTree.this.treeClicked == this) {
                  ElementListTree.this.triggerParent();
               }

               ElementListTree.this.treeClicked = this;
               ElementListTree.this.clickTimeout = 10;
               if (ElementListTree.this.canDrag) {
                  ElementListTree.this.treeDragged = this;
                  ElementListTree.this.dragX = mouseX;
                  ElementListTree.this.dragY = mouseY;
                  ElementListTree.this.dragScroll = ElementListTree.this.sliderProg;
               }
            }
         }

         return null;
      }

      public void deselectOthers(ArrayList<ElementListTree.Tree> trees) {
         for(ElementListTree.Tree tree : trees) {
            if (tree != this && tree.selected) {
               tree.selected = false;
            }
         }

      }
   }
}
