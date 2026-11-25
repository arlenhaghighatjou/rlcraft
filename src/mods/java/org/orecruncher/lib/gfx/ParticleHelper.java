package org.orecruncher.lib.gfx;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ParticleHelper {
   protected ParticleHelper() {
   }

   @Nullable
   public static Particle spawnParticle(@Nonnull EnumParticleTypes particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {
      return Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(particleId.getParticleID(), xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, new int[0]);
   }

   @Nullable
   public static Particle spawnParticle(@Nonnull EnumParticleTypes particleId, double xCoord, double yCoord, double zCoord) {
      return spawnParticle(particleId, xCoord, yCoord, zCoord, 0.0D, 0.0D, 0.0D);
   }

   @Nullable
   public static Particle spawnParticle(@Nonnull EnumParticleTypes particleId, double xCoord, double yCoord, double zCoord, double ySpeed) {
      return spawnParticle(particleId, xCoord, yCoord, zCoord, 0.0D, ySpeed, 0.0D);
   }

   public static void addParticle(@Nonnull Particle particle) {
      Minecraft.getMinecraft().effectRenderer.addEffect(particle);
   }
}
