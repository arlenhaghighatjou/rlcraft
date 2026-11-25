package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.creature.EntityEpion;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityEpion.class})
public abstract class EntityEpionRenderMixin {
   public EntityEpionRenderMixin() {
   }

   @Inject(
      method = {"getRenderBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void rlmixins_entityEpion_getRenderBoundingBox(CallbackInfoReturnable<AxisAlignedBB> cir) {
      cir.setReturnValue(((EntityEpion)this).getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D).offset(0.0D, 0.5D, 0.0D));
   }
}
