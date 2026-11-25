package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.network.toclient.AddLockablePacket;
import melonslise.locks.common.network.toclient.RemoveLockablePacket;
import melonslise.locks.common.network.toclient.UpdateLockablePacket;
import melonslise.locks.common.util.BiIntFunction;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;

public class LockableHandler implements ILockableHandler {
   public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_handler");
   public final World world;
   public AtomicInteger lastId = new AtomicInteger();
   public Int2ObjectMap<Lockable> lockables = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap());
   public Int2ObjectMap<Lockable> emptyLockables = new Int2ObjectLinkedOpenHashMap();

   public LockableHandler(World world) {
      this.world = world;
   }

   public int nextId() {
      return this.lastId.incrementAndGet();
   }

   public Int2ObjectMap<Lockable> getLoaded() {
      return this.lockables;
   }

   public Int2ObjectMap<Lockable> getInChunk(BlockPos pos) {
      return LocksUtil.hasChunkAt(this.world, pos) ? ((ILockableStorage)this.world.getChunkFromBlockCoords(pos).getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).get() : this.emptyLockables;
   }

   public boolean add(Lockable lkb) {
      if (lkb.box.volume() > LocksConfig.SERVER.maxLockableVolume) {
         return false;
      } else {
         boolean emptyCollision = false;
         List<ILockableStorage> sts = lkb.box.<ILockableStorage>containedChunksTo((x, z) -> {
            if (!LocksUtil.hasChunk(this.world, x, z)) {
               return null;
            } else {
               ILockableStorage st = (ILockableStorage)this.world.getChunkFromChunkCoords(x, z).getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null);
               return st.get().values().stream().anyMatch((lkb1) -> {
                  return lkb1.box.intersects(lkb.box);
               }) ? null : st;
            }
         }, true);
         if (sts == null) {
            return false;
         } else {
            if (this.world.isRemote) {
               for(int a = 0; a < sts.size(); ++a) {
                  ILockableStorage st = (ILockableStorage)sts.get(a);
                  if (st instanceof LockableStorage && ((LockableStorage)st).chunk instanceof EmptyChunk) {
                     return false;
                  }
               }
            }

            for(int a = 0; a < sts.size(); ++a) {
               ((ILockableStorage)sts.get(a)).add(lkb);
            }

            this.lockables.put(lkb.networkID, lkb);
            lkb.addObserver(this);
            if (this.world.isRemote) {
               lkb.shake(10);
            } else {
               LocksUtil.sendToTrackingPlayers(lkb.box, new AddLockablePacket(lkb), this.world);
            }

            return true;
         }
      }
   }

   public boolean remove(int id) {
      Lockable lkb = (Lockable)this.lockables.get(id);
      if (lkb == this.lockables.defaultReturnValue()) {
         return false;
      } else {
         List<Chunk> chs = lkb.box.<Chunk>containedChunksTo((x, z) -> {
            return LocksUtil.hasChunk(this.world, x, z) ? this.world.getChunkFromChunkCoords(x, z) : null;
         }, true);

         for(int a = 0; a < chs.size(); ++a) {
            ((ILockableStorage)((Chunk)chs.get(a)).getCapability(LocksCapabilities.LOCKABLE_STORAGE, (EnumFacing)null)).remove(id);
         }

         this.lockables.remove(id);
         lkb.deleteObserver(this);
         if (this.world.isRemote) {
            return true;
         } else {
            LocksUtil.sendToTrackingPlayers(lkb.box, new RemoveLockablePacket(id), this.world);
            return true;
         }
      }
   }

   public void update(Observable o, Object arg) {
      if (!this.world.isRemote && o instanceof Lockable) {
         Lockable lockable = (Lockable)o;
         LocksUtil.sendToTrackingPlayers(lockable.box, new UpdateLockablePacket(lockable.networkID, lockable.lock.isLocked()), this.world);
      }
   }

   public NBTTagInt serializeNBT() {
      return new NBTTagInt(this.lastId.get());
   }

   public void deserializeNBT(NBTTagInt nbt) {
      this.lastId.set(nbt.getInt());
   }
}
