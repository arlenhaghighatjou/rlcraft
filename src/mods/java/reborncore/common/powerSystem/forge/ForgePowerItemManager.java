package reborncore.common.powerSystem.forge;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;
import reborncore.api.power.IEnergyItemInfo;
import reborncore.common.RebornCoreConfig;

public class ForgePowerItemManager implements IEnergyStorage, IEnergyStorageModifiable {
   ItemStack stack;
   IEnergyItemInfo itemPowerInfo;

   public ForgePowerItemManager(ItemStack stack) {
      this.stack = stack;
      if (stack.getItem() instanceof IEnergyItemInfo) {
         this.itemPowerInfo = (IEnergyItemInfo)stack.getItem();
      }

      this.validateNBT();
   }

   public ForgePowerItemManager(ItemStack stack, IEnergyItemInfo itemPowerInfo) {
      this.stack = stack;
      this.itemPowerInfo = itemPowerInfo;
      this.validateNBT();
   }

   private int getEnergyInStack() {
      this.validateNBT();
      return this.stack.getTagCompound().getInteger("energy");
   }

   private void setEnergyInStack(int energy) {
      this.validateNBT();
      this.stack.getTagCompound().setInteger("energy", energy);
   }

   private void validateNBT() {
      if (!this.stack.hasTagCompound()) {
         this.stack.setTagCompound(new NBTTagCompound());
         this.stack.getTagCompound().setInteger("energy", 0);
      } else if (this.stack.getTagCompound().hasKey("charge")) {
         this.stack.getTagCompound().setInteger("energy", this.stack.getTagCompound().getInteger("charge") * RebornCoreConfig.euPerFU);
         this.stack.getTagCompound().removeTag("charge");
      }

   }

   public void setEnergyStored(int value) {
      this.setEnergyInStack(value);
   }

   public int receiveEnergy(int maxReceive, boolean simulate) {
      if (!this.itemPowerInfo.canAcceptEnergy(this.stack)) {
         return 0;
      } else {
         int energyReceived = Math.min(this.getMaxEnergyStored() - this.getEnergyStored(), Math.min((int)this.itemPowerInfo.getMaxTransfer(this.stack), maxReceive));
         if (!simulate) {
            this.setEnergyInStack(this.getEnergyInStack() + energyReceived);
         }

         return energyReceived;
      }
   }

   public int extractEnergy(int maxExtract, boolean simulate) {
      if (!this.itemPowerInfo.canAcceptEnergy(this.stack)) {
         return 0;
      } else {
         int energyExtracted = Math.min(this.getEnergyStored(), maxExtract);
         if (!simulate) {
            this.setEnergyInStack(this.getEnergyInStack() - energyExtracted);
         }

         return energyExtracted;
      }
   }

   public int getEnergyStored() {
      return this.getEnergyInStack();
   }

   public int getMaxEnergyStored() {
      return (int)this.itemPowerInfo.getMaxPower(this.stack);
   }

   public boolean canExtract() {
      return this.itemPowerInfo.canProvideEnergy(this.stack);
   }

   public boolean canReceive() {
      return this.itemPowerInfo.canAcceptEnergy(this.stack);
   }

   public void setEnergy(int energy) {
      this.setEnergyInStack(energy);
   }
}
