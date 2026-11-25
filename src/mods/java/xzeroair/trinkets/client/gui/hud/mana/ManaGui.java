package xzeroair.trinkets.client.gui.hud.mana;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;

public class ManaGui extends Gui {
   public static ResourceLocation background = null;
   private static ResourceLocation manaBar = new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar.png");
   private final Minecraft mc;
   private final EaglercraftRandom rand = new EaglercraftRandom();
   private float oldMouseX;
   private float oldMouseY;

   public ManaGui(Minecraft mc) {
      this.mc = mc;
   }

   public void renderManaGui(RenderGameOverlayEvent event, int x, int y, int tick, float mana, float maxMana, float cost) {
      FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
      int barWidth = TrinketsConfig.CLIENT.MPBar.width;
      int barHeight = TrinketsConfig.CLIENT.MPBar.height;
      int texUVHeight = barHeight * 2;
      if (mana > maxMana) {
         mana = maxMana;
      }

      if (cost > mana) {
         cost = mana;
      }

      float test = (mana - cost) * 100.0F / maxMana * 0.01F;
      int currentManaWithCost = (int)((float)barWidth * test);
      this.mc.getTextureManager().bindTexture(manaBar);
      GlStateManager.translate((float)x, (float)y, 0.0F);

      try {
         int hs = this.mc.displayHeight;
         int ws = this.mc.displayWidth;
         int hsf = event.getResolution().getScaledHeight();
         int wsf = event.getResolution().getScaledWidth();
         int sF = event.getResolution().getScaleFactor();
         float scaleH = (float)hsf * 1.0F / (float)hs;
         float scaleW = (float)wsf * 1.0F / (float)ws;
         GlStateManager.scale(scaleW * (float)sF, scaleH * (float)sF, 0.0F);
      } catch (Exception var24) {
         ;
      }

      if (!TrinketsConfig.CLIENT.MPBar.mana_horizontal) {
         GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
      }

      GlStateManager.translate((float)(-x), (float)(-y), 0.0F);
      GlStateManager.enableBlend();
      drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, barWidth, barHeight, (float)barWidth, (float)texUVHeight);
      if (mana != 0.0F) {
         drawModalRectWithCustomSizedTexture(x, y, 0.0F, (float)barHeight, currentManaWithCost, barHeight, (float)barWidth, (float)texUVHeight);
      }

      if (!TrinketsConfig.CLIENT.MPBar.hide_text) {
         GlStateManager.pushMatrix();
         mana = (float)(Math.round(mana * 100.0F) / 100);
         maxMana = (float)(Math.round(MathHelper.clamp(maxMana, 0.0F, maxMana) * 100.0F) / 100);
         String txt = (int)mana + "/" + (int)maxMana;
         x = x + (barWidth / 2 - txt.length() * 6 / 2);
         ++x;
         y = y + (barHeight / 2 - 4);
         x = fontRenderer.drawStringWithShadow(txt, (float)x, (float)y, -1);
         GlStateManager.popMatrix();
         x = x - (barWidth / 2 - txt.length() * 6 / 2);
         --x;
         int var10000 = y - (barHeight / 2 - 4);
      }

      GlStateManager.disableBlend();
   }

   private int percentValue(int value, float percent) {
      return (int)((float)value * percent);
   }

   private void drawImage(int x, int y, int width, int height, float tileWidth, float tileHeight, float u, float v, int uWidth, int vHeight) {
      this.drawImage(x, y, width, height, tileWidth, tileHeight, u, v, uWidth, vHeight, true, true, false, false);
   }

   private void drawImage(int x, int y, int width, int height, float tileWidth, float tileHeight, float u, float v, int uWidth, int vHeight, boolean flipH, boolean flipV, boolean flipU, boolean flipUV) {
      float f = 1.0F / tileWidth;
      float f1 = 1.0F / tileHeight;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      int xF = flipH ? x - width : x;
      int xF2 = flipH ? x : x + width;
      int yF = flipV ? y : y - height;
      int yF2 = flipV ? y + height : y;
      double uF = flipU ? (double)(u + (float)uWidth) : (double)u;
      double uF2 = flipU ? (double)u : (double)(u + (float)uWidth);
      double vF = flipUV ? (double)(v + (float)vHeight) : (double)v;
      double vF2 = flipUV ? (double)v : (double)(v + (float)vHeight);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)xF, (double)yF2, 0.0D).tex(uF * (double)f, vF2 * (double)f1).endVertex();
      bufferbuilder.pos((double)xF2, (double)yF2, 0.0D).tex(uF2 * (double)f, vF2 * (double)f1).endVertex();
      bufferbuilder.pos((double)xF2, (double)yF, 0.0D).tex(uF2 * (double)f, vF * (double)f1).endVertex();
      bufferbuilder.pos((double)xF, (double)yF, 0.0D).tex(uF * (double)f, vF * (double)f1).endVertex();
      tessellator.draw();
   }
}
