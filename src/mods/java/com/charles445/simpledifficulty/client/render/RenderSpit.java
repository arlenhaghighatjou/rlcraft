package com.charles445.simpledifficulty.client.render;

import com.charles445.simpledifficulty.tileentity.TileEntitySpit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

public class RenderSpit extends TileEntitySpecialRenderer<TileEntitySpit> {
   private final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
   private boolean initialized = false;
   private final double itemBoundStart = 0.3D;
   private final double itemBoundSize = 0.8141999999999998D;

   public RenderSpit() {
   }

   public void render(TileEntitySpit te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      GlStateManager.rotate(45.0F, 0.0F, 5.0F, 0.0F);
      int slots = te.items.getSlots();
      double separationAmt = 0.0D;
      if (slots > 1) {
         separationAmt = 0.8141999999999998D / (double)(slots - 1);
      }

      for(int i = 0; i < slots; ++i) {
         ItemStack stack = te.items.getStackInSlot(i);
         if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0D, 0.0D, (double)i * separationAmt + 0.3D);
            this.itemRenderer.renderItem(stack, TransformType.GROUND);
            GlStateManager.popMatrix();
         }
      }

      GlStateManager.popMatrix();
   }
}
