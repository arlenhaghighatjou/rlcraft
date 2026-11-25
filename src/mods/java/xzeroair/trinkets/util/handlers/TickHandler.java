package xzeroair.trinkets.util.handlers;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.nbt.NBTTagCompound;

public class TickHandler {
   Map<String, Counter> Counters = new TreeMap();

   public TickHandler() {
   }

   public Map<String, Counter> getCounters() {
      return this.Counters;
   }

   public void addCounter(String key, int length, boolean countdown, boolean shouldTick, boolean saveToNBT) {
      if (this.Counters != null && !this.Counters.containsKey(key)) {
         this.Counters.put(key, new Counter(key, length, countdown, shouldTick, saveToNBT));
      }

   }

   public void removeCounter(String key) {
      if (!this.Counters.isEmpty() && this.Counters.containsKey(key)) {
         this.Counters.remove(key);
      }

   }

   public void clearCounters() {
      if (!this.Counters.isEmpty()) {
         this.Counters.clear();
      }

   }

   public Counter getCounter(String key, int length, boolean isCountdown, boolean shouldTick, boolean create, boolean saveNBT) {
      return this.getCounter(key, length, isCountdown, shouldTick, true, create, saveNBT);
   }

   public Counter getCounter(String key, int length, boolean isCountdown, boolean shouldTick, boolean autoReset, boolean create, boolean saveNBT) {
      if (!this.Counters.isEmpty() && this.Counters.containsKey(key)) {
         return (Counter)this.Counters.get(key);
      } else if (create) {
         Counter value = new Counter(key, length, isCountdown, shouldTick, autoReset, saveNBT);
         this.Counters.put(key, value);
         return value;
      } else {
         return null;
      }
   }

   public Counter getCounter(String key) {
      return !this.Counters.isEmpty() && this.Counters.containsKey(key) ? (Counter)this.Counters.get(key) : null;
   }

   public void saveCountersToNBT(NBTTagCompound compound) {
      NBTTagCompound counters = new NBTTagCompound();

      for(Entry<String, Counter> counter : this.Counters.entrySet()) {
         String name = ((Counter)counter.getValue()).getName();
         int tick = ((Counter)counter.getValue()).getTick();
         int length = ((Counter)counter.getValue()).getLength();
         boolean countdown = ((Counter)counter.getValue()).getCountdown();
         boolean shouldTick = ((Counter)counter.getValue()).shouldTick();
         boolean saveToNBT = ((Counter)counter.getValue()).saveToNBT();
         if (saveToNBT) {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Tick", tick);
            nbt.setInteger("Length", length);
            nbt.setBoolean("Countdown", countdown);
            nbt.setBoolean("ShouldTick", shouldTick);
            counters.setTag(name, nbt);
         }
      }

      compound.setTag("Counters", counters);
   }

   public void loadCountersFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("Counters")) {
         NBTTagCompound counters = compound.getCompoundTag("Counters");
         counters.getKeySet().forEach((name) -> {
            try {
               NBTTagCompound counter = counters.getCompoundTag(name);
               int tick = counter.getInteger("Tick");
               int length = counter.getInteger("Length");
               boolean countdown = counter.getBoolean("Countdown");
               boolean shouldTick = counter.getBoolean("ShouldTick");
               Counter nbtCounter = (new Counter(name, length, countdown, shouldTick)).setTick(tick);
               if (this.Counters.containsKey(name)) {
                  this.Counters.replace(name, nbtCounter);
               } else {
                  this.Counters.putIfAbsent(name, nbtCounter);
               }
            } catch (Exception var9) {
               var9.printStackTrace();
            }

         });
      }

   }
}
