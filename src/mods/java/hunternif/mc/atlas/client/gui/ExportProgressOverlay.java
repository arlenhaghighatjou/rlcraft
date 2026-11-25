package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.util.ExportImageUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum ExportProgressOverlay {
   INSTANCE;

   private ExportProgressOverlay() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void draw(Post event) {
      int x = event.getResolution().getScaledWidth() - 40;
      int y = event.getResolution().getScaledHeight() - 20;
      int barWidth = 50;
      int barHeight = 2;
      ExportUpdateListener l = ExportUpdateListener.INSTANCE;
      if (event.getType() == ElementType.ALL && ExportImageUtil.isExporting) {
         FontRenderer font = Minecraft.getMinecraft().fontRenderer;
         int s = 2;
         GlStateManager.scale(1.0D / (double)s, 1.0D / (double)s, 1.0D);
         int headerWidth = font.getStringWidth(l.header);
         font.drawStringWithShadow(l.header, (float)(x * s - headerWidth / 2), (float)(y * s - 14), 16777215);
         int statusWidth = font.getStringWidth(l.status);
         font.drawStringWithShadow(l.status, (float)(x * s - statusWidth / 2), (float)(y * s), 16777215);
         GlStateManager.scale((float)s, (float)s, 1.0F);
         y = y + 7;
         x = x - barWidth / 2;
         double p = (double)((float)l.currentProgress / l.maxProgress);
         if (l.maxProgress < 0.0F) {
            p = 0.0D;
         }

         GlStateManager.disableTexture2D();
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder vb = tessellator.getBuffer();
         vb.begin(7, DefaultVertexFormats.POSITION_COLOR);
         vb.pos((double)x, (double)y, 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
         vb.pos((double)x, (double)(y + barHeight), 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
         vb.pos((double)(x + barWidth), (double)(y + barHeight), 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
         vb.pos((double)(x + barWidth), (double)y, 0.0D).color(0.5F, 0.5F, 0.5F, 1.0F).endVertex();
         vb.pos((double)x, (double)y, 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
         vb.pos((double)x, (double)(y + barHeight), 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
         vb.pos((double)x + (double)barWidth * p, (double)(y + barHeight), 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
         vb.pos((double)x + (double)barWidth * p, (double)y, 0.0D).color(0.5F, 1.0F, 0.5F, 1.0F).endVertex();
         tessellator.draw();
         GlStateManager.enableTexture2D();
      }
   }
}
