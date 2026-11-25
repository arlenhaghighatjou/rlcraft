package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class FBPGuiButton extends GuiButton {
   boolean toggleButton = false;
   boolean toggle;
   public int offsetX;
   public int size;

   public FBPGuiButton(int buttonId, int x, int y, String buttonText, boolean toggle, boolean toggleButton) {
      super(buttonId, x, y, buttonText);
      byte var8 = -1;
      switch(buttonText.hashCode()) {
      case 43:
         if (buttonText.equals("+")) {
            var8 = 0;
         }
         break;
      case 45:
         if (buttonText.equals("-")) {
            var8 = 1;
         }
         break;
      case 1920:
         if (buttonText.equals("<<")) {
            var8 = 3;
         }
         break;
      case 1984:
         if (buttonText.equals(">>")) {
            var8 = 2;
         }
         break;
      case 45678:
         if (buttonText.equals("...")) {
            var8 = 4;
         }
      }

      switch(var8) {
      case 0:
         this.displayString = "\u00a7a\u00a7L" + this.displayString;
         this.offsetX = (this.height - 5) / 2;
         break;
      case 1:
         this.displayString = "\u00a7c\u00a7L" + this.displayString;
         this.offsetX = (this.height - 5) / 2;
         break;
      case 2:
         this.displayString = "\u00a76" + this.displayString;
         this.offsetX = (this.height - 7) / 2;
         break;
      case 3:
         this.displayString = "\u00a76" + this.displayString;
         this.offsetX = (this.height - 10) / 2;
         break;
      case 4:
         this.displayString = "\u00a7a\u00a7L...";
         this.offsetX = 6;
         break;
      default:
         this.offsetX = -1;
      }

      if (this.toggleButton = toggleButton) {
         this.toggle = toggle;
      }

   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         int centerX1 = this.x + this.height / 2;
         int centerY1 = this.y + this.height / 2 - 1;
         int centerX2 = this.x + this.width - this.height / 2;
         int centerY2 = this.y + this.height / 2;
         double distance1 = Math.sqrt((double)((mouseX - centerX1) * (mouseX - centerX1) + (mouseY - centerY1) * (mouseY - centerY1)));
         double radius = (double)((this.height - 1) / 2);
         double distance2 = Math.sqrt((double)((mouseX - centerX2) * (mouseX - centerX2) + (mouseY - centerY2) * (mouseY - centerY2)));
         boolean isOverRectangle = mouseX >= this.x + this.height / 2 - 2 && mouseY >= this.y + 1 && mouseX < this.x + this.width - this.height / 2 + 3 && mouseY < this.y + this.height;
         this.hovered = distance1 <= radius || distance2 <= radius || isOverRectangle;
         FontRenderer fontrenderer = mc.fontRenderer;
         mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int i = this.getHoverState(this.hovered);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, 0, i * 20, this.width / 2, this.height);
         this.drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, i * 20, this.width / 2, this.height);
         this.mouseDragged(mc, mouseX, mouseY);
         int j = 14737632;
         if (this.packedFGColour != 0) {
            j = this.packedFGColour;
         } else if (!this.enabled) {
            j = 10526880;
         } else if (this.hovered) {
            j = 16777120;
         }

         if (!this.toggleButton) {
            if (this.offsetX == -1) {
               this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
            } else {
               this.drawString(fontrenderer, this.displayString, this.x + this.offsetX, this.y + (this.height - 8) / 2, j);
            }
         } else {
            this.drawString(fontrenderer, this.displayString, this.x + 8, this.y + (this.height - 8) / 2, j);
            this.drawString(fontrenderer, this.toggle ? "\u00a7a ON" : "\u00a7cOFF", this.x + this.width - 25, this.y + (this.height - 8) / 2, j);
         }
      }

   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (this.enabled && this.visible && this.hovered) {
         this.playPressSound(mc.getSoundHandler());
         this.toggle = !this.toggle;
         return true;
      } else {
         return false;
      }
   }
}
