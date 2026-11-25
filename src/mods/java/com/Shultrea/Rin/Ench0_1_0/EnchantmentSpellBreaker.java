package com.Shultrea.Rin.Ench0_1_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enum.EnumList;
import com.Shultrea.Rin.Interfaces.IEnchantmentDamage;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentSpellBreaker extends EnchantmentBase implements IEnchantmentDamage {
   public EnchantmentSpellBreaker() {
      super(Rarity.RARE, EnumList.COMBAT, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("SpellBreaker");
      this.setRegistryName("SpellBreaker");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.SpellBreakerEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.SpellBreaker;
   }

   public int getMinEnchantability(int par1) {
      return 15 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onHurt(LivingHurtEvent e) {
      if (EnchantmentsUtility.checkEventCondition(e, this)) {
         EntityLivingBase attacker = (EntityLivingBase)e.getSource().getTrueSource();
         ItemStack stack = attacker.getHeldItemMainhand();
         int level = EnchantmentHelper.getEnchantmentLevel(this, stack);
         if (level > 0) {
            if (!isOffensivePetDisallowed(e.getSource().getImmediateSource(), e.getSource().getTrueSource())) {
               if (e.getEntityLiving().getActivePotionEffects().size() > 0) {
                  e.setAmount(e.getAmount() + 0.625F * (float)level * (float)e.getEntityLiving().getActivePotionEffects().size());
               }

               if (e.getEntityLiving() instanceof EntityWitch || e.getEntityLiving() instanceof EntityEvoker) {
                  e.setAmount(e.getAmount() + 1.5F * (float)level);
               }

            }
         }
      }
   }
}
