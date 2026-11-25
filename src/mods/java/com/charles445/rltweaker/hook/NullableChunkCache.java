package com.charles445.rltweaker.hook;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public class NullableChunkCache extends ChunkCache {
   public NullableChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
      super(worldIn, posFromIn, posToIn, subIn);
   }
}
