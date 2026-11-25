package bl4ckscor3.mod.particleculling.mixin;

import bl4ckscor3.mod.particleculling.CullCheck;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Particle.class})
public class MixinParticle implements CullCheck {
   @Unique
   private boolean culled = true;

   public MixinParticle() {
   }

   public void setCulled(boolean culled) {
      this.culled = culled;
   }

   public boolean isCulled() {
      return this.culled;
   }
}
