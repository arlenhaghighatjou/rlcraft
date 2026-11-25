package com.endertech.minecraft.forge.data;

import net.minecraft.nbt.NBTTagCompound;

public interface INBTSerializable<T> {
   default String getTagName() {
      return this.getClass().getSimpleName();
   }

   T readFrom(NBTTagCompound var1);

   NBTTagCompound writeTo(NBTTagCompound var1);
}
