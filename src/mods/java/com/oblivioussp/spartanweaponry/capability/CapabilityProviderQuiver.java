package com.oblivioussp.spartanweaponry.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class CapabilityProviderQuiver implements ICapabilitySerializable<NBTTagCompound> {
   protected ItemStack quiver;
   protected final ItemStackHandler handler;
   protected final int inventorySize;

   public CapabilityProviderQuiver(ItemStack stack, int invSize) {
      this.quiver = stack;
      this.inventorySize = invSize;
      this.handler = new ItemStackHandler(invSize);
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(this.hasCapability(capability, facing) ? this.handler : null);
   }

   public NBTTagCompound serializeNBT() {
      if (!this.quiver.hasTagCompound()) {
         this.quiver.setTagCompound(new NBTTagCompound());
      }

      return this.handler.serializeNBT();
   }

   public void deserializeNBT(NBTTagCompound nbt) {
      this.handler.deserializeNBT(nbt);
   }
}
