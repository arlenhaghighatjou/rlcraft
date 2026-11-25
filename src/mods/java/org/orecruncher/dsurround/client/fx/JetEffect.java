package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.dsurround.client.handlers.ParticleSystemHandler;
import org.orecruncher.dsurround.expression.ExpressionEngine;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public abstract class JetEffect extends BlockEffect {
   protected static final int MAX_STRENGTH = 10;

   protected static int countBlocks(IBlockAccessEx provider, BlockPos pos, Predicate<IBlockState> pred, int step) {
      int count = 0;

      for(int idx = pos.getY(); count < 10 && pred.test(provider.getBlockState(pos.getX(), idx, pos.getZ())); idx += step) {
         ++count;
      }

      return count;
   }

   public JetEffect(int chance) {
      super(chance);
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      if (!this.alwaysExecute() && random.nextInt(this.getChance()) != 0) {
         return false;
      } else {
         return ParticleSystemHandler.okToSpawn(pos) && ExpressionEngine.instance().check(this.getConditions());
      }
   }

   protected void addEffect(ParticleJet fx) {
      ParticleSystemHandler.addSystem(fx);
   }
}
