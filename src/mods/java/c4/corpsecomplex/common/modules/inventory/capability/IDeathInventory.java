package c4.corpsecomplex.common.modules.inventory.capability;

import net.minecraft.nbt.NBTTagCompound;

public interface IDeathInventory {
   NBTTagCompound getStorage(String var1);

   void addStorage(String var1, NBTTagCompound var2);

   void setDeathInventory(NBTTagCompound var1);

   NBTTagCompound getDeathInventory();
}
