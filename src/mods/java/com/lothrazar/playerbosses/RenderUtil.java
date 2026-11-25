package com.lothrazar.playerbosses;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
   public RenderUtil() {
   }

   public static void renderEntityBoundingBox(EntityLiving entity, double x, double y, double z) {
      AxisAlignedBB bb = entity.getEntityBoundingBox();
      if (bb != null) {
         GlStateManager.disableTexture2D();
         GlStateManager.disableLighting();
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.translate((float)x, (float)y, (float)z);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(-entity.renderYawOffset, 0.0F, 1.0F, 0.0F);
         WorldRenderer tes = Tessellator.getInstance().getBuffer();
         double width = (bb.maxX - bb.minX) / 2.0D;
         double height = bb.maxY - bb.minY;
         double depth = (bb.maxZ - bb.minZ) / 2.0D;
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         tes.begin(7, DefaultVertexFormats.POSITION);
         tes.pos(-width, 0.0D, 0.0D).endVertex();
         tes.pos(width, 0.0D, 0.0D).endVertex();
         tes.pos(width, height, 0.0D).endVertex();
         tes.pos(-width, height, 0.0D).endVertex();
         tes.pos(0.0D, 0.0D, -depth).endVertex();
         tes.pos(0.0D, 0.0D, depth).endVertex();
         tes.pos(0.0D, height, depth).endVertex();
         tes.pos(0.0D, height, -depth).endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.popMatrix();
         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         GlStateManager.enableLighting();
         GlStateManager.enableCull();
      }

   }
}
