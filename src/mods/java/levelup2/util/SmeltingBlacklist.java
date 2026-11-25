package levelup2.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SmeltingBlacklist {
   private static List<ItemStack> blacklist = new ArrayList();

   public SmeltingBlacklist() {
   }

   public static boolean addItem(ItemStack stack) {
      return blacklist.add(stack.copy());
   }

   public static void addOres(String name) {
      for(ItemStack stack : OreDictionary.getOres(name)) {
         addItem(stack);
      }

   }

   public static boolean contains(ItemStack stack) {
      boolean match = false;
      boolean wild = stack.getMetadata() == 32767;

      for(ItemStack black : blacklist) {
         if (match) {
            break;
         }

         if (wild) {
            match = black.getItem() == stack.getItem();
         } else {
            match = ItemStack.areItemsEqual(black, stack) || black.getMetadata() == 32767 && stack.getItem() == black.getItem();
         }
      }

      return match;
   }
}
