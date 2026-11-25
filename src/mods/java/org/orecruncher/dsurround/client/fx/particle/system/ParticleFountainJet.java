package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.ParticleFountain;

@SideOnly(Side.CLIENT)
public class ParticleFountainJet extends ParticleJet {
   protected final IBlockState block;

   public ParticleFountainJet(int strength, World world, double x, double y, double z, IBlockState block) {
      super(1, strength, world, x, y, z, 1);
      this.block = block;
   }

   protected void spawnJetParticle() {
      double motionX = RANDOM.nextGaussian() * 0.03D;
      double motionZ = RANDOM.nextGaussian() * 0.03D;
      double x = this.posX + RANDOM.nextGaussian() * 0.2D;
      double z = this.posZ + RANDOM.nextGaussian() * 0.2D;
      Particle particle = (new ParticleFountain(this.world, x, this.posY, z, motionX, 0.5D, motionZ, this.block)).init();
      this.addParticle(particle);
   }
}
