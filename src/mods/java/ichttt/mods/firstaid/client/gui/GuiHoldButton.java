package ichttt.mods.firstaid.client.gui;

import ichttt.mods.firstaid.FirstAid;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import org.lwjgl.input.Mouse;

public class GuiHoldButton extends GuiButton {
   private int holdTime;
   private float textScaleFactor;
   public final boolean isRightSide;
   private long pressStart = -1L;

   public GuiHoldButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, boolean isRightSide) {
      super(buttonId, x, y, widthIn, heightIn, buttonText);
      this.isRightSide = isRightSide;
   }

   public void setup(int holdTime, float textScaleFactor) {
      this.holdTime = holdTime;
      if (textScaleFactor > 0.95F) {
         textScaleFactor = 1.0F;
      }

      if (textScaleFactor < 0.8F) {
         textScaleFactor = 0.8F;
      }

      this.textScaleFactor = textScaleFactor;
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (!Mouse.isCreated()) {
         FirstAid.LOGGER.warn("Mouse is not created!");
      } else {
         boolean pressed = Mouse.isButtonDown(0);
         if (pressed) {
            pressed = super.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
         }

         if (!pressed) {
            this.pressStart = -1L;
         } else if (this.pressStart == -1L) {
            this.pressStart = Minecraft.getSystemTime();
         }

         this.drawScaledTextButton(mc, mouseX, mouseY, partialTicks);
      }
   }

   private void drawScaledTextButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         FontRenderer fontrenderer = mc.fontRenderer;
         mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int j = 14737632;
         if (this.packedFGColour != 0) {
            j = this.packedFGColour;
         } else if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         if (this.textScaleFactor != 1.0F) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(this.textScaleFactor, this.textScaleFactor, 1.0F);
            this.drawCenteredString(fontrenderer, this.displayString, Math.round((float)(this.x + this.width / 2) / this.textScaleFactor), Math.round((float)(this.y + (this.height - 8) / 2) / this.textScaleFactor), j);
            GlStateManager.popMatrix();
         } else {
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
         }
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      boolean result = this.pressStart != -1L && this.pressStart + (long)this.holdTime <= Minecraft.getSystemTime() && super.mousePressed(mc, mouseX, mouseY);
      if (result) {
         this.pressStart = -1L;
      }

      return result;
   }

   public int getTimeLeft() {
      return this.pressStart == -1L ? -1 : (int)Math.max(0L, (long)this.holdTime - (Minecraft.getSystemTime() - this.pressStart));
   }
}
