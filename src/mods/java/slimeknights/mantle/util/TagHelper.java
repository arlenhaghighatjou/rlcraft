package slimeknights.mantle.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class TagHelper {
   public static int TAG_TYPE_STRING = (new NBTTagString()).getId();
   public static int TAG_TYPE_COMPOUND = (new NBTTagCompound()).getId();

   private TagHelper() {
   }

   public static NBTTagCompound getTagSafe(ItemStack stack) {
      return !stack.isEmpty() && stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
   }

   public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
      return tag != null && tag.hasKey(key) ? tag.getCompoundTag(key) : new NBTTagCompound();
   }

   public static NBTTagList getTagListSafe(NBTTagCompound tag, String key, int type) {
      return tag != null && tag.hasKey(key) ? tag.getTagList(key, type) : new NBTTagList();
   }
}
