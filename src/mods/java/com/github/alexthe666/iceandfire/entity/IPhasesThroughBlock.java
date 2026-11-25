package com.github.alexthe666.iceandfire.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPhasesThroughBlock {
   boolean canPhaseThroughBlock(World var1, BlockPos var2);
}
