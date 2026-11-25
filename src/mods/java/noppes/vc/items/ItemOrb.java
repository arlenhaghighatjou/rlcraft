package noppes.vc.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemOrb extends ItemBasic {
   public ItemOrb() {
      this.setHasSubtypes(true);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List) {
      if (this.isInCreativeTab(tab)) {
         for(int var4 = 0; var4 < 16; ++var4) {
            par3List.add(new ItemStack(this, 1, var4));
         }
      }

   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }
}
