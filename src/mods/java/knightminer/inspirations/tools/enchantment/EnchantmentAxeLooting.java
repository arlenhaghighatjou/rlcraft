package knightminer.inspirations.tools.enchantment;

import knightminer.inspirations.common.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;

public class EnchantmentAxeLooting extends EnchantmentLootBonus {
   public EnchantmentAxeLooting(Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, typeIn, slots);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return Config.axeEnchantmentTable && stack.getItem() instanceof ItemAxe || super.canApplyAtEnchantingTable(stack);
   }

   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemAxe || super.canApplyAtEnchantingTable(stack);
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.FORTUNE;
   }
}
