package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskInteractEntity;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskInteractEntity;
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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskInteractEntity implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   private final HashMap<UUID, Integer> userProgress = new HashMap();
   public BigItemStack targetItem = new BigItemStack(Items.AIR);
   public boolean ignoreItemNBT = false;
   public boolean partialItemMatch = true;
   public boolean useMainHand = true;
   public boolean useOffHand = true;
   public String entityID = "minecraft:villager";
   public NBTTagCompound entityTags = new NBTTagCompound();
   public boolean entitySubtypes = true;
   public boolean ignoreEntityNBT = true;
   public boolean onInteract = true;
   public boolean onHit = false;
   public int required = 1;

   public TaskInteractEntity() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.interact_entity";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskInteractEntity.INSTANCE.getRegistryName();
   }

   public void onInteract(ParticipantInfo pInfo, DBEntry<IQuest> quest, EnumHand hand, ItemStack item, Entity entity, boolean isHit) {
      if ((this.onHit || !isHit) && (this.onInteract || isHit)) {
         if ((this.useMainHand || hand != EnumHand.MAIN_HAND) && (this.useOffHand || hand != EnumHand.OFF_HAND)) {
            ResourceLocation targetRes = new ResourceLocation(this.entityID);
            Class<? extends Entity> targetClass = EntityList.getClass(targetRes);
            if (targetClass != null) {
               Class<? extends Entity> subjectClass = entity.getClass();
               ResourceLocation subjectRes = EntityList.getKey(entity);
               if (subjectRes != null) {
                  if (this.entitySubtypes) {
                     if (!targetClass.isAssignableFrom(subjectClass)) {
                        return;
                     }
                  } else if (!subjectRes.equals(targetRes)) {
                     return;
                  }

                  if (!this.ignoreEntityNBT) {
                     NBTTagCompound subjectTags = new NBTTagCompound();
                     entity.writeToNBTOptional(subjectTags);
                     if (!ItemComparison.CompareNBTTag(this.entityTags, subjectTags, true)) {
                        return;
                     }
                  }

                  if (this.targetItem.getBaseStack().getItem() != Items.AIR) {
                     if (this.targetItem.hasOreDict() && !ItemComparison.OreDictionaryMatch(this.targetItem.getOreIngredient(), this.targetItem.GetTagCompound(), item, !this.ignoreItemNBT, this.partialItemMatch)) {
                        return;
                     }

                     if (!ItemComparison.StackMatch(this.targetItem.getBaseStack(), item, !this.ignoreItemNBT, this.partialItemMatch)) {
                        return;
                     }
                  }

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

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      List<Tuple<UUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
      progress.forEach((value) -> {
         if (((Integer)value.getSecond()).intValue() >= this.required) {
            this.setComplete((UUID)value.getFirst());
         }

      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
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
      return new PanelTaskInteractEntity(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
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

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("item", this.targetItem.writeToNBT(new NBTTagCompound()));
      nbt.setBoolean("ignoreItemNBT", this.ignoreItemNBT);
      nbt.setBoolean("partialItemMatch", this.partialItemMatch);
      nbt.setString("targetID", this.entityID);
      nbt.setTag("targetNBT", this.entityTags);
      nbt.setBoolean("ignoreTargetNBT", this.ignoreEntityNBT);
      nbt.setBoolean("targetSubtypes", this.entitySubtypes);
      nbt.setBoolean("allowMainHand", this.useMainHand);
      nbt.setBoolean("allowOffHand", this.useOffHand);
      nbt.setInteger("requiredUses", this.required);
      nbt.setBoolean("onInteract", this.onInteract);
      nbt.setBoolean("onHit", this.onHit);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.targetItem = new BigItemStack(nbt.getCompoundTag("item"));
      this.ignoreItemNBT = nbt.getBoolean("ignoreItemNBT");
      this.partialItemMatch = nbt.getBoolean("partialItemMatch");
      this.entityID = nbt.getString("targetID");
      this.entityTags = nbt.getCompoundTag("targetNBT");
      this.ignoreEntityNBT = nbt.getBoolean("ignoreTargetNBT");
      this.entitySubtypes = nbt.getBoolean("targetSubtypes");
      this.useMainHand = nbt.getBoolean("allowMainHand");
      this.useOffHand = nbt.getBoolean("allowOffHand");
      this.required = nbt.getInteger("requiredUses");
      this.onInteract = nbt.getBoolean("onInteract");
      this.onHit = nbt.getBoolean("onHit");
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
