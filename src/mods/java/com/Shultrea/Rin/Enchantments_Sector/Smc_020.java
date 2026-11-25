package com.Shultrea.Rin.Enchantments_Sector;

import com.Shultrea.Rin.Ench0_2_0.EnchantmentAdvancedKnockback;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentCombatVeterancy;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentCounterAttack;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentCulling;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentLifesteal;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentMortalitas;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentParry;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentPenetratingEdge;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentRune_MagicalBlessing;
import com.Shultrea.Rin.Ench0_2_0.EnchantmentUnpredictable;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import net.minecraft.enchantment.Enchantment;

public class Smc_020 {
   public static EnchantmentBase PenetratingEdge;
   public static EnchantmentBase CombatRegeneration;
   public static EnchantmentBase AdvancedKnockback;
   public static EnchantmentBase Rune_MagicalBlessing;
   public static EnchantmentBase CounterAttack;
   public static EnchantmentBase Parry;
   public static EnchantmentBase Unpredictable;
   public static EnchantmentBase Lifesteal;
   public static EnchantmentBase Culling;
   public static EnchantmentBase Mortalitas;

   public Smc_020() {
   }

   public static void init() {
      Mortalitas = registerAs(new EnchantmentMortalitas());
      PenetratingEdge = registerAs(new EnchantmentPenetratingEdge());
      CombatRegeneration = registerAs(new EnchantmentCombatVeterancy());
      AdvancedKnockback = registerAs(new EnchantmentAdvancedKnockback());
      Rune_MagicalBlessing = registerAs(new EnchantmentRune_MagicalBlessing());
      CounterAttack = registerAs(new EnchantmentCounterAttack());
      Parry = registerAs(new EnchantmentParry());
      Unpredictable = registerAs(new EnchantmentUnpredictable());
      Lifesteal = registerAs(new EnchantmentLifesteal());
      Culling = registerAs(new EnchantmentCulling());
   }

   private static EnchantmentBase registerAs(Enchantment enchant) {
      return OrderedRegistry.registerAs(enchant);
   }
}
