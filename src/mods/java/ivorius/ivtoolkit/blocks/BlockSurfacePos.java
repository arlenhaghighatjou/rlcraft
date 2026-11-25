package ivorius.ivtoolkit.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class BlockSurfacePos {
   public static final BlockSurfacePos ORIGIN = new BlockSurfacePos(0, 0);
   public final int x;
   public final int z;

   public BlockSurfacePos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public static BlockSurfacePos from(ChunkPos chunkCoords, int x, int z) {
      return new BlockSurfacePos(chunkCoords.x << 4 + x, chunkCoords.z << 4 + z);
   }

   public static BlockSurfacePos from(BlockPos pos) {
      return new BlockSurfacePos(pos.getX(), pos.getZ());
   }

   public ChunkPos chunkCoord() {
      return new ChunkPos(this.x >> 4, this.z >> 4);
   }

   public BlockPos blockPos(int y) {
      return new BlockPos(this.x, y, this.z);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public static long toLong(int x, int z) {
      return (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BlockSurfacePos that = (BlockSurfacePos)o;
         if (this.x != that.x) {
            return false;
         } else {
            return this.z == that.z;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.x;
      result = 31 * result + this.z;
      return result;
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockSurfacePos add(int x, int z) {
      return new BlockSurfacePos(this.x + x, this.z + z);
   }

   public BlockSurfacePos subtract(int x, int z) {
      return new BlockSurfacePos(this.x - x, this.z - z);
   }
}
