package vazkii.quark.world.item;

import net.minecraft.creativetab.CreativeTabs;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.item.IQuarkItem;

public class ItemBiotite extends ItemMod implements IQuarkItem {
   public ItemBiotite() {
      super("biotite", new String[0]);
      this.setCreativeTab(CreativeTabs.MATERIALS);
   }
}
