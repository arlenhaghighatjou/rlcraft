package vazkii.arl.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public final class AtlasSpriteHelper {
   public AtlasSpriteHelper() {
   }

   public static TextureAtlasSprite forName(TextureMap ir, ResourceLocation res) {
      return ir.registerSprite(res);
   }

   public static void renderIconThicc(Tessellator tess, float maxU, float maxV, float minU, float minV, int width, int height, float thickness) {
      BufferBuilder wr = tess.getBuffer();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      wr.pos(0.0D, 0.0D, 0.0D).tex((double)maxU, (double)minV).normal(0.0F, 0.0F, 1.0F).endVertex();
      wr.pos(1.0D, 0.0D, 0.0D).tex((double)minU, (double)minV).normal(0.0F, 0.0F, 1.0F).endVertex();
      wr.pos(1.0D, 1.0D, 0.0D).tex((double)minU, (double)maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
      wr.pos(0.0D, 1.0D, 0.0D).tex((double)maxU, (double)maxV).normal(0.0F, 0.0F, 1.0F).endVertex();
      tess.draw();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
      wr.pos(0.0D, 1.0D, (double)(0.0F - thickness)).tex((double)maxU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
      wr.pos(1.0D, 1.0D, (double)(0.0F - thickness)).tex((double)minU, (double)maxV).normal(0.0F, 0.0F, -1.0F).endVertex();
      wr.pos(1.0D, 0.0D, (double)(0.0F - thickness)).tex((double)minU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
      wr.pos(0.0D, 0.0D, (double)(0.0F - thickness)).tex((double)maxU, (double)minV).normal(0.0F, 0.0F, -1.0F).endVertex();
      tess.draw();
      float f5 = 0.5F * (maxU - minU) / (float)width;
      float f6 = 0.5F * (minV - maxV) / (float)height;
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int k = 0; k < width; ++k) {
         float f7 = (float)k / (float)width;
         float f8 = maxU + (minU - maxU) * f7 - f5;
         wr.pos((double)f7, 0.0D, (double)(0.0F - thickness)).tex((double)f8, (double)minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f7, 0.0D, 0.0D).tex((double)f8, (double)minV).normal(-1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f7, 1.0D, 0.0D).tex((double)f8, (double)maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f7, 1.0D, (double)(0.0F - thickness)).tex((double)f8, (double)maxV).normal(-1.0F, 0.0F, 0.0F).endVertex();
      }

      tess.draw();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var15 = 0; var15 < width; ++var15) {
         float f7 = (float)var15 / (float)width;
         float f8 = maxU + (minU - maxU) * f7 - f5;
         float f9 = f7 + 1.0F / (float)width;
         wr.pos((double)f9, 1.0D, (double)(0.0F - thickness)).tex((double)f8, (double)maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f9, 1.0D, 0.0D).tex((double)f8, (double)maxV).normal(1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f9, 0.0D, 0.0D).tex((double)f8, (double)minV).normal(1.0F, 0.0F, 0.0F).endVertex();
         wr.pos((double)f9, 0.0D, (double)(0.0F - thickness)).tex((double)f8, (double)minV).normal(1.0F, 0.0F, 0.0F).endVertex();
      }

      tess.draw();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var16 = 0; var16 < height; ++var16) {
         float f7 = (float)var16 / (float)height;
         float f8 = minV + (maxV - minV) * f7 - f6;
         float f9 = f7 + 1.0F / (float)height;
         wr.pos(0.0D, (double)f9, 0.0D).tex((double)maxU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         wr.pos(1.0D, (double)f9, 0.0D).tex((double)minU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         wr.pos(1.0D, (double)f9, (double)(0.0F - thickness)).tex((double)minU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
         wr.pos(0.0D, (double)f9, (double)(0.0F - thickness)).tex((double)maxU, (double)f8).normal(0.0F, 1.0F, 0.0F).endVertex();
      }

      tess.draw();
      wr.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);

      for(int var17 = 0; var17 < height; ++var17) {
         float f7 = (float)var17 / (float)height;
         float f8 = minV + (maxV - minV) * f7 - f6;
         wr.pos(1.0D, (double)f7, 0.0D).tex((double)minU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         wr.pos(0.0D, (double)f7, 0.0D).tex((double)maxU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         wr.pos(0.0D, (double)f7, (double)(0.0F - thickness)).tex((double)maxU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
         wr.pos(1.0D, (double)f7, (double)(0.0F - thickness)).tex((double)minU, (double)f8).normal(0.0F, -1.0F, 0.0F).endVertex();
      }

      tess.draw();
   }

   public static void renderIconThicc(TextureAtlasSprite icon, float thickness) {
      renderIconThicc(Tessellator.getInstance(), icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), thickness);
   }
}
