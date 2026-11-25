package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.creature.EntityAmalgalich;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityAmalgalich.class})
public abstract class EntityAmalgalichRenderMixin {
   public EntityAmalgalichRenderMixin() {
   }

   @Inject(
      method = {"getRenderBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void rlmixins_entityAmalgalich_getRenderBoundingBox(CallbackInfoReturnable<AxisAlignedBB> cir) {
      cir.setReturnValue(((EntityAmalgalich)this).getEntityBoundingBox().grow(5.0D, 1.0D, 5.0D));
   }
}
