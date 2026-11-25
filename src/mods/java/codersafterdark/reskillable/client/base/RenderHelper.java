package codersafterdark.reskillable.client.base;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderHelper {
   public RenderHelper() {
   }

   public static void renderTooltip(int x, int y, List<String> tooltipData) {
      int color = 1347420415;
      int color2 = -267386864;
      renderTooltip(x, y, tooltipData, color, color2);
   }

   public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2) {
      boolean lighting = GL11.glGetBoolean(2896);
      if (lighting) {
         net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
      }

      if (!tooltipData.isEmpty()) {
         int var5 = 0;
         FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

         for(int var6 = 0; var6 < tooltipData.size(); ++var6) {
            int var7 = fontRenderer.getStringWidth((String)tooltipData.get(var6));
            if (var7 > var5) {
               var5 = var7;
            }
         }

         int var20 = x + 12;
         int var7 = y - 12;
         int var9 = 8;
         if (tooltipData.size() > 1) {
            var9 += 2 + (tooltipData.size() - 1) * 10;
         }

         ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
         int right = var20 + var5 + 5;
         int swidth = res.getScaledWidth();
         if (right > swidth) {
            var20 -= right - swidth;
         }

         int bottom = var7 + var9 + 5;
         int sheight = res.getScaledHeight();
         if (bottom > sheight) {
            var7 -= bottom - sheight;
         }

         float z = 300.0F;
         drawGradientRect(var20 - 3, var7 - 4, z, var20 + var5 + 3, var7 - 3, color2, color2);
         drawGradientRect(var20 - 3, var7 + var9 + 3, z, var20 + var5 + 3, var7 + var9 + 4, color2, color2);
         drawGradientRect(var20 - 3, var7 - 3, z, var20 + var5 + 3, var7 + var9 + 3, color2, color2);
         drawGradientRect(var20 - 4, var7 - 3, z, var20 - 3, var7 + var9 + 3, color2, color2);
         drawGradientRect(var20 + var5 + 3, var7 - 3, z, var20 + var5 + 4, var7 + var9 + 3, color2, color2);
         int var12 = (color & 16777215) >> 1 | color & -16777216;
         drawGradientRect(var20 - 3, var7 - 3 + 1, z, var20 - 3 + 1, var7 + var9 + 3 - 1, color, var12);
         drawGradientRect(var20 + var5 + 2, var7 - 3 + 1, z, var20 + var5 + 3, var7 + var9 + 3 - 1, color, var12);
         drawGradientRect(var20 - 3, var7 - 3, z, var20 + var5 + 3, var7 - 3 + 1, color, color);
         drawGradientRect(var20 - 3, var7 + var9 + 2, z, var20 + var5 + 3, var7 + var9 + 3, var12, var12);
         GlStateManager.disableDepth();

         for(int var13 = 0; var13 < tooltipData.size(); ++var13) {
            String var14 = (String)tooltipData.get(var13);
            fontRenderer.drawStringWithShadow(var14, (float)var20, (float)var7, -1);
            if (var13 == 0) {
               var7 += 2;
            }

            var7 += 10;
         }

         GlStateManager.enableDepth();
      }

      if (!lighting) {
         net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void drawGradientRect(int par1, int par2, float z, int par3, int par4, int par5, int par6) {
      float var7 = (float)(par5 >> 24 & 255) / 255.0F;
      float var8 = (float)(par5 >> 16 & 255) / 255.0F;
      float var9 = (float)(par5 >> 8 & 255) / 255.0F;
      float var10 = (float)(par5 & 255) / 255.0F;
      float var11 = (float)(par6 >> 24 & 255) / 255.0F;
      float var12 = (float)(par6 >> 16 & 255) / 255.0F;
      float var13 = (float)(par6 >> 8 & 255) / 255.0F;
      float var14 = (float)(par6 & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.shadeModel(7425);
      Tessellator var15 = Tessellator.getInstance();
      BufferBuilder buff = var15.getBuffer();
      buff.begin(7, DefaultVertexFormats.POSITION_COLOR);
      buff.pos((double)par3, (double)par2, (double)z).color(var8, var9, var10, var7).endVertex();
      buff.pos((double)par1, (double)par2, (double)z).color(var8, var9, var10, var7).endVertex();
      buff.pos((double)par1, (double)par4, (double)z).color(var12, var13, var14, var11).endVertex();
      buff.pos((double)par3, (double)par4, (double)z).color(var12, var13, var14, var11).endVertex();
      var15.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   public static void drawTexturedModalRect(int par1, int par2, float z, int par3, int par4, int par5, int par6) {
      drawTexturedModalRect(par1, par2, z, par3, par4, par5, par6, 0.00390625F, 0.00390625F);
   }

   public static void drawTexturedModalRect(int par1, int par2, float z, int par3, int par4, int par5, int par6, float f, float f1) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buff = tessellator.getBuffer();
      buff.begin(7, DefaultVertexFormats.POSITION_TEX);
      buff.pos((double)par1, (double)(par2 + par6), (double)z).tex((double)((float)par3 * f), (double)((float)(par4 + par6) * f1)).endVertex();
      buff.pos((double)(par1 + par5), (double)(par2 + par6), (double)z).tex((double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1)).endVertex();
      buff.pos((double)(par1 + par5), (double)par2, (double)z).tex((double)((float)(par3 + par5) * f), (double)((float)par4 * f1)).endVertex();
      buff.pos((double)par1, (double)par2, (double)z).tex((double)((float)par3 * f), (double)((float)par4 * f1)).endVertex();
      tessellator.draw();
   }
}
