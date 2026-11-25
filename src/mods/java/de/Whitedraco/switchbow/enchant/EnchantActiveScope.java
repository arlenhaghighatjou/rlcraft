package de.Whitedraco.switchbow.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class EnchantActiveScope extends Enchantment {
   public EnchantActiveScope() {
      super(Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("activeScope");
      this.setRegistryName("activeScope");
   }

   public int getMaxEnchantability(int level) {
      return 60;
   }

   public int getMinEnchantability(int level) {
      return 20;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.getItem() instanceof ItemBow;
   }
}
