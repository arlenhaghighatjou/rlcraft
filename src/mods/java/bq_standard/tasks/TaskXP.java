package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.XPHelper;
import bq_standard.client.gui.tasks.PanelTaskXP;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskXP;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Level;

public class TaskXP implements ITaskTickable {
   private final Set<UUID> completeUsers = new TreeSet();
   private final HashMap<UUID, Long> userProgress = new HashMap();
   public boolean levels = true;
   public int amount = 30;
   public boolean consume = true;

   public TaskXP() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskXP.INSTANCE.getRegistryName();
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void tickTask(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (!this.consume && pInfo.PLAYER.ticksExisted % 60 == 0) {
         long curProg = this.getUsersProgress(pInfo.UUID);
         long nxtProg = XPHelper.getPlayerXP(pInfo.PLAYER);
         if (curProg != nxtProg) {
            this.setUserProgress(pInfo.UUID, XPHelper.getPlayerXP(pInfo.PLAYER));
            pInfo.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }

         long rawXP = this.levels ? XPHelper.getLevelXP(this.amount) : (long)this.amount;
         long totalXP = this.getUsersProgress(pInfo.UUID);
         if (totalXP >= rawXP) {
            this.setComplete(pInfo.UUID);
         }

      }
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (!this.isComplete(pInfo.UUID)) {
         long progress = this.getUsersProgress(pInfo.UUID);
         long rawXP = this.levels ? XPHelper.getLevelXP(this.amount) : (long)this.amount;
         long plrXP = XPHelper.getPlayerXP(pInfo.PLAYER);
         long remaining = rawXP - progress;
         long cost = Math.min(remaining, plrXP);
         boolean changed = false;
         if (this.consume && cost != 0L) {
            progress = progress + cost;
            this.setUserProgress(pInfo.UUID, progress);
            XPHelper.addXP(pInfo.PLAYER, -cost);
            changed = true;
         } else if (!this.consume && progress != plrXP) {
            this.setUserProgress(pInfo.UUID, plrXP);
            changed = true;
         }

         long totalXP = this.getUsersProgress(pInfo.UUID);
         if (totalXP >= rawXP) {
            this.setComplete(pInfo.UUID);
            changed = true;
         }

         if (changed) {
            pInfo.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }

      }
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.xp";
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setInteger("amount", this.amount);
      json.setBoolean("isLevels", this.levels);
      json.setBoolean("consume", this.consume);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.amount = json.hasKey("amount", 99) ? json.getInteger("amount") : 30;
      this.levels = json.getBoolean("isLevels");
      this.consume = json.getBoolean("consume");
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
         this.userProgress.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var9) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load UUID for task", var9);
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            UUID uuid = UUID.fromString(pTag.getString("uuid"));
            this.userProgress.put(uuid, Long.valueOf(pTag.getLong("value")));
         } catch (Exception var8) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load user progress for task", var8);
         }
      }

   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      NBTTagList progArray = new NBTTagList();
      if (users != null) {
         users.forEach((uuid) -> {
            if (this.completeUsers.contains(uuid)) {
               jArray.appendTag(new NBTTagString(uuid.toString()));
            }

            Long data = (Long)this.userProgress.get(uuid);
            if (data != null) {
               NBTTagCompound pJson = new NBTTagCompound();
               pJson.setString("uuid", uuid.toString());
               pJson.setLong("value", data.longValue());
               progArray.appendTag(pJson);
            }

         });
      } else {
         this.completeUsers.forEach((uuid) -> {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         });
         this.userProgress.forEach((uuid, data) -> {
            NBTTagCompound pJson = new NBTTagCompound();
            pJson.setString("uuid", uuid.toString());
            pJson.setLong("value", data.longValue());
            progArray.appendTag(pJson);
         });
      }

      nbt.setTag("completeUsers", jArray);
      nbt.setTag("userProgress", progArray);
      return nbt;
   }

   public void resetUser(@Nullable UUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
         this.userProgress.clear();
      } else {
         this.completeUsers.remove(uuid);
         this.userProgress.remove(uuid);
      }

   }

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskXP(rect, this);
   }

   public GuiScreen getTaskEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }

   public void setUserProgress(UUID uuid, long progress) {
      this.userProgress.put(uuid, Long.valueOf(progress));
   }

   public long getUsersProgress(UUID uuid) {
      Long n = (Long)this.userProgress.get(uuid);
      return n == null ? 0L : n.longValue();
   }
}
