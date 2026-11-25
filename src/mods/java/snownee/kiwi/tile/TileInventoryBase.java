package snownee.kiwi.tile;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileInventoryBase extends TileBase {
   public final TileInventoryBase.StackHandler stacks;

   public TileInventoryBase(int slot) {
      this(slot, Items.AIR.getItemStackLimit());
   }

   public TileInventoryBase(int slot, int stackLimit) {
      this.stacks = new TileInventoryBase.StackHandler(slot, stackLimit);
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stacks) : super.getCapability(capability, facing));
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("Items", 10)) {
         this.stacks.deserializeNBT(compound.getCompoundTag("Items"));
      }

   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setTag("Items", this.stacks.serializeNBT());
      return compound;
   }

   protected void readPacketData(NBTTagCompound data) {
      this.stacks.deserializeNBT(data.getCompoundTag("Items"));
   }

   @Nonnull
   protected NBTTagCompound writePacketData(NBTTagCompound data) {
      data.setTag("Items", this.stacks.serializeNBT());
      return data;
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public void onContentsChanged(int slot) {
      this.refresh();
   }

   public class StackHandler extends ItemStackHandler {
      private final int stackLimit;

      /** @deprecated */
      @Deprecated
      StackHandler(TileInventoryBase tile, int slot, int stackLimit) {
         this(slot, stackLimit);
      }

      StackHandler(int slot, int stackLimit) {
         super(slot);
         this.stackLimit = stackLimit;
      }

      public int getSlotLimit(int slot) {
         return this.stackLimit;
      }

      public List<ItemStack> getStacks() {
         return this.stacks;
      }

      public boolean isItemValid(int slot, ItemStack stack) {
         return TileInventoryBase.this.isItemValidForSlot(slot, stack);
      }

      @Nonnull
      public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
         return !this.isItemValid(slot, stack) ? stack : super.insertItem(slot, stack, simulate);
      }

      protected void onContentsChanged(int slot) {
         TileInventoryBase.this.onContentsChanged(slot);
      }
   }
}
