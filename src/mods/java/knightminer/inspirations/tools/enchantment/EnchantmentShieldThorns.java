package knightminer.inspirations.tools.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentShieldThorns extends EnchantmentThorns {
   public EnchantmentShieldThorns(Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, slots);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.getItem().isShield(stack, (EntityLivingBase)null) || super.canApplyAtEnchantingTable(stack);
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.FIRE_ASPECT;
   }
}
