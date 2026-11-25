package xzeroair.trinkets.capabilities;

import net.minecraft.nbt.NBTTagCompound;

public interface ITrinketCapability<T> {
   void onUpdate();

   NBTTagCompound saveToNBT(NBTTagCompound var1);

   void loadFromNBT(NBTTagCompound var1);

   void copyFrom(T var1, boolean var2, boolean var3);
}
