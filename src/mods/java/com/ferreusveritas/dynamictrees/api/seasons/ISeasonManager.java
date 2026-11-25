package com.ferreusveritas.dynamictrees.api.seasons;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISeasonManager {
   void updateTick(World var1, long var2);

   void flushMappings();

   float getGrowthFactor(World var1, BlockPos var2, float var3);

   float getSeedDropFactor(World var1, BlockPos var2, float var3);

   float getFruitProductionFactor(World var1, BlockPos var2, float var3);

   Float getSeasonValue(World var1, BlockPos var2);

   boolean isTropical(World var1, BlockPos var2);

   boolean shouldSnowMelt(World var1, BlockPos var2);
}
