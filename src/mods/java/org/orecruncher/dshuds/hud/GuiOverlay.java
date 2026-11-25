package org.orecruncher.dshuds.hud;

import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiOverlay extends Gui {
   public GuiOverlay() {
   }

   public void doRender(Pre event) {
   }

   public void doRender(Post event) {
   }

   public void doTick(int tickRef) {
   }

   protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      float zLevel = 0.0F;
      float f = (float)(startColor >> 24 & 255) / 255.0F;
      float f1 = (float)(startColor >> 16 & 255) / 255.0F;
      float f2 = (float)(startColor >> 8 & 255) / 255.0F;
      float f3 = (float)(startColor & 255) / 255.0F;
      float f4 = (float)(endColor >> 24 & 255) / 255.0F;
      float f5 = (float)(endColor >> 16 & 255) / 255.0F;
      float f6 = (float)(endColor >> 8 & 255) / 255.0F;
      float f7 = (float)(endColor & 255) / 255.0F;
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.shadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer t = tessellator.getBuffer();
      t.begin(7, DefaultVertexFormats.POSITION_COLOR);
      t.pos((double)(left + right), (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      t.pos((double)left, (double)top, 0.0D).color(f1, f2, f3, f).endVertex();
      t.pos((double)left, (double)(top + bottom), 0.0D).color(f5, f6, f7, f4).endVertex();
      t.pos((double)(left + right), (double)(top + bottom), 0.0D).color(f5, f6, f7, f4).endVertex();
      tessellator.draw();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
   }

   protected void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
      this.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
      this.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
      this.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
      this.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
      this.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
      this.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
      this.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
      this.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
   }
}
