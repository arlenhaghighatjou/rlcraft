package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.editors.tasks.GuiEditTaskHunt;
import bq_standard.client.gui.tasks.PanelTaskHunt;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskHunt;
import java.util.ArrayList;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskHunt implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   private final HashMap<UUID, Integer> userProgress = new HashMap();
   public String idName = "minecraft:zombie";
   public String damageType = "";
   public int required = 1;
   public boolean ignoreNBT = true;
   public boolean subtypes = true;
   public NBTTagCompound targetTags = new NBTTagCompound();

   public TaskHunt() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskHunt.INSTANCE.getRegistryName();
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.hunt";
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      List<Tuple<UUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
      progress.forEach((value) -> {
         if (((Integer)value.getSecond()).intValue() >= this.required) {
            this.setComplete((UUID)value.getFirst());
         }

      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   public void onKilledByPlayer(ParticipantInfo pInfo, DBEntry<IQuest> quest, @Nonnull EntityLivingBase entity, DamageSource source) {
      if (this.damageType.length() <= 0 || source != null && this.damageType.equalsIgnoreCase(source.damageType)) {
         Class<? extends Entity> subject = entity.getClass();
         ResourceLocation targetID = new ResourceLocation(this.idName);
         Class<? extends Entity> target = EntityList.getClass(targetID);
         ResourceLocation subjectID = EntityList.getKey(subject);
         if (subjectID != null && target != null) {
            if (!this.subtypes || target.isAssignableFrom(subject)) {
               if (this.subtypes || subjectID.equals(targetID)) {
                  NBTTagCompound subjectTags = new NBTTagCompound();
                  entity.writeToNBTOptional(subjectTags);
                  if (this.ignoreNBT || ItemComparison.CompareNBTTag(this.targetTags, subjectTags, true)) {
                     List<Tuple<UUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
                     progress.forEach((value) -> {
                        if (!this.isComplete((UUID)value.getFirst())) {
                           int np = Math.min(this.required, ((Integer)value.getSecond()).intValue() + 1);
                           this.setUserProgress((UUID)value.getFirst(), np);
                           if (np >= this.required) {
                              this.setComplete((UUID)value.getFirst());
                           }

                        }
                     });
                     pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
                  }
               }
            }
         }
      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setString("target", this.idName);
      json.setInteger("required", this.required);
      json.setBoolean("subtypes", this.subtypes);
      json.setBoolean("ignoreNBT", this.ignoreNBT);
      json.setTag("targetNBT", this.targetTags);
      json.setString("damageType", this.damageType);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.idName = json.getString("target");
      this.required = json.getInteger("required");
      this.subtypes = json.getBoolean("subtypes");
      this.ignoreNBT = json.getBoolean("ignoreNBT");
      this.targetTags = json.getCompoundTag("targetNBT");
      this.damageType = json.getString("damageType");
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
            this.userProgress.put(uuid, Integer.valueOf(pTag.getInteger("value")));
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

            Integer data = (Integer)this.userProgress.get(uuid);
            if (data != null) {
               NBTTagCompound pJson = new NBTTagCompound();
               pJson.setString("uuid", uuid.toString());
               pJson.setInteger("value", data.intValue());
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
            pJson.setInteger("value", data.intValue());
            progArray.appendTag(pJson);
         });
      }

      nbt.setTag("completeUsers", jArray);
      nbt.setTag("userProgress", progArray);
      return nbt;
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return new GuiEditTaskHunt(parent, quest, this);
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

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskHunt(rect, this);
   }

   private void setUserProgress(UUID uuid, int progress) {
      this.userProgress.put(uuid, Integer.valueOf(progress));
   }

   public int getUsersProgress(UUID uuid) {
      Integer n = (Integer)this.userProgress.get(uuid);
      return n == null ? 0 : n.intValue();
   }

   private List<Tuple<UUID, Integer>> getBulkProgress(@Nonnull List<UUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<UUID, Integer>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }
}
