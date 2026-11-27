package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Interfaces.IConditionalDamage;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentViper extends EnchantmentBase implements IConditionalDamage {
   public EnchantmentViper() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Viper");
      this.setRegistryName("Viper");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.ViperEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Viper;
   }

   public int getMinEnchantability(int par1) {
      return 18 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 42;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IConditionalDamage);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.HIGH, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_030.Viper, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levelViper = EnchantmentHelper.getEnchantmentLevel(Smc_030.Viper, dmgSource);
                        float forgeDamage = fEvent.getAmount();
                        float FDamage = 0.0F;
                        if (fEvent.getEntityLiving().isPotionActive(MobEffects.POISON)) {
                           FDamage = (float)((double)FDamage + 1.25D + (double)(0.75F * (float)levelViper));
                        }

                        if (fEvent.getEntityLiving().isPotionActive(MobEffects.WITHER)) {
                           FDamage = (float)((double)FDamage + 0.5D + (double)(0.5F * (float)levelViper));
                        }

                        forgeDamage = EnchantmentsUtility.CalculateDamageIgnoreSwipe(forgeDamage + FDamage, 0.0F, 1.0F, 1.0F, attacker, (Enchantment)null);
                        fEvent.setAmount(forgeDamage);
                     }
                  }
               }
            }
         }
      }
   }
}
