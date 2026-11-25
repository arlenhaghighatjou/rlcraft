package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;

public class TextureDisplayStyle extends DisplayStyle {
   public ResourceLocation location;
   public int u;
   public int v;

   public TextureDisplayStyle(ResourceLocation location, int u, int v) {
      this.location = location;
      this.u = u;
      this.v = v;
   }

   public void renderStyle(GuiRenderHelper helper, int width, int height) {
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      this.colorize();
      GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
      helper.drawTexturedModalRect(this.location, 0, 0, this.u, this.v, width, height);
      GlStateManager.popMatrix();
   }

   protected void colorize() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
