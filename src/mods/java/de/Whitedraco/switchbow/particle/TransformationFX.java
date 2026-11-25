package de.Whitedraco.switchbow.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TransformationFX extends Particle {
   private final float oSize;
   private final double coordX;
   private final double coordY;
   private final double coordZ;

   public TransformationFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.coordX = xCoordIn;
      this.coordY = yCoordIn;
      this.coordZ = zCoordIn;
      this.prevPosX = xCoordIn + xSpeedIn;
      this.prevPosY = yCoordIn + ySpeedIn;
      this.prevPosZ = zCoordIn + zSpeedIn;
      this.posX = this.prevPosX;
      this.posY = this.prevPosY;
      this.posZ = this.prevPosZ;
      float f = this.rand.nextFloat() * 0.6F + 0.4F;
      this.particleScale = this.rand.nextFloat() * 0.5F + 0.2F;
      this.oSize = this.particleScale;
      this.particleRed = 0.9F * f;
      this.particleGreen = 0.9F * f;
      this.particleBlue = f;
      this.particleMaxAge = (int)(Math.random() * 10.0D) + 50;
      this.setParticleTextureIndex((int)(Math.random() * 26.0D + 1.0D + 224.0D));
   }

   public void move(double x, double y, double z) {
      this.setBoundingBox(this.getBoundingBox().offset(x, y, z));
      this.resetPositionToBB();
   }

   public int getBrightnessForRender(float p_189214_1_) {
      int i = super.getBrightnessForRender(p_189214_1_);
      float f = (float)this.particleAge / (float)this.particleMaxAge;
      f = f * f;
      f = f * f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k = k + (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      float f = (float)this.particleAge / (float)this.particleMaxAge * 3.0F;
      f = 1.0F - f;
      this.posX = this.coordX + this.motionX * (double)f;
      this.posY = this.coordY + this.motionY * (double)f;
      this.posZ = this.coordZ + this.motionZ * (double)f;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

   }

   @SideOnly(Side.CLIENT)
   public static class Podest implements IParticleFactory {
      public Podest() {
      }

      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new TransformationFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
