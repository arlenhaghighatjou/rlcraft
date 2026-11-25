package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleMultipleGore extends Particle {
   public static final ResourceLocation PARTICLES_TEXTUREINFECTED = new ResourceLocation("srparasites", "particle/splashi");
   public static final ResourceLocation PARTICLES_TEXTURESPIDER = new ResourceLocation("srparasites", "particle/splashd");
   public static final ResourceLocation PARTICLES_TEXTUREPRIMITIVE = new ResourceLocation("srparasites", "particle/splashp");
   public static final ResourceLocation PARTICLES_TEXTUREADAPTED = new ResourceLocation("srparasites", "particle/splasha");
   public static final ResourceLocation PARTICLES_TEXTUREVOMIT = new ResourceLocation("srparasites", "particle/splashv");

   protected ParticleMultipleGore(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int texture) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0.0D, 0.0D, 0.0D);
      if (texture == 0) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTUREINFECTED.toString()));
      } else if (texture == 1) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTURESPIDER.toString()));
      } else if (texture == 2) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTUREPRIMITIVE.toString()));
      } else if (texture == 3) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTUREADAPTED.toString()));
      } else if (texture == 4) {
         this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTUREVOMIT.toString()));
      }

      this.motionX = xSpeedIn;
      this.motionY = ySpeedIn;
      this.motionZ = zSpeedIn;
      this.setSize(0.01F, 0.01F);
      this.particleGravity = 0.06F;
      this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.particleGravity = 0.04F;
      this.particleMaxAge = 20 * (this.rand.nextInt(3) + 1);
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.motionY -= (double)this.particleGravity;
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9800000190734863D;
      this.motionY *= 0.9800000190734863D;
      this.motionZ *= 0.9800000190734863D;
      if (this.particleMaxAge-- <= 0) {
         this.setExpired();
      }

      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }

   public int getFXLayer() {
      return 1;
   }
}
