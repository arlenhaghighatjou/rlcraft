package lykrast.defiledlands.common.enchantment;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentDestructive extends Enchantment {
   public EnchantmentDestructive(Rarity rarityIn) {
      super(rarityIn, ModEnchantments.DESTRUCTIVE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 10;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 15;
   }

   public int getMaxLevel() {
      return 5;
   }
}
