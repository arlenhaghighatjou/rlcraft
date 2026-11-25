package betterquesting.questing;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class QuestDatabase extends SimpleDatabase<IQuest> implements IQuestDatabase {
   public static final QuestDatabase INSTANCE = new QuestDatabase();

   public QuestDatabase() {
   }

   public synchronized IQuest createNew(int id) {
      IQuest quest = new QuestInstance();
      if (id >= 0) {
         this.add(id, quest);
      }

      return quest;
   }

   public synchronized boolean removeID(int id) {
      boolean success = super.removeID(id);
      if (success) {
         for(DBEntry<IQuest> entry : this.getEntries()) {
            this.removeReq(entry.getValue(), id);
         }
      }

      return success;
   }

   public synchronized boolean removeValue(IQuest value) {
      int id = this.getID(value);
      if (id < 0) {
         return false;
      } else {
         boolean success = this.removeValue(value);
         if (success) {
            for(DBEntry<IQuest> entry : this.getEntries()) {
               this.removeReq(entry.getValue(), id);
            }
         }

         return success;
      }
   }

   private void removeReq(IQuest quest, int id) {
      int[] orig = quest.getRequirements();
      if (orig.length > 0) {
         boolean hasRemoved = false;
         int[] rem = new int[orig.length - 1];

         for(int i = 0; i < orig.length; ++i) {
            if (!hasRemoved && orig[i] == id) {
               hasRemoved = true;
            } else {
               if (!hasRemoved && i >= rem.length) {
                  break;
               }

               rem[!hasRemoved ? i : i - 1] = orig[i];
            }
         }

         if (hasRemoved) {
            quest.setRequirements(rem);
         }

      }
   }

   public synchronized NBTTagList writeToNBT(NBTTagList json, @Nullable List<Integer> subset) {
      for(DBEntry<IQuest> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound jq = (NBTTagCompound)((IQuest)entry.getValue()).writeToNBT(new NBTTagCompound());
            if (subset == null || !jq.hasNoTags()) {
               jq.setInteger("questID", entry.getID());
               json.appendTag(jq);
            }
         }
      }

      return json;
   }

   public synchronized void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.reset();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound qTag = nbt.getCompoundTagAt(i);
         int qID = qTag.hasKey("questID", 99) ? qTag.getInteger("questID") : -1;
         if (qID >= 0) {
            IQuest quest = (IQuest)this.getValue(qID);
            if (quest == null) {
               quest = this.createNew(qID);
            }

            quest.readFromNBT(qTag);
         }
      }

   }

   public synchronized NBTTagList writeProgressToNBT(NBTTagList json, @Nullable List<EaglercraftUUID> users) {
      for(DBEntry<IQuest> entry : this.getEntries()) {
         NBTTagCompound jq = (NBTTagCompound)((IQuest)entry.getValue()).writeProgressToNBT(new NBTTagCompound(), users);
         jq.setInteger("questID", entry.getID());
         json.appendTag(jq);
      }

      return json;
   }

   public synchronized void readProgressFromNBT(NBTTagList json, boolean merge) {
      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound qTag = json.getCompoundTagAt(i);
         int qID = qTag.hasKey("questID", 99) ? qTag.getInteger("questID") : -1;
         if (qID >= 0) {
            IQuest quest = (IQuest)this.getValue(qID);
            if (quest != null) {
               quest.readProgressFromNBT(qTag, merge);
            }
         }
      }

   }
}
