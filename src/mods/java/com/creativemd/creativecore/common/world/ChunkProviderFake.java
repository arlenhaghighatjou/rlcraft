package com.creativemd.creativecore.common.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderFake implements IChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Set<Long> droppedChunksSet = Sets.newHashSet();
   public final IChunkGenerator chunkGenerator;
   public final IChunkLoader chunkLoader;
   public final Long2ObjectMap<Chunk> id2ChunkMap = new Long2ObjectOpenHashMap(4);
   public final CreativeWorld worldObj;
   private Set<Long> loadingChunks = Sets.newHashSet();
   private static Field loadedChunk = ReflectionHelper.findField(Chunk.class, new String[]{"loaded", "loaded"});

   public ChunkProviderFake(CreativeWorld worldObjIn, IChunkLoader chunkLoaderIn, IChunkGenerator chunkGeneratorIn) {
      this.worldObj = worldObjIn;
      this.chunkLoader = chunkLoaderIn;
      this.chunkGenerator = chunkGeneratorIn;
   }

   public Collection<Chunk> getLoadedChunks() {
      return this.id2ChunkMap.values();
   }

   public void unload(Chunk chunkIn) {
      if (this.worldObj.provider.canDropChunk(chunkIn.x, chunkIn.z)) {
         this.droppedChunksSet.add(Long.valueOf(ChunkPos.asLong(chunkIn.x, chunkIn.z)));
         if (this.markLoaded()) {
            chunkIn.markLoaded(false);
         }
      }

   }

   public boolean markLoaded() {
      return true;
   }

   public void unloadAllChunks() {
      ObjectIterator var1 = this.id2ChunkMap.values().iterator();

      while(var1.hasNext()) {
         Chunk chunk = (Chunk)var1.next();
         this.unload(chunk);
      }

   }

   @Nullable
   public Chunk getLoadedChunk(int x, int z) {
      long i = ChunkPos.asLong(x, z);
      Chunk chunk = (Chunk)this.id2ChunkMap.get(i);
      if (this.worldObj.isRemote && chunk != null && this.markLoaded()) {
         ;
      }

      return chunk;
   }

   @Nullable
   public Chunk loadChunk(int x, int z) {
      return this.loadChunk(x, z, (Runnable)null);
   }

   @Nullable
   public Chunk loadChunk(int x, int z, Runnable runnable) {
      Chunk chunk = this.getLoadedChunk(x, z);
      if (chunk == null) {
         ChunkPos.asLong(x, z);
         chunk = this.loadChunkFromFile(x, z);
         this.id2ChunkMap.put(ChunkPos.asLong(x, z), chunk);
      }

      return chunk;
   }

   public Chunk provideChunk(int x, int z) {
      Chunk chunk = this.loadChunk(x, z);
      if (chunk == null) {
         long i = ChunkPos.asLong(x, z);

         try {
            chunk = this.chunkGenerator.generateChunk(x, z);
         } catch (Throwable var9) {
            CrashReport crashreport = CrashReport.makeCrashReport(var9, "Exception generating new chunk");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
            crashreportcategory.addCrashSection("Location", String.format("%d,%d", x, z));
            crashreportcategory.addCrashSection("Position hash", Long.valueOf(i));
            crashreportcategory.addCrashSection("Generator", this.chunkGenerator);
            throw new ReportedException(crashreport);
         }

         this.id2ChunkMap.put(i, chunk);
         chunk.onLoad();
         chunk.populate(this, this.chunkGenerator);
      }

      return chunk;
   }

   @Nullable
   private Chunk loadChunkFromFile(int x, int z) {
      try {
         Chunk chunk = this.chunkLoader.loadChunk(this.worldObj, x, z);
         if (chunk != null) {
            chunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
         }

         loadedChunk.setBoolean(chunk, true);
         return chunk;
      } catch (Exception var4) {
         LOGGER.error("Couldn't load chunk", var4);
         return null;
      }
   }

   private void saveChunkExtraData(Chunk chunkIn) {
      try {
         this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
      } catch (Exception var3) {
         LOGGER.error("Couldn't save entities", var3);
      }

   }

   private void saveChunkData(Chunk chunkIn) {
      try {
         chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
         this.chunkLoader.saveChunk(this.worldObj, chunkIn);
      } catch (IOException var3) {
         LOGGER.error("Couldn't save chunk", var3);
      } catch (MinecraftException var4) {
         LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", var4);
      }

   }

   public boolean saveChunks(boolean p_186027_1_) {
      int i = 0;
      List<Chunk> list = Lists.newArrayList(this.id2ChunkMap.values());

      for(int j = 0; j < list.size(); ++j) {
         Chunk chunk = (Chunk)list.get(j);
         if (p_186027_1_) {
            this.saveChunkExtraData(chunk);
         }

         if (chunk.needsSaving(p_186027_1_)) {
            this.saveChunkData(chunk);
            chunk.setModified(false);
            ++i;
            if (i == 24 && !p_186027_1_) {
               return false;
            }
         }
      }

      return true;
   }

   public boolean unloadQueuedChunks() {
      return false;
   }

   public boolean canSave() {
      return false;
   }

   public String makeString() {
      return "ServerChunkCache: " + this.id2ChunkMap.size() + " Drop: " + this.droppedChunksSet.size();
   }

   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
   }

   @Nullable
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position, boolean p_180513_4_) {
      return this.chunkGenerator.getNearestStructurePos(worldIn, structureName, position, p_180513_4_);
   }

   public int getLoadedChunkCount() {
      return this.id2ChunkMap.size();
   }

   public boolean chunkExists(int x, int z) {
      return this.id2ChunkMap.containsKey(ChunkPos.asLong(x, z));
   }

   public boolean isChunkGeneratedAt(int p_191062_1_, int p_191062_2_) {
      return this.id2ChunkMap.containsKey(ChunkPos.asLong(p_191062_1_, p_191062_2_)) || this.chunkLoader.isChunkGeneratedAt(p_191062_1_, p_191062_2_);
   }

   public boolean tick() {
      return false;
   }
}
