package ivorius.ivtoolkit.item;

import net.minecraft.item.ItemStack;

public class IvItemStacks {
   public IvItemStacks() {
   }

   public static int getNBT(ItemStack stack, String key, int defaultValue) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, 3) ? stack.getTagCompound().getInteger(key) : defaultValue;
   }

   public static long getNBT(ItemStack stack, String key, long defaultValue) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, 4) ? stack.getTagCompound().getLong(key) : defaultValue;
   }

   public static float getNBT(ItemStack stack, String key, float defaultValue) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, 5) ? stack.getTagCompound().getFloat(key) : defaultValue;
   }

   public static double getNBT(ItemStack stack, String key, double defaultValue) {
      return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, 6) ? stack.getTagCompound().getDouble(key) : defaultValue;
   }
}
