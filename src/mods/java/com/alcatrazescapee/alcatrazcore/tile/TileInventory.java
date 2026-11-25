package com.alcatrazescapee.alcatrazcore.tile;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

@ParametersAreNonnullByDefault
public abstract class TileInventory extends TileCore {
   protected final ItemStackHandler inventory;

   public TileInventory(int numSlots) {
      this.inventory = new ItemStackHandler(numSlots);
   }

   public void setAndUpdateSlots(int slot) {
      this.markDirty();
   }

   public int getSlotLimit(int slot) {
      return 64;
   }

   public boolean isItemValid(int slot, ItemStack stack) {
      return true;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
      super.readFromNBT(nbt);
   }

   @Nonnull
   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("inventory", this.inventory.serializeNBT());
      return super.writeToNBT(nbt);
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound nbt = this.getUpdateTag();
      return new SPacketUpdateTileEntity(this.pos, 1, nbt);
   }

   @Nonnull
   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbt = new NBTTagCompound();
      this.writeToNBT(nbt);
      return nbt;
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound nbt = pkt.getNbtCompound();
      this.handleUpdateTag(nbt);
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }

   public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null || super.hasCapability(capability, facing);
   }

   public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing == null ? this.inventory : super.getCapability(capability, facing));
   }

   public void onBreakBlock() {
      for(int i = 0; i < this.inventory.getSlots(); ++i) {
         CoreHelpers.dropItemInWorld(this.world, this.pos, this.inventory.getStackInSlot(i));
      }

      super.onBreakBlock();
   }
}
