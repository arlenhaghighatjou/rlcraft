package com.teamacronymcoders.base.nbt.converters;

import com.teamacronymcoders.base.nbt.INBTConverter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;

public class NBTItemStackHandlerConverter implements INBTConverter<ItemStackHandler> {
   private String name;

   public NBTItemStackHandlerConverter() {
   }

   public NBTItemStackHandlerConverter setKey(String key) {
      this.name = key;
      return this;
   }

   public ItemStackHandler convert(NBTTagCompound tag) {
      ItemStackHandler handler = new ItemStackHandler();
      handler.deserializeNBT(tag.getCompoundTag("items"));
      System.out.println(handler);
      return handler;
   }

   public NBTTagCompound convert(NBTTagCompound tag, ItemStackHandler value) {
      tag.setTag("items", value.serializeNBT());
      return tag;
   }
}
