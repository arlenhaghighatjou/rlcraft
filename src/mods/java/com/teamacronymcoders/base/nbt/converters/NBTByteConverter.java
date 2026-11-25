package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTByteConverter implements INBTConverter<Byte> {
   private String name;

   public NBTByteConverter() {
   }

   public NBTByteConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Byte convert(NBTTagCompound tag) {
      return tag.getByte(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Byte value) {
      tag.setByte(this.name, value.byteValue());
      return tag;
   }
}
