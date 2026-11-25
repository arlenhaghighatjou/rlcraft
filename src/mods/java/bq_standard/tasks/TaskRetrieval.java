package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.IItemTask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskRetrieval;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskRetrieval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskRetrieval implements ITaskInventory, IItemTask {
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   public final NonNullList<BigItemStack> requiredItems = NonNullList.create();
   private final HashMap<EaglercraftUUID, int[]> userProgress = new HashMap();
   public boolean partialMatch = true;
   public boolean ignoreNBT = false;
   public boolean consume = true;
   public boolean groupDetect = false;
   public boolean autoConsume = false;

   public TaskRetrieval() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.retrieval";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskRetrieval.INSTANCE.getRegistryName();
   }

   public boolean isComplete(EaglercraftUUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(EaglercraftUUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void onInventoryChange(@Nonnull DBEntry<IQuest> quest, @Nonnull ParticipantInfo pInfo) {
      if (!this.consume || this.autoConsume) {
         this.detect(pInfo, quest);
      }

   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (!this.isComplete(pInfo.EaglercraftUUID)) {
         List<Tuple<EaglercraftUUID, int[]>> progress = this.getBulkProgress(this.consume ? Collections.singletonList(pInfo.EaglercraftUUID) : pInfo.ALL_UUIDS);
         boolean updated = false;
         if (!this.consume) {
            if (this.groupDetect) {
               progress.forEach((valuex) -> {
                  Arrays.fill((int[])value.getSecond(), 0);
               });
            } else {
               for(int i = 0; i < this.requiredItems.size(); ++i) {
                  int r = ((BigItemStack)this.requiredItems.get(i)).stackSize;

                  for(Tuple<EaglercraftUUID, int[]> value : progress) {
                     int n = ((int[])value.getSecond())[i];
                     if (n != 0 && n < r) {
                        ((int[])value.getSecond())[i] = 0;
                        updated = true;
                     }
                  }
               }
            }
         }

         List<InventoryPlayer> invoList;
         if (this.consume) {
            invoList = Collections.singletonList(pInfo.PLAYER.inventory);
         } else {
            invoList = new ArrayList();
            pInfo.ACTIVE_PLAYERS.forEach((p) -> {
               invoList.add(p.inventory);
            });
         }

         for(InventoryPlayer invo : invoList) {
            for(int i = 0; i < invo.getSizeInventory(); ++i) {
               ItemStack stack = invo.getStackInSlot(i);
               if (!stack.isEmpty()) {
                  int[] remCounts = new int[progress.size()];
                  Arrays.fill(remCounts, stack.getCount());

                  for(int j = 0; j < this.requiredItems.size(); ++j) {
                     BigItemStack rStack = (BigItemStack)this.requiredItems.get(j);
                     if (ItemComparison.StackMatch(rStack.getBaseStack(), stack, !this.ignoreNBT, this.partialMatch) || ItemComparison.OreDictionaryMatch(rStack.getOreIngredient(), rStack.GetTagCompound(), stack, !this.ignoreNBT, this.partialMatch)) {
                        for(int n = 0; n < progress.size(); ++n) {
                           Tuple<EaglercraftUUID, int[]> value = (Tuple)progress.get(n);
                           if (((int[])value.getSecond())[j] < rStack.stackSize) {
                              int remaining = rStack.stackSize - ((int[])value.getSecond())[j];
                              if (this.consume) {
                                 ItemStack removed = invo.decrStackSize(i, remaining);
                                 int[] var10000 = (int[])value.getSecond();
                                 var10000[j] += removed.getCount();
                              } else {
                                 int temp = Math.min(remaining, remCounts[n]);
                                 remCounts[n] -= temp;
                                 int[] var23 = (int[])value.getSecond();
                                 var23[j] += temp;
                              }

                              updated = true;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (updated) {
            this.setBulkProgress(progress);
         }

         this.checkAndComplete(pInfo, quest, updated);
      }
   }

   private void checkAndComplete(ParticipantInfo pInfo, DBEntry<IQuest> quest, boolean resync) {
      List<Tuple<EaglercraftUUID, int[]>> progress = this.getBulkProgress(this.consume ? Collections.singletonList(pInfo.EaglercraftUUID) : pInfo.ALL_UUIDS);
      boolean updated = resync;

      label38:
      for(Tuple<EaglercraftUUID, int[]> value : progress) {
         for(int j = 0; j < this.requiredItems.size(); ++j) {
            if (((int[])value.getSecond())[j] < ((BigItemStack)this.requiredItems.get(j)).stackSize) {
               continue label38;
            }
         }

         updated = true;
         if (!this.consume) {
            progress.forEach((pair) -> {
               this.setComplete((EaglercraftUUID)pair.getFirst());
            });
            break;
         }

         this.setComplete((EaglercraftUUID)value.getFirst());
      }

      if (updated) {
         if (this.consume) {
            pInfo.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
         } else {
            pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setBoolean("partialMatch", this.partialMatch);
      json.setBoolean("ignoreNBT", this.ignoreNBT);
      json.setBoolean("consume", this.consume);
      json.setBoolean("groupDetect", this.groupDetect);
      json.setBoolean("autoConsume", this.autoConsume);
      NBTTagList itemArray = new NBTTagList();

      for(BigItemStack stack : this.requiredItems) {
         itemArray.appendTag(JsonHelper.ItemStackToJson(stack, new NBTTagCompound()));
      }

      json.setTag("requiredItems", itemArray);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.partialMatch = json.getBoolean("partialMatch");
      this.ignoreNBT = json.getBoolean("ignoreNBT");
      this.consume = json.getBoolean("consume");
      this.groupDetect = json.getBoolean("groupDetect");
      this.autoConsume = json.getBoolean("autoConsume");
      this.requiredItems.clear();
      NBTTagList iList = json.getTagList("requiredItems", 10);

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
            this.completeUsers.add(EaglercraftUUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var11) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load EaglercraftUUID for task", var11);
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            EaglercraftUUID uuid = EaglercraftUUID.fromString(pTag.getString("uuid"));
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

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
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

   public void resetUser(@Nullable EaglercraftUUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
         this.userProgress.clear();
      } else {
         this.completeUsers.remove(uuid);
         this.userProgress.remove(uuid);
      }

   }

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskRetrieval(rect, this);
   }

   public boolean canAcceptItem(EaglercraftUUID owner, DBEntry<IQuest> quest, ItemStack stack) {
      if (owner != null && stack != null && !stack.isEmpty() && this.consume && !this.isComplete(owner) && this.requiredItems.size() > 0) {
         int[] progress = this.getUsersProgress(owner);

         for(int j = 0; j < this.requiredItems.size(); ++j) {
            BigItemStack rStack = (BigItemStack)this.requiredItems.get(j);
            if (progress[j] < rStack.stackSize && (ItemComparison.StackMatch(rStack.getBaseStack(), stack, !this.ignoreNBT, this.partialMatch) || ItemComparison.OreDictionaryMatch(rStack.getOreIngredient(), rStack.GetTagCompound(), stack, !this.ignoreNBT, this.partialMatch))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public ItemStack submitItem(EaglercraftUUID owner, DBEntry<IQuest> quest, ItemStack input) {
      if (owner != null && !input.isEmpty() && this.consume && !this.isComplete(owner)) {
         ItemStack stack = input.copy();
         int[] progress = this.getUsersProgress(owner);
         boolean updated = false;

         for(int j = 0; j < this.requiredItems.size() && !stack.isEmpty(); ++j) {
            BigItemStack rStack = (BigItemStack)this.requiredItems.get(j);
            if (progress[j] < rStack.stackSize) {
               int remaining = rStack.stackSize - progress[j];
               if (ItemComparison.StackMatch(rStack.getBaseStack(), stack, !this.ignoreNBT, this.partialMatch) || ItemComparison.OreDictionaryMatch(rStack.getOreIngredient(), rStack.GetTagCompound(), stack, !this.ignoreNBT, this.partialMatch)) {
                  int removed = Math.min(stack.getCount(), remaining);
                  stack.shrink(removed);
                  progress[j] += removed;
                  updated = true;
                  if (stack.isEmpty()) {
                     break;
                  }
               }
            }
         }

         if (updated) {
            this.setUserProgress(owner, progress);
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUUID(owner);
            if (player != null) {
               this.checkAndComplete(new ParticipantInfo(player), quest, true);
            } else {
               boolean hasAll = true;

               for(int j = 0; j < this.requiredItems.size(); ++j) {
                  if (progress[j] < ((BigItemStack)this.requiredItems.get(j)).stackSize) {
                     hasAll = false;
                     break;
                  }
               }

               if (hasAll) {
                  this.setComplete(owner);
               }
            }
         }

         return stack.isEmpty() ? ItemStack.EMPTY : stack;
      } else {
         return input;
      }
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }

   public void setUserProgress(EaglercraftUUID uuid, int[] progress) {
      this.userProgress.put(uuid, progress);
   }

   public int[] getUsersProgress(EaglercraftUUID uuid) {
      int[] progress = (int[])this.userProgress.get(uuid);
      return progress != null && progress.length == this.requiredItems.size() ? progress : new int[this.requiredItems.size()];
   }

   private List<Tuple<EaglercraftUUID, int[]>> getBulkProgress(@Nonnull List<EaglercraftUUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<EaglercraftUUID, int[]>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }

   private void setBulkProgress(@Nonnull List<Tuple<EaglercraftUUID, int[]>> list) {
      list.forEach((entry) -> {
         this.setUserProgress((EaglercraftUUID)entry.getFirst(), (int[])entry.getSecond());
      });
   }
}
