package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTLongConverter implements INBTConverter<Long> {
   private String name;

   public NBTLongConverter() {
   }

   public NBTLongConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Long convert(NBTTagCompound tag) {
      return tag.getLong(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Long value) {
      tag.setLong(this.name, value.longValue());
      return tag;
   }
}
