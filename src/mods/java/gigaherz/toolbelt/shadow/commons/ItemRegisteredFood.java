package gigaherz.toolbelt.shadow.commons;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class ItemRegisteredFood extends ItemFood {
   public ItemRegisteredFood(String name, int amount, float saturation, boolean isWolfFood) {
      super(amount, saturation, isWolfFood);
      this.setRegistryName(name);
      this.setUnlocalizedName(this.getRegistryName().getResourceDomain() + "." + name);
      this.setCreativeTab(CreativeTabs.FOOD);
   }
}
