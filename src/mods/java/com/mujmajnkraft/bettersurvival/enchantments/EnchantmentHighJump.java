package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentHighJump extends Enchantment {
   public EnchantmentHighJump() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR_FEET, new EntityEquipmentSlot[]{EntityEquipmentSlot.FEET});
      this.setRegistryName("highjump");
      this.setName("mujmajnkraftsbettersurvival.highjump");
   }

   public static void boostJump(EntityLivingBase jumper, int level) {
      jumper.motionY += (double)level / 10.0D;
   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.highJumpLevel;
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.highJumpTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.highJumpLevel != 0;
   }
}
