package com.creativemd.creativecore.common.world;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;

public class SubWorldServer extends SubWorld {
   protected SubWorldServer(WorldServer world) {
      super(world);
      this.chunkProvider = this.createChunkProvider();
   }

   protected IChunkProvider createChunkProvider() {
      try {
         if (Class.forName("org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer") != null) {
            return new ChunkProviderFakeServerSponge(this, this.saveHandler.getChunkLoader(this.provider), new ChunkGeneratorFake(this));
         }
      } catch (ClassNotFoundException var2) {
         ;
      }

      return new ChunkProviderFakeServer(this, this.saveHandler.getChunkLoader(this.provider), new ChunkGeneratorFake(this));
   }
}
