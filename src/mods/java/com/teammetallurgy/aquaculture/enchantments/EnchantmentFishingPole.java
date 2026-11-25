package com.teammetallurgy.aquaculture.enchantments;

import com.teammetallurgy.aquaculture.items.ItemAdminFishingRod;
import com.teammetallurgy.aquaculture.items.ItemAquacultureFishingRod;
import javax.annotation.Nonnull;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentFishingPole extends Enchantment {
   public EnchantmentFishingPole(Rarity rarity) {
      super(rarity, AquacultureEnchants.enumFishingPole, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
   }

   public boolean canApply(@Nonnull ItemStack stack) {
      return stack.getItem() instanceof ItemAquacultureFishingRod || stack.getItem() instanceof ItemAdminFishingRod;
   }

   public boolean canApplyAtEnchantingTable(@Nonnull ItemStack stack) {
      return this.canApply(stack);
   }
}
