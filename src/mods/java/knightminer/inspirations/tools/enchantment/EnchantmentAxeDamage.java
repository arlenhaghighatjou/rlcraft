package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentAxeDamage extends EnchantmentDamage {
   public EnchantmentAxeDamage(Rarity rarityIn, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, damageTypeIn, slots);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.getItem() instanceof ItemAxe || super.canApplyAtEnchantingTable(stack);
   }
}
