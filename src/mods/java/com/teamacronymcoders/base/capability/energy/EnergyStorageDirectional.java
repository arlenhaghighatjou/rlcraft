package com.teamacronymcoders.base.capability.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageDirectional implements IEnergyStorage {
   private final IEnergyStorage energyStorage;
   private final boolean input;

   public EnergyStorageDirectional(IEnergyStorage energyStorage, boolean input) {
      this.energyStorage = energyStorage;
      this.input = input;
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      return this.canReceive() ? this.energyStorage.receiveEnergy(maxReceive, simulate) : 0;
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      return this.canExtract() ? this.energyStorage.extractEnergy(maxExtract, simulate) : 0;
   }

   public int getEnergyStored() {
      return this.energyStorage.getEnergyStored();
   }

   public int getMaxEnergyStored() {
      return this.energyStorage.getMaxEnergyStored();
   }

   public boolean canExtract() {
      return !this.input;
   }

   public boolean canReceive() {
      return this.input;
   }
}
