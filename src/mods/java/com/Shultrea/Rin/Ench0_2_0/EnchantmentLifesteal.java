package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Main_Sector.somanyenchantments;
import com.Shultrea.Rin.Utility_Sector.UtilityAccessor;
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

public class EnchantmentLifesteal extends EnchantmentBase {
   public EnchantmentLifesteal() {
      super(Rarity.RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Lifesteal");
      this.setRegistryName("Lifesteal");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.LifestealEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Lifesteal;
   }

   public int getMinEnchantability(int par1) {
      return 6 + 8 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && fTest != Smc_010.BlessedEdge && fTest != Smc_010.CursedEdge;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void HandlingFirst(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon != null) {
               int levellifesteal = EnchantmentHelper.getEnchantmentLevel(Smc_020.Lifesteal, weapon);
               if (EnchantmentHelper.getEnchantmentLevel(Smc_020.Lifesteal, weapon) > 0) {
                  if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                     attacker.heal(fEvent.getAmount() * ((float)levellifesteal * 0.025F + 0.025F));
                     UtilityAccessor.damageTarget(fEvent.getEntityLiving(), somanyenchantments.PhysicalDamage, fEvent.getAmount() * (0.05F + (float)levellifesteal * 0.05F));
                  }
               }
            }
         }
      }
   }
}
