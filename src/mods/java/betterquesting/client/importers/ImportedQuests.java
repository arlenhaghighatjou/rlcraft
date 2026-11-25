package betterquesting.client.importers;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.questing.QuestInstance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ImportedQuests extends SimpleDatabase<IQuest> implements IQuestDatabase {
   public ImportedQuests() {
   }

   public IQuest createNew(int id) {
      return (IQuest)this.add(id, new QuestInstance()).getValue();
   }

   public List<DBEntry<IQuest>> bulkLookup(int... ids) {
      if (ids != null && ids.length > 0) {
         List<DBEntry<IQuest>> values = new ArrayList();
         synchronized(this) {
            for(int i : ids) {
               IQuest v = (IQuest)this.getValue(i);
               if (v != null) {
                  values.add(new DBEntry(i, v));
               }
            }

            return values;
         }
      } else {
         return Collections.emptyList();
      }
   }

   public NBTTagList writeToNBT(NBTTagList nbt, List<Integer> subset) {
      for(DBEntry<IQuest> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound jq = new NBTTagCompound();
            ((IQuest)entry.getValue()).writeToNBT(jq);
            jq.setInteger("questID", entry.getID());
            nbt.appendTag(jq);
         }
      }

      return nbt;
   }

   public void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.reset();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound qTag = nbt.getCompoundTagAt(i);
         int qID = qTag.hasKey("questID", 99) ? qTag.getInteger("questID") : -1;
         if (qID >= 0) {
            IQuest quest = (IQuest)this.getValue(qID);
            quest = quest != null ? quest : this.createNew(qID);
            quest.readFromNBT(qTag);
         }
      }

   }

   public NBTTagList writeProgressToNBT(NBTTagList nbt, @Nullable List<UUID> users) {
      return nbt;
   }

   public void readProgressFromNBT(NBTTagList nbt, boolean merge) {
   }
}
