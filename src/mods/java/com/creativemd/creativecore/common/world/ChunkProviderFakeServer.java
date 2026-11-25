package com.creativemd.creativecore.common.world;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Optional.Interface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.bridge.world.chunk.ChunkProviderBridge;

@Interface(
   modid = "spongeforge",
   iface = "org.spongepowered.common.bridge.world.chunk.ChunkProviderBridge"
)
public class ChunkProviderFakeServer extends ChunkProviderFake implements ChunkProviderBridge {
   private static final Logger LOGGER = LogManager.getLogger();

   public ChunkProviderFakeServer(CreativeWorld worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
      super(worldObjIn, chunkLoaderIn, chunkGeneratorIn);
   }

   public boolean tick() {
      return false;
   }

   public boolean canSave() {
      return false;
   }

   public void bridge$setMaxChunkUnloads(int maxUnloads) {
   }

   public Chunk bridge$getLoadedChunkWithoutMarkingActive(int x, int z) {
      return this.getLoadedChunk(x, z);
   }
}
