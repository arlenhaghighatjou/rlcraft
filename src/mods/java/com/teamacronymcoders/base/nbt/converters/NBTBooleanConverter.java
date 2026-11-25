package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTBooleanConverter implements INBTConverter<Boolean> {
   private String name;

   public NBTBooleanConverter() {
   }

   public NBTBooleanConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public Boolean convert(NBTTagCompound tag) {
      return tag.getBoolean(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, Boolean value) {
      tag.setBoolean(this.name, value.booleanValue());
      return tag;
   }
}
