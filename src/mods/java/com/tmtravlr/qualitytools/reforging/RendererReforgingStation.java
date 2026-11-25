package com.tmtravlr.qualitytools.reforging;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class RendererReforgingStation extends TileEntitySpecialRenderer<TileEntityReforgingStation> {
   public RendererReforgingStation() {
   }

   public void render(TileEntityReforgingStation te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate(x + 0.5D, y, z + 0.5D);
      if (te != null) {
         double yOffset = 1.01D;
         if (te.getStackInSlot(0) != null && !te.getStackInSlot(0).isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, yOffset, -0.25D);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.rotate(160.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(te.getStackInSlot(0), TransformType.FIXED);
            GlStateManager.popMatrix();
         }

         if (te.getStackInSlot(1) != null && !te.getStackInSlot(1).isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.25D, yOffset, 0.25D);
            GlStateManager.scale(0.5D, 0.5D, 0.5D);
            GlStateManager.rotate(40.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            Minecraft.getMinecraft().getRenderItem().renderItem(te.getStackInSlot(1), TransformType.FIXED);
            GlStateManager.popMatrix();
         }
      }

      GlStateManager.popMatrix();
   }
}
