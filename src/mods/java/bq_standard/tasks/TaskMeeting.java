package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.editors.tasks.GuiEditTaskMeeting;
import bq_standard.client.gui.tasks.PanelTaskMeeting;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskMeeting;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskMeeting implements ITaskTickable {
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   public String idName = "minecraft:villager";
   public int range = 4;
   public int amount = 1;
   public boolean ignoreNBT = true;
   public boolean subtypes = true;
   public NBTTagCompound targetTags = new NBTTagCompound();

   public TaskMeeting() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskMeeting.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.meeting";
   }

   public boolean isComplete(EaglercraftUUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(EaglercraftUUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable EaglercraftUUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
      } else {
         this.completeUsers.remove(uuid);
      }

   }

   public void tickTask(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER.ticksExisted % 60 == 0) {
         this.detect(pInfo, quest);
      }

   }

   public void detect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER.isEntityAlive()) {
         ResourceLocation targetID = new ResourceLocation(this.idName);
         Class<? extends Entity> target = EntityList.getClass(targetID);
         if (target != null) {
            List<Entity> list = pInfo.PLAYER.world.getEntitiesWithinAABBExcludingEntity(pInfo.PLAYER, pInfo.PLAYER.getEntityBoundingBox().expand((double)this.range, (double)this.range, (double)this.range));
            int n = 0;
            Iterator var7 = list.iterator();

            while(true) {
               while(true) {
                  if (!var7.hasNext()) {
                     return;
                  }

                  Entity entity = (Entity)var7.next();
                  Class<? extends Entity> subject = entity.getClass();
                  ResourceLocation subjectID = EntityList.getKey(subject);
                  if (subjectID != null && (!this.subtypes || target.isAssignableFrom(subject)) && (this.subtypes || subjectID.equals(targetID))) {
                     if (this.ignoreNBT) {
                        break;
                     }

                     NBTTagCompound subjectTags = new NBTTagCompound();
                     entity.writeToNBTOptional(subjectTags);
                     if (ItemComparison.CompareNBTTag(this.targetTags, subjectTags, true)) {
                        break;
                     }
                  }
               }

               ++n;
               if (n >= this.amount) {
                  break;
               }
            }

            pInfo.ALL_UUIDS.forEach((uuid) -> {
               if (!this.isComplete(uuid)) {
                  this.setComplete(uuid);
               }

            });
            pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }
      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setString("target", this.idName);
      json.setInteger("range", this.range);
      json.setInteger("amount", this.amount);
      json.setBoolean("subtypes", this.subtypes);
      json.setBoolean("ignoreNBT", this.ignoreNBT);
      json.setTag("targetNBT", this.targetTags);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.idName = json.hasKey("target", 8) ? json.getString("target") : "minecraft:villager";
      this.range = json.getInteger("range");
      this.amount = json.getInteger("amount");
      this.subtypes = json.getBoolean("subtypes");
      this.ignoreNBT = json.getBoolean("ignoreNBT");
      this.targetTags = json.getCompoundTag("targetNBT");
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
      NBTTagList jArray = new NBTTagList();
      this.completeUsers.forEach((uuid) -> {
         if (users == null || users.contains(uuid)) {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         }

      });
      nbt.setTag("completeUsers", jArray);
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(EaglercraftUUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load EaglercraftUUID for task", var6);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return new GuiEditTaskMeeting(parent, quest, this);
   }

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskMeeting(rect, this);
   }
}
