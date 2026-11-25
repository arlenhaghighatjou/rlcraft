package org.orecruncher.dsurround.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.World;
import org.orecruncher.dsurround.ModOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({EntityArrow.class})
public abstract class MixinEntityArrow extends Entity {
   public MixinEntityArrow(World worldIn) {
      super(worldIn);
   }

   @Shadow
   public boolean getIsCritical() {
      return false;
   }

   @Redirect(
      method = {"onUpdate()V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/entity/projectile/EntityArrow;getIsCritical()Z"
)
   )
   private boolean isCriticalCheck(EntityArrow self) {
      return !ModOptions.asm.disableArrowParticleTrail && self.getIsCritical();
   }
}
