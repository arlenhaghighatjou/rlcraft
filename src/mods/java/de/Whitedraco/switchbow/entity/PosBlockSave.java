package de.Whitedraco.switchbow.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

class PosBlockSave {
   IBlockState s;
   BlockPos i;

   PosBlockSave(IBlockState s, BlockPos i) {
      this.s = s;
      this.i = i;
   }
}
