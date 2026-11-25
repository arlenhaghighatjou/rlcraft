package betterquesting.client;

import betterquesting.core.BetterQuesting;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabQuesting extends CreativeTabs {
   private ItemStack tabStack;

   public CreativeTabQuesting() {
      super("betterquesting");
   }

   public ItemStack getTabIconItem() {
      if (this.tabStack == null) {
         this.tabStack = new ItemStack(BetterQuesting.extraLife);
      }

      return this.tabStack;
   }
}
