package com.oblivioussp.spartanweaponry.client.render.projectile;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingKnife;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderThrowingKnife extends RenderThrownWeapon<EntityThrowingKnife> {
   public RenderThrowingKnife(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn, itemRendererIn);
   }

   protected void doRenderTransformations(EntityThrowingKnife entity, float partialTicks) {
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 45.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.05D, -0.2D, 0.0D);
   }
}
