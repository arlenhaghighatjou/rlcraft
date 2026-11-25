package net.ilexiconn.llibrary.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.ClientProxy;
import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SliderElement<T extends IElementGUI, P extends IFloatRangeProperty & IStringProperty> extends Element<T> {
   private final P value;
   private float step;
   private boolean hasSlider;
   private float sliderWidth;
   private boolean editable;
   private boolean dragging;
   private PropertyInputElement<T> inputElement;

   public SliderElement(T gui, float posX, float posY, P value, float step) {
      this(gui, posX, posY, 0.0F, value, step);
   }

   public SliderElement(T gui, float posX, float posY, float sliderWidth, P value, float step) {
      super(gui, posX, posY, (int)(38.0F + sliderWidth), 12);
      this.editable = true;
      this.value = value;
      this.step = step;
      this.hasSlider = sliderWidth > 0.0F;
      this.sliderWidth = sliderWidth;
   }

   public void init() {
      this.inputElement = (PropertyInputElement)(new PropertyInputElement(this.gui, -1.0F, 0.0F, 28, (IStringProperty)this.value)).withParent(this);
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      float posX = this.getPosX();
      float posY = this.getPosY();
      int width = this.getWidth();
      int height = this.getHeight();
      this.drawRectangle((double)posX, (double)posY, (double)width, (double)height, this.editable ? LLibrary.CONFIG.getSecondaryColor() : LLibrary.CONFIG.getSecondarySubcolor());
      boolean selected = this.isSelected(mouseX, mouseY);
      boolean upperSelected = this.editable && selected && mouseX >= posX + (float)width - this.sliderWidth - 11.0F && mouseY < posY + 6.0F && mouseX < posX + (float)width - this.sliderWidth;
      boolean lowerSelected = this.editable && selected && mouseX >= posX + (float)width - this.sliderWidth - 11.0F && mouseY > posY + 6.0F && mouseX < posX + (float)width - this.sliderWidth;
      this.drawRectangle((double)(posX + (float)width - 11.0F - this.sliderWidth), (double)posY, 11.0D, 6.0D, this.editable ? (upperSelected ? LLibrary.CONFIG.getDarkAccentColor() : LLibrary.CONFIG.getAccentColor()) : LLibrary.CONFIG.getTertiaryColor());
      this.drawRectangle((double)(posX + (float)width - 11.0F - this.sliderWidth), (double)(posY + 6.0F), 11.0D, 6.0D, this.editable ? (lowerSelected ? LLibrary.CONFIG.getDarkAccentColor() : LLibrary.CONFIG.getAccentColor()) : LLibrary.CONFIG.getTertiaryColor());
      int textColor = LLibrary.CONFIG.getTextColor();
      this.drawRectangle((double)(posX + (float)width - 8.0F - this.sliderWidth), (double)(posY + 4.0F), 5.0D, 1.0D, textColor);
      this.drawRectangle((double)(posX + (float)width - 7.0F - this.sliderWidth), (double)(posY + 3.0F), 3.0D, 1.0D, textColor);
      this.drawRectangle((double)(posX + (float)width - 6.0F - this.sliderWidth), (double)(posY + 2.0F), 1.0D, 1.0D, textColor);
      this.drawRectangle((double)(posX + (float)width - 8.0F - this.sliderWidth), (double)(posY + 7.0F), 5.0D, 1.0D, textColor);
      this.drawRectangle((double)(posX + (float)width - 7.0F - this.sliderWidth), (double)(posY + 8.0F), 3.0D, 1.0D, textColor);
      this.drawRectangle((double)(posX + (float)width - 6.0F - this.sliderWidth), (double)(posY + 9.0F), 1.0D, 1.0D, textColor);
      GL11.glEnable(3089);
      float scaleFactor = (float)(new ScaledResolution(ClientProxy.MINECRAFT)).getScaleFactor();
      GL11.glScissor((int)(posX * scaleFactor), (int)(((float)this.gui.getHeight() - (posY + (float)height)) * scaleFactor), (int)((float)(width - 11) * scaleFactor), (int)((float)height * scaleFactor));
      GL11.glDisable(3089);
      if (this.hasSlider) {
         float offsetX = (this.sliderWidth - 4.0F) * (this.getValue() - this.value.getMinFloatValue()) / this.value.getFloatValueRange();
         boolean indicatorSelected = this.editable && selected && mouseX >= this.getPosX() + 38.0F + offsetX && mouseX <= this.getPosX() + 38.0F + offsetX + 4.0F;
         this.drawRectangle((double)(this.getPosX() + 38.0F + offsetX), (double)this.getPosY(), 4.0D, (double)this.getHeight(), this.editable ? (indicatorSelected ? LLibrary.CONFIG.getDarkAccentColor() : LLibrary.CONFIG.getAccentColor()) : LLibrary.CONFIG.getTertiaryColor());
      }

   }

   private boolean trySetValue(float value) {
      if (this.value.trySetFloat(value)) {
         this.inputElement.readValue();
         return true;
      } else {
         return false;
      }
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
      this.inputElement.setEditable(editable);
   }

   /** @deprecated */
   @Deprecated
   public SliderElement<T, P> withValue(float value) {
      this.trySetValue(value);
      return this;
   }

   public boolean mouseScrolled(float mouseX, float mouseY, int amount) {
      if (this.editable && (this.isSelected(mouseX, mouseY) || this.inputElement.isSelected(mouseX, mouseY))) {
         float delta = (float)amount / 120.0F * this.step;
         if (GuiScreen.isShiftKeyDown()) {
            delta *= 10.0F;
         }

         return this.trySetValue(MathHelper.clamp(this.value.getFloat() + delta, this.value.getMinFloatValue(), this.value.getMaxFloatValue()));
      } else {
         return false;
      }
   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      if (!this.editable) {
         return super.mouseClicked(mouseX, mouseY, button);
      } else {
         float offsetX = (this.sliderWidth - 4.0F) * (this.getValue() - this.value.getMinFloatValue()) / this.value.getFloatValueRange();
         boolean selected = this.isSelected(mouseX, mouseY);
         boolean indicatorSelected = selected && mouseX >= this.getPosX() + 38.0F + offsetX && mouseX <= this.getPosX() + 38.0F + offsetX + 4.0F;
         boolean upperSelected = selected && mouseX >= this.getPosX() + (float)this.getWidth() - this.sliderWidth - 11.0F && mouseY < this.getPosY() + 6.0F && mouseX < this.getPosX() + (float)this.getWidth() - this.sliderWidth;
         boolean lowerSelected = selected && mouseX >= this.getPosX() + (float)this.getWidth() - this.sliderWidth - 11.0F && mouseY > this.getPosY() + 6.0F && mouseX < this.getPosX() + (float)this.getWidth() - this.sliderWidth;
         if (indicatorSelected) {
            this.dragging = true;
            this.gui.playClickSound();
            return true;
         } else {
            if (upperSelected || lowerSelected) {
               float delta = 0.0F;
               if (upperSelected) {
                  delta += this.step;
               }

               if (lowerSelected) {
                  delta -= this.step;
               }

               if (GuiScreen.isShiftKeyDown()) {
                  delta *= 10.0F;
               }

               if (this.trySetValue(this.value.getFloat() + delta)) {
                  this.gui.playClickSound();
                  return true;
               }
            }

            return false;
         }
      }
   }

   private float getValue() {
      return this.value.getFloat();
   }

   public boolean mouseDragged(float mouseX, float mouseY, int button, long timeSinceClick) {
      if (this.dragging) {
         float newValue = (mouseX - (this.getPosX() + 38.0F)) / ((float)this.getWidth() - 38.0F - 4.0F) * this.value.getFloatValueRange() + this.value.getMinFloatValue();
         if (newValue > this.value.getMaxFloatValue()) {
            newValue = this.value.getMaxFloatValue();
         }

         if (newValue < this.value.getMinFloatValue()) {
            newValue = this.value.getMinFloatValue();
         }

         this.trySetValue(newValue);
      }

      return this.dragging;
   }

   public boolean mouseReleased(float mouseX, float mouseY, int button) {
      if (this.dragging) {
         this.gui.playClickSound();
      }

      this.dragging = false;
      return false;
   }

   public InputElementBase getValueInput() {
      return this.inputElement;
   }
}
