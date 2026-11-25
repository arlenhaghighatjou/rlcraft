package net.silentchaos512.scalinghealth.client.render.particle;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.silentchaos512.lib.client.particle.ParticleSL;
import net.silentchaos512.lib.client.render.BufferBuilderSL;

public class ParticleSH extends ParticleSL {
   public static final int MAX_AGE = 10;
   public static final int MAX_SCALE = 1;

   protected ParticleSH(World world, double posX, double posY, double posZ) {
      this(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D, 1.0F, 10, 1.0F, 0.9F, 0.4F);
   }

   public ParticleSH(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
      this(world, posX, posY, posZ, motionX, motionY, motionZ, 1.0F, 10, 1.0F, 0.9F, 0.4F);
   }

   public ParticleSH(World world, double posX, double posY, double posZ, double motionX, double motionY, double motionZ, float scale, int maxAge, float red, float green, float blue) {
      super(world, posX, posY, posZ, 0.0D, 0.0D, 0.0D);
      this.motionX = motionX;
      this.motionY = motionY;
      this.motionZ = motionZ;
      this.particleTextureIndexX = 7;
      this.particleTextureIndexY = 10;
      this.particleRed = red;
      this.particleGreen = green;
      this.particleBlue = blue;
      this.particleScale = scale;
      this.particleMaxAge = maxAge;
      this.canCollide = false;
      this.particleGravity = 0.1F;
   }

   public void onUpdate() {
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionY -= (double)(this.particleGravity / 20.0F);
      this.particleTextureIndexX = 7 - 7 * this.particleAge / this.particleMaxAge;
      this.particleScale *= 0.95F;
   }

   public void clRenderParticle(BufferBuilderSL buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
      float f = 1.5F * ((float)this.particleAge + partialTicks) / (float)this.particleMaxAge * 32.0F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      this.particleScale = 1.0F * f;
      super.clRenderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
   }
}
