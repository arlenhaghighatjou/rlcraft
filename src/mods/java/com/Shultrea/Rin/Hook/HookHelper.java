package com.Shultrea.Rin.Hook;

import com.Shultrea.Rin.Ench0_3_0.EnchantmentAdvancedBlastProtection;
import com.Shultrea.Rin.Ench0_3_0.EnchantmentAdvancedLooting;
import com.Shultrea.Rin.Ench0_3_0.EnchantmentAdvancedLuckOfTheSea;
import com.Shultrea.Rin.Ench0_3_0.EnchantmentAdvancedLure;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class HookHelper {
   public HookHelper() {
   }

   public static int modifyFishingLuckBonus(int original, ItemStack stack) {
      return Math.max(original, EnchantmentAdvancedLuckOfTheSea.getValue(stack));
   }

   public static int modifyFishingSpeedBonus(int original, ItemStack stack) {
      return Math.max(original, EnchantmentAdvancedLure.getValue(stack));
   }

   public static int modifyLootingModifier(int original, EntityLivingBase entity) {
      return Math.max(original, EnchantmentAdvancedLooting.getValue(original, entity));
   }

   public static double modifyBlastDamageReduction(double damage, EntityLivingBase entity) {
      return EnchantmentAdvancedBlastProtection.getKnockbackValue(damage, entity);
   }
}
