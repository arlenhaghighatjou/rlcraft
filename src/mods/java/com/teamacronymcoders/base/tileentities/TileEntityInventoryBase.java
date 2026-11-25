package com.teamacronymcoders.base.tileentities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityInventoryBase extends TileEntityBase {
   private ItemStackHandler inventory;

   public TileEntityInventoryBase(int inventorySize) {
      this(new ItemStackHandler(inventorySize));
   }

   public TileEntityInventoryBase(ItemStackHandler inventory) {
      this.inventory = inventory;
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
   }

   @Nonnull
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? this.inventory : super.getCapability(capability, facing));
   }

   protected void readFromDisk(NBTTagCompound data) {
      this.inventory.deserializeNBT(data.getCompoundTag("Items"));
   }

   protected NBTTagCompound writeToDisk(NBTTagCompound data) {
      data.setTag("Items", this.inventory.serializeNBT());
      return data;
   }
}
