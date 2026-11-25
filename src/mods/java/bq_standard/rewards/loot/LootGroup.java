package bq_standard.rewards.loot;

import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.INBTSaveLoad;
import betterquesting.api2.storage.SimpleDatabase;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LootGroup extends SimpleDatabase<LootGroup.LootEntry> implements INBTSaveLoad<NBTTagCompound> {
   public String name = "Loot Group";
   public int weight = 1;

   public LootGroup() {
   }

   public List<BigItemStack> getRandomReward(Random rand) {
      int total = this.getTotalWeight();
      float r = rand.nextFloat() * (float)total;
      int cnt = 0;

      for(DBEntry<LootGroup.LootEntry> entry : this.getEntries()) {
         cnt += ((LootGroup.LootEntry)entry.getValue()).weight;
         if ((float)cnt >= r) {
            return ((LootGroup.LootEntry)entry.getValue()).items;
         }
      }

      return Collections.emptyList();
   }

   public int getTotalWeight() {
      int i = 0;

      for(DBEntry<LootGroup.LootEntry> entry : this.getEntries()) {
         i += ((LootGroup.LootEntry)entry.getValue()).weight;
      }

      return i;
   }

   public void readFromNBT(NBTTagCompound tag) {
      this.reset();
      this.name = tag.getString("name");
      this.weight = Math.max(tag.getInteger("weight"), 1);
      List<LootGroup.LootEntry> legacyEntry = new ArrayList();
      NBTTagList jRew = tag.getTagList("rewards", 10);

      for(int i = 0; i < jRew.tagCount(); ++i) {
         NBTTagCompound entry = jRew.getCompoundTagAt(i);
         int id = entry.hasKey("ID", 99) ? entry.getInteger("ID") : -1;
         LootGroup.LootEntry loot = new LootGroup.LootEntry();
         loot.readFromNBT(entry);
         if (id >= 0) {
            this.add(id, loot);
         } else {
            legacyEntry.add(loot);
         }
      }

      for(LootGroup.LootEntry entry : legacyEntry) {
         this.add(this.nextID(), entry);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      tag.setString("name", this.name);
      tag.setInteger("weight", this.weight);
      NBTTagList jRew = new NBTTagList();

      for(DBEntry<LootGroup.LootEntry> entry : this.getEntries()) {
         if (entry != null) {
            NBTTagCompound jLoot = ((LootGroup.LootEntry)entry.getValue()).writeToNBT(new NBTTagCompound());
            jLoot.setInteger("ID", entry.getID());
            jRew.appendTag(jLoot);
         }
      }

      tag.setTag("rewards", jRew);
      return tag;
   }

   public static class LootEntry implements INBTSaveLoad<NBTTagCompound> {
      public int weight = 1;
      public final List<BigItemStack> items = new ArrayList();

      public LootEntry() {
      }

      public void readFromNBT(NBTTagCompound json) {
         this.weight = json.getInteger("weight");
         this.weight = Math.max(1, this.weight);
         this.items.clear();
         NBTTagList jItm = json.getTagList("items", 10);

         for(int i = 0; i < jItm.tagCount(); ++i) {
            this.items.add(JsonHelper.JsonToItemStack(jItm.getCompoundTagAt(i)));
         }

      }

      public NBTTagCompound writeToNBT(NBTTagCompound tag) {
         tag.setInteger("weight", this.weight);
         NBTTagList jItm = new NBTTagList();

         for(BigItemStack stack : this.items) {
            jItm.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
         }

         tag.setTag("items", jItm);
         return tag;
      }
   }
}
