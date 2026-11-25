package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Interfaces.IPotionDebuffer;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class EnchantmentHors_de_combat extends EnchantmentBase implements IPotionDebuffer {
   public EnchantmentHors_de_combat() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Hors_de_combat");
      this.setRegistryName("Hors_de_combat");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Hors_de_combat;
   }

   public int getMaxLevel() {
      return ModConfig.level.Hors_de_combat;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return fTest instanceof IPotionDebuffer ? false : super.canApplyTogether(fTest);
   }

   public void onEntityDamagedAlt(EntityLivingBase user, Entity victims, ItemStack stack, int level) {
      if (this.isEnabled()) {
         if (victims instanceof EntityLivingBase) {
            EntityLivingBase victim = (EntityLivingBase)victims;
            if (Math.random() * 100.0D < 10.0D) {
               if (level == 1 || level == 2) {
                  victim.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 + level * 10, level - 1));
                  victim.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20 + level * 10, level * 2));
               }

               if (level >= 3) {
                  victim.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 20 + level * 10, level - 1));
                  victim.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 20 + level * 10, level - 1));
                  victim.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20 + level * 10, level - 1));
                  victim.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 20 + level * 10, level - 3));
               }
            }

         }
      }
   }
}
