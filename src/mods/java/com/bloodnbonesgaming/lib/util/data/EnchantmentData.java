package com.bloodnbonesgaming.lib.util.data;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.enchantment.Enchantment;

public class EnchantmentData {
   private MinMaxBounds levels = MinMaxBounds.UNBOUNDED;
   private Enchantment enchantment;

   public EnchantmentData() {
   }

   public EnchantmentPredicate buildPredicate() {
      return this.enchantment == null && this.levels == MinMaxBounds.UNBOUNDED ? new EnchantmentPredicate() : new EnchantmentPredicate(this.enchantment, this.levels);
   }

   public void setEnchantment(String enchantment) throws Exception {
      this.enchantment = Enchantment.getEnchantmentByLocation(enchantment);
      if (this.enchantment == null) {
         throw new Exception(enchantment + " is not a valid enchantment id!");
      }
   }

   public void setMinMaxBounds(MinMaxBounds levels) {
      this.levels = levels;
   }
}
