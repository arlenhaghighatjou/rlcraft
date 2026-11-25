package lykrast.defiledlands.common.enchantment;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentBlazing extends Enchantment {
   public EnchantmentBlazing(Rarity rarityIn) {
      super(rarityIn, ModEnchantments.UMBRA_BLASTER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 20;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
