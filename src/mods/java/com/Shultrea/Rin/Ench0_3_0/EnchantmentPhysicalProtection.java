package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IEnchantmentProtection;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentProtection.Type;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;

public class EnchantmentPhysicalProtection extends EnchantmentBase implements IEnchantmentProtection {
   public EnchantmentPhysicalProtection() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
      this.setName("PhysicalProtection");
      this.setRegistryName("PhysicalProtection");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.PhysicalProtectionEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.PhysicalProtection;
   }

   public int getMinEnchantability(int par1) {
      return 14 + (par1 - 1) * 12;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 45;
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
      return source.canHarmInCreative() ? 0 : (!source.isMagicDamage() && !source.isFireDamage() && !source.isExplosion() && !source.isProjectile() && !source.damageType.equals("outOfWorld") && !source.damageType.equals("drown") && !source.damageType.equals("generic") && !source.damageType.equals("wither") && !source.damageType.equals("lightningBolt") && !source.damageType.equals("inFire") && !source.damageType.equals("onFire") && !source.damageType.equals("hotFloor") && !source.damageType.equals("Ethereal") && !source.damageType.equals("Culled") ? level * 3 : 0);
   }
}
