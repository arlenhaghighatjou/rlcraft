package com.Shultrea.Rin.Ench0_4_5;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentEvasion extends EnchantmentBase {
   public EnchantmentEvasion() {
      super(Rarity.RARE, EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
      this.setName("Evasion");
      this.setRegistryName("Evasion");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Evasion;
   }

   public int getMaxLevel() {
      return ModConfig.level.Evasion;
   }

   public int getMinEnchantability(int par1) {
      return 25 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   @SubscribeEvent(
      priority = EventPriority.LOW,
      receiveCanceled = false
   )
   public void HandleEnchant(LivingAttackEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob" || fEvent.getSource().damageType != "arrow") {
         if (fEvent.getEntity() instanceof EntityLivingBase) {
            if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
               EntityLivingBase victim = fEvent.getEntityLiving();
               if (victim != null) {
                  EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
                  if (attacker != null) {
                     if (EnchantmentHelper.getEnchantmentLevel(Smc_040.TrueStrike, attacker.getHeldItemMainhand()) <= 0 || EnchantmentsUtility.RANDOM.nextInt(100) >= 75) {
                        int level = EnchantmentHelper.getMaxEnchantmentLevel(this, victim);
                        if (level > 0) {
                           double randX = 0.65D + victim.getRNG().nextDouble() * 0.25D;
                           randX = victim.getRNG().nextBoolean() ? randX * -1.0D : randX;
                           double randZ = 0.65D + victim.getRNG().nextDouble() * 0.25D;
                           randZ = victim.getRNG().nextBoolean() ? randZ * -1.0D : randZ;
                           if (fEvent.getEntityLiving().world.rand.nextInt(100) < 5 + level * 15) {
                              if (!attacker.world.isRemote && ModConfig.miscellaneous.EvasionKnockback) {
                                 EnchantmentsUtility.ImprovedKnockBack(victim, 0.7F, (attacker.posX - victim.posX) * randX, (attacker.posZ - victim.posZ) * randZ);
                              }

                              victim.getEntityWorld().playSound((EntityPlayer)null, victim.posX, victim.posY, victim.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.3F, victim.getRNG().nextFloat() * 2.25F + 0.75F);
                              fEvent.setCanceled(true);
                              victim.hurtResistantTime = 15 + 5 * level;
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
