package com.oblivioussp.spartanweaponry.util;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTHelper {
   public NBTHelper() {
   }

   protected static void initNBTCompound(ItemStack stack) {
      if (stack.getTagCompound() == null) {
         stack.setTagCompound(new NBTTagCompound());
      }

   }

   public static void setBoolean(ItemStack stack, String tag, boolean value) {
      initNBTCompound(stack);
      stack.getTagCompound().setBoolean(tag, value);
   }

   public static void setInteger(ItemStack stack, String tag, int value) {
      initNBTCompound(stack);
      stack.getTagCompound().setInteger(tag, value);
   }

   public static void setFloat(ItemStack stack, String tag, float value) {
      initNBTCompound(stack);
      stack.getTagCompound().setFloat(tag, value);
   }

   public static void setDouble(ItemStack stack, String tag, double value) {
      initNBTCompound(stack);
      stack.getTagCompound().setDouble(tag, value);
   }

   public static void setString(ItemStack stack, String tag, String value) {
      initNBTCompound(stack);
      stack.getTagCompound().setString(tag, value);
   }

   public static void setTagCompound(ItemStack stack, String tag, NBTTagCompound value) {
      initNBTCompound(stack);
      stack.getTagCompound().setTag(tag, value);
   }

   public static NBTTagList saveItemStacksAsList(List<ItemStack> stacks) {
      NBTTagList list = new NBTTagList();

      for(ItemStack stack : stacks) {
         if (stack != null) {
            NBTTagCompound stackTag = new NBTTagCompound();
            stack.writeToNBT(stackTag);
            list.appendTag(stackTag);
         }
      }

      return list;
   }

   public static boolean getBoolean(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getBoolean(tag) : false;
   }

   public static int getInteger(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getInteger(tag) : 0;
   }

   public static float getFloat(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getFloat(tag) : 0.0F;
   }

   public static double getDouble(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getDouble(tag) : 0.0D;
   }

   public static String getString(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getString(tag) : "";
   }

   public static NBTTagCompound getTagCompound(ItemStack stack, String tag) {
      return stack.getTagCompound() != null && stack.getTagCompound().hasKey(tag) ? stack.getTagCompound().getCompoundTag(tag) : null;
   }

   public static ItemStack[] retrieveItemStacksFromList(NBTTagList list) {
      ItemStack[] result = new ItemStack[list.tagCount()];

      for(int i = 0; i < list.tagCount(); ++i) {
         NBTTagCompound stackTag = list.getCompoundTagAt(i);
         result[i] = new ItemStack(stackTag);
      }

      return result;
   }
}
