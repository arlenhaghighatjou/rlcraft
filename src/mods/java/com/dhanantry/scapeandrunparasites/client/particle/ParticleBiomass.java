package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleBiomass extends Particle {
   public static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("srparasites", "particle/biomass");

   protected ParticleBiomass(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTURE.toString()));
      this.particleScale = 3.0F;
      this.particleMaxAge = 35;
      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      if (this.particleAge >= 20) {
         this.particleScale = (float)((double)this.particleScale - 0.1D);
      }

      this.motionY += 0.005D;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }

   public int getFXLayer() {
      return 1;
   }
}
