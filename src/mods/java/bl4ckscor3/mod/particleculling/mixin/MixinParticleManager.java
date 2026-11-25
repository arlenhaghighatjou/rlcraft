package bl4ckscor3.mod.particleculling.mixin;

import bl4ckscor3.mod.particleculling.CullCheck;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ParticleManager.class})
public class MixinParticleManager {
   public MixinParticleManager() {
   }

   @Redirect(
      method = {"renderParticles"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/particle/Particle;renderParticle(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V"
)
   )
   private void renderParticles(Particle particle, WorldRenderer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!((CullCheck)particle).isCulled()) {
         particle.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
      }

   }

   @Redirect(
      method = {"renderLitParticles"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/particle/Particle;renderParticle(Lnet/minecraft/client/renderer/WorldRenderer;Lnet/minecraft/entity/Entity;FFFFFF)V"
)
   )
   private void renderLitParticles(Particle particle, WorldRenderer buffer, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      if (!((CullCheck)particle).isCulled()) {
         particle.renderParticle(buffer, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
      }

   }
}
