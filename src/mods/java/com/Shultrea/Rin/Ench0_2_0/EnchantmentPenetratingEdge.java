package com.Shultrea.Rin.Ench0_2_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Interfaces.IEnchantmentRune;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class EnchantmentPenetratingEdge extends EnchantmentBase implements IEnchantmentDamage {
   public EnchantmentPenetratingEdge() {
      super(Rarity.RARE, EnumList.COMBAT_AXE, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("PenetratingEdge");
      this.setRegistryName("PenetratingEdge");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.PenetratingEdgeEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.PenetratingEdge;
   }

   public int getMinEnchantability(int par1) {
      return 14 + 12 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest) && !(fTest instanceof IEnchantmentRune);
   }

   public void register(EventBus bus) {
      bus.register(LivingHurtEvent.class, EventPriority.HIGHEST, false, this::HandleEnchant);
   }

   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            ItemStack weapon = attacker.getHeldItemMainhand();
            if (weapon != null) {
               if (fEvent.getEntity() instanceof EntityLivingBase) {
                  if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                     EntityLivingBase entity = (EntityLivingBase)fEvent.getEntity();
                     float armor = (float)fEvent.getEntityLiving().getTotalArmorValue();
                     if (armor >= 3.0F) {
                        int level = EnchantmentHelper.getEnchantmentLevel(Smc_020.PenetratingEdge, weapon);
                        if (EnchantmentHelper.getEnchantmentLevel(Smc_020.PenetratingEdge, weapon) != 0) {
                           fEvent.setAmount(fEvent.getAmount() + armor / 3.0F + 0.5F + (float)level * 0.16F);
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
