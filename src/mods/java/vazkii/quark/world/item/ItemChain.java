package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemChain extends ItemMod implements IQuarkItem {
   public ItemChain() {
      super("chain", new String[0]);
      this.setCreativeTab(CreativeTabs.TRANSPORTATION);
   }
}
