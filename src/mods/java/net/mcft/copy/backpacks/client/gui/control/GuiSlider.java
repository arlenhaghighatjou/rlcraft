package net.mcft.copy.backpacks.client.gui.control;

import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.function.DoubleFunction;
import net.mcft.copy.backpacks.client.gui.Direction;
import net.mcft.copy.backpacks.client.gui.GuiElementBase;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiSlider extends GuiElementBase {
   public static final DecimalFormat DEFAULT_FORMATTER = new DecimalFormat("0.##");
   public static final int DEFAULT_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public final EnumSet<Direction> directions;
   private double _amountX;
   private double _amountY;
   private double _minX;
   private double _minY;
   private double _maxX;
   private double _maxY;
   private double _stepX;
   private double _stepY;
   private int _sliderSize;
   private Runnable _changedAction;
   private DoubleFunction<String> _valueFormatter;

   public GuiSlider() {
      this(Direction.HORIZONTAL);
   }

   public GuiSlider(Direction direction) {
      this(200, direction);
   }

   public GuiSlider(int size) {
      this(size, Direction.HORIZONTAL);
   }

   public GuiSlider(int size, Direction direction) {
      this(direction == Direction.HORIZONTAL ? size : 20, direction == Direction.VERTICAL ? size : 20, direction);
   }

   public GuiSlider(int width, int height) {
      this(width, height, Direction.HORIZONTAL);
   }

   public GuiSlider(int width, int height, Direction direction) {
      this(width, height, EnumSet.of(direction));
   }

   public GuiSlider(int width, int height, EnumSet<Direction> directions) {
      this(0, 0, width, height, directions);
   }

   public GuiSlider(int x, int y, int width, int height, Direction direction) {
      this(x, y, width, height, EnumSet.of(direction));
   }

   public GuiSlider(int x, int y, int width, int height, EnumSet<Direction> directions) {
      this._amountX = 0.5D;
      this._amountY = 0.5D;
      this._minX = 0.0D;
      this._minY = 0.0D;
      this._maxX = 1.0D;
      this._maxY = 1.0D;
      this._stepX = 0.0D;
      this._stepY = 0.0D;
      this._sliderSize = 8;
      this._changedAction = null;
      DecimalFormat var10001 = DEFAULT_FORMATTER;
      DEFAULT_FORMATTER.getClass();
      this._valueFormatter = var10001::format;
      if (directions.isEmpty()) {
         throw new IllegalArgumentException("Argument 'directions' must be at least one Direction");
      } else {
         this.setPosition(x, y);
         this.setSize(width, height);
         this.directions = directions;
      }
   }

   public int getSliderSize() {
      return this._sliderSize;
   }

   public void setSliderSize(int value) {
      this._sliderSize = value;
   }

   public double getSliderRaw(Direction direction) {
      this.ensureValidDirection(direction);
      return direction == Direction.HORIZONTAL ? this._amountX : this._amountY;
   }

   public final double getSliderRawX() {
      return this.getSliderRaw(Direction.HORIZONTAL);
   }

   public final double getSliderRawY() {
      return this.getSliderRaw(Direction.VERTICAL);
   }

   public final double getSliderRaw() {
      return this.getSliderRaw(this.getOnlyDirection());
   }

   public void setSliderRaw(Direction direction, double value) {
      this.setSliderRawInternal(direction, value, true);
   }

   private boolean setSliderRawInternal(Direction direction, double value, boolean fireChanged) {
      this.ensureValidDirection(direction);
      value = MathHelper.clamp(value, 0.0D, 1.0D);
      double stepSize = this.getStepSize(direction);
      if (stepSize > 0.0D) {
         double min = this.getMin(direction);
         double max = this.getMax(direction);
         value = min + value * (max - min);
         value = (double)Math.round(value / stepSize) * stepSize;
         value = (value - min) / (max - min);
      }

      if (direction == Direction.HORIZONTAL) {
         if (value == this._amountX) {
            return false;
         }

         this._amountX = value;
      } else {
         if (value == this._amountY) {
            return false;
         }

         this._amountY = value;
      }

      if (fireChanged) {
         this.onChanged();
      }

      return true;
   }

   public final void setSliderRawX(double value) {
      this.setSliderRaw(Direction.HORIZONTAL, value);
   }

   public final void setSliderRawY(double value) {
      this.setSliderRaw(Direction.VERTICAL, value);
   }

   public final void setSliderRaw(double value) {
      this.setSliderRaw(this.getOnlyDirection(), value);
   }

   public final void setSliderRaw(double hor, double vert) {
      if (this.setSliderRawInternal(Direction.HORIZONTAL, hor, false) | this.setSliderRawInternal(Direction.VERTICAL, vert, false)) {
         this.onChanged();
      }

   }

   public void setChangedAction(Runnable value) {
      this._changedAction = value;
   }

   protected void onChanged() {
      if (this._changedAction != null) {
         this._changedAction.run();
      }

   }

   public double getMin(Direction direction) {
      this.ensureValidDirection(direction);
      return direction == Direction.HORIZONTAL ? this._minX : this._minY;
   }

   public double getMax(Direction direction) {
      this.ensureValidDirection(direction);
      return direction == Direction.HORIZONTAL ? this._maxX : this._maxY;
   }

   public void setRange(Direction direction, double min, double max) {
      this.ensureValidDirection(direction);
      if (min > max) {
         throw new IllegalArgumentException("min is greater than max");
      } else {
         if (direction == Direction.HORIZONTAL) {
            this._minX = min;
            this._maxX = max;
         } else {
            this._minY = min;
            this._maxY = max;
         }

      }
   }

   public void setRangeX(double min, double max) {
      this.setRange(Direction.HORIZONTAL, min, max);
   }

   public void setRangeY(double min, double max) {
      this.setRange(Direction.VERTICAL, min, max);
   }

   public void setRange(double min, double max) {
      this.setRange(this.getOnlyDirection(), min, max);
   }

   public double getStepSize(Direction direction) {
      this.ensureValidDirection(direction);
      return direction == Direction.HORIZONTAL ? this._stepX : this._stepY;
   }

   public void setStepSize(Direction direction, double value) {
      this.ensureValidDirection(direction);
      if (value < 0.0D) {
         throw new IllegalArgumentException("value must be positive");
      } else {
         if (direction == Direction.HORIZONTAL) {
            this._stepX = value;
         } else {
            this._stepY = value;
         }

      }
   }

   public final void setStepSize(double value) {
      this._stepX = this._stepY = value;
   }

   public double getSliderValue(Direction direction) {
      double min = this.getMin(direction);
      double max = this.getMax(direction);
      double stepSize = this.getStepSize(direction);
      double value = min + this.getSliderRaw(direction) * (max - min);
      return stepSize > 0.0D ? (double)Math.round(value / stepSize) * stepSize : value;
   }

   public final double getSliderValueX() {
      return this.getSliderValue(Direction.HORIZONTAL);
   }

   public final double getSliderValueY() {
      return this.getSliderValue(Direction.VERTICAL);
   }

   public final double getSliderValue() {
      return this.getSliderValue(this.getOnlyDirection());
   }

   public void setSliderValue(Direction direction, double value) {
      double min = this.getMin(direction);
      double max = this.getMax(direction);
      this.setSliderRaw(direction, (value - min) / (max - min));
   }

   public final void setSliderValue(double hor, double vert) {
      double hMin = this.getMin(Direction.HORIZONTAL);
      double hMax = this.getMax(Direction.HORIZONTAL);
      double vMin = this.getMin(Direction.VERTICAL);
      double vMax = this.getMax(Direction.VERTICAL);
      this.setSliderRaw((hor - hMin) / (hMax - hMin), (vert - vMin) / (vMax - vMin));
   }

   public final void setSliderValueX(double value) {
      this.setSliderValue(Direction.HORIZONTAL, value);
   }

   public final void setSliderValueY(double value) {
      this.setSliderValue(Direction.VERTICAL, value);
   }

   public final void setSliderValue(double value) {
      this.setSliderValue(this.getOnlyDirection(), value);
   }

   public void setValueFormatter(DoubleFunction<String> value) {
      if (value == null) {
         throw new NullPointerException("Argument value can't be null");
      } else {
         this._valueFormatter = value;
      }
   }

   public String getValueText() {
      return (String)this._valueFormatter.apply(this.getSliderValue());
   }

   public boolean canDrag() {
      return true;
   }

   public void onPressed(int mouseX, int mouseY) {
      super.onPressed(mouseX, mouseY);
      this.onDragged(mouseX, mouseY, 0, 0, mouseX, mouseY);
   }

   public void onDragged(int mouseX, int mouseY, int deltaX, int deltaY, int startX, int startY) {
      for(Direction direction : this.directions) {
         int pos = direction == Direction.HORIZONTAL ? mouseX : mouseY;
         this.setSliderRaw(direction, (double)((float)(pos - this.getSliderSize() / 2) / (float)(this.getSize(direction) - this.getSliderSize())));
      }

   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         boolean isHighlighted = this.isHighlighted(mouseX, mouseY);
         this.drawSliderBackground(isHighlighted, partialTicks);
         this.drawSliderBar(isHighlighted, partialTicks);
         this.drawSliderForeground(isHighlighted, partialTicks);
      }
   }

   protected void drawSliderBackground(boolean isHighlighted, float partialTicks) {
      GuiUtils.drawContinuousTexturedBox(GuiButton.BUTTON_TEX, 0, 0, 0, 46, this.getWidth(), this.getHeight(), 200, 20, 2, 3, 2, 2, 0.0F);
   }

   protected void drawSliderBar(boolean isHighlighted, float partialTicks) {
      int sliderSize = this.getSliderSize();
      boolean slideHorizontal = this.directions.contains(Direction.HORIZONTAL);
      boolean slideVertical = this.directions.contains(Direction.VERTICAL);
      int x = slideHorizontal ? (int)((double)(this.getWidth() - sliderSize) * this.getSliderRawX()) : 0;
      int y = slideVertical ? (int)((double)(this.getHeight() - sliderSize) * this.getSliderRawY()) : 0;
      int w = slideHorizontal ? sliderSize : this.getWidth();
      int h = slideVertical ? sliderSize : this.getHeight();
      int ty = this.isEnabled() ? 66 : 46;
      GuiUtils.drawContinuousTexturedBox(GuiButton.BUTTON_TEX, x, y, 0, ty, w, h, 200, 20, 2, 3, 2, 2, 0.0F);
   }

   protected void drawSliderForeground(boolean isHighlighted, float partialTicks) {
      String text = this.getValueText();
      if (!text.isEmpty()) {
         FontRenderer fontRenderer = getFontRenderer();
         int textWidth = fontRenderer.getStringWidth(text);
         int textColor = !this.isEnabled() ? -6250336 : (isHighlighted ? -96 : -2039584);
         fontRenderer.drawStringWithShadow(text, (float)(this.getWidth() / 2 - textWidth / 2), (float)((this.getHeight() - 8) / 2), textColor);
      }
   }

   private void ensureValidDirection(Direction direction) {
      if (!this.directions.contains(direction)) {
         throw new UnsupportedOperationException("Element doesn't slide in direction '" + direction + "'");
      }
   }

   private Direction getOnlyDirection() {
      if (this.directions.size() != 1) {
         throw new UnsupportedOperationException("Element slides in both directions");
      } else {
         return (Direction)this.directions.iterator().next();
      }
   }
}
