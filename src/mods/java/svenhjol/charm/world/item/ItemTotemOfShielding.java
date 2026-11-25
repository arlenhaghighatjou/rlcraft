package svenhjol.charm.world.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import svenhjol.charm.world.feature.TotemOfShielding;
import svenhjol.meson.iface.IMesonItem;

public class ItemTotemOfShielding extends Item implements IMesonItem {
   public ItemTotemOfShielding() {
      this.register("totem_of_shielding");
      this.setMaxStackSize(1);
      this.setMaxDamage(TotemOfShielding.maxHealth);
      this.setCreativeTab(CreativeTabs.COMBAT);
   }

   public String getModId() {
      return "charm";
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }
}
