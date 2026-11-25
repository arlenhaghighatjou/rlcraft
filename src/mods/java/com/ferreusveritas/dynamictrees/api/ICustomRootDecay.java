package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICustomRootDecay {
   boolean doDecay(World var1, BlockPos var2, IBlockState var3, Species var4);
}
