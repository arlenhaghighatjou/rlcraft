package com.charles445.simpledifficulty.api.temperature;

import net.minecraft.util.math.BlockPos;

public interface ITemperatureTileEntity {
   float getInfluence(BlockPos var1, double var2);
}
