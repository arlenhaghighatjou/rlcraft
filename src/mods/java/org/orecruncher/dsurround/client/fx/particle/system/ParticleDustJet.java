package org.orecruncher.dsurround.client.fx.particle.system;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.ParticleDust;

@SideOnly(Side.CLIENT)
public class ParticleDustJet extends ParticleJet {
   protected final IBlockState blockState;

   public ParticleDustJet(int strength, World world, double x, double y, double z, IBlockState state) {
      super(1, strength, world, x, y, z, 2);
      this.blockState = state;
   }

   protected void spawnJetParticle() {
      double x = this.posX + RANDOM.nextGaussian() * 0.2D;
      double z = this.posZ + RANDOM.nextGaussian() * 0.2D;
      Particle particle = (new ParticleDust(this.world, x, this.posY, z, this.blockState)).init();
      this.addParticle(particle);
   }
}
