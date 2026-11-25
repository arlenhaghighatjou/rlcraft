package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleDustJet;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class DustJetEffect extends JetEffect {
   public DustJetEffect(int chance) {
      super(chance);
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.DUST_JET;
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      return provider.isAirBlock(pos.down()) && super.canTrigger(provider, state, pos, random);
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      ParticleJet effect = new ParticleDustJet(2, provider.getWorld(), (double)pos.getX() + 0.5D, (double)pos.getY() - 0.2D, (double)pos.getZ() + 0.5D, state);
      this.addEffect(effect);
   }
}
