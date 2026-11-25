package tschipp.callablehorses.common.worlddata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class StoredHorsesWorldData extends WorldSavedData {
   private static String name = "callablehorses_stored_horses";
   private Map<String, Integer> entries = new HashMap();
   private List<String> killedHorses = new ArrayList();
   private List<String> disbandedHorses = new ArrayList();
   private Map<String, NBTTagCompound> offlineSavedHorses = new HashMap();
   private int i = 0;

   public StoredHorsesWorldData() {
      super(name);
   }

   public StoredHorsesWorldData(String name) {
      super(name);
   }

   public void readFromNBT(NBTTagCompound nbt) {
      for(int i = 0; nbt.hasKey("" + i); ++i) {
         NBTTagCompound subTag = nbt.getCompoundTag("" + i);
         String storageID = subTag.getString("id");
         int num = subTag.getInteger("num");
         this.entries.put(storageID, Integer.valueOf(num));
      }

      int var6 = 0;

      for(NBTTagCompound killed = nbt.getCompoundTag("killed"); killed.hasKey("" + var6); ++var6) {
         this.killedHorses.add(killed.getCompoundTag("" + var6).getString("id"));
      }

      var6 = 0;

      for(NBTTagCompound disbanded = nbt.getCompoundTag("disbanded"); disbanded.hasKey("" + var6); ++var6) {
         this.disbandedHorses.add(disbanded.getCompoundTag("" + var6).getString("id"));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagCompound tag = new NBTTagCompound();
      this.entries.forEach((storageID, num) -> {
         NBTTagCompound subTag = new NBTTagCompound();
         subTag.setString("id", storageID);
         subTag.setInteger("num", num.intValue());
         tag.setTag("" + this.i, subTag);
         ++this.i;
      });
      this.i = 0;
      NBTTagCompound killed = new NBTTagCompound();

      for(int k = 0; k < this.killedHorses.size(); ++k) {
         NBTTagCompound subTag = new NBTTagCompound();
         subTag.setString("id", (String)this.killedHorses.get(k));
         killed.setTag("" + k, subTag);
      }

      tag.setTag("killed", killed);
      NBTTagCompound disbanded = new NBTTagCompound();

      for(int k = 0; k < this.disbandedHorses.size(); ++k) {
         NBTTagCompound subTag = new NBTTagCompound();
         subTag.setString("id", (String)this.disbandedHorses.get(k));
         disbanded.setTag("" + k, subTag);
      }

      tag.setTag("disbanded", disbanded);
      return tag;
   }

   public void addHorseNum(String id, int num) {
      this.entries.put(id, Integer.valueOf(num));
      this.markDirty();
   }

   public int getHorseNum(String id) {
      Integer i = (Integer)this.entries.get(id);
      return i == null ? 0 : i.intValue();
   }

   public void disbandHorse(String id) {
      this.disbandedHorses.add(id);
      this.markDirty();
   }

   public boolean isDisbanded(String id) {
      return this.disbandedHorses.contains(id);
   }

   public void clearDisbanded(String id) {
      this.disbandedHorses.remove(id);
      this.markDirty();
   }

   public void markKilled(String id) {
      this.killedHorses.add(id);
      this.markDirty();
   }

   public boolean wasKilled(String id) {
      return this.killedHorses.contains(id);
   }

   public void clearKilled(String id) {
      this.killedHorses.remove(id);
      this.markDirty();
   }

   public void addOfflineSavedHorse(String id, NBTTagCompound nbt) {
      this.offlineSavedHorses.put(id, nbt);
      this.markDirty();
   }

   public boolean wasOfflineSaved(String id) {
      return this.offlineSavedHorses.containsKey(id);
   }

   public NBTTagCompound getOfflineSavedHorse(String id) {
      return (NBTTagCompound)this.offlineSavedHorses.get(id);
   }

   public void clearOfflineSavedHorse(String id) {
      this.offlineSavedHorses.remove(id);
      this.markDirty();
   }

   public static StoredHorsesWorldData getInstance(World world) {
      MapStorage storage = world.getMapStorage();
      StoredHorsesWorldData instance = (StoredHorsesWorldData)storage.getOrLoadData(StoredHorsesWorldData.class, name);
      if (instance == null) {
         instance = new StoredHorsesWorldData();
         storage.setData(name, instance);
      }

      return instance;
   }
}
