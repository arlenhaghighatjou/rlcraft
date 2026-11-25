package de.Whitedraco.switchbow.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FireFX extends Particle {
   private float flameScale;
   private static final String __OBFID = "CL_00000907";
   private double EntityPosx;
   private double EntityPosz;
   private int Winkel = 0;
   private double Radius = 1.0D;

   public FireFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double EntityPosx, double EntityPosz, int Winkel) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      this.motionX = this.motionX * 0.009999999776482582D + xSpeedIn;
      this.motionY = this.motionY * 0.009999999776482582D + ySpeedIn;
      this.motionZ = this.motionZ * 0.009999999776482582D + zSpeedIn;
      this.posX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.posZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.05F);
      this.flameScale = this.particleScale + 0.5F;
      this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
      if (Minecraft.getDebugFPS() < 25) {
         this.particleMaxAge = 25;
      } else if (Minecraft.getDebugFPS() < 45) {
         this.particleMaxAge = 50;
      } else {
         this.particleMaxAge = 100;
      }

      this.setParticleTextureIndex(48);
      this.EntityPosx = EntityPosx;
      this.EntityPosz = EntityPosz;
      this.Winkel = Winkel;
   }

   public void renderParticle(WorldRenderer buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge;
      this.particleScale = this.flameScale * (1.0F - f * f * 0.5F);
      super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }

   public int getBrightnessForRender(float p_189214_1_) {
      float f = ((float)this.particleAge + p_189214_1_) / (float)this.particleMaxAge;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      int i = super.getBrightnessForRender(p_189214_1_);
      int j = i & 255;
      int k = i >> 16 & 255;
      j = j + (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      double x = 0.0D;
      double z = 0.0D;
      double Wirkungswinkel = 0.13962634015954636D;
      x = Math.cos((double)this.Winkel * Wirkungswinkel) * this.Radius + this.EntityPosx;
      z = Math.sin((double)this.Winkel * Wirkungswinkel) * this.Radius + this.EntityPosz;
      ++this.Winkel;
      this.setPosition(x, this.posY - 0.05D, z);
   }

   @SideOnly(Side.CLIENT)
   public static class Factory implements IParticleFactory {
      public Factory() {
      }

      public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
         return new FireFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, 0.0D, 0.0D, 0);
      }
   }
}
