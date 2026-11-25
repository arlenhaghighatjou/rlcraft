package com.endertech.minecraft.forge.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IPole {
   EnumFacing getSlideDirection(World var1, BlockPos var2);

   boolean isSamePole(IBlockAccess var1, BlockPos var2);
}
