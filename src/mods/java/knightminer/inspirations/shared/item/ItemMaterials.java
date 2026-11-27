package knightminer.inspirations.shared.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import slimeknights.mantle.item.ItemMetaDynamic;

public class ItemMaterials extends ItemMetaDynamic {
   protected Int2ObjectOpenHashMap<CreativeTabs> creativeTabs = new Int2ObjectOpenHashMap();
   protected Set<CreativeTabs> allTabs = new HashSet();

   public ItemMaterials() {
   }

   public Item setCreativeTab(CreativeTabs tab) {
      super.setCreativeTab(tab);
      this.allTabs.add(tab);
      return this;
   }

   public ItemStack addMeta(int meta, String name) {
      return this.addMeta(meta, name, this.getCreativeTab());
   }

   public ItemStack addMeta(int meta, String name, CreativeTabs tab) {
      ItemStack stack = super.addMeta(meta, name);
      this.creativeTabs.put(meta, tab);
      this.allTabs.add(tab);
      return stack;
   }

   public CreativeTabs[] getCreativeTabs() {
      return (CreativeTabs[])this.allTabs.stream().toArray((x$0) -> {
         return new CreativeTabs[x$0];
      });
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i <= this.availabilityMask.length; ++i) {
            if (this.isValid(i) && this.isInCreativeTab(tab, i)) {
               subItems.add(new ItemStack(this, 1, i));
            }
         }
      }

   }

   private boolean isInCreativeTab(CreativeTabs targetTab, int i) {
      CreativeTabs tab = (CreativeTabs)this.creativeTabs.get(i);
      if (tab == null) {
         return false;
      } else if (targetTab == CreativeTabs.SEARCH) {
         return true;
      } else {
         return targetTab == tab;
      }
   }
}
