package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ParticleJet extends ParticleSystem {
   protected final int jetStrength;
   protected final int updateFrequency;
   protected final int particleMaxAge;
   protected int particleAge;

   public ParticleJet(int strength, World world, double x, double y, double z) {
      this(0, strength, world, x, y, z, 3);
   }

   public ParticleJet(int layer, int strength, World world, double x, double y, double z, int freq) {
      super(world, x, y, z);
      this.jetStrength = strength;
      this.updateFrequency = freq;
      this.particleMaxAge = (RANDOM.nextInt(strength) + 2) * 20;
   }

   protected abstract void spawnJetParticle();

   public boolean shouldDie() {
      return this.particleAge >= this.particleMaxAge;
   }

   public void think() {
      if (this.particleAge % this.updateFrequency == 0) {
         this.spawnJetParticle();
      }

      ++this.particleAge;
   }
}
