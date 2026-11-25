package com.creativemd.creativecore.common.gui.controls.gui.timeline;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.ColoredDisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.DisplayStyle;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.container.GuiParent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlEvent;
import com.creativemd.creativecore.common.utils.math.SmoothValue;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class GuiTimeline extends GuiParent {
   protected double ticksPerPixel;
   protected double basePixelWidth;
   protected static final double maxZoom = 10.0D;
   protected SmoothValue zoom = new SmoothValue(100L);
   protected SmoothValue scrollX = new SmoothValue(100L);
   protected SmoothValue scrollY = new SmoothValue(100L);
   protected int duration;
   public DisplayStyle timelineBackground = new ColoredDisplayStyle(255, 255, 255);
   public IAnimationHandler handler;
   public List<TimelineChannel> channels;
   public int sidebarWidth = 50;
   private int channelHeight = 10;
   private int timelineHeight = 11;
   protected KeyControl dragged = null;
   private KeyControl selected = null;
   private boolean movedSelected = false;
   private int movedStart = 0;
   private boolean draggedTimeline = false;
   private double lastZoom = 0.0D;
   protected double sizeX;
   protected double maxScrollX;
   protected double sizeY;
   protected double maxScrollY;

   public GuiTimeline(String name, int x, int y, int width, int height, int duration, List<TimelineChannel> channels, IAnimationHandler handler) {
      super(name, x, y, width, height);
      this.handler = handler;
      this.marginWidth = 0;
      this.channels = channels;
      int i = 0;

      for(TimelineChannel channel : channels) {
         channel.index = i;

         for(Object control : channel.controls) {
            this.adjustKeyPositionY((KeyControl)control);
            this.addControl((GuiControl)control);
         }

         ++i;
      }

      this.setDuration(duration);
   }

   public GuiTimeline setSidebarWidth(int width) {
      this.sidebarWidth = width;
      return this;
   }

   public void adjustKeyPositionY(KeyControl control) {
      control.posY = this.timelineHeight + control.height / 2 + this.channelHeight * control.channel.index;
   }

   public void adjustKeyPositionX(KeyControl control) {
      control.posX = (int)((double)control.tick * this.getTickWidth()) - control.width / 2;
   }

   public void adjustKeysPositionX() {
      double tickWidth = this.getTickWidth();

      for(TimelineChannel channel : this.channels) {
         for(Object control : channel.controls) {
            ((KeyControl)control).posX = (int)((double)((KeyControl)control).tick * tickWidth) - ((KeyControl)control).width / 2;
         }
      }

   }

   public double getOffsetX() {
      return (double)this.sidebarWidth - this.scrollX.current();
   }

   public void mouseMove(int x, int y, int button) {
      if (this.dragged != null) {
         if (!this.movedSelected) {
            this.movedSelected = Math.abs(this.movedStart - x) > 5;
         }

         if (this.movedSelected) {
            int tick = Math.max(0, this.getTickAt(x));
            if (this.dragged.channel.isSpaceFor(this.dragged, tick)) {
               this.dragged.tick = tick;
               this.adjustKeyPositionX(this.dragged);
            }
         }
      } else if (this.draggedTimeline) {
         this.handler.set(MathHelper.clamp((int)(((double)(x - this.sidebarWidth) + this.getTickWidth() / 2.0D + this.scrollX.current()) / this.getTickWidth()), 0, this.duration));
      }

      super.mouseMove(x, y, button);
   }

   public boolean mousePressed(int x, int y, int button) {
      this.selected = null;
      boolean result = super.mousePressed(x, y, button);

      for(GuiControl control : this.controls) {
         if (control instanceof KeyControl && this.selected != control && ((KeyControl)control).selected) {
            ((KeyControl)control).selected = false;
            if (this.selected == null) {
               this.raiseEvent(new GuiTimeline.KeyDeselectedEvent((KeyControl)control));
            }
         }
      }

      if (!result && this.selected == null) {
         int channel = this.getChannelAt(y);
         if (channel != -1 && button == 1) {
            int tick = this.getTickAt(x);
            KeyControl control = ((TimelineChannel)this.channels.get(channel)).addKey(tick, ((TimelineChannel)this.channels.get(channel)).getValueAt(tick));
            if (control == null) {
               return result;
            }

            this.adjustKeyPositionX(control);
            this.adjustKeyPositionY(control);
            this.addControl(control);
            this.raiseEvent(new GuiControlChangedEvent(this));
         } else if (channel != -1) {
            int tick = this.getTickAt(x);
            if (tick >= 0 && tick <= this.duration) {
               this.handler.set(tick);
               this.draggedTimeline = true;
            }
         }
      }

      return result;
   }

   protected void clickControl(GuiControl control, int x, int y, int button) {
      if (control instanceof KeyControl) {
         if (button == 1) {
            if (!((KeyControl)control).modifiable) {
               return;
            }

            ((KeyControl)control).removeKey();
            this.selected = null;
            this.raiseEvent(new GuiControlChangedEvent(this));
            return;
         }

         this.selected = (KeyControl)control;
         this.selected.selected = true;
         if (this.selected.modifiable) {
            this.dragged = (KeyControl)control;
            this.movedSelected = false;
            this.movedStart = x;
         }

         this.raiseEvent(new GuiTimeline.KeySelectedEvent(this.selected));
         playSound(SoundEvents.UI_BUTTON_CLICK);
      }

      super.clickControl(control, x, y, button);
   }

   public void mouseReleased(int x, int y, int button) {
      super.mouseReleased(x, y, button);
      if (this.dragged != null) {
         this.dragged.channel.movedKey(this.dragged);
         this.dragged = null;
         this.raiseEvent(new GuiControlChangedEvent(this));
      }

      this.draggedTimeline = false;
   }

   public GuiTimeline setDuration(int duration) {
      int useableWidth = this.width - this.sidebarWidth - 2 - this.getContentOffset() * 2;
      this.ticksPerPixel = (double)duration / (double)useableWidth;
      this.basePixelWidth = 1.0D / this.ticksPerPixel;
      this.zoom.setStart(0.0D);
      this.duration = duration;
      this.scrollX.setStart(0.0D);
      this.scrollY.setStart(0.0D);
      this.adjustKeysPositionX();
      this.raiseEvent(new GuiControlChangedEvent(this));
      return this;
   }

   public int getDuration() {
      return this.duration;
   }

   public boolean mouseScrolled(int x, int y, int scrolled) {
      if (GuiScreen.isShiftKeyDown()) {
         this.scrollX.set(MathHelper.clamp(this.scrollX.aimed() - (double)(scrolled * 10), 0.0D, this.maxScrollX));
      } else if (GuiScreen.isCtrlKeyDown()) {
         this.scrollY.set(MathHelper.clamp(this.scrollY.aimed() + (double)scrolled, 0.0D, this.maxScrollY));
      } else {
         int focusedTick = Math.max(0, this.getTickAtAimed(x));
         this.zoom.set(MathHelper.clamp(this.zoom.aimed() + (double)scrolled * this.basePixelWidth * 2.0D * Math.max(this.basePixelWidth * 2.0D, this.zoom.aimed()) / 10.0D, 0.0D, 10.0D));
         int currentTick = Math.max(0, this.getTickAtAimed(x));
         double aimedTickWidth = this.getTickWidthAimed();
         double sizeX = aimedTickWidth * (double)this.duration;
         double maxScrollX = Math.max(0.0D, sizeX - (double)(this.width - this.getContentOffset() * 2));
         this.scrollX.set(MathHelper.clamp(this.scrollX.aimed() + (double)(focusedTick - currentTick) * aimedTickWidth, 0.0D, maxScrollX));
      }

      return true;
   }

   public int getChannelAt(int y) {
      int channel = (y - this.timelineHeight) / this.channelHeight;
      return channel >= 0 && channel < this.channels.size() ? channel : -1;
   }

   public int getTickAt(int x) {
      return x <= this.sidebarWidth ? -1 : MathHelper.clamp((int)(((double)(x - this.sidebarWidth) + this.getTickWidth() / 2.0D + this.scrollX.current()) / this.getTickWidth()), 0, this.duration);
   }

   public int getTickAtAimed(int x) {
      return x <= this.sidebarWidth ? -1 : MathHelper.clamp((int)(((double)(x - this.sidebarWidth) + this.getTickWidth() / 2.0D + this.scrollX.aimed()) / this.getTickWidthAimed()), 0, this.duration);
   }

   protected double getTickWidth() {
      return this.basePixelWidth + this.zoom.current();
   }

   protected double getTickWidthAimed() {
      return this.basePixelWidth + this.zoom.aimed();
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      this.zoom.tick();
      this.scrollX.tick();
      this.scrollY.tick();
      int usuableWidth = width - this.sidebarWidth;
      double tickWidth = this.getTickWidth();
      if (this.lastZoom != this.zoom.current()) {
         this.sizeX = tickWidth * (double)this.duration;
         this.maxScrollX = Math.max(0.0D, this.sizeX - (double)usuableWidth);
         this.lastZoom = this.zoom.current();
         this.adjustKeysPositionX();
      }

      GlStateManager.pushMatrix();
      this.timelineBackground.renderStyle(helper, this.sidebarWidth, height);
      GlStateManager.translate(0.0F, (float)this.timelineHeight, 0.0F);

      for(int i = 0; i < this.channels.size(); ++i) {
         font.drawString(((TimelineChannel)this.channels.get(i)).name, 1, 1, -16777216);
         GlStateManager.translate(0.0F, (float)this.channelHeight, 0.0F);
      }

      GlStateManager.popMatrix();
      GlStateManager.translate((float)this.sidebarWidth, 0.0F, 0.0F);
      GlStateManager.pushMatrix();
      this.timelineBackground.renderStyle(helper, width, this.timelineHeight);
      GlStateManager.translate(0.0F, (float)this.timelineHeight, 0.0F);
      this.getStyle().getBorder(this).renderStyle(helper, width, 1);
      int ticks = (int)((double)usuableWidth / tickWidth);
      int area = 5;
      int halfArea = 5;

      int smallestStep;
      for(smallestStep = 0; Math.pow((double)area, (double)smallestStep) * tickWidth < 3.0D; ++smallestStep) {
         ;
      }

      smallestStep = (int)Math.pow((double)area, (double)smallestStep);
      double stepWidth = tickWidth * (double)smallestStep;
      int stepOffset = (int)(this.scrollX.current() / stepWidth);
      int stamps = ticks / smallestStep;
      int begin = Math.max(0, stepOffset);
      int end = stepOffset + stamps + 1;
      GlStateManager.pushMatrix();
      int pointerWidth = Math.max((int)tickWidth, 1);
      GlStateManager.translate(tickWidth * (double)this.handler.get() - (double)pointerWidth / 2.0D - this.scrollX.current(), (double)(-this.timelineHeight), 0.0D);
      (new ColoredDisplayStyle(200, 200, 0, 150)).renderStyle(helper, pointerWidth, this.timelineHeight);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(-this.scrollX.current() + (double)begin * stepWidth, -2.0D, 0.0D);

      for(int i = begin; i < end; ++i) {
         if (i % halfArea == 0) {
            this.getStyle().getBorder(this).renderStyle(helper, 1, 2);
            this.getStyle().getDisableEffect(this).renderStyle(0, 2, helper, 1, height - 2);
            String text = "" + i * smallestStep;
            font.drawString(text, 0 - font.getStringWidth(text) / 2, -8, -16777216);
         } else {
            this.getStyle().getBorder(this).renderStyle(helper, 1, 1);
         }

         GlStateManager.translate(stepWidth, 0.0D, 0.0D);
      }

      GlStateManager.popMatrix();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, (float)this.timelineHeight, 0.0F);

      for(int i = 0; i < this.channels.size(); ++i) {
         GlStateManager.translate(0.0F, (float)this.channelHeight, 0.0F);
         this.getStyle().getBorder(this).renderStyle(helper, usuableWidth, 1);
      }

      GlStateManager.popMatrix();
      if (this.maxScrollX > 0.0D) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(this.scrollX.current() / this.sizeX * (double)usuableWidth, (double)(height - 2), 0.0D);
         this.getStyle().getBorder(this).renderStyle(helper, (int)Math.max(1.0D, (1.0D - this.maxScrollX / this.sizeX) * (double)usuableWidth), 1);
         GlStateManager.popMatrix();
      }

   }

   public boolean hasMouseOverEffect() {
      return false;
   }

   public List<String> getTooltip() {
      Vec3d mouse = this.getMousePos();
      double x = mouse.x + this.getOffsetX();
      List<String> lines = new ArrayList();
      if (x > (double)this.sidebarWidth && x < (double)(this.width - this.getContentOffset() * 2)) {
         int channelId = this.getChannelAt((int)mouse.y);
         if (channelId >= 0) {
            TimelineChannel channel = (TimelineChannel)this.channels.get(channelId);
            int tick = this.getTickAt((int)x);
            lines.add("" + tick + ". " + channel.name + ": " + channel.getValueAt(tick));
         }
      }

      return lines;
   }

   public static class KeyDeselectedEvent extends GuiControlEvent {
      public KeyDeselectedEvent(KeyControl source) {
         super(source);
      }

      public boolean isCancelable() {
         return false;
      }
   }

   public static class KeySelectedEvent extends GuiControlEvent {
      public KeySelectedEvent(KeyControl source) {
         super(source);
      }

      public boolean isCancelable() {
         return false;
      }
   }
}
