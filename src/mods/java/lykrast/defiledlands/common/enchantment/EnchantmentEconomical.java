package lykrast.defiledlands.common.enchantment;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentEconomical extends Enchantment {
   public EnchantmentEconomical(Rarity rarityIn) {
      super(rarityIn, ModEnchantments.GUN, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + (enchantmentLevel - 1) * 8;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return 4;
   }
}
