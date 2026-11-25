package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.creature.EntityRahovart;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRahovart.class})
public abstract class EntityRahovartRenderMixin {
   public EntityRahovartRenderMixin() {
   }

   @Inject(
      method = {"getRenderBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void rlmixins_entityRahovart_getRenderBoundingBox(CallbackInfoReturnable<AxisAlignedBB> cir) {
      cir.setReturnValue(((EntityRahovart)this).getEntityBoundingBox().grow(7.5D, 1.0D, 7.5D));
   }
}
