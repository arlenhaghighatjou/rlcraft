package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Interfaces.IWeatherEnchantment;
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
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentWinter extends EnchantmentBase implements IWeatherEnchantment {
   public EnchantmentWinter() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Winter");
      this.setRegistryName("Winter");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.WinterEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Winter;
   }

   public int getMinEnchantability(int par1) {
      return 10 + (par1 - 1) * 10;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IWeatherEnchantment);
   }

   public int level(ItemStack stack) {
      return EnchantmentHelper.getEnchantmentLevel(Smc_030.Winter, stack);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.HIGHEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      float SkyDamage = 0.0F;
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_030.Winter, dmgSource) > 0) {
                     BlockPos blockpos = attacker.getPosition();
                     float Damage = fEvent.getAmount();
                     int levelWinter = EnchantmentHelper.getEnchantmentLevel(Smc_030.Winter, dmgSource);
                     if (attacker.world.isRaining() && EnchantmentsUtility.noBlockLight(attacker) && EnchantmentsUtility.isInColdTemperature(attacker, attacker.getEntityWorld().getBiome(blockpos), true)) {
                        float FDamage = EnchantmentsUtility.CalculateDamageIgnoreSwipe(Damage, 0.1F, 0.9F, 1.15F, attacker, Smc_030.Winter);
                        fEvent.setAmount(FDamage);
                        if (levelWinter >= 2) {
                           fEvent.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 60, levelWinter - 2));
                        }

                        if (levelWinter >= 4) {
                           fEvent.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 60, levelWinter - 4));
                        }

                        fEvent.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 60, levelWinter - 3));
                     } else if ((attacker.world.isRaining() || !EnchantmentsUtility.noBlockLight(attacker)) && EnchantmentsUtility.isInColdTemperature(attacker, attacker.getEntityWorld().getBiome(blockpos), true)) {
                        if (!EnchantmentsUtility.noBlockLight(attacker)) {
                           float Fid = EnchantmentsUtility.CalculateDamageForNegativeSwipe(Damage, 0.0F, -0.8F, 1.0F, attacker, Smc_030.Winter);
                           fEvent.setAmount(Fid);
                        }
                     } else {
                        float Fin = EnchantmentsUtility.CalculateDamageForNegativeSwipe(Damage, 0.0F, -0.6F, 1.0F, attacker, Smc_030.Winter);
                        fEvent.setAmount(Fin);
                        if (Math.random() * 5.0D < (double)(0.02F + (float)levelWinter)) {
                           EnchantmentsUtility.Raining(attacker.getEntityWorld());
                        }
                     }

                  }
               }
            }
         }
      }
   }
}
