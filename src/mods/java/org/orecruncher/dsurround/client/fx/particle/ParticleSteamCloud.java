package org.orecruncher.dsurround.client.fx.particle;

import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class ParticleSteamCloud extends ParticleCloud {
   public static boolean isHotBlock(@Nonnull IBlockState state) {
      return state.getMaterial() == Material.LAVA || state.getBlock() == Blocks.MAGMA;
   }

   public ParticleSteamCloud(World world, double x, double y, double z, double dY) {
      super(world, x, y, z, XorShiftRandom.current().nextGaussian() * 0.02D, dY, XorShiftRandom.current().nextGaussian() * 0.02D);
   }

   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.particleAge++ >= this.particleMaxAge) {
         this.setExpired();
      }

      this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
      this.move(this.motionX, this.motionY, this.motionZ);
      this.motionX *= 0.9599999785423279D;
      this.motionY *= 0.9599999785423279D;
      this.motionZ *= 0.9599999785423279D;
      if (this.onGround) {
         this.motionX *= 0.699999988079071D;
         this.motionZ *= 0.699999988079071D;
      }

   }
}
