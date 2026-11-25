package levelup2.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CraftingBlacklist {
   private static List<ItemStack> blacklist = new ArrayList();

   public CraftingBlacklist() {
   }

   public static void addItem(Block block) {
      blacklist.add(new ItemStack(block, 1, 32767));
   }

   public static void addItem(Item item) {
      blacklist.add(new ItemStack(item, 1, 32767));
   }

   public static boolean addItem(ItemStack stack) {
      return blacklist.add(stack.copy());
   }

   public static void addOres(String name) {
      for(ItemStack stack : OreDictionary.getOres(name)) {
         addItem(stack);
      }

   }

   public static void add(Object obj) {
      if (obj instanceof ItemStack) {
         addItem((ItemStack)obj);
      } else if (obj instanceof String) {
         addOres((String)obj);
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

   public static boolean contains(Item item) {
      return contains(new ItemStack(item, 1, 32767));
   }

   public static boolean contains(Block block) {
      return contains(new ItemStack(block, 1, 32767));
   }

   public static boolean remove(ItemStack stack) {
      return contains(stack) ? blacklist.remove(stack) : false;
   }

   public static void removeOres(String name) {
      for(ItemStack stack : OreDictionary.getOres(name)) {
         remove(stack);
      }

   }

   public static List<ItemStack> getBlacklist() {
      return blacklist;
   }
}
