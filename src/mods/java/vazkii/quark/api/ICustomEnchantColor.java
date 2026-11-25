package vazkii.quark.api;

import net.minecraft.item.ItemStack;

public interface ICustomEnchantColor {
   int getEnchantEffectColor(ItemStack var1);

   default boolean shouldTruncateColorBrightness(ItemStack stack) {
      return true;
   }
}
