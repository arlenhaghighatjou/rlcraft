package mcjty.lostcities.dimensions.world.driver;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

public class SafeDriver implements IPrimerDriver {
   private ChunkPrimer primer;
   private int currentX;
   private int currentY;
   private int currentZ;

   public SafeDriver() {
   }

   public void setPrimer(ChunkPrimer primer) {
      this.primer = primer;
   }

   public ChunkPrimer getPrimer() {
      return this.primer;
   }

   public IPrimerDriver current(int x, int y, int z) {
      this.currentX = x;
      this.currentY = y;
      this.currentZ = z;
      return this;
   }

   public IPrimerDriver current(IIndex index) {
      SafeDriver.Index i = (SafeDriver.Index)index;
      this.currentX = i.x;
      this.currentY = i.y;
      this.currentZ = i.z;
      return this;
   }

   public IIndex getCurrent() {
      return new SafeDriver.Index(this.currentX, this.currentY, this.currentZ);
   }

   public void incY() {
      ++this.currentY;
   }

   public void incY(int amount) {
      this.currentY += amount;
   }

   public void decY() {
      --this.currentY;
   }

   public void incX() {
      ++this.currentX;
   }

   public void incZ() {
      ++this.currentZ;
   }

   public int getX() {
      return this.currentX;
   }

   public int getY() {
      return this.currentY;
   }

   public int getZ() {
      return this.currentZ;
   }

   public void setBlockRange(int x, int y, int z, int y2, char c) {
      for(IBlockState state = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(c); y < y2; ++y) {
         this.primer.setBlockState(x, y, z, state);
      }

   }

   public void setBlockRangeSafe(int x, int y, int z, int y2, char c) {
      for(IBlockState state = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(c); y < y2; ++y) {
         this.primer.setBlockState(x, y, z, state);
      }

   }

   public IPrimerDriver block(char c) {
      IBlockState state = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(c);
      this.primer.setBlockState(this.currentX, this.currentY, this.currentZ, state);
      return this;
   }

   public IPrimerDriver block(IBlockState c) {
      this.primer.setBlockState(this.currentX, this.currentY, this.currentZ, c);
      return this;
   }

   public IPrimerDriver add(char c) {
      IBlockState state = (IBlockState)Block.BLOCK_STATE_IDS.getByValue(c);
      this.primer.setBlockState(this.currentX, this.currentY++, this.currentZ, state);
      return this;
   }

   public char getBlock() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX, this.currentY, this.currentZ));
   }

   public char getBlockDown() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX, this.currentY - 1, this.currentZ));
   }

   public char getBlockEast() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX + 1, this.currentY, this.currentZ));
   }

   public char getBlockWest() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX - 1, this.currentY, this.currentZ));
   }

   public char getBlockSouth() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX, this.currentY, this.currentZ + 1));
   }

   public char getBlockNorth() {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(this.currentX, this.currentY, this.currentZ - 1));
   }

   public char getBlock(int x, int y, int z) {
      return (char)Block.BLOCK_STATE_IDS.get(this.primer.getBlockState(x, y, z));
   }

   public IIndex getIndex(int x, int y, int z) {
      return new SafeDriver.Index(x, y, z);
   }

   public IPrimerDriver copy() {
      SafeDriver driver = new SafeDriver();
      driver.currentX = this.currentX;
      driver.currentY = this.currentY;
      driver.currentZ = this.currentZ;
      driver.primer = this.primer;
      return driver;
   }

   private class Index implements IIndex {
      private final int x;
      private final int y;
      private final int z;

      Index(int x, int y, int z) {
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            SafeDriver.Index index = (SafeDriver.Index)o;
            return this.x == index.x && this.y == index.y && this.z == index.z;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.x, this.y, this.z});
      }
   }
}
