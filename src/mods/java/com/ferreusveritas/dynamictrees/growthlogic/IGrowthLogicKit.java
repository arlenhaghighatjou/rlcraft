package com.ferreusveritas.dynamictrees.growthlogic;

import com.ferreusveritas.dynamictrees.systems.GrowSignal;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGrowthLogicKit {
   int[] directionManipulation(World var1, BlockPos var2, Species var3, int var4, GrowSignal var5, int[] var6);

   EnumFacing newDirectionSelected(Species var1, EnumFacing var2, GrowSignal var3);

   float getEnergy(World var1, BlockPos var2, Species var3, float var4);
}
