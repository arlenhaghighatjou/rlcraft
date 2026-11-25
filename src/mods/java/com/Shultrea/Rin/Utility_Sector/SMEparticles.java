package com.Shultrea.Rin.Utility_Sector;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SMEparticles {
   public SMEparticles() {
   }

   @SideOnly(Side.CLIENT)
   public static void spawnParticle(String name, double x, double y, double z, double vx, double vy, double vz) {
      World world = Minecraft.getMinecraft().world;

      for(EnumParticleTypes p : EnumParticleTypes.values()) {
         if (p.getParticleName().equals(name)) {
            world.spawnParticle(p, x, y, z, vx, vy, vz, new int[0]);
            return;
         }
      }

   }

   @SideOnly(Side.CLIENT)
   private static void addEffect(Particle e) {
      Minecraft.getMinecraft().effectRenderer.addEffect(e);
   }
}
