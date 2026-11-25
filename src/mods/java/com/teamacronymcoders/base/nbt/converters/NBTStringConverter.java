package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;

public class NBTStringConverter implements INBTConverter<String> {
   private String name;

   public NBTStringConverter() {
   }

   public NBTStringConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public String convert(NBTTagCompound tag) {
      return tag.getString(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, String value) {
      tag.setString(this.name, value);
      return tag;
   }
}
