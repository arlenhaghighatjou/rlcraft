package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTDoubleConverter implements INBTConverter<Double> {
   private String name;

   public NBTDoubleConverter() {
   }

   public NBTDoubleConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Double convert(NBTTagCompound tag) {
      return tag.getDouble(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Double value) {
      tag.setDouble(this.name, value.doubleValue());
      return tag;
   }
}
