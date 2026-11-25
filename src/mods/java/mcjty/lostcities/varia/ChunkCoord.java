package mcjty.lostcities.varia;

import mcjty.lostcities.dimensions.world.lost.Orientation;

public class ChunkCoord {
   private final int dimension;
   private final int chunkX;
   private final int chunkZ;

   public ChunkCoord(int dimension, int chunkX, int chunkZ) {
      this.dimension = dimension;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
   }

   public int getDimension() {
      return this.dimension;
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public ChunkCoord lower(Orientation o) {
      switch(o) {
      case X:
         return new ChunkCoord(this.dimension, this.chunkX - 1, this.chunkZ);
      case Z:
         return new ChunkCoord(this.dimension, this.chunkX, this.chunkZ - 1);
      default:
         throw new IllegalArgumentException("Cannot happen!");
      }
   }

   public ChunkCoord higher(Orientation o) {
      switch(o) {
      case X:
         return new ChunkCoord(this.dimension, this.chunkX + 1, this.chunkZ);
      case Z:
         return new ChunkCoord(this.dimension, this.chunkX, this.chunkZ + 1);
      default:
         throw new IllegalArgumentException("Cannot happen!");
      }
   }

   public int getCoord(Orientation o) {
      switch(o) {
      case X:
         return this.chunkX;
      case Z:
         return this.chunkZ;
      default:
         throw new IllegalArgumentException("Cannot happen!");
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ChunkCoord that = (ChunkCoord)o;
         if (this.dimension != that.dimension) {
            return false;
         } else if (this.chunkX != that.chunkX) {
            return false;
         } else {
            return this.chunkZ == that.chunkZ;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.dimension;
      result = 31 * result + this.chunkX;
      result = 31 * result + this.chunkZ;
      return result;
   }

   public String toString() {
      return "ChunkCoord{dimension=" + this.dimension + ", chunkX=" + this.chunkX + ", chunkZ=" + this.chunkZ + '}';
   }
}
