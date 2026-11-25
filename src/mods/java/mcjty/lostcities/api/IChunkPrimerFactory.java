package mcjty.lostcities.api;

import net.minecraft.world.chunk.ChunkPrimer;

public interface IChunkPrimerFactory {
   void fillChunk(int var1, int var2, ChunkPrimer var3);

   int getHeight(int var1, int var2, int var3, int var4);
}
