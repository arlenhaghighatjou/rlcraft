package bq_standard.rewards.loot;

import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.INBTPartial;
import betterquesting.api2.storage.SimpleDatabase;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LootRegistry extends SimpleDatabase<LootGroup> implements INBTPartial<NBTTagCompound, Integer> {
   public static final LootRegistry INSTANCE = new LootRegistry();
   private final Comparator<DBEntry<LootGroup>> groupSorter = Comparator.comparingInt((o) -> {
      return ((LootGroup)o.getValue()).weight;
   });
   public boolean updateUI = false;

   public LootRegistry() {
   }

   public synchronized LootGroup createNew(int id) {
      LootGroup group = new LootGroup();
      if (id >= 0) {
         this.add(id, group);
      }

      return group;
   }

   public int getTotalWeight() {
      int i = 0;

      for(DBEntry<LootGroup> lg : this.getEntries()) {
         i += ((LootGroup)lg.getValue()).weight;
      }

      return i;
   }

   public LootGroup getWeightedGroup(float weight, EaglercraftRandom rand) {
      int total = this.getTotalWeight();
      if (total <= 0) {
         return null;
      } else {
         float r = rand.nextFloat() * (float)total / 4.0F + weight * (float)total * 0.75F;
         int cnt = 0;
         List<DBEntry<LootGroup>> sorted = new ArrayList(this.getEntries());
         sorted.sort(this.groupSorter);

         for(DBEntry<LootGroup> entry : sorted) {
            cnt += ((LootGroup)entry.getValue()).weight;
            if ((float)cnt >= r) {
               return (LootGroup)entry.getValue();
            }
         }

         return null;
      }
   }

   public synchronized NBTTagCompound writeToNBT(NBTTagCompound tag, @Nullable List<Integer> subset) {
      NBTTagList jRew = new NBTTagList();

      for(DBEntry<LootGroup> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound jGrp = ((LootGroup)entry.getValue()).writeToNBT(new NBTTagCompound());
            jGrp.setInteger("ID", entry.getID());
            jRew.appendTag(jGrp);
         }
      }

      tag.setTag("groups", jRew);
      return tag;
   }

   public synchronized void readFromNBT(NBTTagCompound tag, boolean merge) {
      if (!merge) {
         this.reset();
      }

      List<LootGroup> legacyGroups = new ArrayList();
      NBTTagList list = tag.getTagList("groups", 10);

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound entry = list.getCompoundTagAt(i);
         int id = entry.hasKey("ID", 99) ? entry.getInteger("ID") : -1;
         LootGroup group = (LootGroup)this.getValue(id);
         if (group == null) {
            group = this.createNew(id);
         }

         group.readFromNBT(entry);
         if (id < 0) {
            legacyGroups.add(group);
         }
      }

      for(LootGroup group : legacyGroups) {
         this.add(this.nextID(), group);
      }

   }
}
