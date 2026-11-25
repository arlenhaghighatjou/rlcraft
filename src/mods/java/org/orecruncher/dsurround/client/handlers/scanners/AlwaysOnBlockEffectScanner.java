package org.orecruncher.dsurround.client.handlers.scanners;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.lib.scanner.CuboidScanner;
import org.orecruncher.dsurround.lib.scanner.ScanLocus;
import org.orecruncher.dsurround.registry.blockstate.BlockStateUtil;
import org.orecruncher.lib.chunk.IBlockAccessEx;

@SideOnly(Side.CLIENT)
public class AlwaysOnBlockEffectScanner extends CuboidScanner {
   public AlwaysOnBlockEffectScanner(@Nonnull ScanLocus locus, int range) {
      super(locus, "AlwaysOnBlockEffectScanner", range, 0);
      this.setLogger(ModBase.log());
   }

   protected boolean interestingBlock(IBlockState state) {
      return BlockStateUtil.getStateData(state).hasAlwaysOnEffects();
   }

   public void blockScan(@Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull EaglercraftRandom rand) {
      IBlockAccessEx provider = this.locus.getWorld();
      BlockEffect[] effects = BlockStateUtil.getStateData(state).getAlwaysOnEffects();

      for(BlockEffect be : effects) {
         if (be.canTrigger(provider, state, pos, rand)) {
            be.doEffect(provider, state, pos, rand);
         }
      }

   }
}
