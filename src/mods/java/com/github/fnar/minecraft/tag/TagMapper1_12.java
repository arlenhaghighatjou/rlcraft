package com.github.fnar.minecraft.tag;

import java.util.function.Consumer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class TagMapper1_12 {
   public TagMapper1_12() {
   }

   public NBTBase map(Tag tag) {
      switch(tag.getType()) {
      case INT:
      default:
         return this.map((IntTag)tag);
      case STRING:
         return this.map((StringTag)tag);
      case COMPOUND:
         return this.map((CompoundTag)tag);
      case LIST:
         return this.map((ListTag)tag);
      }
   }

   public NBTTagString map(StringTag stringTag) {
      return new NBTTagString(stringTag.getValue());
   }

   public NBTTagInt map(IntTag intTag) {
      return new NBTTagInt(intTag.getValue());
   }

   public NBTTagList map(ListTag tagList) {
      NBTTagList nbtTagList = new NBTTagList();
      tagList.getTags().forEach((tag) -> {
         nbtTagList.appendTag(this.map(tag));
      });
      return nbtTagList;
   }

   public NBTTagCompound map(CompoundTag compoundTag) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      compoundTag.getTags().forEach((tag) -> {
         nbtTagCompound.setTag((String)tag.getKey(), this.map((Tag)tag.getValue()));
      });
      return nbtTagCompound;
   }
}
