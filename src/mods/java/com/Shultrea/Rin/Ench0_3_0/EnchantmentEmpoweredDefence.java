package com.Shultrea.Rin.Ench0_3_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentEmpoweredDefence extends EnchantmentBase {
   public EnchantmentEmpoweredDefence() {
      super(Rarity.RARE, EnumList.SHIELD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("EmpoweredDefence");
      this.setRegistryName("EmpoweredDefence");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.EmpoweredDefenceEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.EmpoweredDefence;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 30;
   }

   public boolean canApply(ItemStack e) {
      return super.canApply(e) || e.getItem().isShield(e, (EntityLivingBase)null);
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void EmpoweredDefenceEvent(LivingAttackEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase victim = (EntityLivingBase)fEvent.getEntity();
         ItemStack shield = victim.getHeldItemOffhand();
         if (shield == ItemStack.EMPTY) {
            shield = victim.getHeldItemMainhand();
            if (shield == ItemStack.EMPTY) {
               return;
            }
         }

         if (EnchantmentHelper.getEnchantmentLevel(Smc_030.EmpoweredDefence, shield) > 0) {
            int levelED = EnchantmentHelper.getEnchantmentLevel(Smc_030.EmpoweredDefence, shield);
            Entity attacker = fEvent.getSource().getImmediateSource();
            ItemStack shield2 = fEvent.getEntityLiving().getActiveItemStack();
            if (shield2.getItem().isShield(shield, victim) && fEvent.getEntityLiving().world.rand.nextInt(100) < 20 + levelED * 5 && EnchantmentsUtility.canBlockDamageSource(fEvent.getSource(), victim)) {
               fEvent.setCanceled(true);
               EnchantmentsUtility.ImprovedKnockBack(attacker, 0.4F + 0.2F * (float)levelED, victim.posX - attacker.posX, victim.posZ - attacker.posZ);
               if (victim instanceof EntityPlayer) {
                  attacker.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)victim), fEvent.getAmount() * 0.225F * (float)levelED);
               } else {
                  attacker.attackEntityFrom(DamageSource.causeMobDamage(victim), fEvent.getAmount() * 0.225F * (float)levelED);
               }

               victim.hurtResistantTime = 15;
            }

         }
      }
   }
}
