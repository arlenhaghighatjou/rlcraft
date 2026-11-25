package net.mcft.copy.backpacks.client.gui.control;

import java.util.function.Predicate;
import net.mcft.copy.backpacks.client.gui.Direction;
import net.mcft.copy.backpacks.client.gui.GuiElementBase;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiField extends GuiElementBase {
   public static final int DEFAULT_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public static final int COLOR_TEXT_DEFAULT = -2039584;
   public static final int COLOR_BACKGROUND_DEFAULT = -16777216;
   public static final int COLOR_BORDER_DEFAULT = -6250336;
   public static final int COLOR_TEXT_DISABLED = -9408400;
   private final GuiTextField _field;
   private int _colorText;
   private int _colorBackground;
   private int _colorBorder;
   private Runnable _changedAction;
   private Predicate<Character> _charValidator;

   public GuiField() {
      this(200);
   }

   public GuiField(int width) {
      this(width, 20);
   }

   public GuiField(int width, int height) {
      this(width, height, "");
   }

   public GuiField(String text) {
      this(200, text);
   }

   public GuiField(int width, String text) {
      this(width, 20, text);
   }

   public GuiField(int width, int height, String text) {
      this(0, 0, width, height, text);
   }

   public GuiField(int x, int y, int width, int height, String text) {
      this._field = new GuiTextField(0, getFontRenderer(), 1, 1, 0, 0);
      this._colorText = -2039584;
      this._colorBackground = -16777216;
      this._colorBorder = -6250336;
      this._changedAction = null;
      this._charValidator = null;
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setText(text);
      this.setMaxLength(Integer.MAX_VALUE);
   }

   public String getText() {
      return this._field.getText();
   }

   public void setText(String value) {
      if (value == null) {
         throw new NullPointerException("Argument can't be null");
      } else if (!value.equals(this.getText())) {
         this._field.setText(value);
         this._field.setCursorPositionZero();
      }
   }

   public int getMaxLength() {
      return this._field.getMaxStringLength();
   }

   public void setMaxLength(int value) {
      this._field.setMaxStringLength(value);
   }

   public int getCursorPosition() {
      return this._field.getCursorPosition();
   }

   public void setTextColor(int value) {
      this._colorText = (value & -16777216) == 0 ? value | -16777216 : value;
   }

   public void resetTextColor() {
      this.setTextColor(-2039584);
   }

   public void setBackgroundColor(int value) {
      this._colorBackground = (value & -16777216) == 0 ? value | -16777216 : value;
   }

   public void resetBackgroundColor() {
      this.setBorderColor(-16777216);
   }

   public void setBorderColor(int value) {
      this._colorBorder = (value & -16777216) == 0 ? value | -16777216 : value;
   }

   public void resetBorderColor() {
      this.setBorderColor(-6250336);
   }

   public void setChangedAction(Runnable value) {
      this._changedAction = value;
   }

   protected void onTextChanged() {
      if (this._changedAction != null) {
         this._changedAction.run();
      }

   }

   public void setCharValidator(Predicate<Character> value) {
      this._charValidator = value;
   }

   protected boolean isCharValid(char chr) {
      return this._charValidator != null ? this._charValidator.test(Character.valueOf(chr)) : true;
   }

   public boolean canFocus() {
      return true;
   }

   public void onSizeChanged(Direction direction) {
      if (direction == Direction.HORIZONTAL) {
         this._field.width = this.getWidth() - 2;
      } else {
         this._field.height = this.getHeight() - 2;
      }

   }

   public void onPressed(int mouseX, int mouseY) {
      this._field.mouseClicked(mouseX, mouseY, GuiElementBase.MouseButton.LEFT);
   }

   public void onKey(int keyCode, char keyChar) {
      if (this.isEnabled() || keyCode == 203 || keyCode == 205 || keyCode == 199 || keyCode == 207) {
         String beforeText = this.getText();
         if (Character.isISOControl(keyChar) || this.isCharValid(keyChar)) {
            this._field.textboxKeyTyped(keyChar, keyCode);
            if (!this.getText().equals(beforeText)) {
               this.onTextChanged();
            }

         }
      }
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         enableBlendAlphaStuffs();
         setRenderColorARGB(this._colorBackground);
         drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
         setRenderColorARGB(this._colorBorder);
         drawOutline(0, 0, this.getWidth(), this.getHeight());
         disableBlendAlphaStuffs();
         this._field.setEnableBackgroundDrawing(false);
         this._field.x = 5;
         this._field.y = (this.getHeight() - 8) / 2;
         this._field.width -= 8;
         this._field.setTextColor(this._colorText);
         this._field.setEnabled(this.isEnabled());
         this._field.setFocused(this.isFocused());
         this._field.drawTextBox();
         this._field.x = this._field.y = 1;
         this._field.width += 8;
         this._field.setEnableBackgroundDrawing(true);
      }
   }
}
