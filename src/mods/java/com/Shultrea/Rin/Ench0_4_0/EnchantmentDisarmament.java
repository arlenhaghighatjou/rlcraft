package com.Shultrea.Rin.Ench0_4_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EnchantmentDisarmament extends EnchantmentBase {
   public EnchantmentDisarmament() {
      super(Rarity.RARE, EnumList.COMBAT_AXE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Disarmament");
      this.setRegistryName("Disarmament");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Disarmament;
   }

   public int getMaxLevel() {
      return ModConfig.level.Disarmament;
   }

   public int getMinEnchantability(int par1) {
      return 10 + 20 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 50;
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_040.Disarmament, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int leveldisarm = EnchantmentHelper.getEnchantmentLevel(Smc_040.Disarmament, dmgSource);
                        if (Math.random() * 100.0D < 25.0D) {
                           fEvent.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 1));
                           fEvent.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 40 + leveldisarm * 20, 254));
                           if (Math.random() * 100.0D < (double)(leveldisarm * 5)) {
                              EnchantmentsUtility.Disarm(fEvent.getEntityLiving());
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
