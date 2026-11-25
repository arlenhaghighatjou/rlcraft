package net.mcft.copy.backpacks.client.gui.control;

import net.mcft.copy.backpacks.client.gui.GuiElementBase;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButton extends GuiElementBase {
   public static final ResourceLocation BUTTON_TEX = new ResourceLocation("textures/gui/widgets.png");
   public static final int DEFAULT_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public static final int MIN_TEXT_PADDING = 6;
   public static final int DEFAULT_TEXT_PADDING = 20;
   private String _text;
   private int _textColor;
   private Runnable _action;
   private boolean _isSwitch;
   private boolean _switchOn;

   public GuiButton() {
      this(200);
   }

   public GuiButton(int width) {
      this(width, 20);
   }

   public GuiButton(int width, int height) {
      this(width, height, "");
   }

   public GuiButton(String text) {
      this(getStringWidth(text) + 20, text);
   }

   public GuiButton(int width, String text) {
      this(width, 20, text);
   }

   public GuiButton(int width, int height, String text) {
      this(0, 0, width, height, text);
   }

   public GuiButton(int x, int y, int width, int height, String text) {
      this._text = "";
      this._textColor = -1;
      this._action = null;
      this._isSwitch = false;
      this._switchOn = false;
      this.setPosition(x, y);
      this.setSize(width, height);
      this.setText(text);
   }

   public String getText() {
      return this._text;
   }

   public void setText(String value) {
      if (value == null) {
         throw new NullPointerException("Argument can't be null");
      } else {
         this._text = value;
      }
   }

   public final void addText(String value) {
      if (value == null) {
         throw new NullPointerException("Argument can't be null");
      } else {
         this.setText(this.getText() + value);
      }
   }

   public int getTextColor() {
      return this.getTextColor(false);
   }

   public int getTextColor(boolean isHighlighted) {
      return this._textColor >= 0 ? this._textColor : (!this.isEnabled() ? -6250336 : (isHighlighted ? -96 : -2039584));
   }

   public final void unsetTextColor() {
      this.setTextColor(-1);
   }

   public void setTextColor(int value) {
      this._textColor = value;
   }

   public boolean isSwitch() {
      return this._isSwitch;
   }

   public boolean isSwitchOn() {
      return this._switchOn;
   }

   public void setSwitch() {
      this.setSwitch(false);
   }

   public void setSwitch(boolean value) {
      this._isSwitch = true;
      this._switchOn = value;
   }

   public void setAction(Runnable value) {
      this._action = value;
   }

   public boolean canPress() {
      return true;
   }

   public void onPressed(int mouseX, int mouseY) {
      if (this.isEnabled()) {
         this.playPressSound();
         if (this.isSwitch()) {
            this.setSwitch(!this.isSwitchOn());
         }

         if (this._action != null) {
            this._action.run();
         }

      }
   }

   public void playPressSound() {
      getMC().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
   }

   public void draw(int mouseX, int mouseY, float partialTicks) {
      if (this.isVisible()) {
         boolean isHighlighted = this.isHighlighted(mouseX, mouseY);
         if (this.isSwitchOn()) {
            GlStateManager.disableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, (float)this.getHeight(), 0.0F);
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
            this.drawButtonBackground(isHighlighted, partialTicks);
            GlStateManager.popMatrix();
            GlStateManager.enableCull();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 1.0F, 0.0F);
            this.drawButtonForeground(isHighlighted, partialTicks);
            GlStateManager.popMatrix();
         } else {
            this.drawButtonBackground(isHighlighted, partialTicks);
            this.drawButtonForeground(isHighlighted, partialTicks);
         }

      }
   }

   protected void drawButtonBackground(boolean isHighlighted, float partialTicks) {
      int buttonIndex = !this.isEnabled() ? 0 : (!isHighlighted ? 1 : 2);
      int ty = 46 + buttonIndex * 20;
      GuiUtils.drawContinuousTexturedBox(BUTTON_TEX, 0, 0, 0, ty, this.getWidth(), this.getHeight(), 200, 20, 2, 3, 2, 2, 0.0F);
   }

   protected void drawButtonForeground(boolean isHighlighted, float partialTicks) {
      String text = this.getText();
      if (!text.isEmpty()) {
         FontRenderer fontRenderer = getFontRenderer();
         int textWidth = fontRenderer.getStringWidth(text);
         int maxTextWidth = this.getWidth() - 6;
         if (textWidth > maxTextWidth && textWidth > ELLIPSIS_WIDTH) {
            text = fontRenderer.trimStringToWidth(text, maxTextWidth - ELLIPSIS_WIDTH).trim() + "...";
            textWidth = fontRenderer.getStringWidth(text);
         }

         fontRenderer.drawStringWithShadow(text, (float)(this.getWidth() / 2 - textWidth / 2), (float)((this.getHeight() - 8) / 2), this.getTextColor(isHighlighted));
      }
   }
}
