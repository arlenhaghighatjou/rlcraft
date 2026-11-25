package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IPostGenFeature extends IGenFeature {
   boolean postGeneration(World var1, BlockPos var2, Species var3, Biome var4, int var5, List<BlockPos> var6, SafeChunkBounds var7, IBlockState var8);
}
