package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IAgeable {
   int age(World var1, BlockPos var2, IBlockState var3, Random var4, SafeChunkBounds var5);
}
