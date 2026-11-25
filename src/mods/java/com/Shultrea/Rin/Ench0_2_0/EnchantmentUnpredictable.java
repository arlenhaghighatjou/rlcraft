package com.Shultrea.Rin.Ench0_2_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_020;
import com.Shultrea.Rin.Interfaces.IEnchantmentCurse;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentUnpredictable extends EnchantmentBase implements IEnchantmentCurse {
   public EnchantmentUnpredictable() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Unpredictable");
      this.setRegistryName("Unpredictable");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.UnpredictableEnable;
   }

   public int getMaxLevel() {
      return ModConfig.level.Unpredictable;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return this.getMinEnchantability(par1) + 40;
   }

   public boolean isCurse() {
      return true;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void HandleEnchant(LivingHurtEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_020.Unpredictable, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levelDamageRandomizer = EnchantmentHelper.getEnchantmentLevel(Smc_020.Unpredictable, dmgSource);
                        float random = (float)Math.random() * fEvent.getAmount() * (float)levelDamageRandomizer * 1.25F;
                        random = (float)(Math.floor((double)random) + 1.0D);
                        fEvent.setAmount(random + 2.0F);
                        if (fEvent.getEntity().world.rand.nextInt(100) <= 45) {
                           fEvent.setAmount(0.0F);
                           fEvent.getEntityLiving().heal(random);
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
