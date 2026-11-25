package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentFieryShield extends EnchantmentBase {
   public EnchantmentFieryShield() {
      super(Rarity.RARE, EnumList.SHIELD, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
      this.setName("fieryshield");
      this.setRegistryName("fieryshield");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.FieryShield;
   }

   public int getMaxLevel() {
      return ModConfig.level.FieryShield;
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
   public void shieldBurn(LivingAttackEvent fEvent) {
      if (fEvent.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase victim = (EntityLivingBase)fEvent.getEntity();
         ItemStack shield = victim.getHeldItemMainhand();
         if (shield.isEmpty() || !shield.getItem().isShield(shield, victim)) {
            shield = victim.getHeldItemOffhand();
            if (shield.isEmpty()) {
               return;
            }
         }

         if (EnchantmentHelper.getEnchantmentLevel(Smc_040.fieryshield, shield) > 0) {
            int levelfs = EnchantmentHelper.getEnchantmentLevel(Smc_040.fieryshield, shield);
            Entity attacker = fEvent.getSource().getImmediateSource();
            if (shield.getItem().isShield(shield, victim) && fEvent.getEntityLiving().world.rand.nextInt(100) < 40 + levelfs * 10 && EnchantmentsUtility.canBlockDamageSource(fEvent.getSource(), victim)) {
               attacker.attackEntityFrom((new EntityDamageSource("player", victim)).setFireDamage(), fEvent.getAmount() * (float)levelfs * 0.1F);
               attacker.setFire(4 + levelfs * 2);
            }

         }
      }
   }
}
