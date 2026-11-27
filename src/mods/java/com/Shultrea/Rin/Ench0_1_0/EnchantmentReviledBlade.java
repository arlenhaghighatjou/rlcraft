package com.Shultrea.Rin.Ench0_1_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Interfaces.IDamageMultiplier;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentReviledBlade extends EnchantmentBase implements IDamageMultiplier {
   public EnchantmentReviledBlade() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("ReviledBlade");
      this.setRegistryName("ReviledBlade");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.ReviledBladeEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.ReviledBlade;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IDamageMultiplier);
   }

   public void register(EventBus bus) {
      bus.register(LivingDamageEvent.class, EventPriority.LOWEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingDamageEvent fEvent) {
      if (EnchantmentsUtility.checkEventCondition(fEvent, this)) {
         EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
         ItemStack weapon = attacker.getHeldItemMainhand();
         if (EnchantmentHelper.getEnchantmentLevel(Smc_010.ReviledBlade, weapon) > 0) {
            if (isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
               return;
            }

            float defenderHealthPercent = fEvent.getEntityLiving().getHealth() / fEvent.getEntityLiving().getMaxHealth();
            int levelfinish = EnchantmentHelper.getEnchantmentLevel(Smc_010.ReviledBlade, weapon);
            float dmgMod = (1.0F - defenderHealthPercent) * ((float)levelfinish * 0.1F + 0.9F);
            ++dmgMod;
            float Damage = fEvent.getAmount();
            fEvent.setAmount(dmgMod * Damage);
         }

      }
   }
}
