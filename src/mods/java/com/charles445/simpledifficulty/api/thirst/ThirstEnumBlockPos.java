package com.charles445.simpledifficulty.api.thirst;

import net.minecraft.util.math.BlockPos;

public class ThirstEnumBlockPos {
   public ThirstEnum thirstEnum;
   public BlockPos pos;

   public ThirstEnumBlockPos(ThirstEnum thirstEnum, BlockPos pos) {
      this.thirstEnum = thirstEnum;
      this.pos = pos;
   }
}
