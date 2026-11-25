package com.fantasticsource.mctools.gui.element;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class GUIElement {
   public static final Color T_GRAY = new Color(-1431655817);
   public static final byte AP_CENTER = -1;
   public static final byte AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM = 0;
   public static final byte AP_CENTERED_H_TOP_TO_BOTTOM = 8;
   public static final byte AP_X_0_TOP_TO_BOTTOM = 10;
   public static final byte AP_Y_0_LEFT_TO_RIGHT = 11;
   public final ArrayList<Runnable> onClickActions;
   public final ArrayList<Runnable> onRecalcActions;
   public final ArrayList<Runnable> onEditActions;
   public final ArrayList<Predicate<GUIElement>> onRemoveChildActions;
   public double x;
   public double y;
   public double width;
   public double height;
   public GUIElement parent;
   public ArrayList<GUIElement> children;
   public Object tooltip;
   public boolean autoplace;
   protected double autoX;
   protected double autoY;
   protected double furthestX;
   protected double furthestY;
   protected byte subElementAutoplaceMethod;
   public GUIScreen screen;
   protected boolean active;
   protected boolean externalDeactivation;
   protected boolean useParentScissor;
   private ArrayList<GUIElement> linkedMouseActivity;
   private ArrayList<GUIElement> linkedMouseActivityReverse;
   protected double dragStartX;
   protected double dragStartY;

   public GUIElement(GUIScreen screen, double width, double height) {
      this(screen, width, height, (byte)0);
   }

   public GUIElement(GUIScreen screen, double width, double height, byte subElementAutoplaceMethod) {
      this(screen, 0.0D, 0.0D, width, height, subElementAutoplaceMethod);
      this.autoplace = true;
   }

   public GUIElement(GUIScreen screen, double x, double y, double width, double height) {
      this(screen, x, y, width, height, (byte)0);
   }

   public GUIElement(GUIScreen screen, double x, double y, double width, double height, byte subElementAutoplaceMethod) {
      this.onClickActions = new ArrayList();
      this.onRecalcActions = new ArrayList();
      this.onEditActions = new ArrayList();
      this.onRemoveChildActions = new ArrayList();
      this.parent = null;
      this.children = new ArrayList();
      this.tooltip = null;
      this.autoplace = false;
      this.autoX = 0.0D;
      this.autoY = 0.0D;
      this.furthestX = 0.0D;
      this.furthestY = 0.0D;
      this.active = false;
      this.externalDeactivation = false;
      this.useParentScissor = false;
      this.linkedMouseActivity = new ArrayList();
      this.linkedMouseActivityReverse = new ArrayList();
      this.screen = screen;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.subElementAutoplaceMethod = subElementAutoplaceMethod;
   }

   public GUIElement useParentScissor(boolean useParentScissor) {
      this.useParentScissor = useParentScissor;
      return this;
   }

   public GUIElement setTooltip(String tooltip) {
      if (tooltip != null && !tooltip.trim().equals("")) {
         if (!(this.tooltip instanceof GUITooltipView)) {
            this.tooltip = new GUITooltipView(this.screen);
         }

         ((GUITooltipView)this.tooltip).setTooltip(tooltip);
         return this;
      } else {
         this.tooltip = null;
         return this;
      }
   }

   public GUIElement setTooltip(ItemStack stack) {
      this.tooltip = stack;
      return this;
   }

   public boolean isWithin(double x, double y) {
      double xx = this.absoluteX();
      double yy = this.absoluteY();
      return xx <= x && x < xx + this.absoluteWidth() && yy <= y && y < yy + this.absoluteHeight();
   }

   private int[] preDraw() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableDepth();
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.x, this.y, 0.0D);
      GlStateManager.scale(this.width, this.height, 1.0D);
      int[] lastScissor = new int[4];
      System.arraycopy(GUIScreen.currentScissor, 0, lastScissor, 0, 4);
      if (!this.useParentScissor) {
         if (this instanceof GUILine) {
            GUIScreen.currentScissor[0] = (int)Tools.max((float)GUIScreen.currentScissor[0], (float)this.absolutePxX() - ((GUILine)this).thickness);
            GUIScreen.currentScissor[1] = (int)Tools.max((float)GUIScreen.currentScissor[1], (float)this.absolutePxY() - ((GUILine)this).thickness);
            GUIScreen.currentScissor[2] = (int)Tools.min((float)GUIScreen.currentScissor[2], (float)(this.absolutePxX() + this.absolutePxWidth()) + ((GUILine)this).thickness * 2.0F);
            GUIScreen.currentScissor[3] = (int)Tools.min((float)GUIScreen.currentScissor[3], (float)(this.absolutePxY() + this.absolutePxHeight()) + ((GUILine)this).thickness * 2.0F);
         } else {
            GUIScreen.currentScissor[0] = Tools.max(GUIScreen.currentScissor[0], this.absolutePxX());
            GUIScreen.currentScissor[1] = Tools.max(GUIScreen.currentScissor[1], this.absolutePxY());
            GUIScreen.currentScissor[2] = Tools.min(GUIScreen.currentScissor[2], this.absolutePxX() + this.absolutePxWidth());
            GUIScreen.currentScissor[3] = Tools.min(GUIScreen.currentScissor[3], this.absolutePxY() + this.absolutePxHeight());
         }
      }

      if (!this.screen.scissor()) {
         GUIScreen.currentScissor = lastScissor;
         GlStateManager.popMatrix();
         return null;
      } else {
         return lastScissor;
      }
   }

   protected final void drawChildren() {
      if (this.children.size() > 0 && this.width > 0.0D && this.height > 0.0D) {
         for(GUIElement element : (GUIElement[])this.children.toArray(new GUIElement[0])) {
            element.tick();
            int[] lastScissor = element.preDraw();
            if (lastScissor != null) {
               element.draw();
               element.postDraw(lastScissor);
            }
         }
      }

      if (this.isMouseWithin()) {
         if (this.tooltip instanceof GUITooltipView) {
            this.screen.tooltips.add((GUITooltipView)this.tooltip);
         } else if (this.tooltip instanceof ItemStack) {
            this.screen.tooltipStack = (ItemStack)this.tooltip;
         }
      }

   }

   private void postDraw(int[] lastScissor) {
      GUIScreen.currentScissor = lastScissor;
      GlStateManager.popMatrix();
   }

   protected void tick() {
   }

   public void draw() {
      this.drawChildren();
   }

   public void mouseWheel(int delta) {
      for(GUIElement child : (ArrayList)this.children.clone()) {
         child.mouseWheel(delta);
      }

   }

   public boolean mousePressed(int button) {
      this.dragStartX = this.x;
      this.dragStartY = this.y;
      boolean result = false;
      if (button == 0 && this.isMouseWithin()) {
         result = true;
         this.setActive(true);
      }

      for(GUIElement child : (ArrayList)this.children.clone()) {
         result |= child.mousePressed(button);
      }

      return result;
   }

   public boolean mouseReleased(int button) {
      boolean result = false;
      if (button == 0) {
         if (this.active && this.isMouseWithin() && this.x == this.dragStartX && this.y == this.dragStartY) {
            if (!MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(this.screen, this))) {
               this.click();
            }

            result = true;
         }

         this.setActive(false);
      }

      for(GUIElement child : (ArrayList)this.children.clone()) {
         child.mouseReleased(button);
      }

      return result;
   }

   public void click() {
      for(Runnable action : this.onClickActions) {
         action.run();
      }

   }

   public GUIElement addClickActions(Runnable... actions) {
      this.onClickActions.addAll(Arrays.asList(actions));
      return this;
   }

   public GUIElement addRecalcActions(Runnable... actions) {
      this.onRecalcActions.addAll(Arrays.asList(actions));
      return this;
   }

   public GUIElement addEditActions(Runnable... actions) {
      this.onEditActions.addAll(Arrays.asList(actions));
      return this;
   }

   public GUIElement addRemoveChildActions(Predicate<GUIElement>... actions) {
      this.onRemoveChildActions.addAll(Arrays.asList(actions));
      return this;
   }

   public void mouseDrag(int button) {
      for(GUIElement child : (ArrayList)this.children.clone()) {
         child.mouseDrag(button);
      }

   }

   public final double absoluteX() {
      if (this.parent == null) {
         return this.x;
      } else {
         return this.parent instanceof GUIPanZoomView ? this.parent.absoluteX() + (this.x - ((GUIPanZoomView)this.parent).viewX) * ((GUIPanZoomView)this.parent).getZoom() * this.parent.absoluteWidth() : this.parent.absoluteX() + this.x * this.parent.absoluteWidth();
      }
   }

   public final double absoluteY() {
      if (this.parent == null) {
         return this.y;
      } else if (this.parent instanceof GUIPanZoomView) {
         return this.parent.absoluteY() + (this.y - ((GUIPanZoomView)this.parent).viewY) * ((GUIPanZoomView)this.parent).getZoom() * this.parent.absoluteHeight();
      } else {
         return this.parent instanceof GUIScrollView ? this.parent.absoluteY() + (this.y - ((GUIScrollView)this.parent).top) * this.parent.absoluteHeight() : this.parent.absoluteY() + this.y * this.parent.absoluteHeight();
      }
   }

   public final GUIElement setAbsoluteX(double absX) {
      if (this.parent == null) {
         this.x = absX;
      } else if (this.parent instanceof GUIPanZoomView) {
         this.x = (absX - this.parent.absoluteX()) / ((GUIPanZoomView)this.parent).getZoom() / this.parent.absoluteWidth() + ((GUIPanZoomView)this.parent).viewX;
      } else {
         this.x = (absX - this.parent.absoluteX()) / this.parent.absoluteWidth();
      }

      return this;
   }

   public final GUIElement setAbsoluteY(double absY) {
      if (this.parent == null) {
         this.y = absY;
      } else if (this.parent instanceof GUIPanZoomView) {
         this.y = (absY - this.parent.absoluteY()) / ((GUIPanZoomView)this.parent).getZoom() / this.parent.absoluteHeight() + ((GUIPanZoomView)this.parent).viewY;
      } else if (this.parent instanceof GUIScrollView) {
         this.y = (absY - this.parent.absoluteY()) / this.parent.absoluteHeight() + ((GUIScrollView)this.parent).top;
      } else {
         this.y = (absY - this.parent.absoluteY()) / this.parent.absoluteHeight();
      }

      return this;
   }

   public final double absoluteWidth() {
      if (this.parent == null) {
         return this.width;
      } else {
         return this.parent instanceof GUIPanZoomView ? this.parent.absoluteWidth() * this.width * ((GUIPanZoomView)this.parent).getZoom() : this.parent.absoluteWidth() * this.width;
      }
   }

   public final double absoluteHeight() {
      if (this.parent == null) {
         return this.height;
      } else {
         return this.parent instanceof GUIPanZoomView ? this.parent.absoluteHeight() * this.height * ((GUIPanZoomView)this.parent).getZoom() : this.parent.absoluteHeight() * this.height;
      }
   }

   public final int absolutePxX() {
      return (int)Math.round(this.absoluteX() * (double)this.screen.pxWidth);
   }

   public final int absolutePxY() {
      return (int)Math.round(this.absoluteY() * (double)this.screen.pxHeight);
   }

   public final int absolutePxWidth() {
      return (int)Math.round(this.absoluteWidth() * (double)this.screen.pxWidth);
   }

   public final int absolutePxHeight() {
      return (int)Math.round(this.absoluteHeight() * (double)this.screen.pxHeight);
   }

   public final double mouseX() {
      return GUIScreen.mouseX;
   }

   public final double mouseY() {
      return GUIScreen.mouseY;
   }

   public final boolean isMouseWithin() {
      for(GUIElement element : this.linkedMouseActivityReverse) {
         if (element.isWithin(this.mouseX(), this.mouseY())) {
            return true;
         }
      }

      return this.isWithin(this.mouseX(), this.mouseY()) && (this.parent == null || this.parent.isMouseWithin());
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }

   public GUIElement recalc(int subIndexChanged) {
      this.recalcAndRepositionSubElements(subIndexChanged);
      this.postRecalc();
      return this;
   }

   public final void postRecalc() {
      for(Runnable action : this.onRecalcActions) {
         action.run();
      }

   }

   public GUIElement addAll(GUIElement... elements) {
      for(GUIElement element : elements) {
         this.add(element);
      }

      return this;
   }

   public GUIElement add(GUIElement element) {
      return this.add(this.children.size(), element);
   }

   public GUIElement add(int index, GUIElement element) {
      element.parent = this;
      this.children.add(index, element);
      switch(this.subElementAutoplaceMethod) {
      case -1:
         this.recalc(0);
         break;
      default:
         this.recalc(index);
      }

      return element;
   }

   public void recalcAndRepositionSubElements(int startIndex) {
      double yy = 0.0D;
      switch(this.subElementAutoplaceMethod) {
      case -1:
         for(GUIElement element : this.children) {
            element.recalc(0);
            if (element.autoplace) {
               element.x = 0.5D - element.width * 0.5D;
               element.y = yy;
               yy = Tools.max(yy, element.y + element.height);
            }
         }

         yy = yy * 0.5D;

         for(GUIElement element : this.children) {
            if (element.autoplace) {
               element.y += 0.5D - yy;
            }
         }

         return;
      case 0:
         if (this.size() <= 1 || startIndex != this.size() - 1) {
            this.autoX = 0.0D;
            this.autoY = 0.0D;
            this.furthestX = 0.0D;
            this.furthestY = 0.0D;

            for(int i = 0; i < startIndex; ++i) {
               GUIElement element = this.get(i);
               if (element.autoplace) {
                  this.autoX = element.x + element.width;
                  this.autoY = element.y;
                  this.furthestX = Tools.max(this.furthestX, this.autoX);
                  this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
               }
            }
         }

         for(int i = startIndex; i < this.size(); ++i) {
            GUIElement element = this.get(i);
            element.recalc(0);
            if (element.autoplace) {
               if (this.autoX != 0.0D && this.autoX + element.width > 1.0D) {
                  element.x = 0.0D;
                  element.y = this.furthestY;
               } else {
                  element.x = this.autoX;
                  element.y = this.autoY;
               }

               this.autoX = element.x + element.width;
               this.autoY = element.y;
               this.furthestX = Tools.max(this.furthestX, this.autoX);
               this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
            }
         }

         return;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 9:
      default:
         throw new IllegalArgumentException("Unimplemented autoplace type: " + this.subElementAutoplaceMethod);
      case 8:
         if (this.size() <= 1 || startIndex != this.size() - 1) {
            this.autoY = 0.0D;
            this.furthestY = 0.0D;

            for(int i = 0; i < startIndex; ++i) {
               GUIElement element = this.get(i);
               if (element.autoplace) {
                  this.autoY = element.y;
                  this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
               }
            }
         }

         for(int i = startIndex; i < this.size(); ++i) {
            GUIElement element = this.get(i);
            element.recalc(0);
            if (element.autoplace) {
               element.x = 0.5D - element.width / 2.0D;
               element.y = this.furthestY;
               this.autoY = element.y;
               this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
            }
         }

         return;
      case 10:
         if (this.size() <= 1 || startIndex != this.size() - 1) {
            this.autoY = 0.0D;
            this.furthestY = 0.0D;

            for(int i = 0; i < startIndex; ++i) {
               GUIElement element = this.get(i);
               if (element.autoplace) {
                  this.autoY = element.y;
                  this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
               }
            }
         }

         for(int i = startIndex; i < this.size(); ++i) {
            GUIElement element = this.get(i);
            element.recalc(0);
            if (element.autoplace) {
               element.x = 0.0D;
               element.y = this.furthestY;
               this.autoY = element.y;
               this.furthestY = Tools.max(this.furthestY, this.autoY + element.height);
            }
         }

         return;
      case 11:
         if (this.size() <= 1 || startIndex != this.size() - 1) {
            this.autoX = 0.0D;
            this.furthestX = 0.0D;

            for(int i = 0; i < startIndex; ++i) {
               GUIElement element = this.get(i);
               if (element.autoplace) {
                  this.autoX = element.x;
                  this.furthestX = Tools.max(this.furthestX, this.autoX + element.width);
               }
            }
         }

         for(int i = startIndex; i < this.size(); ++i) {
            GUIElement element = this.get(i);
            element.recalc(0);
            if (element.autoplace) {
               element.x = this.furthestX;
               element.y = 0.0D;
               this.autoX = element.x;
               this.furthestX = Tools.max(this.furthestX, this.autoX + element.width);
            }
         }

      }
   }

   public void setSubElementAutoplaceMethod(byte subElementAutoplaceMethod) {
      this.subElementAutoplaceMethod = subElementAutoplaceMethod;
      this.recalc(0);
   }

   public void remove(GUIElement element) {
      int index = this.indexOf(element);
      if (index != -1) {
         this.remove(index);
      } else if (element.parent == this) {
         element.parent = null;
      }

   }

   public void remove(int index) {
      GUIElement element = (GUIElement)this.children.remove(index);
      if (element != null) {
         if (element.parent == this) {
            element.parent = null;
         }

         this.recalc(0);

         for(Predicate<GUIElement> action : this.onRemoveChildActions) {
            action.test(element);
         }

         this.recalc(0);
      }

   }

   public int size() {
      return this.children.size();
   }

   public void clear() {
      for(GUIElement child : (ArrayList)this.children.clone()) {
         this.remove(child);
      }

   }

   public GUIElement get(int index) {
      return (GUIElement)this.children.get(index);
   }

   public int indexOf(GUIElement child) {
      int i = 0;

      for(GUIElement element : this.children) {
         if (element == child) {
            return i;
         }

         ++i;
      }

      return -1;
   }

   public void linkMouseActivity(GUIElement element) {
      this.linkedMouseActivity.add(element);
      element.linkedMouseActivityReverse.add(this);
   }

   public void unlinkMouseActivity(GUIElement element) {
      this.linkedMouseActivity.remove(element);
      element.linkedMouseActivityReverse.remove(this);
   }

   public void setExternalDeactivation(boolean external, boolean recursive) {
      this.externalDeactivation = external;
      if (recursive) {
         for(GUIElement child : this.children) {
            child.setExternalDeactivation(external, true);
         }
      }

   }

   public void setActive(boolean active) {
      this.setActive(active, false);
   }

   public void setActive(boolean active, boolean external) {
      if (active || !this.externalDeactivation || external) {
         this.active = active;

         for(GUIElement element : this.linkedMouseActivity) {
            element.active = active;
         }

      }
   }

   public void setActiveRecursive(boolean active) {
      this.setActive(active, true);

      for(GUIElement child : this.children) {
         child.setActiveRecursive(active);
      }

   }

   public boolean isActive() {
      return this.active;
   }

   public void keyTyped(char typedChar, int keyCode) {
      for(GUIElement child : (ArrayList)this.children.clone()) {
         child.keyTyped(typedChar, keyCode);
      }

   }
}
