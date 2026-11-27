package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Interfaces.IEnchantmentProtection;
import com.Shultrea.Rin.Interfaces.IEnhancedEnchantment;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentAdvancedFireProtection extends EnchantmentBase implements IEnchantmentProtection, IEnhancedEnchantment {
   public EnchantmentAdvancedFireProtection() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.ARMOR, new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET});
      this.setName("AdvancedFireProtection");
      this.setRegistryName("AdvancedFireProtection");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.AdvancedFireProtection;
   }

   public int getMaxLevel() {
      return ModConfig.level.AdvancedFireProtection;
   }

   public int getMinEnchantability(int par1) {
      return 27 + (par1 - 1) * 11;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest != Smc_030.AdvancedProjectileProtection && fTest != Smc_030.AdvancedBlastProtection && fTest != Smc_030.AdvancedProtection && fTest != Smc_030.MagicProtection && fTest != Enchantments.FIRE_PROTECTION && fTest != Enchantments.BLAST_PROTECTION && fTest != Enchantments.PROJECTILE_PROTECTION && fTest != Enchantments.PROTECTION && fTest != Smc_030.PhysicalProtection ? super.canApplyTogether(fTest) : false;
   }

   public int calcModifierDamage(int level, DamageSource source) {
      return source.canHarmInCreative() ? 0 : (source.isFireDamage() ? level * 3 : 0);
   }

   public void register(EventBus bus) {
      bus.register(LivingUpdateEvent.class, EventPriority.HIGHEST, false, this::updateFireEntity);
   }

   public void updateFireEntity(LivingUpdateEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase victim = fEvent.getEntityLiving();
         int totalFireProtectLevel = EnchantmentHelper.getMaxEnchantmentLevel(Smc_030.AdvancedFireProtection, victim);
         if (totalFireProtectLevel > 0) {
            if (victim.isBurning()) {
               if (Math.random() < 0.025D + 0.0025D * (double)totalFireProtectLevel) {
                  victim.extinguish();
               }

            }
         }
      }
   }
}
