package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTByteArrayConverter implements INBTConverter<byte[]> {
   private String name;

   public NBTByteArrayConverter() {
   }

   public NBTByteArrayConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public byte[] convert(NBTTagCompound tag) {
      return tag.getByteArray(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, byte[] value) {
      tag.setByteArray(this.name, value);
      return tag;
   }
}
