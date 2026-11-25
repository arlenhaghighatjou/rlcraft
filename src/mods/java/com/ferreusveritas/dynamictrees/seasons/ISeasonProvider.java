package com.ferreusveritas.dynamictrees.seasons;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISeasonProvider {
   Float getSeasonValue(World var1, BlockPos var2);

   void updateTick(World var1, long var2);

   boolean shouldSnowMelt(World var1, BlockPos var2);
}
