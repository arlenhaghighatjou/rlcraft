package reborncore.common.powerSystem.forge;

import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyStorageModifiable extends IEnergyStorage {
   void setEnergy(int var1);
}
