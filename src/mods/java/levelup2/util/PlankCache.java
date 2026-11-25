package levelup2.util;

import java.util.Hashtable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class PlankCache {
   private static Hashtable<String, ItemStack> planks = new Hashtable();

   public PlankCache() {
   }

   public static void refresh() {
      planks = new Hashtable();
   }

   public static void addBlock(Block block, int meta, ItemStack plank) {
      planks.put(block + ":" + meta, plank);
   }

   public static boolean contains(Block block, int meta) {
      return planks.containsKey(block + ":" + meta);
   }

   public static ItemStack getProduct(Block block, int meta) {
      return (ItemStack)planks.get(block + ":" + meta);
   }
}
