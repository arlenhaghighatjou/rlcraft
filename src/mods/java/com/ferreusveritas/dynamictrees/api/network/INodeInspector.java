package com.ferreusveritas.dynamictrees.api.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INodeInspector {
   boolean run(IBlockState var1, World var2, BlockPos var3, EnumFacing var4);

   boolean returnRun(IBlockState var1, World var2, BlockPos var3, EnumFacing var4);
}
