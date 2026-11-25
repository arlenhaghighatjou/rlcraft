package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentProtection;
import com.Shultrea.Rin.Interfaces.IEnhancedEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentProtection.Type;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;

public class EnchantmentAdvancedProjectileProtection extends EnchantmentBase implements IEnchantmentProtection, IEnhancedEnchantment {
   public EnchantmentAdvancedProjectileProtection() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
      this.setName("AdvancedProjectileProtection");
      this.setRegistryName("AdvancedProjectileProtection");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedProjectileProtection;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedProjectileProtection;
   }

   public int getMinEnchantability(int par1) {
      return 27 + (par1 - 1) * 11;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      if (fTest instanceof EnchantmentProtection) {
         EnchantmentProtection p = (EnchantmentProtection)fTest;
         return p.protectionType == Type.FALL;
      } else {
         return super.canApplyTogether(fTest) && !(fTest instanceof IEnchantmentProtection);
      }
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return source.canHarmInCreative() ? 0 : (source.isProjectile() ? level * 3 : 0);
   }
}
