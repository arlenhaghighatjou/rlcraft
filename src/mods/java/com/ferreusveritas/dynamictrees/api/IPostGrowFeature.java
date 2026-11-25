package com.ferreusveritas.dynamictrees.api;

import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPostGrowFeature extends IGenFeature {
   boolean postGrow(World var1, BlockPos var2, BlockPos var3, Species var4, int var5, boolean var6);
}
