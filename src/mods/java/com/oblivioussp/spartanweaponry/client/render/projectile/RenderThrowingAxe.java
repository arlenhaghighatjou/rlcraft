package com.oblivioussp.spartanweaponry.client.render.projectile;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;

public class RenderThrowingAxe<T extends EntityThrownWeapon> extends RenderThrownWeapon<T> {
   public RenderThrowingAxe(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn, itemRendererIn);
   }

   protected void doRenderTransformations(T entity, float partialTicks) {
      GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 1.0F, 0.0F);
      GlStateManager.translate(-0.05D, -0.25D, 0.0D);
      if ((float)entity.getTicksInAir() != 0.0F) {
         float rotation = ((float)entity.getTicksInAir() + partialTicks) * 30.0F % 360.0F;
         if (entity.isReturning()) {
            rotation *= -1.0F;
         }

         GlStateManager.rotate(rotation, 0.0F, 0.0F, 1.0F);
      }

   }
}
