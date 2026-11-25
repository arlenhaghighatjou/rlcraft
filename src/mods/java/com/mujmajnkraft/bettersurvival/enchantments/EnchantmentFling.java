package com.mujmajnkraft.bettersurvival.enchantments;

import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentFling extends Enchantment {
   public EnchantmentFling() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setRegistryName("fling");
      this.setName("mujmajnkraftsbettersurvival.fling");
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + 20 * (enchantmentLevel - 1);
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return super.getMinEnchantability(enchantmentLevel) + 50;
   }

   public int getMaxLevel() {
      return ForgeConfigHandler.enchantments.flingLevel;
   }

   public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
      target.motionY += 0.15D * (double)level;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.KNOCKBACK;
   }

   public boolean isTreasureEnchantment() {
      return ForgeConfigHandler.enchantments.flingTreasure;
   }

   public boolean isAllowedOnBooks() {
      return ForgeConfigHandler.enchantments.flingLevel != 0;
   }
}
