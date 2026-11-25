package org.orecruncher.dsurround.client.fx;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleBubbleJet;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class BubbleJetEffect extends JetEffect {
   public BubbleJetEffect(int chance) {
      super(chance);
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.BUBBLE_JET;
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      if (!state.getMaterial().isLiquid()) {
         return false;
      } else {
         boolean isSolidBlock = provider.getBlockState(pos.down()).getMaterial().isSolid();
         return isSolidBlock && super.canTrigger(provider, state, pos, random);
      }
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom random) {
      int liquidBlocks = countBlocks(provider, pos, (s) -> {
         return s.getMaterial().isLiquid();
      }, 1);
      if (liquidBlocks > 0) {
         ParticleJet effect = new ParticleBubbleJet(liquidBlocks, provider.getWorld(), (double)pos.getX() + 0.5D, (double)pos.getY() + 0.1D, (double)pos.getZ() + 0.5D);
         this.addEffect(effect);
      }

   }
}
