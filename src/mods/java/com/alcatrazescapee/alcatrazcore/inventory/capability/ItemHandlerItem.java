package com.alcatrazescapee.alcatrazcore.inventory.capability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerItem extends ItemStackHandler implements ICapabilityProvider {
   public ItemHandlerItem(@Nullable NBTTagCompound nbt, int size) {
      super(size);
      if (nbt != null) {
         this.deserializeNBT(nbt);
      }

   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing enumFacing) {
      return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing enumFacing) {
      return (T)(this.hasCapability(capability, (EnumFacing)null) ? this : null);
   }
}
