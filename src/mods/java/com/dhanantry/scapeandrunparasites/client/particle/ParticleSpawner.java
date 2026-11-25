package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;

public class ParticleSpawner {
   private static Minecraft mc = Minecraft.getMinecraft();

   public ParticleSpawner() {
   }

   public static Particle spawnParticle(SRPEnumParticle type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
      if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
         int canSpawn = Minecraft.getMinecraft().gameSettings.particleSetting;
         if (canSpawn >= 1) {
            return null;
         } else {
            Particle part = null;
            if (type == SRPEnumParticle.FOG) {
               part = new ParticleFog(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.SPORE) {
               part = new ParticleSpore(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.GCLOUD) {
               part = new ParticleRGBSmoke(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
            } else if (type == SRPEnumParticle.GSPLASH) {
               part = new ParticleMultipleGore(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r);
            } else if (type == SRPEnumParticle.RHAPPY) {
               part = new ParticleRHappy(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.BIOMASS) {
               part = new ParticleBiomass(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            } else if (type == SRPEnumParticle.EEN) {
               part = new ParticleEen(mc.world, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
            }

            mc.effectRenderer.addEffect(part);
            return part;
         }
      } else {
         return null;
      }
   }
}
