package com.github.fnar.minecraft.item.mapper;

import com.github.fnar.minecraft.item.CouldNotMapItemException;
import com.github.fnar.minecraft.item.ItemMapper;
import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.minecraft.tag.TagMapper1_12;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseItemMapper1_12<ItemClass> implements ItemMapper {
   private static final TagMapper1_12 tagMapper = new TagMapper1_12();

   public BaseItemMapper1_12() {
   }

   public abstract Class<ItemClass> getClazz();

   public ItemStack map(RldItemStack rldItemStack) {
      Class<ItemClass> clazz = this.getClazz();
      if (!clazz.isAssignableFrom(rldItemStack.getItem().getClass())) {
         throw new CouldNotMapItemException(rldItemStack);
      } else {
         ItemStack itemStack = this.map(this.getClazz().cast(rldItemStack.getItem()));
         itemStack.setItemDamage(rldItemStack.getDamage());
         itemStack.setCount(rldItemStack.getCount());
         mapPlzNbt(rldItemStack, itemStack);
         mergeTags(rldItemStack, itemStack);
         return itemStack;
      }
   }

   public abstract ItemStack map(ItemClass var1);

   private static void mapPlzNbt(RldItemStack rldItemStack, ItemStack itemStack) {
      if (rldItemStack.isPlzNbt()) {
         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(rldItemStack.getPlzNbt()));
         } catch (NBTException var3) {
            throw new CouldNotMapItemException(rldItemStack);
         }
      }
   }

   private static void mergeTags(RldItemStack rldItemStack, ItemStack itemStack) {
      Optional.ofNullable(rldItemStack.getTags()).ifPresent((compoundTag) -> {
         ((NBTTagCompound)Optional.ofNullable(itemStack.getTagCompound()).orElseGet(() -> {
            return ensureNbtTags(itemStack);
         })).merge(tagMapper.map(compoundTag));
      });
   }

   private static NBTTagCompound ensureNbtTags(ItemStack itemStack) {
      itemStack.setTagCompound(new NBTTagCompound());
      return itemStack.getTagCompound();
   }
}
