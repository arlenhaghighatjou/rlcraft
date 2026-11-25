package com.Shultrea.Rin.Ench0_4_0;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class EnchantmentBiomePresence extends Enchantment {
   private static final String[] PRESENCE_NAMES = new String[]{"plains", "iceplains", "desert", "forest", "mountain", "roofForest", "taiga", "ocean", "mushroom", "swamp", "mesa", "savanna"};
   private static final int[] MIN_COST = new int[]{1, 120, 1, 120, 1, 120};
   private static final int[] LEVEL_COST = new int[]{4, 80, 5, 80, 4, 80};
   private static final int[] LEVEL_COST_SPAN = new int[]{20, 180, 20, 180, 20, 180};
   public final int presenceType;

   public EnchantmentBiomePresence(Rarity rarityIn, int damageTypeIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.ARMOR, slots);
      this.presenceType = damageTypeIn;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return MIN_COST[this.presenceType] + (enchantmentLevel - 1) * LEVEL_COST[this.presenceType];
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + LEVEL_COST_SPAN[this.presenceType];
   }

   public int getMaxLevel() {
      return 4;
   }

   public String getName() {
      return "enchantment." + PRESENCE_NAMES[this.presenceType];
   }

   public boolean canApplyTogether(Enchantment ench) {
      return !(ench instanceof EnchantmentBiomePresence);
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack) {
      return super.canApplyAtEnchantingTable(stack);
   }

   public boolean canApply(ItemStack stack) {
      return super.canApply(stack);
   }
}
