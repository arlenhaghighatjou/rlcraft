package com.creativemd.creativecore.common.gui.client.style;

import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import javax.vecmath.Vector4f;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ColoredTextureDisplayStyle extends TextureDisplayStyle {
   public Vector4f color;

   public ColoredTextureDisplayStyle(int color, ResourceLocation location, int u, int v) {
      super(location, u, v);
      this.color = new Vector4f((float)ColorUtils.getRed(color) / 255.0F, (float)ColorUtils.getGreen(color) / 255.0F, (float)ColorUtils.getBlue(color) / 255.0F, (float)ColorUtils.getAlpha(color) / 255.0F);
   }

   protected void colorize() {
      GlStateManager.color(this.color.x, this.color.y, this.color.z, this.color.w);
   }
}
