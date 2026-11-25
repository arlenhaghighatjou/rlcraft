package me.jellysquid.mods.phosphor.mod.world.lighting;

import java.util.concurrent.locks.ReentrantLock;
import me.jellysquid.mods.phosphor.api.IChunkLighting;
import me.jellysquid.mods.phosphor.api.ILightingEngine;
import me.jellysquid.mods.phosphor.mixins.plugins.LightingEnginePlugin;
import me.jellysquid.mods.phosphor.mod.PhosphorMod;
import me.jellysquid.mods.phosphor.mod.collections.PooledLongQueue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LightingEngine implements ILightingEngine {
   private static final int MAX_SCHEDULED_COUNT = 4194304;
   private static final int MAX_LIGHT = 15;
   private final Thread ownedThread = Thread.currentThread();
   private final World world;
   private final Profiler profiler;
   private final PooledLongQueue[] queuedLightUpdates = new PooledLongQueue[EnumSkyBlock.values().length];
   private final PooledLongQueue[] queuedDarkenings = new PooledLongQueue[16];
   private final PooledLongQueue[] queuedBrightenings = new PooledLongQueue[16];
   private final PooledLongQueue initialBrightenings;
   private final PooledLongQueue initialDarkenings;
   private boolean updating = false;
   private static final int lX = 26;
   private static final int lY = 8;
   private static final int lZ = 26;
   private static final int lL = 4;
   private static final int sZ = 0;
   private static final int sX = 26;
   private static final int sY = 52;
   private static final int sL = 60;
   private static final long mX = 67108863L;
   private static final long mY = 255L;
   private static final long mZ = 67108863L;
   private static final long mL = 15L;
   private static final long mPos = 1152921504606846975L;
   private static final long yCheck = 1152921504606846976L;
   private static final long[] neighborShifts = new long[6];
   private static final long mChunk = 4503598620737520L;
   private final MutableBlockPos curPos = new MutableBlockPos();
   private Chunk curChunk;
   private long curChunkIdentifier;
   private long curData;
   private boolean isNeighborDataValid = false;
   private final LightingEngine.NeighborInfo[] neighborInfos = new LightingEngine.NeighborInfo[6];
   private PooledLongQueue.LongQueueIterator queueIt;
   private final ReentrantLock lock = new ReentrantLock();
   private static int ITEMS_PROCESSED;
   private static int CHUNKS_FETCHED;

   public LightingEngine(World world) {
      this.world = world;
      this.profiler = world.profiler;
      PooledLongQueue.Pool pool = new PooledLongQueue.Pool();
      this.initialBrightenings = new PooledLongQueue(pool);
      this.initialDarkenings = new PooledLongQueue(pool);

      for(int i = 0; i < EnumSkyBlock.values().length; ++i) {
         this.queuedLightUpdates[i] = new PooledLongQueue(pool);
      }

      for(int i = 0; i < this.queuedDarkenings.length; ++i) {
         this.queuedDarkenings[i] = new PooledLongQueue(pool);
      }

      for(int i = 0; i < this.queuedBrightenings.length; ++i) {
         this.queuedBrightenings[i] = new PooledLongQueue(pool);
      }

      for(int i = 0; i < this.neighborInfos.length; ++i) {
         this.neighborInfos[i] = new LightingEngine.NeighborInfo();
      }

   }

   public void scheduleLightUpdate(EnumSkyBlock lightType, BlockPos pos) {
      this.acquireLock();

      try {
         this.scheduleLightUpdate(lightType, encodeWorldCoord(pos));
      } finally {
         this.releaseLock();
      }

   }

   private void scheduleLightUpdate(EnumSkyBlock lightType, long pos) {
      PooledLongQueue queue = this.queuedLightUpdates[lightType.ordinal()];
      queue.add(pos);
      if (queue.size() >= 4194304) {
         this.processLightUpdatesForType(lightType);
      }

   }

   public void processLightUpdates() {
      this.processLightUpdatesForType(EnumSkyBlock.SKY);
      this.processLightUpdatesForType(EnumSkyBlock.BLOCK);
   }

   public void processLightUpdatesForType(EnumSkyBlock lightType) {
      if (!this.world.isRemote || this.isCallingFromMainThread()) {
         PooledLongQueue queue = this.queuedLightUpdates[lightType.ordinal()];
         if (!queue.isEmpty()) {
            this.acquireLock();

            try {
               this.processLightUpdatesForTypeInner(lightType, queue);
            } finally {
               this.releaseLock();
            }

         }
      }
   }

   @SideOnly(Side.CLIENT)
   private boolean isCallingFromMainThread() {
      return Minecraft.getMinecraft().isCallingFromMinecraftThread();
   }

   private void acquireLock() {
      if (!this.lock.tryLock()) {
         if (LightingEnginePlugin.ENABLE_ILLEGAL_THREAD_ACCESS_WARNINGS) {
            Thread current = Thread.currentThread();
            if (current != this.ownedThread) {
               IllegalAccessException e = new IllegalAccessException(String.format("World is owned by '%s' (ID: %s), but was accessed from thread '%s' (ID: %s)", this.ownedThread.getName(), this.ownedThread.getId(), current.getName(), current.getId()));
               PhosphorMod.LOGGER.warn("Something (likely another mod) has attempted to modify the world's state from the wrong thread!\nThis is *bad practice* and can cause severe issues in your game. Phosphor has done as best as it can to mitigate this violation, but it may negatively impact performance or introduce stalls.\nIn a future release, this violation may result in a hard crash instead of the current soft warning. You should report this issue to our issue tracker with the following stacktrace information.\n(If you are aware you have misbehaving mods and cannot resolve this issue, you can safely disable this warning by setting `enable_illegal_thread_access_warnings` to `false` in Phosphor's configuration file for the time being.)", e);
            }
         }

         this.lock.lock();
      }

   }

   private void releaseLock() {
      this.lock.unlock();
   }

   private void processLightUpdatesForTypeInner(EnumSkyBlock lightType, PooledLongQueue queue) {
      if (this.updating) {
         throw new IllegalStateException("Already processing updates!");
      } else {
         this.updating = true;
         this.curChunkIdentifier = -1L;
         this.profiler.startSection("lighting");
         this.profiler.startSection("checking");
         this.queueIt = queue.iterator();

         while(this.nextItem()) {
            if (this.curChunk != null) {
               int oldLight = this.getCursorCachedLight(lightType);
               int newLight = this.calculateNewLightFromCursor(lightType);
               if (oldLight < newLight) {
                  this.initialBrightenings.add((long)newLight << 60 | this.curData);
               } else if (oldLight > newLight) {
                  this.initialDarkenings.add(this.curData);
               }
            }
         }

         this.queueIt = this.initialBrightenings.iterator();

         while(this.nextItem()) {
            int newLight = (int)(this.curData >> 60 & 15L);
            if (newLight > this.getCursorCachedLight(lightType)) {
               this.enqueueBrightening(this.curPos, this.curData & 1152921504606846975L, newLight, this.curChunk, lightType);
            }
         }

         this.queueIt = this.initialDarkenings.iterator();

         while(this.nextItem()) {
            int oldLight = this.getCursorCachedLight(lightType);
            if (oldLight != 0) {
               this.enqueueDarkening(this.curPos, this.curData, oldLight, this.curChunk, lightType);
            }
         }

         this.profiler.endSection();

         for(int curLight = 15; curLight >= 0; --curLight) {
            this.profiler.startSection("darkening");
            this.queueIt = this.queuedDarkenings[curLight].iterator();

            while(this.nextItem()) {
               if (this.getCursorCachedLight(lightType) < curLight) {
                  IBlockState state = LightingEngineHelpers.posToState(this.curPos, this.curChunk);
                  int luminosity = this.getCursorLuminosity(state, lightType);
                  int opacity;
                  if (luminosity >= 14) {
                     opacity = 1;
                  } else {
                     opacity = this.getPosOpacity(this.curPos, state);
                  }

                  if (this.calculateNewLightFromCursor(luminosity, opacity, lightType) < curLight) {
                     int newLight = luminosity;
                     this.fetchNeighborDataFromCursor(lightType);

                     for(LightingEngine.NeighborInfo info : this.neighborInfos) {
                        Chunk nChunk = info.chunk;
                        if (nChunk != null) {
                           int nLight = info.light;
                           if (nLight != 0) {
                              MutableBlockPos nPos = info.pos;
                              if (curLight - this.getPosOpacity(nPos, LightingEngineHelpers.posToState(nPos, info.section)) >= nLight) {
                                 this.enqueueDarkening(nPos, info.key, nLight, nChunk, lightType);
                              } else {
                                 newLight = Math.max(newLight, nLight - opacity);
                              }
                           }
                        }
                     }

                     this.enqueueBrighteningFromCursor(newLight, lightType);
                  } else {
                     this.enqueueBrighteningFromCursor(curLight, lightType);
                  }
               }
            }

            this.profiler.endStartSection("brightening");
            this.queueIt = this.queuedBrightenings[curLight].iterator();

            while(this.nextItem()) {
               int oldLight = this.getCursorCachedLight(lightType);
               if (oldLight == curLight) {
                  this.world.notifyLightSet(this.curPos);
                  if (curLight > 1) {
                     this.spreadLightFromCursor(curLight, lightType);
                  }
               }
            }

            this.profiler.endSection();
         }

         this.profiler.endSection();
         this.updating = false;
      }
   }

   private void fetchNeighborDataFromCursor(EnumSkyBlock lightType) {
      if (!this.isNeighborDataValid) {
         this.isNeighborDataValid = true;

         for(int i = 0; i < this.neighborInfos.length; ++i) {
            LightingEngine.NeighborInfo info = this.neighborInfos[i];
            long nLongPos = info.key = this.curData + neighborShifts[i];
            if ((nLongPos & 1152921504606846976L) != 0L) {
               info.chunk = null;
               info.section = null;
            } else {
               MutableBlockPos nPos = decodeWorldCoord(info.pos, nLongPos);
               Chunk nChunk;
               if ((nLongPos & 4503598620737520L) == this.curChunkIdentifier) {
                  nChunk = info.chunk = this.curChunk;
               } else {
                  nChunk = info.chunk = this.getChunk(nPos);
               }

               if (nChunk != null) {
                  ExtendedBlockStorage nSection = nChunk.getBlockStorageArray()[nPos.getY() >> 4];
                  info.light = getCachedLightFor(nChunk, nSection, nPos, lightType);
                  info.section = nSection;
               }
            }
         }

      }
   }

   private static int getCachedLightFor(Chunk chunk, ExtendedBlockStorage storage, BlockPos pos, EnumSkyBlock type) {
      int i = pos.getX() & 15;
      int j = pos.getY();
      int k = pos.getZ() & 15;
      if (storage == Chunk.NULL_BLOCK_STORAGE) {
         return type == EnumSkyBlock.SKY && chunk.canSeeSky(pos) ? type.defaultLightValue : 0;
      } else if (type == EnumSkyBlock.SKY) {
         return !chunk.getWorld().provider.hasSkyLight() ? 0 : storage.getSkyLight(i, j & 15, k);
      } else {
         return type == EnumSkyBlock.BLOCK ? storage.getBlockLight(i, j & 15, k) : type.defaultLightValue;
      }
   }

   private int calculateNewLightFromCursor(EnumSkyBlock lightType) {
      IBlockState state = LightingEngineHelpers.posToState(this.curPos, this.curChunk);
      int luminosity = this.getCursorLuminosity(state, lightType);
      int opacity;
      if (luminosity >= 14) {
         opacity = 1;
      } else {
         opacity = this.getPosOpacity(this.curPos, state);
      }

      return this.calculateNewLightFromCursor(luminosity, opacity, lightType);
   }

   private int calculateNewLightFromCursor(int luminosity, int opacity, EnumSkyBlock lightType) {
      if (luminosity >= 15 - opacity) {
         return luminosity;
      } else {
         int newLight = luminosity;
         this.fetchNeighborDataFromCursor(lightType);

         for(LightingEngine.NeighborInfo info : this.neighborInfos) {
            if (info.chunk != null) {
               int nLight = info.light;
               newLight = Math.max(nLight - opacity, newLight);
            }
         }

         return newLight;
      }
   }

   private void spreadLightFromCursor(int curLight, EnumSkyBlock lightType) {
      this.fetchNeighborDataFromCursor(lightType);

      for(LightingEngine.NeighborInfo info : this.neighborInfos) {
         Chunk nChunk = info.chunk;
         if (nChunk != null) {
            int newLight = curLight - this.getPosOpacity(info.pos, LightingEngineHelpers.posToState(info.pos, info.section));
            if (newLight > info.light) {
               this.enqueueBrightening(info.pos, info.key, newLight, nChunk, lightType);
            }
         }
      }

   }

   private void enqueueBrighteningFromCursor(int newLight, EnumSkyBlock lightType) {
      this.enqueueBrightening(this.curPos, this.curData, newLight, this.curChunk, lightType);
   }

   private void enqueueBrightening(BlockPos pos, long longPos, int newLight, Chunk chunk, EnumSkyBlock lightType) {
      this.queuedBrightenings[newLight].add(longPos);
      chunk.setLightFor(lightType, pos, newLight);
   }

   private void enqueueDarkening(BlockPos pos, long longPos, int oldLight, Chunk chunk, EnumSkyBlock lightType) {
      this.queuedDarkenings[oldLight].add(longPos);
      chunk.setLightFor(lightType, pos, 0);
   }

   private static MutableBlockPos decodeWorldCoord(MutableBlockPos pos, long longPos) {
      int posX = (int)(longPos >> 26 & 67108863L) - 33554432;
      int posY = (int)(longPos >> 52 & 255L);
      int posZ = (int)(longPos >> 0 & 67108863L) - 33554432;
      return pos.setPos(posX, posY, posZ);
   }

   private static long encodeWorldCoord(BlockPos pos) {
      return encodeWorldCoord((long)pos.getX(), (long)pos.getY(), (long)pos.getZ());
   }

   private static long encodeWorldCoord(long x, long y, long z) {
      return y << 52 | x + 33554432L << 26 | z + 33554432L << 0;
   }

   private boolean nextItem() {
      if (!this.queueIt.hasNext()) {
         this.queueIt.finish();
         this.queueIt = null;
         return false;
      } else {
         this.curData = this.queueIt.next();
         this.isNeighborDataValid = false;
         decodeWorldCoord(this.curPos, this.curData);
         long chunkIdentifier = this.curData & 4503598620737520L;
         if (this.curChunkIdentifier != chunkIdentifier) {
            this.curChunk = this.getChunk(this.curPos);
            this.curChunkIdentifier = chunkIdentifier;
            ++CHUNKS_FETCHED;
         }

         ++ITEMS_PROCESSED;
         return true;
      }
   }

   private int getCursorCachedLight(EnumSkyBlock lightType) {
      return ((IChunkLighting)this.curChunk).getCachedLightFor(lightType, this.curPos);
   }

   private int getCursorLuminosity(IBlockState state, EnumSkyBlock lightType) {
      if (lightType == EnumSkyBlock.SKY) {
         return this.curChunk.canSeeSky(this.curPos) ? EnumSkyBlock.SKY.defaultLightValue : 0;
      } else {
         return MathHelper.clamp(state.getLightValue(this.world, this.curPos), 0, 15);
      }
   }

   private int getPosOpacity(BlockPos pos, IBlockState state) {
      return MathHelper.clamp(state.getLightOpacity(this.world, pos), 1, 15);
   }

   private Chunk getChunk(BlockPos pos) {
      return this.world.getChunkProvider().getLoadedChunk(pos.getX() >> 4, pos.getZ() >> 4);
   }

   static {
      for(int i = 0; i < 6; ++i) {
         Vec3i offset = EnumFacing.VALUES[i].getDirectionVec();
         neighborShifts[i] = (long)offset.getY() << 52 | (long)offset.getX() << 26 | (long)offset.getZ() << 0;
      }

      ITEMS_PROCESSED = 0;
      CHUNKS_FETCHED = 0;
   }

   private static class NeighborInfo {
      Chunk chunk;
      ExtendedBlockStorage section;
      int light;
      long key;
      final MutableBlockPos pos;

      private NeighborInfo() {
         this.pos = new MutableBlockPos();
      }
   }
}
