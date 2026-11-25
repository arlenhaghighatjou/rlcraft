package lykrast.defiledlands.common.enchantment;

import lykrast.defiledlands.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentSharpshooter extends Enchantment {
   public EnchantmentSharpshooter(Rarity rarityIn) {
      super(rarityIn, ModEnchantments.GUN, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 8;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 30;
   }

   public int getMaxLevel() {
      return 3;
   }
}
