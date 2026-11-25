package hunternif.mc.atlas.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

class ProgressBarOverlay {
   private final int barWidth;
   private final int barHeight;
   private final FontRenderer font;

   @SideOnly(Side.CLIENT)
   public ProgressBarOverlay(int barWidth, int barHeight) {
      this.barWidth = barWidth;
      this.barHeight = barHeight;
      this.font = Minecraft.getMinecraft().fontRenderer;
   }

   @SideOnly(Side.CLIENT)
   public void draw(int x, int y) {
      ExportUpdateListener l = ExportUpdateListener.INSTANCE;
      int headerWidth = this.font.getStringWidth(l.header);
      this.font.drawStringWithShadow(l.header, (float)(x + (this.barWidth - headerWidth) / 2), (float)(y - 14), 16777215);
      int statusWidth = this.font.getStringWidth(l.status);
      this.font.drawStringWithShadow(l.status, (float)(x + (this.barWidth - statusWidth) / 2), (float)y, 16777215);
      y = y + 14;
      double p = (double)((float)l.currentProgress / l.maxProgress);
      if (l.maxProgress < 0.0F) {
         p = 0.0D;
      }

      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder vb = tessellator.getBuffer();
      vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
      vb.pos((double)x, (double)y, 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
      vb.pos((double)x, (double)(y + this.barHeight), 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
      vb.pos((double)(x + this.barWidth), (double)(y + this.barHeight), 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
      vb.pos((double)(x + this.barWidth), (double)y, 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
      vb.pos((double)x, (double)y, 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
      vb.pos((double)x, (double)(y + this.barHeight), 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
      vb.pos((double)x + (double)this.barWidth * p, (double)(y + this.barHeight), 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
      vb.pos((double)x + (double)this.barWidth * p, (double)y, 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
   }
}
