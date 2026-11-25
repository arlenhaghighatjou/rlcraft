package com.creativemd.creativecore.common.gui.container;

import com.creativemd.creativecore.common.gui.CoreControl;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.Rect;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.gui.GuiFocusControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlClickEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.math.Vec3d;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiParent extends GuiControl implements IControlParent {
   public ArrayList<GuiControl> controls = new ArrayList();
   protected int lastRenderedHeight = 0;

   public GuiParent(String name, int x, int y, int width, int height) {
      super(name, x, y, width, height);
   }

   public List getControls() {
      return this.controls;
   }

   public void refreshControls() {
      for(int i = 0; i < this.controls.size(); ++i) {
         this.updateControl((GuiControl)this.controls.get(i), i);
      }

   }

   public void updateControl(GuiControl control, int id) {
      control.parent = this;
      control.setID(id);
   }

   public void addControl(GuiControl control) {
      this.updateControl(control, this.controls.size());
      this.controls.add(control);
   }

   public boolean removeControl(GuiControl control) {
      int index = this.controls.indexOf(control);
      if (index == -1) {
         return false;
      } else {
         this.controls.remove(index);

         for(int i = index; i < this.controls.size(); ++i) {
            this.updateControl(control, i);
         }

         return true;
      }
   }

   public SubGui getOrigin() {
      if (this.parent instanceof SubGui) {
         return (SubGui)this.parent;
      } else {
         return this.parent != null ? ((GuiParent)this.parent).getOrigin() : null;
      }
   }

   public float getScaleFactor() {
      return 1.0F;
   }

   public double getOffsetY() {
      return 0.0D;
   }

   public double getOffsetX() {
      return 0.0D;
   }

   public boolean shouldRenderControl(GuiControl control, int index) {
      return true;
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height, Rect relativeMaximumRect) {
      GlStateManager.clear(256);
      GlStateManager.disableDepth();
      float scale = this.getScaleFactor();
      double xOffset = this.getOffsetX();
      double yOffset = this.getOffsetY();
      Rect newRect = relativeMaximumRect.mergeRects(this.getRect());
      Rect scaledRect = relativeMaximumRect.mergeRects(this.getRect());
      this.lastRenderedHeight = 0;
      scaledRect.scale(1.0F / scale);

      for(int i = this.controls.size() - 1; i >= 0; --i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         if (this.shouldRenderControl(control, i)) {
            if (control.visible && (control.canOverlap() || control.isVisibleInsideRect((int)(-xOffset), (int)(-yOffset), width, height, scale))) {
               if (control.canOverlap()) {
                  GL11.glDisable(2960);
               } else {
                  GL11.glEnable(2960);
                  this.prepareContentStencil(helper, newRect);
                  GL11.glStencilOp(7680, 7680, 7680);
                  GL11.glStencilFunc(514, 1, 1);
               }

               GlStateManager.pushMatrix();
               GlStateManager.scale(scale, scale, 1.0F);
               GlStateManager.translate(xOffset, yOffset, 0.0D);
               control.renderControl(helper, 1.0F, control.canOverlap() ? getScreenRect() : scaledRect.getOffsetRect((int)(-xOffset) - control.posX - control.getContentOffset(), (int)(-yOffset) - control.posY - control.getContentOffset()));
               GlStateManager.popMatrix();
               GL11.glDisable(2960);
            }

            this.lastRenderedHeight = (int)Math.max((float)this.lastRenderedHeight, (float)(control.posY + control.height) * scale);
         }
      }

      GL11.glEnable(2960);
      this.prepareContentStencil(helper, newRect);
      GL11.glStencilOp(7680, 7680, 7680);
      GL11.glStencilFunc(514, 1, 1);
      this.renderContent(helper, style, width, height);
      GL11.glDisable(2960);
   }

   public GuiControl get(String name) {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         if (control.is(new String[]{name})) {
            return control;
         }

         if (control instanceof IControlParent) {
            CoreControl tempcontrol = ((IControlParent)control).get(name);
            if (tempcontrol != null) {
               return (GuiControl)tempcontrol;
            }
         }
      }

      return null;
   }

   public boolean has(String name) {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         if (control.name.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   public boolean isMouseOver() {
      return this.parent != null ? super.isMouseOver() : true;
   }

   public Vec3d getMousePos() {
      float scale = this.getScaleFactor();
      if (scale != 1.0F) {
         scale = 1.0F / this.getScaleFactor();
      }

      if (this.parent != null) {
         return this.getParent().getMousePos().addVector((double)(-this.getContentOffset()) - this.getOffsetX() * (double)this.getScaleFactor() - (double)this.posX, (double)(-this.getContentOffset()) - this.getOffsetY() * (double)this.getScaleFactor() - (double)this.posY, 0.0D).scale((double)scale);
      } else {
         ScaledResolution scaledresolution = new ScaledResolution(mc);
         int i = scaledresolution.getScaledWidth();
         int j = scaledresolution.getScaledHeight();
         int x = Mouse.getX() * i / mc.displayWidth;
         int y = j - Mouse.getY() * j / mc.displayHeight - 1;
         int movex = (i - this.width) / 2;
         int movey = (j - this.height) / 2;
         x = x - movex;
         y = y - movey;
         return (new Vec3d((double)(x - this.getContentOffset()) - this.getOffsetX(), (double)(y - this.getContentOffset()) - this.getOffsetY(), 0.0D)).scale((double)(1.0F / this.getScaleFactor()));
      }
   }

   public boolean isAnyControlFocused() {
      return this.getFocusedControl(true) != null;
   }

   public GuiControl getFocusedControl(boolean parent) {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         if (control instanceof GuiParent) {
            GuiControl focused = ((GuiParent)control).getFocusedControl(false);
            if (focused != null) {
               return focused;
            }
         }

         if (control instanceof GuiFocusControl && ((GuiFocusControl)control).focused) {
            return control;
         }
      }

      if (parent && this.getParent() != null) {
         return this.getParent().getFocusedControl(true);
      } else {
         return null;
      }
   }

   public void onOpened() {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         control.parent = this;
         control.onOpened();
      }

      this.refreshControls();
   }

   public void onClosed() {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         control.onClosed();
      }

   }

   public void onTick() {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         control.onTick();
      }

   }

   public void onLoseFocus() {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         control.onLoseFocus();
      }

   }

   public boolean mouseScrolled(int x, int y, int scrolled) {
      Vec3d mouse = this.getMousePos();

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         Vec3d pos = control.rotateMouseVec(mouse);
         if (control.isInteractable() && control.isMouseOver((int)pos.x, (int)pos.y) && control.mouseScrolled((int)pos.x, (int)pos.y, scrolled)) {
            return true;
         }
      }

      return false;
   }

   protected void clickControl(GuiControl control, int x, int y, int button) {
      this.raiseEvent(new GuiControlClickEvent(control, x, y, button));
   }

   public boolean mousePressed(int x, int y, int button) {
      boolean result = false;
      Vec3d mouse = this.getMousePos();

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         Vec3d pos = control.rotateMouseVec(mouse);
         if (!result && control.isInteractable() && control.isMouseOver((int)pos.x, (int)pos.y) && control.mousePressed((int)pos.x, (int)pos.y, button)) {
            this.clickControl(control, x, y, button);
            result = true;
         } else {
            control.onLoseFocus();
         }
      }

      return result;
   }

   public void mouseMove(int x, int y, int button) {
      Vec3d mouse = this.getMousePos();

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         Vec3d pos = control.rotateMouseVec(mouse);
         if (control.isInteractable()) {
            control.mouseMove((int)pos.x, (int)pos.y, button);
         }
      }

   }

   public void mouseDragged(int x, int y, int button, long time) {
      Vec3d mouse = this.getMousePos();

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         Vec3d pos = control.rotateMouseVec(mouse);
         if (control.isInteractable()) {
            control.mouseDragged((int)pos.x, (int)pos.y, button, time);
         }
      }

   }

   public void mouseReleased(int x, int y, int button) {
      Vec3d mouse = this.getMousePos();

      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         Vec3d pos = control.rotateMouseVec(mouse);
         if (control.isInteractable()) {
            control.mouseReleased((int)pos.x, (int)pos.y, button);
         }
      }

   }

   public boolean onKeyPressed(char character, int key) {
      for(int i = 0; i < this.controls.size(); ++i) {
         GuiControl control = (GuiControl)this.controls.get(i);
         if (control.isInteractable() && control.onKeyPressed(character, key)) {
            return true;
         }
      }

      return false;
   }

   public GuiToolTipEvent getToolTipEvent() {
      GuiToolTipEvent event = super.getToolTipEvent();
      if (event != null) {
         return event;
      } else {
         Vec3d mouse = this.getMousePos();

         for(int i = 0; i < this.controls.size(); ++i) {
            GuiControl control = (GuiControl)this.controls.get(i);
            Vec3d pos = control.rotateMouseVec(mouse);
            if (control.isInteractable() && control.isMouseOver((int)pos.x, (int)pos.y)) {
               event = control.getToolTipEvent();
               if (event != null) {
                  return event;
               }
            }
         }

         return null;
      }
   }
}
