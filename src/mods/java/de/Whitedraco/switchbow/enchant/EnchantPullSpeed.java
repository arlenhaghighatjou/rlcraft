package de.Whitedraco.switchbow.enchant;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.EnchantmentConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantPullSpeed extends Enchantment {
   public EnchantPullSpeed() {
      super(Rarity.RARE, EnumEnchantmentType.BOW, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("pullSpeed");
      this.setRegistryName("pullSpeed");
   }

   public int getMaxEnchantability(int level) {
      return 60;
   }

   public int getMinEnchantability(int level) {
      return 10;
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return EnchantmentConfig.enchantPullingSpeedMax.getInteger();
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return stack.getItem() == ItemInit.SwitchBow;
   }
}
