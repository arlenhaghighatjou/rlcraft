package org.orecruncher.lib.gfx;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
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
