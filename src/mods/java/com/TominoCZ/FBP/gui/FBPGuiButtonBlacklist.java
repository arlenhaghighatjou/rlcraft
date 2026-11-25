package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.FBP;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class FBPGuiButtonBlacklist extends FBPGuiButton {
   public boolean particle;
   public boolean isInExceptions;

   public FBPGuiButtonBlacklist(int buttonId, int x, int y, String buttonText, boolean particle, boolean isInExceptions) {
      super(buttonId, x, y, buttonText, false, false);
      this.particle = particle;
      this.isInExceptions = isInExceptions;
      this.width = 60;
      this.height = 60;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible) {
         int centerX1 = this.x + this.height / 2;
         int centerY1 = this.y + this.height / 2 - 1;
         double distance = Math.sqrt((double)((mouseX - centerX1) * (mouseX - centerX1) + (mouseY - centerY1) * (mouseY - centerY1)));
         double radius = (double)((this.height - 1) / 2);
         this.hovered = distance <= radius;
         mc.getTextureManager().bindTexture(FBP.FBP_WIDGETS);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         this.drawTexturedModalRect(this.x, this.y, this.enabled ? (this.isInExceptions ? 60 : 0) : 120, 196, 60, 60);
         if (!this.enabled) {
            GlStateManager.color(0.25F, 0.25F, 0.25F);
         }

         this.drawTexturedModalRect((float)this.x + (float)this.width / 2.0F - 22.5F + (float)(this.particle ? 0 : 2), (float)this.y + (float)this.height / 2.0F - 22.5F, 211, this.particle ? 45 : 0, 45, 45);
         this.mouseDragged(mc, mouseX, mouseY);
      }

   }
}
