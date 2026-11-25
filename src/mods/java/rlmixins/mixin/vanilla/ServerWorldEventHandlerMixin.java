package rlmixins.mixin.vanilla;

import java.util.Objects;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.ServerWorldEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerWorldEventHandler.class})
public abstract class ServerWorldEventHandlerMixin {
   public ServerWorldEventHandlerMixin() {
   }

   @Inject(
      method = {"spawnParticle(IZDDDDDD[I)V"},
      at = {@At("HEAD")}
   )
   public void rlmixins_vanilla_serverWorldEventHandler_spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int[] parameters, CallbackInfo ci) {
      EnumParticleTypes particle = (EnumParticleTypes)Objects.requireNonNull(EnumParticleTypes.getParticleFromId(particleID));
      if (particle != EnumParticleTypes.SPELL_MOB && particle != EnumParticleTypes.SPELL_MOB_AMBIENT) {
         if (parameters.length == particle.getArgumentCount()) {
            ((ServerWorldEventHandlerAccessor)this).getWorld().spawnParticle(particle, xCoord, yCoord, zCoord, 0, xSpeed, ySpeed, zSpeed, 1.0D, parameters);
         }

      }
   }
}
