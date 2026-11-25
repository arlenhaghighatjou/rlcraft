package rlmixins.mixin.vanilla;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rlmixins.RLMixins;

@Mixin({Entity.class})
public abstract class EntityCollisionMixin {
   public EntityCollisionMixin() {
   }

   @Redirect(
      method = {"move"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/util/math/AxisAlignedBB;expand(DDD)Lnet/minecraft/util/math/AxisAlignedBB;",
   ordinal = 0
)
   )
   public AxisAlignedBB rlmixins_vanillaEntity_move(AxisAlignedBB instance, double x, double y, double z) {
      if (x <= 128.0D && x >= -128.0D && z <= 128.0D && z >= -128.0D) {
         return instance.expand(x, y, z);
      } else {
         RLMixins.LOGGER.log(Level.WARN, "RLMixins detected attempt at iterating over-sized movement collision box, shrinking.");
         return instance.expand(Math.min(128.0D, Math.max(-128.0D, x)), y, Math.min(128.0D, Math.max(-128.0D, z)));
      }
   }
}
