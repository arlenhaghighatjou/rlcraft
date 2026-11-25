package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentNaturalBlocking extends EnchantmentBase {
   public EnchantmentNaturalBlocking() {
      super(Rarity.RARE, EnumList.SHIELD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("NaturalBlocking");
      this.setRegistryName("NaturalBlocking");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.NaturalBlocking;
   }

   public int getMaxLevel() {
      return ModConfig.level.NaturalBlocking;
   }

   public int getMinEnchantability(int par1) {
      return 18 + 12 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void naturalBlock(LivingDamageEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase victim = (EntityLivingBase)fEvent.getEntity();
         if (victim != null) {
            ItemStack shield = victim.getHeldItemMainhand();
            if (shield.isEmpty() || !shield.getItem().isShield(shield, victim)) {
               shield = victim.getHeldItemOffhand();
               if (shield.isEmpty()) {
                  return;
               }
            }

            if (EnchantmentHelper.getEnchantmentLevel(Smc_040.NaturalBlocking, shield) > 0) {
               int level = EnchantmentHelper.getEnchantmentLevel(Smc_040.NaturalBlocking, shield);
               float damage = fEvent.getAmount() - fEvent.getAmount() * ((float)level * 0.1F + 0.1F);
               if (shield.getItem().isShield(shield, victim) && victim.getActiveItemStack() != shield) {
                  float percentage = damage / fEvent.getAmount();
                  percentage = 1.0F - percentage;
                  fEvent.setAmount(damage);
                  if (level >= 3) {
                     shield.damageItem((int)(1.25F * fEvent.getAmount() * percentage) + 1, victim);
                  } else {
                     shield.damageItem((int)(1.75F * fEvent.getAmount() * percentage) + 1, victim);
                  }
               }

            }
         }
      }
   }
}
