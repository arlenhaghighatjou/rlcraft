package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NBTTagConverter implements INBTConverter<NBTBase> {
   private String name;

   public NBTTagConverter() {
   }

   public NBTTagConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public NBTBase convert(NBTTagCompound tag) {
      return tag.getTag(this.name);
   }

   public NBTTagCompound convert(NBTTagCompound tag, NBTBase value) {
      tag.setTag(this.name, value);
      return tag;
   }
}
