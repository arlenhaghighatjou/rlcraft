package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTIntConverter implements INBTConverter<Integer> {
   private String name;

   public NBTIntConverter() {
   }

   public NBTIntConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Integer convert(NBTTagCompound tag) {
      return tag.getInteger(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Integer value) {
      tag.setInteger(this.name, value.intValue());
      return tag;
   }
}
