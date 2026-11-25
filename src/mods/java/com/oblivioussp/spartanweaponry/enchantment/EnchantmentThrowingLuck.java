package com.oblivioussp.spartanweaponry.enchantment;

import net.minecraft.enchantment.EnchantmentLootBonus;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentThrowingLuck extends EnchantmentLootBonus {
   public EnchantmentThrowingLuck(Rarity rarityIn) {
      super(rarityIn, EnchantmentSW.TYPE_THROWING_WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("lucky_throw");
      this.setRegistryName("lucky_throw");
   }

   public String getName() {
      return String.format("enchantment.%s:%s", "spartanweaponry", this.name);
   }
}
