package svenhjol.charm.enchanting.enchantment;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import svenhjol.charm.enchanting.feature.Magnetic;
import svenhjol.meson.MesonEnchantment;

public class EnchantmentMagnetic extends MesonEnchantment {
   public EnchantmentMagnetic() {
      super("magnetic", Rarity.RARE, EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return Magnetic.minEnchantability;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isTreasureEnchantment() {
      return false;
   }

   public boolean canApply(ItemStack stack) {
      return stack.getItem() instanceof ItemShears || super.canApply(stack);
   }

   public String getModId() {
      return "charm";
   }
}
