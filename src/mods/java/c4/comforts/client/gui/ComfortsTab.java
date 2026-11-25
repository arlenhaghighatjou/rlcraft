package c4.comforts.client.gui;

import c4.comforts.common.items.ComfortsItems;
import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ComfortsTab extends CreativeTabs {
   public ComfortsTab() {
      super("comforts");
   }

   @Nonnull
   public ItemStack getTabIconItem() {
      return new ItemStack(ComfortsItems.SLEEPING_BAG, 1, 14);
   }
}
