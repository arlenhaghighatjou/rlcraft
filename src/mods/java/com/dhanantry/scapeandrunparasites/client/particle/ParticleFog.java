package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFog extends Particle {
   public static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("srparasites", "particle/fog");

   public ParticleFog(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
      super(worldIn, xCoordIn + 0.0D, yCoordIn, zCoordIn + 0.0D, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.motionY = ySpeedIn * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.motionZ = zSpeedIn * 0.20000000298023224D + (Math.random() * 2.0D - 1.0D) * 0.019999999552965164D;
      this.particleRed = (float)r;
      this.particleGreen = (float)g;
      this.particleBlue = (float)b;
      this.particleScale = 0.0F;
      this.setAlphaF(0.8F);
      this.particleMaxAge = 200;
      this.particleGravity = 0.0F;
   }

   public Particle multiplyVelocity(float multiplier) {
      this.motionX *= (double)multiplier;
      this.motionY *= (double)multiplier;
      this.motionZ *= (double)multiplier;
      return this;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if ((double)this.particleScale < 8.0D && this.particleAge < 175) {
         this.particleScale = (float)((double)this.particleScale + 0.3D);
      }

      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      if (this.particleAge >= 175) {
         this.particleScale = (float)((double)this.particleScale - 0.3D);
      }

      this.motionY -= 0.04D * (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY += 1.0E-16D;
      this.motionZ *= 0.9800000190734863D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
         this.motionY += 1.0E-5D;
      }

   }

   public int getFXLayer() {
      return 0;
   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Factory() {
      }

      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new ParticleFog(worldIn, xCoordIn, yCoordIn, zCoordIn, (double)((float)xSpeedIn), (double)((float)ySpeedIn), (double)((float)zSpeedIn), 0, 0, 0);
      }
   }
}
