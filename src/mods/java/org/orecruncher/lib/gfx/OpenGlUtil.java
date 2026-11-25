package org.orecruncher.lib.gfx;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraftforge.fml.client.FMLClientHandler;

public final class OpenGlUtil {
   private OpenGlUtil() {
   }

   public static void setStandardBlend() {
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
   }

   public static void setAuroraBlend() {
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
   }

   public static boolean areFrameBuffersSafe() {
      return !FMLClientHandler.instance().hasOptifine() && OpenGlHelper.isFramebufferEnabled();
   }
}
