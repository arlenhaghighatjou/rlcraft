package com.Shultrea.Rin.Ench0_3_0;

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
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentClearsky extends EnchantmentBase implements IWeatherEnchantment {
   public EnchantmentClearsky() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Clearsky");
      this.setRegistryName("Clearsky");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.ClearskyEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Clearsky;
   }

   public int getMinEnchantability(int par1) {
      return 15 + (par1 - 1) * 15;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IWeatherEnchantment);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void HandleEnchant(LivingHurtEvent fEvent) {
      float SkyDamage = 0.0F;
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_030.Clearsky, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levelSunshine = EnchantmentHelper.getEnchantmentLevel(Smc_030.Clearsky, dmgSource);
                        if (!attacker.world.isRaining() && EnchantmentsUtility.noBlockLight(attacker)) {
                           float FDamage = EnchantmentsUtility.CalculateDamageIgnoreSwipe(fEvent.getAmount(), 0.5F, 0.75F, 1.0F, attacker, Smc_030.Clearsky);
                           fEvent.setAmount(FDamage);
                        } else if (attacker.world.isRaining() || attacker.world.isThundering()) {
                           if (Math.random() * 2000.0D < (double)(3 + levelSunshine * 2)) {
                              EnchantmentsUtility.clearSky(fEvent.getEntityLiving().getEntityWorld());
                           }

                           float Fi = EnchantmentsUtility.CalculateDamageForNegativeSwipe(fEvent.getAmount(), 0.0F, -0.6F, 1.0F, attacker, Smc_030.Clearsky);
                           fEvent.setAmount(Fi);
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
