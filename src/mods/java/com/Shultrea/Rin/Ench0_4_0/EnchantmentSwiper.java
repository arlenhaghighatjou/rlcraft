package com.Shultrea.Rin.Ench0_4_0;

import com.Shultrea.Rin.Enchantment_Base_Sector.EnchantmentBase;
import com.Shultrea.Rin.Enchantments_Sector.Smc_010;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Enchantments_Sector.Smc_040;
import com.Shultrea.Rin.Main_Sector.ModConfig;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import java.util.ArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentSweepingEdge;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EnchantmentSwiper extends EnchantmentBase {
   public EnchantmentSwiper() {
      super(Rarity.VERY_RARE, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Swiper");
      this.setRegistryName("Swiper");
   }

   public boolean isConfigEnabled() {
      return ModConfig.enabled.Swiper;
   }

   public int getMaxLevel() {
      return ModConfig.level.Swiper;
   }

   public int getMinEnchantability(int par1) {
      return 20 + 15 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 40;
   }

   public boolean canApplyTogether(Enchantment e) {
      return super.canApplyTogether(e) && !(e instanceof EnchantmentSweepingEdge);
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public void HandleEnchant(LivingDamageEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  int levelCleave = Math.min(10, EnchantmentHelper.getEnchantmentLevel(Smc_040.Swiper, dmgSource));
                  int lf = EnchantmentHelper.getEnchantmentLevel(Smc_010.FieryEdge, dmgSource) * 5;
                  lf = lf + EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, dmgSource) * 3;
                  lf = lf + EnchantmentHelper.getEnchantmentLevel(Smc_040.AdvancedFireAspect, dmgSource) * 6;
                  lf = lf + EnchantmentHelper.getEnchantmentLevel(Smc_040.SupremeFireAspect, dmgSource) * 12;
                  lf = lf + (int)((float)EnchantmentHelper.getEnchantmentLevel(Smc_040.LesserFireAspect, dmgSource) * 1.5F);
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_040.Swiper, dmgSource) > 0) {
                     if (!isOffensivePetDisallowed(fEvent.getSource().getImmediateSource(), fEvent.getSource().getTrueSource())) {
                        int levitationLevel = EnchantmentHelper.getEnchantmentLevel(Smc_030.Levitator, dmgSource);
                        float splashDamage = fEvent.getAmount() * (float)levelCleave * 0.25F;
                        AxisAlignedBB boundBox = new AxisAlignedBB(attacker.posX - 5.0D - (double)levelCleave, attacker.posY - 5.0D - (double)levelCleave, attacker.posZ - 5.0D - (double)levelCleave, attacker.posX + 5.0D + (double)levelCleave, attacker.posY + 5.0D + (double)levelCleave, attacker.posZ + 5.0D + (double)levelCleave);

                        for(Entity target : new ArrayList(attacker.getEntityWorld().getEntitiesWithinAABBExcludingEntity(fEvent.getEntity(), boundBox))) {
                           if (target instanceof EntityLivingBase && target != attacker && target != fEvent.getEntityLiving() && target.getDistance(attacker) <= 3.0F + (float)levelCleave * 0.25F) {
                              Vec3d attackerCheck = EnchantmentsUtility.Cleave(target.posX - attacker.posX, target.posY - attacker.posY, target.posZ - attacker.posZ);
                              double angle = Math.toDegrees(Math.acos(attackerCheck.normalize().dotProduct(attacker.getLookVec())));
                              if (angle < MathHelper.clamp(60.0D + (double)(levelCleave * 10), 60.0D, 359.0D)) {
                                 DamageSource source = null;
                                 if (attacker instanceof EntityPlayer) {
                                    source = new EntityDamageSource("playerCleave", attacker);
                                    target.attackEntityFrom(source, splashDamage);
                                    target.setFire(lf);
                                    if (levitationLevel > 0) {
                                       ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 30 + levitationLevel * 12, 1 + levitationLevel));
                                    }
                                 }

                                 if (attacker instanceof EntityMob) {
                                    source = new EntityDamageSource("mobCleave", attacker);
                                    target.attackEntityFrom(source, splashDamage);
                                    target.setFire(lf);
                                    if (levitationLevel > 0) {
                                       ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 30 + levitationLevel * 12, 1 + levitationLevel));
                                    }
                                 }

                                 if (source == null) {
                                    target.attackEntityFrom(DamageSource.GENERIC, splashDamage);
                                 }

                                 if (attacker instanceof EntityLivingBase) {
                                    int modKnockback = 1;
                                    modKnockback = (int)((float)modKnockback + (float)EnchantmentHelper.getKnockbackModifier(attacker) * 0.5F);
                                    if (attacker.isSprinting()) {
                                       ++modKnockback;
                                    }

                                    if (modKnockback > 0) {
                                       EnchantmentsUtility.ImprovedKnockBack(target, 0.3F * (float)modKnockback, attacker.posX - target.posX, attacker.posZ - target.posZ);
                                    }
                                 }
                              }
                           }
                        }

                        attacker.setSprinting(false);
                     }
                  }
               }
            }
         }
      }
   }
}
