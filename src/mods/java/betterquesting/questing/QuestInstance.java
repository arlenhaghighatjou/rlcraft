package betterquesting.questing;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.enums.EnumLogic;
import betterquesting.api.enums.EnumQuestState;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.IPropertyType;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabaseNBT;
import betterquesting.api2.utils.ParticipantInfo;
import betterquesting.core.BetterQuesting;
import betterquesting.questing.rewards.RewardStorage;
import betterquesting.questing.tasks.TaskStorage;
import betterquesting.storage.PropertyContainer;
import betterquesting.storage.QuestSettings;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import org.apache.logging.log4j.Level;

public class QuestInstance implements IQuest {
   private final TaskStorage tasks = new TaskStorage();
   private final RewardStorage rewards = new RewardStorage();
   private final HashMap<UUID, NBTTagCompound> completeUsers = new HashMap();
   private int[] preRequisites = new int[0];
   private final PropertyContainer qInfo = new PropertyContainer();

   public QuestInstance() {
      this.setupProps();
   }

   private void setupProps() {
      this.setupValue(NativeProps.NAME, "New Quest");
      this.setupValue(NativeProps.DESC, "No Description");
      this.setupValue(NativeProps.ICON, new BigItemStack(Items.NETHER_STAR));
      this.setupValue(NativeProps.SOUND_COMPLETE);
      this.setupValue(NativeProps.SOUND_UPDATE);
      this.setupValue(NativeProps.LOGIC_QUEST, EnumLogic.AND);
      this.setupValue(NativeProps.LOGIC_TASK, EnumLogic.AND);
      this.setupValue(NativeProps.REPEAT_TIME, Integer.valueOf(-1));
      this.setupValue(NativeProps.REPEAT_REL, Boolean.valueOf(true));
      this.setupValue(NativeProps.LOCKED_PROGRESS, Boolean.valueOf(false));
      this.setupValue(NativeProps.AUTO_CLAIM, Boolean.valueOf(false));
      this.setupValue(NativeProps.SILENT, Boolean.valueOf(false));
      this.setupValue(NativeProps.MAIN, Boolean.valueOf(false));
      this.setupValue(NativeProps.GLOBAL_SHARE, Boolean.valueOf(false));
      this.setupValue(NativeProps.SIMULTANEOUS, Boolean.valueOf(false));
      this.setupValue(NativeProps.VISIBILITY, EnumQuestVisibility.NORMAL);
   }

   private <T> void setupValue(IPropertyType<T> prop) {
      this.setupValue(prop, prop.getDefault());
   }

   private <T> void setupValue(IPropertyType<T> prop, T def) {
      this.qInfo.setProperty(prop, this.qInfo.getProperty(prop, def));
   }

   public void update(EntityPlayer player) {
      UUID playerID = QuestingAPI.getQuestingUUID(player);
      int done = 0;

      for(DBEntry<ITask> entry : this.tasks.getEntries()) {
         if (((ITask)entry.getValue()).isComplete(playerID)) {
            ++done;
         }
      }

      if (this.tasks.size() > 0 && !((EnumLogic)this.qInfo.getProperty(NativeProps.LOGIC_TASK)).getResult(done, this.tasks.size())) {
         if (done > 0 && ((Boolean)this.qInfo.getProperty(NativeProps.SIMULTANEOUS)).booleanValue()) {
            this.resetUser(playerID, false);
         }
      } else {
         this.setComplete(playerID, System.currentTimeMillis());
      }

   }

