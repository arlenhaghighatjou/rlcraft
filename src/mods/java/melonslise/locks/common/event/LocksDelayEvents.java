package melonslise.locks.common.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import melonslise.locks.common.capability.ILockableWorldGenHandler;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class LocksDelayEvents {
   private static Map<Integer, Map<ChunkPos, LocksDelayEvents.ChunkDelay>> chunksToPoll = new ConcurrentHashMap();
   public static int delay = 20;

   private LocksDelayEvents() {
   }

   public static void register(EventBus bus) {
      bus.register(Watch.class, LocksDelayEvents::onChunkWatch);
      bus.register(WorldTickEvent.class, LocksDelayEvents::onWorldTick);
      bus.register(Load.class, LocksDelayEvents::onWorldLoad);
      bus.register(Unload.class, LocksDelayEvents::onWorldUnload);
   }

   public static void onChunkWatch(Watch event) {
      tryAddChunkToPoll(event.getChunkInstance());
   }

   public static void onWorldTick(WorldTickEvent event) {
      if (event.phase != Phase.START && !event.world.isRemote) {
         pollChunksInWorld(event.world);
      }
   }

   public static void onWorldLoad(Load event) {
      if (!event.getWorld().isRemote) {
         clearDimensionInPollMap(event.getWorld());
      }
   }

   public static void onWorldUnload(Unload event) {
      if (!event.getWorld().isRemote) {
         clearDimensionInPollMap(event.getWorld());
      }
   }

   private static void tryAddChunkToPoll(Chunk c) {
      int dimension = c.getWorld().provider.getDimension();
      Map<ChunkPos, LocksDelayEvents.ChunkDelay> dimChunkMap = getChunksToPollMap(dimension);
      if (!dimChunkMap.containsKey(c.getPos())) {
         dimChunkMap.put(c.getPos(), new LocksDelayEvents.ChunkDelay(delay));
      }

   }

   private static void clearDimensionInPollMap(World world) {
      int dimension = world.provider.getDimension();
      Map<ChunkPos, LocksDelayEvents.ChunkDelay> dimChunkMap = getChunksToPollMap(dimension);
      dimChunkMap.clear();
   }

   private static void pollChunksInWorld(World world) {
      int dimension = world.provider.getDimension();
      Map<ChunkPos, LocksDelayEvents.ChunkDelay> dimChunkMap = getChunksToPollMap(dimension);
      Iterator<Entry<ChunkPos, LocksDelayEvents.ChunkDelay>> it = dimChunkMap.entrySet().iterator();

      while(it.hasNext()) {
         Entry<ChunkPos, LocksDelayEvents.ChunkDelay> entry = (Entry)it.next();
         if (((LocksDelayEvents.ChunkDelay)entry.getValue()).tick()) {
            ChunkPos cpos = (ChunkPos)entry.getKey();
            if (LocksUtil.hasChunk(world, cpos.x, cpos.z)) {
               if (LocksUtil.hasChunk(world, cpos.x + 1, cpos.z) && LocksUtil.hasChunk(world, cpos.x - 1, cpos.z) && LocksUtil.hasChunk(world, cpos.x, cpos.z - 1) && LocksUtil.hasChunk(world, cpos.x, cpos.z + 1)) {
                  ((ILockableWorldGenHandler)world.getChunkFromChunkCoords(cpos.x, cpos.z).getCapability(LocksCapabilities.LOCKABLE_WORLDGEN_HANDLER, (EnumFacing)null)).tryGeneratingLocks();
                  it.remove();
               } else {
                  ((LocksDelayEvents.ChunkDelay)entry.getValue()).reset();
               }
            } else {
               it.remove();
            }
         }
      }

   }

   @Nonnull
   private static Map<ChunkPos, LocksDelayEvents.ChunkDelay> getChunksToPollMap(int dimension) {
      Map<ChunkPos, LocksDelayEvents.ChunkDelay> dimChunkMap = (Map)chunksToPoll.get(Integer.valueOf(dimension));
      if (dimChunkMap == null) {
         dimChunkMap = new ConcurrentHashMap();
         chunksToPoll.put(Integer.valueOf(dimension), dimChunkMap);
      }

      return dimChunkMap;
   }

   private static class ChunkDelay {
      private int delay;

      public ChunkDelay(int delay) {
         this.delay = delay;
      }

      public boolean tick() {
         --this.delay;
         return this.delay <= 0;
      }

      public void reset() {
         this.delay = LocksDelayEvents.delay;
      }
   }
}
