package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;

public class TextureStretchDisplayStyle extends TextureDisplayStyle {
   public int h;
   public int w;

   public TextureStretchDisplayStyle(ResourceLocation location, int u, int v, int width, int height) {
      super(location, u, v);
      this.h = height;
      this.w = width;
   }

   public void renderStyle(GuiRenderHelper helper, int width, int height) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      this.colorize();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
      helper.drawTexturedModalRect(this.location, 0, 0, this.u, this.v, width, height, this.w, this.h);
      GlStateManager.popMatrix();
   }
}
