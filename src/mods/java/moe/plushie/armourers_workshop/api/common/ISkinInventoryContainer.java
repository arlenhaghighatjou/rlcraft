package moe.plushie.armourers_workshop.api.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface ISkinInventoryContainer {
   void writeToNBT(NBTTagCompound var1);

   void readFromNBT(NBTTagCompound var1);

   void dropItems(EntityPlayer var1);

   void clear();
}
