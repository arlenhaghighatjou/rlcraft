package hunternif.mc.atlas.core;

import net.minecraft.world.chunk.Chunk;

interface IBiomeDetector {
   int NOT_FOUND = -1;

   int getBiomeID(Chunk var1);
}
