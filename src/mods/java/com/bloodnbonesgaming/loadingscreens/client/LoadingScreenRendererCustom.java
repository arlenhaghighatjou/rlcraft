package com.bloodnbonesgaming.loadingscreens.client;

import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class LoadingScreenRendererCustom extends LoadingScreenRenderer {
   public LoadingScreenRendererCustom(Minecraft mcIn) {
      super(mcIn);
   }

   public void setLoadingProgress(int progress) {
      if (this.mc.running) {
         long i = Minecraft.getSystemTime();
         if (i - this.systemTime >= 100L) {
            this.systemTime = i;
            ScaledResolution scaledresolution = this.mc.scaledResolution;
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            GlStateManager.clear(16640);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            LoadingScreenHandler.getInstance().renderScreen(this.mc, k, l);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (float)((k - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2), (float)(l / 2 - 4 - 16), 16777215);
            if (this.message != null) {
               this.mc.fontRenderer.drawStringWithShadow(this.message, (float)((k - this.mc.fontRenderer.getStringWidth(this.message)) / 2), (float)(l / 2 - 4 + 8), 16777215);
            }

            this.mc.updateDisplay();
         }
      }

   }
}
