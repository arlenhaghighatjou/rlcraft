package rlmixins.mixin.lycanites.boundingbox;

import com.lycanitesmobs.core.entity.creature.EntityAsmodeus;
import net.minecraft.util.math.AxisAlignedBB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityAsmodeus.class})
public abstract class EntityAsmodeusRenderMixin {
   public EntityAsmodeusRenderMixin() {
   }

   @Inject(
      method = {"getRenderBoundingBox"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void rlmixins_entityAsmodeus_getRenderBoundingBox(CallbackInfoReturnable<AxisAlignedBB> cir) {
      cir.setReturnValue(((EntityAsmodeus)this).getEntityBoundingBox().grow(7.0D, 0.0D, 7.0D));
   }
}
