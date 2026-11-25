package mcjty.lostcities.varia;

import mcjty.lostcities.dimensions.world.lost.Orientation;

public class BlockIndex {
   private int index;

   public BlockIndex(int index) {
      this.index = index;
   }

   public BlockIndex(int x, int y, int z) {
      this.index = x << 12 | (z << 8) + y;
   }

   public BlockIndex(int x, int y, int z, Orientation orientation) {
      switch(orientation) {
      case X:
         this.index = x << 12 | (z << 8) + y;
         break;
      case Z:
         this.index = z << 12 | (x << 8) + y;
      }

      throw new IllegalArgumentException("Cannot happen!");
   }

   public void incY() {
      ++this.index;
   }

   public int getIndex() {
      return this.index;
   }

   public int getIndex(Orientation o, int amount) {
      return this.index;
   }
}
