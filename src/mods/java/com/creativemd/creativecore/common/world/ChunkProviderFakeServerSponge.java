package com.creativemd.creativecore.common.world;

import com.flowpowered.math.vector.Vector3i;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.Optional.Interface;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer;

@Interface(
   modid = "spongeforge",
   iface = "org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer"
)
public class ChunkProviderFakeServerSponge extends ChunkProviderFake implements IMixinChunkProviderServer {
   private static final Logger LOGGER = LogManager.getLogger();

   public ChunkProviderFakeServerSponge(CreativeWorld worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
      super(worldObjIn, chunkLoaderIn, chunkGeneratorIn);
   }

   public boolean tick() {
      return false;
   }

   public boolean canSave() {
      return false;
   }

   public boolean getForceChunkRequests() {
      return false;
   }

   public void setMaxChunkUnloads(int maxUnloads) {
   }

   public void setDenyChunkRequests(boolean flag) {
   }

   public void setForceChunkRequests(boolean flag) {
   }

   public Chunk getLoadedChunkWithoutMarkingActive(int x, int z) {
      return this.getLoadedChunk(x, z);
   }

   public long getChunkUnloadDelay() {
      return 0L;
   }

   public WorldServer getWorld() {
      return null;
   }

   public CompletableFuture<Boolean> doesChunkExistSync(Vector3i chunkCoords) {
      return null;
   }

   public void unloadChunkAndSave(Chunk chunk) {
   }
}
