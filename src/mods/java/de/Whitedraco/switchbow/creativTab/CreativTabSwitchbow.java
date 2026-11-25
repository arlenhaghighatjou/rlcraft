package de.Whitedraco.switchbow.creativTab;

import de.Whitedraco.switchbow.ItemInit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativTabSwitchbow extends CreativeTabs {
   public CreativTabSwitchbow(int index, String label) {
      super(index, label);
   }

   public ItemStack getTabIconItem() {
      return new ItemStack(ItemInit.SwitchBow);
   }
}
