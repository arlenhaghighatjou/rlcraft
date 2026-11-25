package com.endertech.minecraft.forge.world;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkBounds extends WorldBounds {
   public static final int WIDTH = 16;
   public static final int HALF_WIDTH = 8;
   public static final int SQUARE = 256;
   public static final int DEFAULT_HEIGHT = ForgeBounds.HEIGHT.getIntBounds().length().intValue() + 1;
   public static final int DEFAULT_CAPACITY = 256 * DEFAULT_HEIGHT;

   protected ChunkBounds(IntBounds x, IntBounds y, IntBounds z) {
      super(x, y, z);
   }

   public static ChunkBounds from(ChunkPos pos, IntBounds y) {
      return new ChunkBounds(new IntBounds(pos.getXStart(), pos.getXEnd()), y, new IntBounds(pos.getZStart(), pos.getZEnd()));
   }

   public static ChunkBounds from(ChunkPos pos) {
      return from(pos, ForgeBounds.HEIGHT.getIntBounds());
   }

   public static ChunkBounds from(Chunk chunk, IntBounds y) {
      return from(chunk.getPos(), y);
   }

   public static ChunkBounds from(Chunk chunk) {
      return from(chunk.getPos());
   }

   public int getCapacity() {
      return 256 * this.getHeight();
   }
}
