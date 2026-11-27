package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Interfaces.IEnchantmentProtection;
import com.Shultrea.Rin.Interfaces.IEnhancedEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentProtection.Type;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;

public class EnchantmentAdvancedBlastProtection extends EnchantmentBase implements IEnchantmentProtection, IEnhancedEnchantment {

   public EnchantmentAdvancedBlastProtection() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
      this.setName("AdvancedBlastProtection");
      this.setRegistryName("AdvancedBlastProtection");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedBlastProtection;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedBlastProtection;
   }

   public int getMinEnchantability(int par1) {
      return 24 + (par1 - 1) * 14;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 50;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      if (fTest instanceof EnchantmentProtection) {
         EnchantmentProtection p = (EnchantmentProtection)fTest;
         return p.protectionType == Type.FALL;
      } else if (fTest instanceof EnchantmentAdvancedFeatherFalling) {
         return true;
      } else {
         return super.canApplyTogether(fTest) && !(fTest instanceof IEnchantmentProtection);
      }
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return source.canHarmInCreative() ? 0 : (source.isExplosion() ? level * 3 : 0);
   }

   public static double getKnockbackValue(double damage, EntityLivingBase entity) {
      if (!Smc_030.AdvancedBlastProtection.isEnabled()) {
         return damage;
      } else {
         int i = EnchantmentHelper.getMaxEnchantmentLevel(Smc_030.AdvancedBlastProtection, entity);
         if (i > 0) {
            damage -= (double)MathHelper.floor(damage * (double)((float)i * 0.3F));
         }

         return damage;
      }
   }
}
