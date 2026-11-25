package org.orecruncher.dsurround.client.fx;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleFireJet;
import org.orecruncher.dsurround.client.fx.particle.system.ParticleJet;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class FireJetEffect extends JetEffect {
   public FireJetEffect(int chance) {
      super(chance);
   }

   @Nonnull
   public BlockEffectType getEffectType() {
      return BlockEffectType.FIRE_JET;
   }

   public boolean canTrigger(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      if (!state.getMaterial().isSolid() && !state.getMaterial().isLiquid()) {
         return false;
      } else {
         return provider.isAirBlock(pos.up()) && super.canTrigger(provider, state, pos, random);
      }
   }

   public void doEffect(@Nonnull IBlockAccessEx provider, @Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Random random) {
      Material blockMaterial = state.getMaterial();
      int blockCount;
      float spawnHeight;
      if (blockMaterial.isSolid()) {
         blockCount = 2;
         spawnHeight = (float)pos.getY() + 1.1F;
      } else {
         if (!blockMaterial.isLiquid()) {
            return;
         }

         blockCount = countBlocks(provider, pos, (s) -> {
            return s.getMaterial() == blockMaterial;
         }, -1);
         spawnHeight = BlockLiquid.getLiquidHeight(state, provider, pos);
      }

      if (blockCount > 0) {
         ParticleJet effect = new ParticleFireJet(blockCount, provider.getWorld(), (double)pos.getX() + 0.5D, (double)spawnHeight, (double)pos.getZ() + 0.5D);
         this.addEffect(effect);
      }

   }
}
