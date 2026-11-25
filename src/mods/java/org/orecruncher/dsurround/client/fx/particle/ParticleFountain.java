package org.orecruncher.dsurround.client.fx.particle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleFountain extends ParticleBlockDust {
   public ParticleFountain(World world, double x, double y, double z, double dX, double dY, double dZ, IBlockState block) {
      super(world, x, y, z, dX, dY, dZ, block);
      this.multipleParticleScaleBy((float)(0.30000001192092896D + this.rand.nextGaussian() / 10.0D));
      this.setPosition(this.posX, this.posY, this.posZ);
   }
}
