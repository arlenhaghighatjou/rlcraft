package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTShortConverter implements INBTConverter<Short> {
   private String name;

   public NBTShortConverter() {
   }

   public NBTShortConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Short convert(NBTTagCompound tag) {
      return tag.getShort(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Short value) {
      tag.setShort(this.name, value.shortValue());
      return tag;
   }
}
