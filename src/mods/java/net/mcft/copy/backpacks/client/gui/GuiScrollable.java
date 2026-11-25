package net.mcft.copy.backpacks.client.gui;

import java.util.EnumSet;
import net.mcft.copy.backpacks.client.gui.control.GuiSlider;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScrollable extends GuiContainer {
   private static final int GRADIENT_SIZE = 4;
   public final EnumSet<Direction> directions;
   private GuiScrollable.GuiScrollbar _scrollbarHor;
   private GuiScrollable.GuiScrollbar _scrollbarVer;
   private int _scrollAmount;
   private int _scrollX;
   private int _scrollY;
   private int _scrollStartX;
   private int _scrollStartY;
   private int _contentWidth;
   private int _contentHeight;
   private int _contentWidthExtra;
   private int _contentHeightExtra;

   public GuiScrollable() {
      this(EnumSet.allOf(Direction.class));
   }

   public GuiScrollable(Direction direction) {
      this(EnumSet.of(direction));
   }

   public GuiScrollable(EnumSet<Direction> directions) {
      this._scrollAmount = 10;
      this._scrollX = 0;
      this._scrollY = 0;
      this._contentWidth = 0;
      this._contentHeight = 0;
      this._contentWidthExtra = 0;
      this._contentHeightExtra = 0;
      if (directions.isEmpty()) {
         throw new IllegalArgumentException("Argument 'directions' must be at least one Direction");
      } else {
         this.directions = directions;

         for(Direction direction : directions) {
            GuiScrollable.GuiScrollbar scrollbar = new GuiScrollable.GuiScrollbar(direction);
            if (direction == Direction.HORIZONTAL) {
               this._scrollbarHor = scrollbar;
            } else {
               this._scrollbarVer = scrollbar;
            }

            this.add(scrollbar);
         }

      }
   }

   public GuiScrollable.GuiScrollbar getScrollbar(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._scrollbarHor : this._scrollbarVer;
   }

   public final GuiScrollable.GuiScrollbar getScrollbar() {
      if (this.directions.size() != 1) {
         throw new UnsupportedOperationException("Element slides in both directions");
      } else {
         return this.getScrollbar((Direction)this.directions.iterator().next());
      }
   }

   public int getScroll(Direction direction) {
      return direction == Direction.HORIZONTAL ? this._scrollX : this._scrollY;
   }

   public final int getScrollX() {
      return this.getScroll(Direction.HORIZONTAL);
   }

   public final int getScrollY() {
      return this.getScroll(Direction.VERTICAL);
   }

   public void setScroll(Direction direction, int value) {
      value = MathHelper.clamp(value, 0, this.getMaxScroll(direction));
      if (direction == Direction.HORIZONTAL) {
         if (value == this._scrollX) {
            return;
         }

         this._scrollX = value;
      } else {
         if (value == this._scrollY) {
            return;
         }

         this._scrollY = value;
      }

      this.onScrollChanged(direction);
   }

   public final void setScrollX(int value) {
      this.setScroll(Direction.HORIZONTAL, value);
   }

   public final void setScrollY(int value) {
      this.setScroll(Direction.VERTICAL, value);
   }

   public int getMaxScroll(Direction direction) {
      return Math.max(0, this.getContentSize(direction) - this.getSize(direction));
   }

   public int getContentSize(Direction direction) {
      return this.getContentSize(direction, false);
   }

   public int getContentSize(Direction direction, boolean withoutPadding) {
      int size = direction == Direction.HORIZONTAL ? this._contentWidth : this._contentHeight;
      if (!withoutPadding) {
         size += direction == Direction.HORIZONTAL ? this._contentWidthExtra : this._contentHeightExtra;
      }

      return size;
   }

   protected void onScrollChanged(Direction direction) {
   }

   public boolean canDrag() {
      return true;
   }

   public void onPressed(int mouseX, int mouseY) {
      this._scrollStartX = this._scrollX;
      this._scrollStartY = this._scrollY;
   }

   public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
      this.setScrollX(this._scrollStartX + (startX - mouseX));
      this.setScrollY(this._scrollStartY + (startY - mouseY));
   }

   public boolean onMouseScroll(int scroll, int mouseX, int mouseY) {
      if (this.isDragged()) {
         return false;
      } else if (super.onMouseScroll(scroll, mouseX, mouseY)) {
         return true;
      } else {
         this.setScrollY(this.getScrollY() - scroll * this._scrollAmount);
         return true;
      }
   }

   public int getChildPos(GuiElementBase element, Direction direction) {
      return this.getChildPos(element, direction, true);
   }

   protected int getChildPos(GuiElementBase element, Direction direction, boolean scroll) {
      Alignment align = element.getAlign(direction);
      int scrollAmount = scroll ? this.getScroll(direction) : 0;
      return align instanceof GuiScrollable.ContentMax ? Math.min(this.getSize(direction) - element.getSize(direction), this.getContentSize(direction, true) + ((GuiScrollable.ContentMax)align).margin) : (align instanceof Alignment.Max ? (align instanceof GuiScrollable.FixedMax ? this.getSize(direction) : this.getContentSize(direction) - scrollAmount - this.getPaddingMax(direction)) - element.getSize(direction) - ((Alignment.Max)align).max : (align instanceof GuiScrollable.IFixedAlign ? super.getChildPos(element, direction) - this.getPaddingMin(direction) : super.getChildPos(element, direction) - scrollAmount));
   }

   protected void updateChildSizes(Direction direction) {
      for(GuiElementBase child : this.children) {
         Alignment align = child.getAlign(direction);
         if (align instanceof Alignment.Both) {
            Alignment.Both both = (Alignment.Both)align;
            int size = both instanceof GuiScrollable.FixedBoth ? this.getSize(direction) : this.getContentSize(direction);
            child.setSize(direction, size - both.min - both.max);
         }
      }

   }

   protected void expandToFitChildren(Direction direction) {
      int max = 0;
      int extra = 0;

      for(GuiElementBase child : this.children) {
         Alignment align = child.getAlign(direction);
         if (align instanceof GuiScrollable.ContentMax) {
            extra = Math.max(extra, ((GuiScrollable.ContentMax)align).margin + child.getSize(direction));
         } else if (!(align instanceof Alignment.Both)) {
            max = Math.max(max, this.getChildPos(child, direction, false) - this.getPaddingMin(direction) + child.getSize(direction));
         }
      }

      if (direction == Direction.HORIZONTAL) {
         this._contentWidth = max;
      } else {
         this._contentHeight = max;
      }

      extra = extra + this.getPadding(direction);
      if (direction == Direction.HORIZONTAL) {
         this._contentWidthExtra = extra;
      } else {
         this._contentHeightExtra = extra;
      }

      if (this.getScroll(direction) > this.getMaxScroll(direction)) {
         this.setScroll(direction, this.getMaxScroll(direction));
      }

   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         ElementInfo info = (ElementInfo)ElementInfo.getElementHierarchy(this).getFirst();
         this.getContext().pushScissor(info.globalX, info.globalY, this.getWidth(), this.getHeight());
         this.drawBackground(info.globalX + this.getScrollX(), info.globalY + this.getScrollY(), mouseX, mouseY, partialTicks);
         this.foreachChildMousePos(mouseX, mouseY, (child, x, y, mx, my) -> {
            if (!(child instanceof GuiScrollable.GuiScrollbar)) {
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x, (float)y, 0.0F);
               child.draw(mx, my, partialTicks);
               GlStateManager.popMatrix();
            }
         });
         this.drawForeground(mouseX, mouseY, partialTicks);
         this.getContext().popScissor();
      }
   }

   public void drawBackground(int x, int y, int mouseX, int mouseY, float partialTicks) {
      float scale = 0.03125F;
      float u1 = (float)x * scale;
      float v1 = (float)y * scale;
      float u2 = (float)(x + this.getWidth()) * scale;
      float v2 = (float)(y + this.getHeight()) * scale;
      bindTexture(Gui.OPTIONS_BACKGROUND);
      setRenderColorRGB(2105376);
      drawRect(0, 0, this.getWidth(), this.getHeight(), u1, v1, u2, v2);
   }

   public void drawForeground(int mouseX, int mouseY, float partialTicks) {
      enableBlendAlphaStuffs();
      if (this.directions.contains(Direction.VERTICAL)) {
         drawColoredRectARGB(0, 0, this.getWidth(), 4, GuiElementBase.Color.BLACK, GuiElementBase.Color.BLACK, GuiElementBase.Color.TRANSPARENT, GuiElementBase.Color.TRANSPARENT);
         drawColoredRectARGB(0, this.getHeight() - 4, this.getWidth(), 4, GuiElementBase.Color.TRANSPARENT, GuiElementBase.Color.TRANSPARENT, GuiElementBase.Color.BLACK, GuiElementBase.Color.BLACK);
      }

      if (this.directions.contains(Direction.HORIZONTAL)) {
         drawColoredRectARGB(0, 0, 4, this.getHeight(), GuiElementBase.Color.BLACK, GuiElementBase.Color.TRANSPARENT, GuiElementBase.Color.BLACK, GuiElementBase.Color.TRANSPARENT);
         drawColoredRectARGB(this.getWidth() - 4, 0, 4, this.getHeight(), GuiElementBase.Color.BLACK, GuiElementBase.Color.TRANSPARENT, GuiElementBase.Color.BLACK, GuiElementBase.Color.TRANSPARENT);
      }

      disableBlendAlphaStuffs();
      this.foreachChildMousePos(mouseX, mouseY, (child, x, y, mx, my) -> {
         if (child instanceof GuiScrollable.GuiScrollbar) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, 0.0F);
            child.draw(mx, my, partialTicks);
            GlStateManager.popMatrix();
         }
      });
   }

   public static class ContentMax extends Alignment {
      public final int margin;

      public ContentMax(int margin) {
         this.margin = margin;
      }
   }

   public static class FixedBoth extends Alignment.Both implements GuiScrollable.IFixedAlign {
      public FixedBoth(int min, int max) {
         super(min, max);
      }
   }

   public static class FixedMax extends Alignment.Max implements GuiScrollable.IFixedAlign {
      public FixedMax(int max) {
         super(max);
      }
   }

   public static class FixedMin extends Alignment.Min implements GuiScrollable.IFixedAlign {
      public FixedMin(int min) {
         super(min);
      }
   }

   public class GuiScrollbar extends GuiSlider {
      public static final int THICKNESS = 6;
      public final Direction direction;
      private int _scrollStart;

      private GuiScrollbar(Direction direction) {
         super(0, 0, direction == Direction.HORIZONTAL ? 200 : 6, direction == Direction.VERTICAL ? 200 : 6, direction);
         this.direction = direction;
         this.setAlign(direction, new GuiScrollable.FixedBoth(0, 0));
         this.setAlign(direction.perpendicular(), new GuiScrollable.FixedMax(0));
         this.setSliderRaw(0.0D);
      }

      public int getSliderSize() {
         return MathHelper.clamp((int)Math.pow((double)GuiScrollable.this.getSize(this.direction), 2.0D) / GuiScrollable.this.getContentSize(this.direction), 32, this.getSize(this.direction) - 8);
      }

      public double getSliderRaw(Direction direction) {
         return (double)GuiScrollable.this.getScroll(direction) / (double)GuiScrollable.this.getMaxScroll(direction);
      }

      public void onPressed(int mouseX, int mouseY) {
         this._scrollStart = GuiScrollable.this.getScroll(this.direction);
         super.onPressed(mouseX, mouseY);
      }

      public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
         int diff = this.direction == Direction.HORIZONTAL ? mouseX - startX : mouseY - startY;
         double scrollFactor = (double)(this.getSize(this.direction) - this.getSliderSize()) / (double)Math.max(1, GuiScrollable.this.getMaxScroll(this.direction));
         GuiScrollable.this.setScroll(this.direction, this._scrollStart + (int)((double)diff / scrollFactor));
      }

      public String getValueText() {
         return "";
      }

      public void draw(int mouseX, int mouseY, float partialTicks) {
         if (this.isVisible() && GuiScrollable.this.getMaxScroll(this.direction) != 0) {
            int sliderSize = this.getSliderSize();
            int sliderPosition = (int)(this.getSliderRaw() * (double)(this.getSize(this.direction) - sliderSize));
            int x;
            int y;
            int w;
            int h;
            if (this.direction == Direction.HORIZONTAL) {
               x = sliderPosition;
               y = 0;
               w = sliderSize;
               h = this.getHeight();
            } else {
               x = 0;
               y = sliderPosition;
               w = this.getWidth();
               h = sliderSize;
            }

            GlStateManager.disableTexture2D();
            drawColoredRectRGB(0, 0, this.getWidth(), this.getHeight(), GuiElementBase.Color.BLACK);
            drawColoredRectRGB(x, y, w, h, 8421504);
            drawColoredRectRGB(x, y, w - 1, h - 1, 12632256);
            GlStateManager.enableTexture2D();
         }
      }
   }

   public interface IFixedAlign {
   }
}
