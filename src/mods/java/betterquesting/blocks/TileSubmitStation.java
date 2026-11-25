package betterquesting.blocks;

import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.IFluidTask;
import betterquesting.api.questing.tasks.IItemTask;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import betterquesting.core.BetterQuesting;
import betterquesting.questing.QuestDatabase;
import betterquesting.storage.QuestSettings;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileSubmitStation extends TileEntity implements IFluidHandler, ISidedInventory, ITickable, IFluidTankProperties {
   private final IItemHandler itemHandler = new SSItemHandler(this);
   private final IFluidHandler fluidHandler = this;
   private NonNullList<ItemStack> itemStack = NonNullList.withSize(2, ItemStack.EMPTY);
   private boolean needsUpdate = false;
   public UUID owner = null;
   public int questID = -1;
   public int taskID = -1;
   private DBEntry<IQuest> qCached;
   private static final int[] slotsForFace = new int[]{0, 1};

   public TileSubmitStation() {
   }

   public DBEntry<IQuest> getQuest() {
      if (this.questID < 0) {
         return null;
      } else {
         if (this.qCached == null) {
            IQuest tmp = (IQuest)QuestDatabase.INSTANCE.getValue(this.questID);
            if (tmp != null) {
               this.qCached = new DBEntry<IQuest>(this.questID, tmp);
            }
         }

         return this.qCached;
      }
   }

   public ITask getRawTask() {
      DBEntry<IQuest> q = this.getQuest();
      return q != null && this.taskID >= 0 ? (ITask)((IQuest)q.getValue()).getTasks().getValue(this.taskID) : null;
   }

   public IItemTask getItemTask() {
      ITask t = this.getRawTask();
      return t == null ? null : (t instanceof IItemTask ? (IItemTask)t : null);
   }

   public IFluidTask getFluidTask() {
      ITask t = this.getRawTask();
      return t == null ? null : (t instanceof IFluidTask ? (IFluidTask)t : null);
   }

   public int getSizeInventory() {
      return 2;
   }

   @Nonnull
   public ItemStack getStackInSlot(int idx) {
      return idx >= 0 && idx < this.itemStack.size() ? (ItemStack)this.itemStack.get(idx) : ItemStack.EMPTY;
   }

   @Nonnull
   public ItemStack decrStackSize(int idx, int amount) {
      return ItemStackHelper.getAndSplit(this.itemStack, idx, amount);
   }

   public void setInventorySlotContents(int idx, @Nonnull ItemStack stack) {
      if (idx >= 0 && idx < this.itemStack.size()) {
         this.itemStack.set(idx, stack);
      }
   }

   @Nonnull
   public String getName() {
      return BetterQuesting.submitStation.getLocalizedName();
   }

   public boolean hasCustomName() {
      return false;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
      return (this.owner == null || player.getUniqueID().equals(this.owner)) && player.getDistanceSq(this.pos) < 256.0D;
   }

   public void openInventory(@Nonnull EntityPlayer player) {
   }

   public void closeInventory(@Nonnull EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int idx, @Nonnull ItemStack stack) {
      if (idx == 0 && this.isSetup()) {
         IItemTask t = this.getItemTask();
         return t != null && ((ItemStack)this.itemStack.get(idx)).isEmpty() && !t.isComplete(this.owner) && t.canAcceptItem(this.owner, this.getQuest(), stack);
      } else {
         return false;
      }
   }

   public int fill(FluidStack fluid, boolean doFill) {
      IFluidTask t = this.getFluidTask();
      if (this.isSetup() && t != null) {
         int amount = fluid.amount;
         int consumed = !doFill ? amount : 0;
         if (doFill) {
            FluidStack remainder = t.submitFluid(this.owner, this.getQuest(), fluid);
            consumed = remainder != null ? amount - remainder.amount : amount;
            if (t.isComplete(this.owner)) {
               this.needsUpdate = true;
               this.reset();
               if (this.world.getMinecraftServer() != null) {
                  this.world.getMinecraftServer().getPlayerList().sendToAllNearExcept((EntityPlayer)null, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 128.0D, this.world.provider.getDimension(), this.getUpdatePacket());
               }
            } else {
               this.needsUpdate = consumed > 0;
            }
         }

         return consumed;
      } else {
         return 0;
      }
   }

   public FluidStack drain(FluidStack resource, boolean doDrain) {
      return null;
   }

   public FluidStack drain(int maxDrain, boolean doDrain) {
      return null;
   }

   public boolean canFill() {
      return true;
   }

   public boolean canFillFluidType(FluidStack fluid) {
      IFluidTask t = this.getFluidTask();
      return t != null && !t.isComplete(this.owner) && t.canAcceptFluid(this.owner, this.getQuest(), new FluidStack(fluid, 1));
   }

   public boolean canDrain() {
      return false;
   }

   public boolean canDrainFluidType(FluidStack fluid) {
      return false;
   }

   public int getCapacity() {
      return Integer.MAX_VALUE;
   }

   public FluidStack getContents() {
      return null;
   }

   public IFluidTankProperties[] getTankProperties() {
      return new IFluidTankProperties[]{this};
   }

   public void update() {
      if (!this.world.isRemote && this.isSetup() && !((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
         long wtt = this.world.getTotalWorldTime();
         if (wtt % 5L == 0L && this.owner != null) {
            if (wtt % 20L == 0L) {
               this.qCached = null;
            }

            DBEntry<IQuest> q = this.getQuest();
            IItemTask t = this.getItemTask();
            MinecraftServer server = this.world.getMinecraftServer();
            EntityPlayerMP player = server == null ? null : server.getPlayerList().getPlayerByUUID(this.owner);
            QuestCache qc = player == null ? null : (QuestCache)player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, (EnumFacing)null);
            if (q != null && t != null && !((ItemStack)this.itemStack.get(0)).isEmpty() && ((ItemStack)this.itemStack.get(1)).isEmpty()) {
               ItemStack inStack = ((ItemStack)this.itemStack.get(0)).copy();
               ItemStack beforeStack = ((ItemStack)this.itemStack.get(0)).copy();
               if (t.canAcceptItem(this.owner, this.getQuest(), inStack)) {
                  this.itemStack.set(0, t.submitItem(this.owner, this.getQuest(), inStack));
                  if (t.isComplete(this.owner) || !((ItemStack)this.itemStack.get(0)).equals(beforeStack)) {
                     this.needsUpdate = true;
                  }
               } else {
                  this.itemStack.set(1, inStack);
                  this.itemStack.set(0, ItemStack.EMPTY);
               }
            }

            if (t != null && t.isComplete(this.owner)) {
               this.reset();
               this.world.getMinecraftServer().getPlayerList().sendToAllNearExcept((EntityPlayer)null, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 128.0D, this.world.provider.getDimension(), this.getUpdatePacket());
               this.needsUpdate = true;
            }

            if (this.needsUpdate) {
               if (q != null && qc != null) {
                  qc.markQuestDirty(q.getID());
               }

               this.needsUpdate = false;
            }
         }

      }
   }

   public void setupTask(UUID owner, IQuest quest, ITask task) {
      if (owner != null && quest != null && task != null) {
         this.questID = QuestDatabase.INSTANCE.getID(quest);
         this.qCached = new DBEntry<IQuest>(this.questID, quest);
         this.taskID = quest.getTasks().getID(task);
         if (this.questID >= 0 && this.taskID >= 0) {
            this.owner = owner;
            this.markDirty();
         } else {
            this.reset();
         }
      } else {
         this.reset();
      }
   }

   public boolean isSetup() {
      return this.owner != null && this.questID >= 0 && this.taskID >= 0;
   }

   public void reset() {
      this.owner = null;
      this.questID = -1;
      this.taskID = -1;
      this.qCached = null;
      this.markDirty();
   }

   @Nonnull
   public SPacketUpdateTileEntity getUpdatePacket() {
      return new SPacketUpdateTileEntity(this.pos, 0, this.writeToNBT(new NBTTagCompound()));
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public void readFromNBT(NBTTagCompound tags) {
      super.readFromNBT(tags);
      ItemStackHelper.loadAllItems(tags.getCompoundTag("inventory"), this.itemStack);

      try {
         this.owner = UUID.fromString(tags.getString("owner"));
      } catch (Exception var3) {
         this.reset();
         return;
      }

      this.questID = tags.hasKey("questID") ? tags.getInteger("questID") : -1;
      this.taskID = tags.hasKey("task") ? tags.getInteger("task") : -1;
      if (!this.isSetup()) {
         this.reset();
      }

   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound tags) {
      super.writeToNBT(tags);
      tags.setString("owner", this.owner != null ? this.owner.toString() : "");
      tags.setInteger("questID", this.questID);
      tags.setInteger("task", this.taskID);
      tags.setTag("inventory", ItemStackHelper.saveAllItems(new NBTTagCompound(), this.itemStack));
      return tags;
   }

   @Nonnull
   public int[] getSlotsForFace(@Nullable EnumFacing side) {
      return slotsForFace;
   }

   public boolean canInsertItem(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing side) {
      return this.isItemValidForSlot(slot, stack);
   }

   public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nullable EnumFacing side) {
      return slot == 1;
   }

   @Nonnull
   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.itemStack, index);
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.itemStack.clear();
   }

   @Nonnull
   public ITextComponent getDisplayName() {
      return new TextComponentString(BetterQuesting.submitStation.getLocalizedName());
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return true;
      } else {
         return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? true : super.hasCapability(capability, facing);
      }
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         return (T)CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.itemHandler);
      } else {
         return (T)(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.fluidHandler) : super.getCapability(capability, facing));
      }
   }

   public boolean isEmpty() {
      return this.itemStack.isEmpty();
   }
}
