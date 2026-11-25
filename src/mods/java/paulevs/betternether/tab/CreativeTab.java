package paulevs.betternether.tab;

import java.util.Comparator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import paulevs.betternether.blocks.BlocksRegister;

public class CreativeTab extends CreativeTabs {
   public CreativeTab() {
      super("better_nether");
   }

   public ItemStack getTabIconItem() {
      return new ItemStack(BlocksRegister.BLOCK_NETHER_GRASS);
   }

   public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
      for(Item item : Item.REGISTRY) {
         item.getSubItems(this, itemList);
      }

      Comparator<? super ItemStack> comparator = new CreativeTab.ItemComparator();
      itemList.sort(comparator);
   }

   class ItemComparator implements Comparator {
      ItemComparator() {
      }

      public int compare(Object arg0, Object arg1) {
         return this.compareStack((ItemStack)arg0, (ItemStack)arg1);
      }

      private int compareStack(ItemStack stack1, ItemStack stack2) {
         String name1 = stack1.getUnlocalizedName();
         String name2 = stack2.getUnlocalizedName();
         return name1.compareTo(name2);
      }
   }
}
