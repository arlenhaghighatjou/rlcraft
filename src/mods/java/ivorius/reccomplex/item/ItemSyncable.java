package ivorius.reccomplex.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface ItemSyncable {
   void writeSyncedNBT(NBTTagCompound var1, ItemStack var2);

   void readSyncedNBT(NBTTagCompound var1, ItemStack var2);
}
