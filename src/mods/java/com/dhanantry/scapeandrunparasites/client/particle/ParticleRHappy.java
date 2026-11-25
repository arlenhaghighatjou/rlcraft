package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleRHappy extends Particle {
   public static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation("srparasites", "particle/rhappy");

   protected ParticleRHappy(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn);
      this.setParticleTexture(Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(PARTICLES_TEXTURE.toString()));
      this.particleScale = 2.0F;
      this.particleMaxAge = 40;
      if (ySpeedIn == 0.0D && (xSpeedIn != 0.0D || zSpeedIn != 0.0D)) {
         this.motionX = xSpeedIn;
         this.motionY = ySpeedIn + 0.1D;
         this.motionZ = zSpeedIn;
      }

   }

   public int getFXLayer() {
      return 1;
   }
}
