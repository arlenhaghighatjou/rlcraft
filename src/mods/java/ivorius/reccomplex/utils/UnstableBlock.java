package ivorius.reccomplex.utils;

import net.minecraft.block.state.IBlockState;

public interface UnstableBlock {
   static boolean shouldSkipState(IBlockState state) {
      return state.getBlock() instanceof UnstableBlock ? ((UnstableBlock)state.getBlock()).shouldSkipOnGeneration(state) : false;
   }

   default boolean shouldSkipOnGeneration(IBlockState state) {
      return true;
   }
}
