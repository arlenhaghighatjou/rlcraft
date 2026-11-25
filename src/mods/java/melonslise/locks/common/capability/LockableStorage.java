package melonslise.locks.common.capability;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import melonslise.locks.Locks;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.util.Lockable;
import melonslise.locks.common.util.LocksUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;

public class LockableStorage implements ILockableStorage {
   public static final ResourceLocation ID = new ResourceLocation("locks", "lockable_storage");
   public final Chunk chunk;
   public Int2ObjectMap<Lockable> lockables = new Int2ObjectLinkedOpenHashMap();

   public LockableStorage(Chunk chunk) {
      this.chunk = chunk;
   }

   public Int2ObjectMap<Lockable> get() {
      return this.lockables;
   }

   public void add(Lockable lkb) {
      this.lockables.put(lkb.networkID, lkb);
      this.chunk.markDirty();
   }

   public void remove(int id) {
      this.lockables.remove(id);
      this.chunk.markDirty();
   }

   public NBTTagList serializeNBT() {
      NBTTagList list = new NBTTagList();
      ObjectIterator var2 = this.lockables.values().iterator();

      while(var2.hasNext()) {
         Lockable lkb = (Lockable)var2.next();
         list.appendTag(LocksUtil.writeLockableToNBT(lkb));
      }

      return list;
   }

   public void deserializeNBT(NBTTagList nbt) {
      ILockableHandler handler = (ILockableHandler)this.chunk.getWorld().getCapability(LocksCapabilities.LOCKABLE_HANDLER, (EnumFacing)null);
      Int2ObjectMap<Lockable> loadedLockables = handler.getLoaded();

      for(int a = 0; a < nbt.tagCount(); ++a) {
         NBTTagCompound nbt1 = nbt.getCompoundTagAt(a);
         Lockable lkb = (Lockable)loadedLockables.get(nbt1.getInteger("id"));
         if (lkb == loadedLockables.defaultReturnValue()) {
            lkb = LocksUtil.readLockableFromNBT(nbt1);
            if (Locks.debug) {
               Locks.logger.debug("Storage deserializing with id: " + lkb.networkID + " ::: " + lkb.toString());
            }
         }

         this.lockables.put(lkb.networkID, lkb);
      }

   }
}
