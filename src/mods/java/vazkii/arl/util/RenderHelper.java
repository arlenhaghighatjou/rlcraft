package vazkii.arl.util;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class RenderHelper {
   public RenderHelper() {
   }

   public static void renderTooltip(int x, int y, List<String> tooltipData) {
      int color = 1347420415;
      int color2 = -267386864;
      renderTooltip(x, y, tooltipData, color, color2);
   }

   public static void renderTooltipOrange(int x, int y, List<String> tooltipData) {
      int color = 1352689152;
      int color2 = -266464768;
      renderTooltip(x, y, tooltipData, color, color2);
   }

   public static void renderTooltipGreen(int x, int y, List<String> tooltipData) {
      int color = 1342218240;
      int color2 = -268427776;
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
            int diff = right - swidth;
            var20 -= diff;
         }

         int bottom = var7 + var9 + 5;
         int sheight = res.getScaledHeight();
         if (bottom > sheight) {
            int diff = bottom - sheight;
            var7 -= diff;
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
      WorldRenderer buff = var15.getBuffer();
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
      WorldRenderer buff = tessellator.getBuffer();
      buff.begin(7, DefaultVertexFormats.POSITION_TEX);
      buff.pos((double)(par1 + 0), (double)(par2 + par6), (double)z).tex((double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1)).endVertex();
      buff.pos((double)(par1 + par5), (double)(par2 + par6), (double)z).tex((double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1)).endVertex();
      buff.pos((double)(par1 + par5), (double)(par2 + 0), (double)z).tex((double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1)).endVertex();
      buff.pos((double)(par1 + 0), (double)(par2 + 0), (double)z).tex((double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1)).endVertex();
      tessellator.draw();
   }

   public static void renderStar(int color, float scale, long seed) {
      renderStar(color, scale, scale, scale, seed);
   }

   public static void renderStar(int color, float xScale, float yScale, float zScale, long seed) {
      Tessellator tessellator = Tessellator.getInstance();
      float ticks = (float)(ClientTicker.ticksInGame % 200) + ClientTicker.partialTicks;
      if (ticks >= 100.0F) {
         ticks = 200.0F - ticks - 1.0F;
      }

      float f1 = ticks / 200.0F;
      float f2 = 0.0F;
      if (f1 > 0.7F) {
         f2 = (f1 - 0.7F) / 0.2F;
      }

      EaglercraftRandom random = new EaglercraftRandom(seed);
      GlStateManager.pushMatrix();
      GlStateManager.disableTexture2D();
      GlStateManager.shadeModel(7425);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 1);
      GlStateManager.disableAlpha();
      GlStateManager.enableCull();
      GlStateManager.depthMask(false);
      GlStateManager.scale(xScale, yScale, zScale);

      for(int i = 0; (float)i < (f1 + f1 * f1) / 2.0F * 90.0F + 30.0F; ++i) {
         GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
         tessellator.getBuffer().begin(6, DefaultVertexFormats.POSITION_COLOR);
         float f3 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
         float f4 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
         float r = (float)((color & 16711680) >> 16) / 255.0F;
         float g = (float)((color & '\uff00') >> 8) / 255.0F;
         float b = (float)(color & 255) / 255.0F;
         tessellator.getBuffer().pos(0.0D, 0.0D, 0.0D).color(r, g, b, 1.0F - f2).endVertex();
         tessellator.getBuffer().pos(-0.866D * (double)f4, (double)f3, (double)(-0.5F * f4)).color(0, 0, 0, 0).endVertex();
         tessellator.getBuffer().pos(0.866D * (double)f4, (double)f3, (double)(-0.5F * f4)).color(0, 0, 0, 0).endVertex();
         tessellator.getBuffer().pos(0.0D, (double)f3, (double)(1.0F * f4)).color(0, 0, 0, 0).endVertex();
         tessellator.getBuffer().pos(-0.866D * (double)f4, (double)f3, (double)(-0.5F * f4)).color(0, 0, 0, 0).endVertex();
         tessellator.draw();
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableCull();
      GlStateManager.disableBlend();
      GlStateManager.shadeModel(7424);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      GlStateManager.popMatrix();
   }

   public static String getKeyDisplayString(String keyName) {
      String key = null;
      KeyBinding[] keys = Minecraft.getMinecraft().gameSettings.keyBindings;

      for(KeyBinding otherKey : keys) {
         if (otherKey.getKeyDescription().equals(keyName)) {
            key = Keyboard.getKeyName(otherKey.getKeyCode());
            break;
         }
      }

      return key;
   }
}
