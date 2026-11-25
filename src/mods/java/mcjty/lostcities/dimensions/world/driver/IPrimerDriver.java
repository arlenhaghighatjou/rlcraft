package mcjty.lostcities.dimensions.world.driver;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.chunk.ChunkPrimer;

public interface IPrimerDriver {
   void setPrimer(ChunkPrimer var1);

   ChunkPrimer getPrimer();

   IPrimerDriver current(int var1, int var2, int var3);

   IPrimerDriver current(IIndex var1);

   IIndex getCurrent();

   void incY();

   void incY(int var1);

   void decY();

   void incX();

   void incZ();

   int getX();

   int getY();

   int getZ();

   IIndex getIndex(int var1, int var2, int var3);

   void setBlockRange(int var1, int var2, int var3, int var4, char var5);

   void setBlockRangeSafe(int var1, int var2, int var3, int var4, char var5);

   IPrimerDriver block(char var1);

   IPrimerDriver block(IBlockState var1);

   IPrimerDriver add(char var1);

   char getBlock();

   char getBlockDown();

   char getBlockEast();

   char getBlockWest();

   char getBlockSouth();

   char getBlockNorth();

   char getBlock(int var1, int var2, int var3);

   IPrimerDriver copy();
}
