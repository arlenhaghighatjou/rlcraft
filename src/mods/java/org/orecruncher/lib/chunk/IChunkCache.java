package org.orecruncher.lib.chunk;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

interface IChunkCache {
   void update(@Nonnull World var1, @Nonnull BlockPos var2, @Nonnull BlockPos var3);

   void clear();
}
