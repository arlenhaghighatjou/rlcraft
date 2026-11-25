package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.IFluidTask;
import betterquesting.api.questing.tasks.IItemTask;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskFluid;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskFluid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TaskFluid implements ITaskInventory, IFluidTask, IItemTask {
   private final Set<UUID> completeUsers = new TreeSet();
   public final NonNullList<FluidStack> requiredFluids = NonNullList.create();
   public final Map<UUID, int[]> userProgress = new HashMap();
   public boolean ignoreNbt = false;
   public boolean consume = true;
   public boolean groupDetect = false;
   public boolean autoConsume = false;

   public TaskFluid() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskFluid.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.fluid";
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void onInventoryChange(@Nonnull DBEntry<IQuest> quest, @Nonnull ParticipantInfo pInfo) {
      if (!this.consume || this.autoConsume) {
         this.detect(pInfo, quest);
      }

   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (!this.isComplete(pInfo.UUID)) {
         List<Tuple<UUID, int[]>> progress = this.getBulkProgress(this.consume ? Collections.singletonList(pInfo.UUID) : pInfo.ALL_UUIDS);
         boolean updated = false;
         if (!this.consume) {
            if (this.groupDetect) {
               progress.forEach((valuex) -> {
                  Arrays.fill((int[])value.getSecond(), 0);
               });
            } else {
               for(int i = 0; i < this.requiredFluids.size(); ++i) {
                  int r = ((FluidStack)this.requiredFluids.get(i)).amount;

                  for(Tuple<UUID, int[]> value : progress) {
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
                  IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
                  if (handler != null) {
                     boolean hasDrained = false;

                     for(int j = 0; j < this.requiredFluids.size(); ++j) {
                        FluidStack rStack = (FluidStack)this.requiredFluids.get(j);
                        FluidStack drainOG = rStack.copy();
                        if (this.ignoreNbt) {
                           drainOG.tag = null;
                        }

                        FluidStack sample = handler.drain(drainOG, false);
                        if (sample != null && sample.amount > 0) {
                           for(Tuple<UUID, int[]> value : progress) {
                              if (((int[])value.getSecond())[j] < rStack.amount) {
                                 int remaining = rStack.amount - ((int[])value.getSecond())[j];
                                 FluidStack drain = rStack.copy();
                                 drain.amount = remaining / stack.getCount();
                                 if (this.ignoreNbt) {
                                    drain.tag = null;
                                 }

                                 if (drain.amount > 0) {
                                    FluidStack fluid = handler.drain(drain, this.consume);
                                    if (fluid != null && fluid.amount > 0) {
                                       int[] var10000 = (int[])value.getSecond();
                                       var10000[j] += fluid.amount * stack.getCount();
                                       hasDrained = true;
                                       updated = true;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (hasDrained && this.consume) {
                        invo.setInventorySlotContents(i, handler.getContainer());
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
      List<Tuple<UUID, int[]>> progress = this.getBulkProgress(this.consume ? Collections.singletonList(pInfo.UUID) : pInfo.ALL_UUIDS);
      boolean updated = resync;

      label38:
      for(Tuple<UUID, int[]> value : progress) {
         boolean hasAll = true;

         for(int j = 0; j < this.requiredFluids.size(); ++j) {
            if (((int[])value.getSecond())[j] < ((FluidStack)this.requiredFluids.get(j)).amount) {
               continue label38;
            }
         }

         updated = true;
         if (!this.consume) {
            progress.forEach((pair) -> {
               this.setComplete((UUID)pair.getFirst());
            });
            break;
         }

         this.setComplete((UUID)value.getFirst());
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
      json.setBoolean("ignoreNBT", this.ignoreNbt);
      json.setBoolean("consume", this.consume);
      json.setBoolean("groupDetect", this.groupDetect);
      json.setBoolean("autoConsume", this.autoConsume);
      NBTTagList itemArray = new NBTTagList();

      for(FluidStack stack : this.requiredFluids) {
         itemArray.appendTag(stack.writeToNBT(new NBTTagCompound()));
      }

      json.setTag("requiredFluids", itemArray);
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.ignoreNbt = json.getBoolean("ignoreNBT");
      this.consume = json.getBoolean("consume");
      this.groupDetect = json.getBoolean("groupDetect");
      this.autoConsume = json.getBoolean("autoConsume");
      this.requiredFluids.clear();
      NBTTagList fList = json.getTagList("requiredFluids", 10);

      for(int i = 0; i < fList.tagCount(); ++i) {
         this.requiredFluids.add(JsonHelper.JsonToFluidStack(fList.getCompoundTagAt(i)));
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
            int[] data = new int[this.requiredFluids.size()];
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

   public void resetUser(UUID uuid) {
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
      return new PanelTaskFluid(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }

   public boolean canAcceptFluid(UUID owner, DBEntry<IQuest> quest, FluidStack fluid) {
      if (owner != null && fluid != null && fluid.getFluid() != null && this.consume && !this.isComplete(owner) && this.requiredFluids.size() > 0) {
         int[] progress = this.getUsersProgress(owner);

         for(int j = 0; j < this.requiredFluids.size(); ++j) {
            FluidStack rStack = ((FluidStack)this.requiredFluids.get(j)).copy();
            if (this.ignoreNbt) {
               rStack.tag = null;
            }

            if (progress[j] < rStack.amount && rStack.equals(fluid)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean canAcceptItem(UUID owner, DBEntry<IQuest> quest, ItemStack item) {
      if (owner != null && item != null && !item.isEmpty() && this.consume && !this.isComplete(owner) && this.requiredFluids.size() > 0) {
         IFluidHandlerItem handler = FluidUtil.getFluidHandler(item);
         if (handler == null) {
            return false;
         } else {
            for(IFluidTankProperties tank : handler.getTankProperties()) {
               if (tank.canDrain()) {
                  for(FluidStack rStack : this.requiredFluids) {
                     if (rStack.equals(tank.getContents())) {
                        return true;
                     }
                  }
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public FluidStack submitFluid(UUID owner, DBEntry<IQuest> quest, FluidStack fluid) {
      return this.submitFluidInternal(owner, quest, fluid, true);
   }

   private FluidStack submitFluidInternal(UUID owner, DBEntry<IQuest> quest, FluidStack fluid, boolean doFill) {
      if (owner != null && fluid != null && fluid.amount > 0 && this.consume && !this.isComplete(owner) && this.requiredFluids.size() > 0) {
         int[] progress = (int[])this.getUsersProgress(owner).clone();
         boolean updated = false;

         for(int j = 0; j < this.requiredFluids.size(); ++j) {
            FluidStack rStack = (FluidStack)this.requiredFluids.get(j);
            if (progress[j] < rStack.amount) {
               int remaining = rStack.amount - progress[j];
               if (rStack.isFluidEqual(fluid)) {
                  int removed = Math.min(fluid.amount, remaining);
                  progress[j] += removed;
                  fluid.amount -= removed;
                  updated = true;
                  if (fluid.amount <= 0) {
                     fluid = null;
                     break;
                  }
               }
            }
         }

         if (updated && doFill) {
            this.setUserProgress(owner, progress);
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUUID(owner);
            if (player != null) {
               this.checkAndComplete(new ParticipantInfo(player), quest, true);
            } else {
               boolean hasAll = true;

               for(int j = 0; j < this.requiredFluids.size(); ++j) {
                  if (progress[j] < ((FluidStack)this.requiredFluids.get(j)).amount) {
                     hasAll = false;
                     break;
                  }
               }

               if (hasAll) {
                  this.setComplete(owner);
               }
            }
         }

         return fluid;
      } else {
         return fluid;
      }
   }

   public ItemStack submitItem(UUID owner, DBEntry<IQuest> quest, ItemStack input) {
      if (owner != null && !input.isEmpty() && this.consume && !this.isComplete(owner)) {
         ItemStack item = input.splitStack(1);
         IFluidHandlerItem handler = FluidUtil.getFluidHandler(item);
         if (handler == null) {
            return item;
         } else {
            boolean hasDrained = false;

            for(IFluidTankProperties tank : handler.getTankProperties()) {
               if (tank.canDrain() && tank.getContents() != null && tank.canDrainFluidType(tank.getContents())) {
                  FluidStack remaining = this.submitFluidInternal(owner, quest, tank.getContents().copy(), false);
                  FluidStack drain = tank.getContents().copy();
                  drain.amount -= remaining == null ? 0 : remaining.amount;
                  if (drain.amount > 0) {
                     this.submitFluidInternal(owner, quest, handler.drain(drain, true), true);
                     hasDrained = true;
                  }
               }
            }

            return hasDrained ? handler.getContainer() : item;
         }
      } else {
         return input;
      }
   }

   private void setUserProgress(UUID uuid, int[] progress) {
      this.userProgress.put(uuid, progress);
   }

   public int[] getUsersProgress(UUID uuid) {
      int[] progress = (int[])this.userProgress.get(uuid);
      return progress != null && progress.length == this.requiredFluids.size() ? progress : new int[this.requiredFluids.size()];
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
