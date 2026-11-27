package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.UtilityAccessor;
import java.util.Collection;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentPurgingBlade extends EnchantmentBase {
   public EnchantmentPurgingBlade() {
      super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("PurgingBlade");
      this.setRegistryName("PurgingBlade");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.PurgingBladeEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.PurgingBlade;
   }

   public int getMinEnchantability(int par1) {
      return 15 + (par1 - 1) * 8;
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.HIGHEST, true, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType.equals("player") || fEvent.getSource().damageType.equals("mob")) {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_030.PurgingBlade, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levelPurging = EnchantmentHelper.getEnchantmentLevel(Smc_030.PurgingBlade, dmgSource);
                        if (Math.random() <= 0.1D + (double)(0.06F * (float)levelPurging)) {
                           UtilityAccessor.damageTarget(fEvent.getEntityLiving(), DamageSource.MAGIC, 1.25F + 0.75F * (float)levelPurging);
                           fEvent.getEntityLiving().hurtResistantTime = 0;
                           fEvent.setAmount(fEvent.getAmount() * 1.2F);
                           Collection<PotionEffect> collectPotion = fEvent.getEntityLiving().getActivePotionEffects();
                           int size = collectPotion.size();

                           for(int i = 0; i < size; ++i) {
                              if (!collectPotion.iterator().hasNext()) {
                                 return;
                              }

                              PotionEffect Potions = (PotionEffect)collectPotion.iterator().next();
                              if (Math.random() > 0.5D) {
                                 if (collectPotion.iterator().hasNext()) {
                                    Potions = (PotionEffect)collectPotion.iterator().next();
                                 }

                                 if (Math.random() > 0.5D && collectPotion.iterator().hasNext()) {
                                    Potions = (PotionEffect)collectPotion.iterator().next();
                                 }
                              }

                              Potions.combine(new PotionEffect(Potions.getPotion(), 0, 256));
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
