package betterquesting.api2.supporter;

import betterquesting.api2.storage.INBTSaveLoad;
import betterquesting.core.BetterQuesting;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;

public class SupporterDB implements INBTSaveLoad<NBTTagCompound> {
   public static final SupporterDB INSTANCE = new SupporterDB();
   private final TreeMap<UUID, SupporterEntry> mapDB = new TreeMap();

   public SupporterDB() {
   }

   public synchronized SupporterEntry add(@Nonnull UUID playerID) {
      SupporterEntry entry = new SupporterEntry();
      if (this.mapDB.putIfAbsent(playerID, entry) != null) {
         BetterQuesting.logger.warn("Tried to add duplicate supporter to DB: " + playerID.toString());
         return (SupporterEntry)this.mapDB.get(playerID);
      } else {
         return entry;
      }
   }

   public synchronized boolean removeID(@Nonnull UUID playerID) {
      return this.mapDB.remove(playerID) != null;
   }

   @Nullable
   public synchronized SupporterEntry getValue(@Nonnull UUID playerID) {
      return (SupporterEntry)this.mapDB.get(playerID);
   }

   @Nonnull
   public synchronized NBTTagCompound writeToNBT(@Nonnull NBTTagCompound nbt) {
      this.mapDB.forEach((key, value) -> {
         nbt.setTag(key.toString(), value.writeToNBT(new NBTTagCompound()));
      });
      return nbt;
   }

   public synchronized void readFromNBT(@Nonnull NBTTagCompound nbt) {
      this.mapDB.clear();
      nbt.getKeySet().forEach((key) -> {
         try {
            SupporterEntry entry = new SupporterEntry();
            entry.readFromNBT(nbt.getCompoundTag(key));
            this.mapDB.put(UUID.fromString(key), entry);
         } catch (Exception var4) {
            ;
         }

      });
   }
}
