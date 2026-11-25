package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTFloatConverter implements INBTConverter<Float> {
   private String name;

   public NBTFloatConverter() {
   }

   public NBTFloatConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Float convert(NBTTagCompound tag) {
      return tag.getFloat(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Float value) {
      tag.setFloat(this.name, value.floatValue());
      return tag;
   }
}
