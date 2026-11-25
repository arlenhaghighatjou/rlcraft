package ivorius.ivtoolkit.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public class IvParticleHelper {
   public IvParticleHelper() {
   }

   public static void spawnParticle(Particle particle) {
      Minecraft.getMinecraft().effectRenderer.addEffect(particle);
   }
}
