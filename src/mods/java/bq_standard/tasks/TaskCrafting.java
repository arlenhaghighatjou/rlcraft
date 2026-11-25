package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskCrafting;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskCrafting;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskCrafting implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   public final NonNullList<BigItemStack> requiredItems = NonNullList.create();
   public final HashMap<UUID, int[]> userProgress = new HashMap();
   public boolean partialMatch = true;
   public boolean ignoreNBT = false;
   public boolean allowAnvil = false;
   public boolean allowSmelt = true;
   public boolean allowCraft = true;

   public TaskCrafting() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskCrafting.INSTANCE.getRegistryName();
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.crafting";
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      pInfo.ALL_UUIDS.forEach((uuid) -> {
         if (!this.isComplete(uuid)) {
            int[] tmp = this.getUsersProgress(uuid);

            for(int i = 0; i < this.requiredItems.size(); ++i) {
               BigItemStack rStack = (BigItemStack)this.requiredItems.get(i);
               if (tmp[i] < rStack.stackSize) {
                  return;
               }
            }

            this.setComplete(uuid);
         }
      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   public void onItemCraft(ParticipantInfo pInfo, DBEntry<IQuest> quest, ItemStack stack) {
      if (this.allowCraft) {
         this.onItemInternal(pInfo, quest, stack);
      }
   }

   public void onItemSmelt(ParticipantInfo pInfo, DBEntry<IQuest> quest, ItemStack stack) {
      if (this.allowSmelt) {
         this.onItemInternal(pInfo, quest, stack);
      }
   }

   public void onItemAnvil(ParticipantInfo pInfo, DBEntry<IQuest> quest, ItemStack stack) {
      if (this.allowAnvil) {
         this.onItemInternal(pInfo, quest, stack);
      }
   }

   private void onItemInternal(ParticipantInfo pInfo, DBEntry<IQuest> quest, ItemStack stack) {
      if (!stack.isEmpty()) {
         List<Tuple<UUID, int[]>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
         boolean changed = false;

         for(int i = 0; i < this.requiredItems.size(); ++i) {
            BigItemStack rStack = (BigItemStack)this.requiredItems.get(i);
            if (ItemComparison.StackMatch(rStack.getBaseStack(), stack, !this.ignoreNBT, this.partialMatch) || ItemComparison.OreDictionaryMatch(rStack.getOreIngredient(), rStack.GetTagCompound(), stack, !this.ignoreNBT, this.partialMatch)) {
               progress.forEach((entry) -> {
                  if (((int[])entry.getSecond())[index] < rStack.stackSize) {
                     ((int[])entry.getSecond())[index] = Math.min(((int[])entry.getSecond())[index] + stack.getCount(), rStack.stackSize);
                  }
               });
               changed = true;
            }
         }

         if (changed) {
            this.setBulkProgress(progress);
            this.detect(pInfo, quest);
         }

      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setBoolean("partialMatch", this.partialMatch);
      nbt.setBoolean("ignoreNBT", this.ignoreNBT);
      nbt.setBoolean("allowCraft", this.allowCraft);
      nbt.setBoolean("allowSmelt", this.allowSmelt);
      nbt.setBoolean("allowAnvil", this.allowAnvil);
      NBTTagList itemArray = new NBTTagList();

      for(BigItemStack stack : this.requiredItems) {
         itemArray.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
      }

      nbt.setTag("requiredItems", itemArray);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.partialMatch = nbt.getBoolean("partialMatch");
      this.ignoreNBT = nbt.getBoolean("ignoreNBT");
      if (nbt.hasKey("allowCraft")) {
         this.allowCraft = nbt.getBoolean("allowCraft");
      }

      if (nbt.hasKey("allowSmelt")) {
         this.allowSmelt = nbt.getBoolean("allowSmelt");
      }

      if (nbt.hasKey("allowAnvil")) {
         this.allowAnvil = nbt.getBoolean("allowAnvil");
      }

      this.requiredItems.clear();
      NBTTagList iList = nbt.getTagList("requiredItems", 10);

      for(int i = 0; i < iList.tagCount(); ++i) {
         this.requiredItems.add(JsonHelper.JsonToItemStack(iList.getCompoundTagAt(i)));
      }

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
         } catch (Exception var11) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load UUID for task", var11);
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            UUID uuid = UUID.fromString(pTag.getString("uuid"));
            int[] data = new int[this.requiredItems.size()];
            NBTTagList dNbt = pTag.getTagList("data", 3);

            for(int i = 0; i < data.length && i < dNbt.tagCount(); ++i) {
               data[i] = dNbt.getIntAt(i);
            }

            this.userProgress.put(uuid, data);
         } catch (Exception var12) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load user progress for task", var12);
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

            int[] data = (int[])this.userProgress.get(uuid);
            if (data != null) {
               NBTTagCompound pJson = new NBTTagCompound();
               pJson.setString("uuid", uuid.toString());
               NBTTagList pArray = new NBTTagList();

               for(int i : data) {
                  pArray.appendTag(new NBTTagInt(i));
               }

               pJson.setTag("data", pArray);
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
            NBTTagList pArray = new NBTTagList();

            for(int i : data) {
               pArray.appendTag(new NBTTagInt(i));
            }

            pJson.setTag("data", pArray);
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

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> context) {
      return new PanelTaskCrafting(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }

   private void setUserProgress(UUID uuid, int[] progress) {
      this.userProgress.put(uuid, progress);
   }

   public int[] getUsersProgress(UUID uuid) {
      int[] progress = (int[])this.userProgress.get(uuid);
      return progress != null && progress.length == this.requiredItems.size() ? progress : new int[this.requiredItems.size()];
   }

   private List<Tuple<UUID, int[]>> getBulkProgress(@Nonnull List<UUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<UUID, int[]>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }

   private void setBulkProgress(@Nonnull List<Tuple<UUID, int[]>> list) {
      list.forEach((entry) -> {
         this.setUserProgress((UUID)entry.getFirst(), (int[])entry.getSecond());
      });
   }
}
