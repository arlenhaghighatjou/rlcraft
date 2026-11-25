package vazkii.quark.decoration.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemTallow extends ItemMod implements IQuarkItem {
   public ItemTallow() {
      super("tallow", new String[0]);
      this.setCreativeTab(CreativeTabs.MISC);
   }
}
