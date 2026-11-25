package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.creature.EntityTreant;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityTreant.class})
public abstract class EntityTreantRenderMixin {
   public EntityTreantRenderMixin() {
   }

   @Inject(
      method = {"getRenderBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void rlmixins_entityTreant_getRenderBoundingBox(CallbackInfoReturnable<AxisAlignedBB> cir) {
      cir.setReturnValue(((EntityTreant)this).getEntityBoundingBox().grow(7.0D, 5.0D, 7.0D).offset(0.0D, 5.0D, 0.0D));
   }
}
