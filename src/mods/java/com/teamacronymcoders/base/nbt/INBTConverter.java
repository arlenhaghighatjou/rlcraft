package com.teamacronymcoders.base.nbt;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTConverter<E> {
   E convert(NBTTagCompound var1);

   INBTConverter<E> setKey(String var1);

   NBTTagCompound convert(NBTTagCompound var1, E var2);
}
