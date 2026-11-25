package cursedflames.bountifulbaubles.capability;

import net.minecraftforge.energy.EnergyStorage;

public class CLEnergyStorage extends EnergyStorage {
   public CLEnergyStorage(int capacity) {
      super(capacity);
   }

   public CLEnergyStorage(int capacity, int maxTransfer) {
      super(capacity, maxTransfer);
   }

   public CLEnergyStorage(int capacity, int maxReceive, int maxExtract) {
      super(capacity, maxReceive, maxExtract);
   }

   public CLEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
      super(capacity, maxReceive, maxExtract, energy);
   }

   public void setEnergyStored(int energy) {
      this.energy = Math.max(0, Math.min(this.capacity, energy));
   }

   public void setMaxEnergyStored(int capacity) {
      this.capacity = capacity;
      this.energy = Math.min(capacity, this.energy);
   }

   public int getMaxExtract() {
      return this.maxExtract;
   }

   public void setMaxExtract(int maxExtract) {
      this.maxExtract = maxExtract;
   }

   public int getMaxReceive() {
      return this.maxReceive;
   }

   public void getMaxReceive(int maxReceive) {
      this.maxReceive = maxReceive;
   }
}
