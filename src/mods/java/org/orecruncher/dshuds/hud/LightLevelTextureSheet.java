package org.orecruncher.dshuds.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.gfx.GeneratedTexture;
import org.orecruncher.lib.gfx.OpenGlUtil;

@SideOnly(Side.CLIENT)
public final class LightLevelTextureSheet extends GeneratedTexture {
   private static final int TEXEL_PER_SIDE = 64;
   private static final float SCALE = 4.0F;
   private static final int SPRITE_COUNT = 16;
   private static final int WIDTH = 1024;
   private static final int HEIGHT = 64;
   private static final float U_SIZE = 0.0625F;
   private static final float V_SIZE = 1.0F;
   private static final Vec2f[] U_COORDS = new Vec2f[16];
   private static final Vec2f V_COORDS = new Vec2f(0.0F, 1.0F);

   public LightLevelTextureSheet() {
      super("DSLightLevelTextures", 1024, 64);
   }

   public Vec2f getMinMaxU(int idx) {
      return U_COORDS[idx];
   }

   public Vec2f getMinMaxV(int idx) {
      return V_COORDS;
   }

   public void render() {
      FontRenderer font = Minecraft.getMinecraft().fontRenderer;
      int color = Color.WHITE.rgbWithAlpha(0.99F);
      int shadow = Color.MC_DARKGRAY.rgbWithAlpha(0.99F);
      OpenGlUtil.setStandardBlend();

      for(int i = 0; i < 15; ++i) {
         String str = Integer.toString(i);
         double mid = 32.0D;
         double posX = (double)(i * 64) + 32.0D;
         double posY = 32.0D;
         GlStateManager.pushMatrix();
         GlStateManager.translate(posX, 32.0D, 0.0D);
         int margin = -(font.getStringWidth(str) + 1) / 2;
         int height = -font.FONT_HEIGHT / 2;
         GlStateManager.scale(4.0F, 4.0F, 0.0F);
         GlStateManager.translate(0.3F, 0.3F, 0.0F);
         font.drawString(str, margin, height, shadow);
         GlStateManager.translate(-0.3F, -0.3F, -0.001F);
         font.drawString(str, margin, height, color);
         GlStateManager.popMatrix();
      }

   }

   static {
      for(int i = 0; i < 16; ++i) {
         float u = (float)i / 16.0F;
         U_COORDS[i] = new Vec2f(u, u + 0.0625F);
      }

   }
}
