package net.silentchaos512.lib.util;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTSerializer<T> {
   T read(NBTTagCompound var1);

   void write(NBTTagCompound var1, T var2);
}
