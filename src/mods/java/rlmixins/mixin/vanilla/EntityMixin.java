package rlmixins.mixin.vanilla;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import rlmixins.handlers.ForgeConfigHandler;

@Mixin({Entity.class})
public abstract class EntityMixin {
   @Shadow
   public float fallDistance;

   public EntityMixin() {
   }

   @ModifyConstant(
      method = {"handleWaterMovement"},
      constant = {@Constant(
   floatValue = 0.0F
)}
   )
   public float rlmixins_vanillaEntity_handleWaterMovement(float constant) {
      return ForgeConfigHandler.server.fallDistanceReduction < 0.0D ? 0.0F : Math.max(this.fallDistance - (float)ForgeConfigHandler.server.fallDistanceReduction, 0.0F);
   }
}
