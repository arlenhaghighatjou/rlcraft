package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemCrabShell extends ItemMod implements IQuarkItem {
   public ItemCrabShell() {
      super("crab_shell", new String[0]);
      this.setCreativeTab(CreativeTabs.BREWING);
   }
}
