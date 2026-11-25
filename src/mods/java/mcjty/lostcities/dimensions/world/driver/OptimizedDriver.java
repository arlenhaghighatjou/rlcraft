package mcjty.lostcities.dimensions.world.driver;

import java.util.Arrays;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

public class OptimizedDriver implements IPrimerDriver {
   private ChunkPrimer primer;
   private int current;

   public OptimizedDriver() {
   }

   public void setPrimer(ChunkPrimer primer) {
      this.primer = primer;
   }

   public ChunkPrimer getPrimer() {
      return this.primer;
   }

   public IPrimerDriver current(int x, int y, int z) {
      this.current = getBlockIndex(x, y, z);
      return this;
   }

   public IPrimerDriver current(IIndex index) {
      this.current = ((OptimizedDriver.Index)index).index;
      return this;
   }

   public IIndex getCurrent() {
      return new OptimizedDriver.Index(this.current);
   }

   public void incY() {
      ++this.current;
   }

   public void incY(int amount) {
      this.current += amount;
   }

   public void decY() {
      --this.current;
   }

   public void incX() {
      this.current += 4096;
   }

   public void incZ() {
      this.current += 256;
   }

   public int getX() {
      return this.current >> 12 & 15;
   }

   public int getY() {
      return this.current & 255;
   }

   public int getZ() {
      return this.current >> 8 & 15;
   }

   public void setBlockRange(int x, int y, int z, int y2, char c) {
      int s = getBlockIndex(x, y, z);
      int e = s + y2 - y;
      Arrays.fill(this.primer.data, s, e, c);
   }

   public void setBlockRangeSafe(int x, int y, int z, int y2, char c) {
      if (y2 > y) {
         int s = getBlockIndex(x, y, z);
         int e = s + y2 - y;
         Arrays.fill(this.primer.data, s, e, c);
      }
   }

   public IPrimerDriver block(char c) {
      this.primer.data[this.current] = c;
      return this;
   }

   public IPrimerDriver block(IBlockState c) {
      this.primer.data[this.current] = (char)Block.BLOCK_STATE_IDS.get(c);
      return this;
   }

   public IPrimerDriver add(char c) {
      this.primer.data[this.current++] = c;
      return this;
   }

   public char getBlock() {
      return this.primer.data[this.current];
   }

   public char getBlockDown() {
      return this.primer.data[this.current - 1];
   }

   public char getBlockEast() {
      return this.primer.data[this.current + 4096];
   }

   public char getBlockWest() {
      return this.primer.data[this.current - 4096];
   }

   public char getBlockSouth() {
      return this.primer.data[this.current + 256];
   }

   public char getBlockNorth() {
      return this.primer.data[this.current - 256];
   }

   public char getBlock(int x, int y, int z) {
      return this.primer.data[getBlockIndex(x, y, z)];
   }

   public IIndex getIndex(int x, int y, int z) {
      return new OptimizedDriver.Index(getBlockIndex(x, y, z));
   }

   private static int getBlockIndex(int x, int y, int z) {
      return x << 12 | z << 8 | y;
   }

   public IPrimerDriver copy() {
      OptimizedDriver driver = new OptimizedDriver();
      driver.current = this.current;
      driver.primer = this.primer;
      return driver;
   }

   private class Index implements IIndex {
      private final int index;

      Index(int index) {
         this.index = index;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            OptimizedDriver.Index index1 = (OptimizedDriver.Index)o;
            return this.index == index1.index;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.index});
      }
   }
}
