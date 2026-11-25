package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTIntArrayConverter implements INBTConverter<int[]> {
   private String name;

   public NBTIntArrayConverter() {
   }

   public NBTIntArrayConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public int[] convert(NBTTagCompound tag) {
      return tag.getIntArray(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, int[] value) {
      tag.setIntArray(this.name, value);
      return tag;
   }
}
