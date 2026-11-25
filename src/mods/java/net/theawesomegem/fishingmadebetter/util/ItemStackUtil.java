package net.theawesomegem.fishingmadebetter.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

public class ItemStackUtil {
   public ItemStackUtil() {
   }

   public static ItemStack appendToolTip(ItemStack stack, Iterable<String> tooltip) {
      NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
      boolean needAppend = false;
      NBTTagCompound tag1;
      if (tag.hasKey("display", 10)) {
         tag1 = tag.getCompoundTag("display");
         if (tag.hasKey("Lore", 9)) {
            needAppend = true;
         }
      } else {
         tag.setTag("display", new NBTTagCompound());
         tag1 = tag.getCompoundTag("display");
      }

      if (!needAppend) {
         tag1.setTag("Lore", new NBTTagList());
      }

      for(String s : tooltip) {
         tag1.getTagList("Lore", 8).appendTag(new NBTTagString(TextFormatting.RESET + s));
      }

      tag.setTag("display", tag1);
      ItemStack output = stack.copy();
      output.setTagCompound(tag);
      return output;
   }

   public static List<String> getToolTip(ItemStack stack) {
      List<String> loreList = new ArrayList();
      if (!stack.hasTagCompound()) {
         return loreList;
      } else {
         NBTTagCompound mainTag = stack.getTagCompound();
         if (!mainTag.hasKey("display", 10)) {
            return loreList;
         } else {
            NBTTagCompound displayTag = mainTag.getCompoundTag("display");
            if (!displayTag.hasKey("Lore", 9)) {
               return loreList;
            } else {
               NBTTagList loreTagList = displayTag.getTagList("Lore", 8);

               for(int i = 0; i < loreTagList.tagCount(); ++i) {
                  loreList.add(loreTagList.getStringTagAt(i));
               }

               return loreList;
            }
         }
      }
   }
}
