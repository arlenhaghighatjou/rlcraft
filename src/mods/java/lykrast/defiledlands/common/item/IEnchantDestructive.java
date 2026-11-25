package lykrast.defiledlands.common.item;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public interface IEnchantDestructive {
   default float getDestructiveBonus(ItemStack gun) {
      int i = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.destructive, gun);
      return i > 0 ? 1.0F + (float)(i + 1) * 0.25F : 1.0F;
   }
}
