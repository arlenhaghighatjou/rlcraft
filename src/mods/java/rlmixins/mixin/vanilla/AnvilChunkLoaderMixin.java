package rlmixins.mixin.vanilla;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AnvilChunkLoader.class})
public abstract class AnvilChunkLoaderMixin {
   @Shadow
   @Final
   public File chunkSaveLocation;
   @Shadow
   @Final
   private static Logger LOGGER;
   @Shadow
   private boolean flushing;
   @Shadow
   private final Map<ChunkPos, NBTTagCompound> chunksToSave = new HashMap();
   private final Map<ChunkPos, NBTTagCompound> chunksInWrite = new HashMap();

   public AnvilChunkLoaderMixin() {
   }

   @Shadow
   private void writeChunkData(ChunkPos pos, NBTTagCompound compound) throws IOException {
   }

   private synchronized void queueChunkToSave(ChunkPos pos, NBTTagCompound data) {
      this.chunksToSave.put(pos, data);
   }

   private synchronized Entry<ChunkPos, NBTTagCompound> fetchChunkToWrite() {
      if (this.chunksToSave.isEmpty()) {
         return null;
      } else {
         Set<Entry<ChunkPos, NBTTagCompound>> entrySet = this.chunksToSave.entrySet();
         Iterator<Entry<ChunkPos, NBTTagCompound>> iter = entrySet.iterator();
         Entry<ChunkPos, NBTTagCompound> entry = (Entry)iter.next();
         iter.remove();
         this.chunksInWrite.put(entry.getKey(), entry.getValue());
         return entry;
      }
   }

   private synchronized void retireChunkToWrite(ChunkPos pos, NBTTagCompound data) {
      this.chunksInWrite.remove(pos);
   }

   private synchronized NBTTagCompound reloadChunkFromSaveQueues(ChunkPos pos) {
      NBTTagCompound data = (NBTTagCompound)this.chunksToSave.get(pos);
      return data != null ? data : (NBTTagCompound)this.chunksInWrite.get(pos);
   }

   private synchronized boolean chunkExistInSaveQueues(ChunkPos pos) {
      return this.chunksToSave.containsKey(pos) || this.chunksInWrite.containsKey(pos);
   }

   @Redirect(
      method = {"loadChunk__Async"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
   ordinal = 0,
   remap = false
),
      remap = false
   )
   private Object pullChunkToSave(Map lChunksToSave, Object lpos) {
      return this.reloadChunkFromSaveQueues((ChunkPos)lpos);
   }

   @Inject(
      method = {"isChunkGeneratedAt"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void overrideIsChunkGeneratedAt(int x, int z, CallbackInfoReturnable<Boolean> ci) {
      ChunkPos chunkpos = new ChunkPos(x, z);
      boolean exists = this.chunkExistInSaveQueues(chunkpos);
      ci.setReturnValue(Boolean.valueOf(exists || RegionFileCache.chunkExists(this.chunkSaveLocation, x, z)));
   }

   @Redirect(
      method = {"addChunkToPending"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/Set;contains(Ljava/lang/Object;)Z",
   remap = false
)
   )
   private boolean overrideAddChunkToPending(Set lChunksBeingSaved, Object lPos, ChunkPos pos, NBTTagCompound compound) {
      this.queueChunkToSave(pos, compound);
      return true;
   }

   @Inject(
      method = {"writeNextIO"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void overrideWriteNextIO(CallbackInfoReturnable<Boolean> ci) {
      Entry<ChunkPos, NBTTagCompound> entry = this.fetchChunkToWrite();
      if (entry == null) {
         if (this.flushing) {
            LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
         }

         ci.setReturnValue(Boolean.valueOf(false));
      } else {
         ChunkPos chunkpos = (ChunkPos)entry.getKey();
         NBTTagCompound nbttagcompound = (NBTTagCompound)entry.getValue();

         try {
            this.writeChunkData(chunkpos, nbttagcompound);
         } catch (Exception var6) {
            LOGGER.error("Failed to save chunk", var6);
         }

         this.retireChunkToWrite(chunkpos, nbttagcompound);
         ci.setReturnValue(Boolean.valueOf(true));
      }
   }
}
