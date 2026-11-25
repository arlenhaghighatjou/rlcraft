package com.oblivioussp.spartanshields.util;

import com.oblivioussp.spartanshields.item.IItemPoweredFE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyCapabilityProviderItem implements ICapabilityProvider {
   public IEnergyStorage storage;

   public EnergyCapabilityProviderItem(final ItemStack stack, final IItemPoweredFE item) {
      this.storage = new IEnergyStorage() {
         public int receiveEnergy(int maxReceive, boolean simulate) {
            return item.receiveFE(stack, maxReceive, simulate);
         }

         public int extractEnergy(int maxExtract, boolean simulate) {
            return item.extractFE(stack, maxExtract, simulate);
         }

         public int getEnergyStored() {
            return item.getFEStored(stack);
         }

         public int getMaxEnergyStored() {
            return item.getFECapacity(stack);
         }

         public boolean canExtract() {
            return item.canExtractFE(stack);
         }

         public boolean canReceive() {
            return item.canReceiveFE(stack);
         }
      };
   }

   public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
      return this.getCapability(capability, facing) != null;
   }

   public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      return (T)(capability == CapabilityEnergy.ENERGY ? this.storage : null);
   }
}
