package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class NBTBlockPosConverter implements INBTConverter<BlockPos> {
   private String name;

   public NBTBlockPosConverter() {
   }

   public NBTBlockPosConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public BlockPos convert(NBTTagCompound tag) {
      return BlockPos.fromLong(tag.getLong(this.name));
   }

   public NBTTagCompound convert(NBTTagCompound tag, BlockPos value) {
      tag.setLong(this.name, value.toLong());
      return tag;
   }
}
