package com.creativemd.creativecore.client.rendering;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRenderChunkSupplier {
   Object getRenderChunk(World var1, BlockPos var2);
}
