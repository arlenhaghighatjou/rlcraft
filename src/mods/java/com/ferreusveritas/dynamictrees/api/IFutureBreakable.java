package com.ferreusveritas.dynamictrees.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IFutureBreakable {
   void futureBreak(IBlockState var1, World var2, BlockPos var3, EntityLivingBase var4);
}
