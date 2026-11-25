package melonslise.locks.common.init;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;

public final class LocksCreativeTabs {
   public static final CreativeTabs TAB = (new CreativeTabs("locks") {
      public ItemStack getTabIconItem() {
         return new ItemStack(LocksItems.IRON_LOCK);
      }
   }).setRelevantEnchantmentTypes(new EnumEnchantmentType[]{LocksEnchantments.LOCK_TYPE});

   private LocksCreativeTabs() {
   }
}
