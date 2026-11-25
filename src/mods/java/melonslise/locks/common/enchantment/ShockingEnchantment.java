package melonslise.locks.common.enchantment;

import melonslise.locks.common.init.LocksEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ShockingEnchantment extends Enchantment {
   public ShockingEnchantment() {
      super(Rarity.UNCOMMON, LocksEnchantments.LOCK_TYPE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public int getMinEnchantability(int level) {
      return 2 + (level - 1) * 9;
   }

   public int getMaxEnchantability(int level) {
      return this.getMinEnchantability(level) + 30;
   }

   public int getMaxLevel() {
      return 5;
   }
}
