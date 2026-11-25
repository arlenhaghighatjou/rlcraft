package com.bloodnbonesgaming.loadingscreens.client;

import java.io.IOException;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MinecraftError;
import net.minecraftforge.fml.client.FMLClientHandler;

public class LoadingScreenRendererCustom extends LoadingScreenRenderer {
   public LoadingScreenRendererCustom(Minecraft mcIn) {
      super(mcIn);
   }

   public void setLoadingProgress(int progress) {
      if (!this.mc.running) {
         if (!this.loadingSuccess) {
            throw new MinecraftError();
         }
      } else {
         long i = Minecraft.getSystemTime();
         if (i - this.systemTime >= 100L) {
            this.systemTime = i;
            ScaledResolution scaledresolution = new ScaledResolution(this.mc);
            int j = scaledresolution.getScaleFactor();
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferClear();
            } else {
               GlStateManager.clear(256);
            }

            this.framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 100.0D, 300.0D);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, 0.0F, -200.0F);
            if (!OpenGlHelper.isFramebufferEnabled()) {
               GlStateManager.clear(16640);
            }

            try {
               if (!FMLClientHandler.instance().handleLoadingScreen(scaledresolution)) {
                  LoadingScreenHandler.getInstance().renderScreen(this.mc, k, l);
                  this.mc.fontRenderer.drawStringWithShadow(this.currentlyDisplayedText, (float)((k - this.mc.fontRenderer.getStringWidth(this.currentlyDisplayedText)) / 2), (float)(l / 2 - 4 - 16), 16777215);
                  this.mc.fontRenderer.drawStringWithShadow(this.message, (float)((k - this.mc.fontRenderer.getStringWidth(this.message)) / 2), (float)(l / 2 - 4 + 8), 16777215);
               }
            } catch (IOException var10) {
               throw new RuntimeException(var10);
            }

            this.framebuffer.unbindFramebuffer();
            if (OpenGlHelper.isFramebufferEnabled()) {
               this.framebuffer.framebufferRender(k * j, l * j);
            }

            this.mc.updateDisplay();

            try {
               Thread.yield();
            } catch (Exception var9) {
               ;
            }
         }
      }

   }
}
