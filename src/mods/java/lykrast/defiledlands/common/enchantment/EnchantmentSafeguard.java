package lykrast.defiledlands.common.enchantment;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentSafeguard extends Enchantment {
   public EnchantmentSafeguard(Rarity rarityIn) {
      super(rarityIn, ModEnchantments.UMBRA_BLASTER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }
}
