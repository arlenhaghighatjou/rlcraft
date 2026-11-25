package meldexun.renderlib.mixin.entity;

import meldexun.renderlib.api.IBoundingBoxCache;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Render.class})
public class MixinRender {
   public MixinRender() {
   }

   @Inject(
      method = {"shouldRender"},
      cancellable = true,
      at = {@At("HEAD")}
   )
   public void shouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> info) {
      if (!entity.isInRangeToRender3d(camX, camY, camZ)) {
         info.setReturnValue(Boolean.valueOf(false));
      } else if (entity.ignoreFrustumCheck) {
         info.setReturnValue(Boolean.valueOf(true));
      } else {
         info.setReturnValue(Boolean.valueOf(((IBoundingBoxCache)entity).getCachedBoundingBox().isVisible(camera)));
      }
   }
}
