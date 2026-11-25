package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBubble.Factory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBubbleJet extends ParticleJet {
   protected final IParticleFactory factory = new Factory();

   public ParticleBubbleJet(int strength, World world, double x, double y, double z) {
      super(strength, world, x, y, z);
   }

   protected void spawnJetParticle() {
      Particle particle = this.factory.createParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), this.world, this.posX, this.posY, this.posZ, 0.0D, 0.5D + (double)this.jetStrength / 10.0D, 0.0D, new int[0]);
      if (particle != null) {
         this.addParticle(particle);
      }

   }
}