   public void detect(EntityPlayer player) {
      UUID playerID = QuestingAPI.getQuestingUUID(player);
      QuestCache qc = (QuestCache)player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      if (qc != null) {
         int questID = QuestDatabase.INSTANCE.getID(this);
         if (!this.isComplete(playerID) || ((Integer)this.qInfo.getProperty(NativeProps.REPEAT_TIME)).intValue() >= 0 && this.rewards.size() > 0) {
            if (this.canSubmit(player)) {
               if (this.isUnlocked(playerID) || ((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
                  int done = 0;
                  boolean update = false;
                  ParticipantInfo partInfo = new ParticipantInfo(player);
                  DBEntry<IQuest> dbe = new DBEntry<IQuest>(questID, this);

                  for(DBEntry<ITask> entry : this.tasks.getEntries()) {
                     if (!((ITask)entry.getValue()).isComplete(playerID)) {
                        (entry.getValue()).detect(partInfo, dbe);
                        if (((ITask)entry.getValue()).isComplete(playerID)) {
                           ++done;
                           update = true;
                        }
                     } else {
                        ++done;
                     }
                  }

                  if (this.tasks.size() > 0 && !((EnumLogic)this.qInfo.getProperty(NativeProps.LOGIC_TASK)).getResult(done, this.tasks.size())) {
                     if (update && ((Boolean)this.qInfo.getProperty(NativeProps.SIMULTANEOUS)).booleanValue()) {
                        this.resetUser(playerID, false);
                        qc.markQuestDirty(questID);
                     } else if (update) {
                        qc.markQuestDirty(questID);
                     }
                  } else {
                     if (((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
                        this.setComplete(playerID, System.currentTimeMillis());
                     }

                     qc.markQuestDirty(questID);
                  }
               }

            }
         }
      }
   }

   public boolean hasClaimed(UUID uuid) {
      if (this.rewards.size() <= 0) {
         return true;
      } else {
         synchronized(this.completeUsers) {
            if (((Boolean)this.qInfo.getProperty(NativeProps.GLOBAL)).booleanValue() && !((Boolean)this.qInfo.getProperty(NativeProps.GLOBAL_SHARE)).booleanValue()) {
               for(NBTTagCompound entry : this.completeUsers.values()) {
                  if (entry.getBoolean("claimed")) {
                     return true;
                  }
               }

               return false;
            } else {
               NBTTagCompound entry = this.getCompletionInfo(uuid);
               return entry != null && entry.getBoolean("claimed");
            }
         }
      }
   }

   public boolean canClaim(EntityPlayer player) {
      UUID pID = QuestingAPI.getQuestingUUID(player);
      NBTTagCompound entry = this.getCompletionInfo(pID);
      if (entry != null && !this.hasClaimed(pID) && !this.canSubmit(player)) {
         DBEntry<IQuest> dbe = new DBEntry<IQuest>(QuestDatabase.INSTANCE.getID(this), this);

         for(DBEntry<IReward> rew : this.rewards.getEntries()) {
            if (!((IReward)rew.getValue()).canClaim(player, dbe)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void claimReward(EntityPlayer player) {
      int questID = QuestDatabase.INSTANCE.getID(this);
      DBEntry<IQuest> dbe = new DBEntry<IQuest>(questID, this);

      for(DBEntry<IReward> rew : this.rewards.getEntries()) {
         (rew.getValue()).claimReward(player, dbe);
      }

      UUID pID = QuestingAPI.getQuestingUUID(player);
      QuestCache qc = (QuestCache)player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
      synchronized(this.completeUsers) {
         NBTTagCompound entry = this.getCompletionInfo(pID);
         if (entry == null) {
            entry = new NBTTagCompound();
            this.completeUsers.put(pID, entry);
         }

         entry.setBoolean("claimed", true);
         entry.setLong("timestamp", System.currentTimeMillis());
      }

      if (qc != null) {
         qc.markQuestDirty(questID);
      }

   }

   public boolean canSubmit(@Nonnull EntityPlayer player) {
      UUID playerID = QuestingAPI.getQuestingUUID(player);
      synchronized(this.completeUsers) {
         NBTTagCompound entry = this.getCompletionInfo(playerID);
         if (entry == null) {
            return true;
         } else if (!entry.getBoolean("claimed") && ((Integer)this.getProperty(NativeProps.REPEAT_TIME)).intValue() >= 0) {
            if (this.tasks.size() <= 0) {
               return true;
            } else {
               int done = 0;

               for(DBEntry<ITask> tsk : this.tasks.getEntries()) {
                  if (((ITask)tsk.getValue()).isComplete(playerID)) {
                     ++done;
                  }
               }

               return !((EnumLogic)this.qInfo.getProperty(NativeProps.LOGIC_TASK)).getResult(done, this.tasks.size());
            }
         } else {
            return false;
         }
      }
   }

   public boolean isUnlocked(UUID uuid) {
      if (this.preRequisites.length <= 0) {
         return true;
      } else {
         int A = 0;
         int B = this.preRequisites.length;

         for(DBEntry<IQuest> quest : QuestDatabase.INSTANCE.bulkLookup(this.getRequirements())) {
            if (((IQuest)quest.getValue()).isComplete(uuid)) {
               ++A;
            }
         }

         return ((EnumLogic)this.qInfo.getProperty(NativeProps.LOGIC_QUEST)).getResult(A, B);
      }
   }

   public void setComplete(UUID uuid, long timestamp) {
      if (uuid != null) {
         synchronized(this.completeUsers) {
            NBTTagCompound entry = this.getCompletionInfo(uuid);
            if (entry == null) {
               entry = new NBTTagCompound();
               this.completeUsers.put(uuid, entry);
            }

            entry.setBoolean("claimed", false);
            entry.setLong("timestamp", timestamp);
         }
      }
   }

   public boolean isComplete(UUID uuid) {
      if (((Boolean)this.qInfo.getProperty(NativeProps.GLOBAL)).booleanValue()) {
         return this.completeUsers.size() > 0;
      } else {
         return this.getCompletionInfo(uuid) != null;
      }
   }

   public EnumQuestState getState(UUID uuid) {
      if (this.isComplete(uuid)) {
         return this.hasClaimed(uuid) ? EnumQuestState.COMPLETED : EnumQuestState.UNCLAIMED;
      } else {
         return this.isUnlocked(uuid) ? EnumQuestState.UNLOCKED : EnumQuestState.LOCKED;
      }
   }

   public NBTTagCompound getCompletionInfo(UUID uuid) {
      synchronized(this.completeUsers) {
         return (NBTTagCompound)this.completeUsers.get(uuid);
      }
   }

   public void setCompletionInfo(UUID uuid, NBTTagCompound nbt) {
      if (uuid != null) {
         synchronized(this.completeUsers) {
            if (nbt == null) {
               this.completeUsers.remove(uuid);
            } else {
               this.completeUsers.put(uuid, nbt);
            }

         }
      }
   }

   public void resetUser(@Nullable UUID uuid, boolean fullReset) {
      synchronized(this.completeUsers) {
         if (fullReset) {
            if (uuid == null) {
               this.completeUsers.clear();
            } else {
               this.completeUsers.remove(uuid);
            }
         } else if (uuid == null) {
            this.completeUsers.forEach((key, value) -> {
               value.setBoolean("claimed", false);
               value.setLong("timestamp", 0L);
            });
         } else {
            NBTTagCompound entry = this.getCompletionInfo(uuid);
            if (entry != null) {
               entry.setBoolean("claimed", false);
               entry.setLong("timestamp", 0L);
            }
         }

         this.tasks.getEntries().forEach((value) -> {
            ((ITask)value.getValue()).resetUser(uuid);
         });
      }
   }

   public IDatabaseNBT<ITask, NBTTagList, NBTTagList> getTasks() {
      return this.tasks;
   }

   public IDatabaseNBT<IReward, NBTTagList, NBTTagList> getRewards() {
      return this.rewards;
   }

   @Nonnull
   public int[] getRequirements() {
      return this.preRequisites;
   }

   public void setRequirements(@Nonnull int[] req) {
      this.preRequisites = req;
   }

   public NBTTagCompound writeToNBT(NBTTagCompound jObj) {
      jObj.setTag("properties", this.qInfo.writeToNBT(new NBTTagCompound()));
      jObj.setTag("tasks", this.tasks.writeToNBT(new NBTTagList(), (List)null));
      jObj.setTag("rewards", this.rewards.writeToNBT(new NBTTagList(), (List)null));
      jObj.setTag("preRequisites", new NBTTagIntArray(this.getRequirements()));
      return jObj;
   }

   public void readFromNBT(NBTTagCompound jObj) {
      this.qInfo.readFromNBT(jObj.getCompoundTag("properties"));
      this.tasks.readFromNBT(jObj.getTagList("tasks", 10), false);
      this.rewards.readFromNBT(jObj.getTagList("rewards", 10), false);
      if (jObj.getTagId("preRequisites") == 11) {
         this.setRequirements(jObj.getIntArray("preRequisites"));
      } else {
         NBTTagList rList = jObj.getTagList("preRequisites", 4);
         int[] req = new int[rList.tagCount()];

         for(int i = 0; i < rList.tagCount(); ++i) {
            NBTBase pTag = rList.get(i);
            req[i] = pTag instanceof NBTPrimitive ? ((NBTPrimitive)pTag).getInt() : -1;
         }

         this.setRequirements(req);
      }

      this.setupProps();
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound json, @Nullable List<UUID> users) {
      synchronized(this.completeUsers) {
         NBTTagList comJson = new NBTTagList();

         for(Entry<UUID, NBTTagCompound> entry : this.completeUsers.entrySet()) {
            if (users == null || users.contains(entry.getKey())) {
               NBTTagCompound tags = ((NBTTagCompound)entry.getValue()).copy();
               tags.setString("uuid", ((UUID)entry.getKey()).toString());
               comJson.appendTag(tags);
            }
         }

         json.setTag("completed", comJson);
         NBTTagList tskJson = this.tasks.writeProgressToNBT(new NBTTagList(), users);
         json.setTag("tasks", tskJson);
         return json;
      }
   }

   public void readProgressFromNBT(NBTTagCompound json, boolean merge) {
      synchronized(this.completeUsers) {
         if (!merge) {
            this.completeUsers.clear();
         }

         NBTTagList comList = json.getTagList("completed", 10);

         for(int i = 0; i < comList.tagCount(); ++i) {
            NBTTagCompound entry = comList.getCompoundTagAt(i).copy();

            try {
               UUID uuid = UUID.fromString(entry.getString("uuid"));
               this.completeUsers.put(uuid, entry);
            } catch (Exception var9) {
               BetterQuesting.logger.log(Level.ERROR, "Unable to load UUID for quest", var9);
            }
         }

         this.tasks.readProgressFromNBT(json.getTagList("tasks", 10), merge);
      }
   }

   public void setClaimed(UUID uuid, long timestamp) {
      synchronized(this.completeUsers) {
         NBTTagCompound entry = this.getCompletionInfo(uuid);
         if (entry != null) {
            entry.setBoolean("claimed", true);
            entry.setLong("timestamp", timestamp);
         } else {
            entry = new NBTTagCompound();
            entry.setBoolean("claimed", true);
            entry.setLong("timestamp", timestamp);
            this.completeUsers.put(uuid, entry);
         }

      }
   }

   public <T> T getProperty(IPropertyType<T> prop) {
      return (T)this.qInfo.getProperty(prop);
   }

   public <T> T getProperty(IPropertyType<T> prop, T def) {
      return (T)this.qInfo.getProperty(prop, def);
   }

   public boolean hasProperty(IPropertyType<?> prop) {
      return this.qInfo.hasProperty(prop);
   }

   public <T> void setProperty(IPropertyType<T> prop, T value) {
      this.qInfo.setProperty(prop, value);
   }

   public void removeProperty(IPropertyType<?> prop) {
      this.qInfo.removeProperty(prop);
   }

   public void removeAllProps() {
      this.qInfo.removeAllProps();
   }
}
