package cursedflames.bountifulbaubles.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ParticleGradient extends Particle {
   float rDiff;
   float gDiff;
   float bDiff;
   float spinSpeed;

   public ParticleGradient(World world, double x, double y, double z, float r, float g, float b, float rT, float gT, float bT, int maxAge) {
      this(world, x, y, z, r, g, b, rT, gT, bT, maxAge, 0.0D, 0.0D, 0.0D);
   }

   public ParticleGradient(World world, double x, double y, double z, float r, float g, float b, float rT, float gT, float bT, int maxAge, double velX, double velY, double velZ) {
      super(world, x, y, z);
      this.motionX = velX;
      this.motionY = velY;
      this.motionZ = velZ;
      this.spinSpeed = 0.1F;
      this.particleRed = r;
      this.particleGreen = g;
      this.particleBlue = b;
      this.particleMaxAge = maxAge;
      this.rDiff = (rT - r) / (float)maxAge;
      this.gDiff = (gT - g) / (float)maxAge;
      this.bDiff = (bT - b) / (float)maxAge;
      this.setParticleTextureIndex((int)(4.0D + Math.random() * 4.0D));
   }

   public void onUpdate() {
      super.onUpdate();
      this.particleRed += this.rDiff;
      this.particleGreen += this.gDiff;
      this.particleBlue += this.bDiff;
      this.particleAngle += this.spinSpeed;
   }

   public int getBrightnessForRender(float pTicks) {
      float f = 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender(pTicks);
      int j = i & 255;
      int k = i >> 16 & 255;
      j = j + (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }
}
