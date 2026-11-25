package betterquesting.api2.cache;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api2.storage.DBEntry;
import betterquesting.network.handlers.NetCacheSync;
import betterquesting.questing.QuestDatabase;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.INBTSerializable;

public class QuestCache implements INBTSerializable<NBTTagCompound> {
   private final TreeSet<Integer> visibleQuests = new TreeSet();
   private final TreeSet<Integer> activeQuests = new TreeSet();
   private final TreeSet<QuestCache.QResetTime> resetSchedule = new TreeSet((o1, o2) -> {
      return o1.questID == o2.questID ? 0 : Long.compare(o1.time, o2.time);
   });
   private final TreeSet<Integer> autoClaims = new TreeSet();
   private final TreeSet<Integer> markedDirty = new TreeSet();

   public QuestCache() {
   }

   public synchronized int[] getActiveQuests() {
      int i = 0;
      int[] aryAct = new int[this.activeQuests.size()];

      for(Integer q : this.activeQuests) {
         aryAct[i++] = q.intValue();
      }

      return aryAct;
   }

   public synchronized int[] getVisibleQuests() {
      int i = 0;
      int[] aryVis = new int[this.visibleQuests.size()];

      for(Integer q : this.visibleQuests) {
         aryVis[i++] = q.intValue();
      }

      return aryVis;
   }

   public synchronized int[] getPendingAutoClaims() {
      int i = 0;
      int[] aryAC = new int[this.autoClaims.size()];

      for(Integer q : this.autoClaims) {
         aryAC[i++] = q.intValue();
      }

      return aryAC;
   }

   public synchronized QuestCache.QResetTime[] getScheduledResets() {
      return (QuestCache.QResetTime[])this.resetSchedule.toArray(new QuestCache.QResetTime[0]);
   }

   public synchronized void markQuestDirty(int questID) {
      if (questID >= 0) {
         this.markedDirty.add(Integer.valueOf(questID));
      }
   }

   public synchronized void markQuestClean(int questID) {
      if (questID >= 0) {
         this.markedDirty.remove(Integer.valueOf(questID));
      }
   }

   public synchronized void cleanAllQuests() {
      this.markedDirty.clear();
   }

   public int[] getDirtyQuests() {
      int i = 0;
      int[] aryMD = new int[this.markedDirty.size()];

      for(Integer q : this.markedDirty) {
         aryMD[i++] = q.intValue();
      }

      return aryMD;
   }

   public synchronized void updateCache(EntityPlayer player) {
      if (player != null) {
         EaglercraftUUID uuid = QuestingAPI.getQuestingUUID(player);
         List<DBEntry<IQuest>> questDB = ((IQuestDatabase)QuestingAPI.getAPI(ApiReference.QUEST_DB)).getEntries();
         NonNullList<Integer> tmpVisible = NonNullList.create();
         NonNullList<Integer> tmpActive = NonNullList.create();
         NonNullList<QuestCache.QResetTime> tmpReset = NonNullList.create();
         NonNullList<Integer> tmpAutoClaim = NonNullList.create();

         for(DBEntry<IQuest> entry : questDB) {
            if (((IQuest)entry.getValue()).isUnlocked(uuid) || ((IQuest)entry.getValue()).isComplete(uuid) || ((Boolean)((IQuest)entry.getValue()).getProperty(NativeProps.LOCKED_PROGRESS)).booleanValue()) {
               int repeat = ((Integer)((IQuest)entry.getValue()).getProperty(NativeProps.REPEAT_TIME)).intValue();
               NBTTagCompound ue = ((IQuest)entry.getValue()).getCompletionInfo(uuid);
               if ((ue != null || ((IQuest)entry.getValue()).getTasks().size() > 0) && !((IQuest)entry.getValue()).canSubmit(player)) {
                  if (ue != null) {
                     if (repeat >= 0 && ((IQuest)entry.getValue()).hasClaimed(uuid)) {
                        long altTime = ue.getLong("timestamp");
                        if (repeat > 1 && !((Boolean)((IQuest)entry.getValue()).getProperty(NativeProps.REPEAT_REL)).booleanValue()) {
                           altTime -= altTime % (long)repeat;
                        }

                        tmpReset.add(new QuestCache.QResetTime(entry.getID(), altTime + (long)(repeat * 50)));
                     }

                     if (!((IQuest)entry.getValue()).hasClaimed(uuid) && ((Boolean)((IQuest)entry.getValue()).getProperty(NativeProps.AUTO_CLAIM)).booleanValue()) {
                        tmpAutoClaim.add(Integer.valueOf(entry.getID()));
                     }
                  }
               } else {
                  tmpActive.add(Integer.valueOf(entry.getID()));
               }
            }

            if (isQuestShown(entry.getValue(), uuid, player)) {
               tmpVisible.add(Integer.valueOf(entry.getID()));
            }
         }

         this.visibleQuests.clear();
         this.visibleQuests.addAll(tmpVisible);
         this.activeQuests.clear();
         this.activeQuests.addAll(tmpActive);
         this.resetSchedule.clear();
         this.resetSchedule.addAll(tmpReset);
         this.autoClaims.clear();
         this.autoClaims.addAll(tmpAutoClaim);
         if (player instanceof EntityPlayerMP) {
            NetCacheSync.sendSync((EntityPlayerMP)player);
         }

      }
   }

