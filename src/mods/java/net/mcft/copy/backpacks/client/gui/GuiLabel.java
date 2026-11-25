package net.mcft.copy.backpacks.client.gui;

import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLabel extends GuiElementBase {
   public static final int SHADOW_DISABLED = -1;
   public static final int SHADOW_AUTOMATIC = -2;
   private String _text;
   private GuiLabel.TextAlign _align;
   private boolean _expands;
   private int _color;
   private int _disabledColor;
   private int _shadowColor;

   public GuiLabel(String text) {
      this(text, GuiLabel.TextAlign.LEFT);
   }

   public GuiLabel(String text, GuiLabel.TextAlign align) {
      this(0, 0, text, align);
   }

   public GuiLabel(int x, int y, String text) {
      this(x, y, text, GuiLabel.TextAlign.LEFT);
   }

   public GuiLabel(int x, int y, String text, GuiLabel.TextAlign align) {
      this._text = "";
      this._align = GuiLabel.TextAlign.LEFT;
      this._color = GuiElementBase.Color.WHITE;
      this._disabledColor = -6250336;
      this._shadowColor = -2;
      this._expands = true;
      this.setPosition(x, y);
      this.setText(text);
      this.setTextAlign(align);
   }

   public GuiLabel(int width, String text) {
      this(width, text, GuiLabel.TextAlign.LEFT);
   }

   public GuiLabel(int width, String text, GuiLabel.TextAlign align) {
      this(0, 0, width, text, align);
   }

   public GuiLabel(int x, int y, int width, String text) {
      this(x, y, width, LINE_HEIGHT, text, GuiLabel.TextAlign.LEFT);
   }

   public GuiLabel(int x, int y, int width, String text, GuiLabel.TextAlign align) {
      this(x, y, width, LINE_HEIGHT, text, align);
   }

   public GuiLabel(int x, int y, int width, int height, String text) {
      this(x, y, width, height, text, GuiLabel.TextAlign.LEFT);
   }

   public GuiLabel(int x, int y, int width, int height, String text, GuiLabel.TextAlign align) {
      this._text = "";
      this._align = GuiLabel.TextAlign.LEFT;
      this._color = GuiElementBase.Color.WHITE;
      this._disabledColor = -6250336;
      this._shadowColor = -2;
      this._expands = false;
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setText(text);
      this.setTextAlign(align);
   }

   public boolean getExpands() {
      return this._expands;
   }

   public GuiLabel.TextAlign getTextAlign() {
      return this._align;
   }

   public void setTextAlign(GuiLabel.TextAlign value) {
      this._align = value;
   }

   public String getText() {
      return this._text;
   }

   public void setText(String text) {
      if (text == null) {
         throw new NullPointerException("Argument text can't be null");
      } else if (!text.equals(this._text)) {
         this._text = text;
         if (this._expands) {
            FontRenderer fontRenderer = getFontRenderer();
            int width = fontRenderer.getStringWidth(this._text);
            int height = fontRenderer.getWordWrappedHeight(this._text, Integer.MAX_VALUE);
            this.setSize(width, height);
            this._expands = true;
         }

      }
   }

   public int getColor() {
      return this._color;
   }

   public void setColor(int value) {
      this._color = value;
   }

   public int getDisabledColor() {
      return this._disabledColor;
   }

   public void setDisabledColor(int value) {
      this._disabledColor = value;
   }

   public int getShadowColor() {
      return this._shadowColor;
   }

   public void setShadowColor(int value) {
      if (value < -2) {
         throw new IllegalArgumentException(String.format("'%d' is not a valid special shadow value", value));
      } else {
         this._shadowColor = value;
      }
   }

   public void setShadowDisabled() {
      this.setShadowColor(-1);
   }

   public void onSizeChanged(Direction direction) {
      super.onSizeChanged(direction);
      this._expands = false;
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         FontRenderer fontRenderer = getFontRenderer();
         List<String> lines = fontRenderer.listFormattedStringToWidth(this._text, Integer.MAX_VALUE);
         int yPos = 1;

         for(String line : lines) {
            int lineWidth = fontRenderer.getStringWidth(line);
            if (!this._expands && lineWidth > this.getWidth()) {
               line = fontRenderer.trimStringToWidth(line, this.getWidth() - ELLIPSIS_WIDTH) + "...";
               lineWidth = fontRenderer.getStringWidth(line);
            }

            int xPos = 0;
            switch(this._align) {
            case LEFT:
               xPos = 0;
               break;
            case RIGHT:
               xPos = this.getWidth() - lineWidth;
               break;
            case CENTER:
               xPos = (this.getWidth() - lineWidth) / 2;
            }

            int color = this.isEnabled() ? this.getColor() : this.getDisabledColor();
            int shadowColor = this.getShadowColor();
            if (shadowColor >= 0) {
               fontRenderer.drawString(line, xPos + 1, yPos + 1, shadowColor);
            }

            if (shadowColor == -2) {
               fontRenderer.drawStringWithShadow(line, (float)xPos, (float)yPos, color);
            } else {
               fontRenderer.drawString(line, xPos, yPos, color);
            }

            yPos += LINE_HEIGHT;
         }

      }
   }

   public static enum TextAlign {
      LEFT,
      RIGHT,
      CENTER;

      private TextAlign() {
      }
   }
}
