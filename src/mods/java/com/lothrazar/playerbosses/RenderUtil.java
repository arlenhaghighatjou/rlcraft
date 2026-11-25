package com.lothrazar.playerbosses;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class RenderUtil {
   public RenderUtil() {
   }

   public static void renderEntityBoundingBox(EntityLiving entity, double x, double y, double z) {
      AxisAlignedBB bb = entity.getEntityBoundingBox();
      if (bb != null) {
         GL11.glDisable(3553);
         GL11.glDisable(2896);
         GL11.glDisable(2884);
         GL11.glPushMatrix();
         GL11.glTranslatef((float)x, (float)y, (float)z);
         GL11.glPushMatrix();
         GL11.glRotatef(-entity.renderYawOffset, 0.0F, 1.0F, 0.0F);
         BufferBuilder tes = Tessellator.getInstance().getBuffer();
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
         GL11.glPopMatrix();
         GL11.glPopMatrix();
         GL11.glEnable(3553);
         GL11.glEnable(2896);
         GL11.glEnable(2884);
      }

   }
}
