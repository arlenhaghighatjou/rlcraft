package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPreGenFeature extends IGenFeature {
   BlockPos preGeneration(World var1, BlockPos var2, Species var3, int var4, EnumFacing var5, SafeChunkBounds var6, JoCode var7);
}
