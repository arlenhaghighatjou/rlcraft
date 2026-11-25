package org.orecruncher.lib;

import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackUtil {
   private ItemStackUtil() {
   }

   public static boolean isValidItemStack(@Nullable ItemStack stack) {
      return stack != null && !stack.isEmpty();
   }

   public static String getItemName(ItemStack stack) {
      Item item = stack.getItem();
      String itemName = MCHelper.nameOf(item);
      if (itemName != null) {
         StringBuilder builder = new StringBuilder();
         builder.append(itemName);
         if (stack.getHasSubtypes()) {
            builder.append(':').append(stack.getItemDamage());
         }

         return builder.toString();
      } else {
         return null;
      }
   }
}