   public synchronized NBTTagCompound serializeNBT() {
      NBTTagCompound tags = new NBTTagCompound();
      tags.setIntArray("visibleQuests", this.getVisibleQuests());
      tags.setIntArray("activeQuests", this.getActiveQuests());
      tags.setIntArray("autoClaims", this.getPendingAutoClaims());
      tags.setIntArray("markedDirty", this.getDirtyQuests());
      NBTTagList tagSchedule = new NBTTagList();

      for(QuestCache.QResetTime entry : this.getScheduledResets()) {
         NBTTagCompound tagEntry = new NBTTagCompound();
         tagEntry.setInteger("quest", entry.questID);
         tagEntry.setLong("time", entry.time);
         tagSchedule.appendTag(tagEntry);
      }

      tags.setTag("resetSchedule", tagSchedule);
      return tags;
   }

   public synchronized void deserializeNBT(NBTTagCompound nbt) {
      this.visibleQuests.clear();
      this.activeQuests.clear();
      this.resetSchedule.clear();
      this.autoClaims.clear();
      this.markedDirty.clear();

      for(int i : nbt.getIntArray("visibleQuests")) {
         this.visibleQuests.add(Integer.valueOf(i));
      }

      for(int i : nbt.getIntArray("activeQuests")) {
         this.activeQuests.add(Integer.valueOf(i));
      }

      for(int i : nbt.getIntArray("autoClaims")) {
         this.autoClaims.add(Integer.valueOf(i));
      }

      for(int i : nbt.getIntArray("markedDirty")) {
         this.markedDirty.add(Integer.valueOf(i));
      }

      NBTTagList tagList = nbt.getTagList("resetSchedule", 10);

      for(int i = 0; i < tagList.tagCount(); ++i) {
         NBTTagCompound tagEntry = tagList.getCompoundTagAt(i);
         if (tagEntry.hasKey("quest", 99)) {
            this.resetSchedule.add(new QuestCache.QResetTime(tagEntry.getInteger("quest"), tagEntry.getLong("time")));
         }
      }

   }

   public static boolean isQuestShown(IQuest quest, EaglercraftUUID uuid, EntityPlayer player) {
      if (quest != null && uuid != null) {
         EnumQuestVisibility vis = (EnumQuestVisibility)quest.getProperty(NativeProps.VISIBILITY);
         if (!((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(player) && vis != EnumQuestVisibility.ALWAYS) {
            if (vis == EnumQuestVisibility.HIDDEN) {
               return false;
            } else if (vis != EnumQuestVisibility.UNLOCKED) {
               if (vis == EnumQuestVisibility.NORMAL) {
                  if (!quest.isComplete(uuid) && !quest.isUnlocked(uuid)) {
                     for(DBEntry<IQuest> q : QuestDatabase.INSTANCE.bulkLookup(quest.getRequirements())) {
                        if (!((IQuest)q.getValue()).isUnlocked(uuid)) {
                           return false;
                        }
                     }

                     return true;
                  } else {
                     return true;
                  }
               } else if (vis == EnumQuestVisibility.COMPLETED) {
                  return quest.isComplete(uuid);
               } else if (vis == EnumQuestVisibility.CHAIN) {
                  if (quest.getRequirements().length <= 0) {
                     return true;
                  } else {
                     for(DBEntry<IQuest> q : QuestDatabase.INSTANCE.bulkLookup(quest.getRequirements())) {
                        if (q == null) {
                           return true;
                        }

                        if (isQuestShown(q.getValue(), uuid, player)) {
                           return true;
                        }
                     }

                     return false;
                  }
               } else {
                  return true;
               }
            } else {
               return quest.isComplete(uuid) || quest.isUnlocked(uuid);
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public class QResetTime implements Comparable<QuestCache.QResetTime> {
      public final int questID;
      public final long time;

      private QResetTime(int questID, long time) {
         this.questID = questID;
         this.time = time;
      }

      public int compareTo(QuestCache.QResetTime o) {
         return Long.compare(o.time, this.time);
      }

      public boolean equals(Object o) {
         if (!(o instanceof QuestCache.QResetTime)) {
            return false;
         } else {
            return ((QuestCache.QResetTime)o).questID == this.questID;
         }
      }
   }
}
