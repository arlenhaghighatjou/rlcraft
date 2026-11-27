package com.Shultrea.Rin.Ench0_3_0;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.Shultrea.Rin.Enchantments_Sector.Smc_030;
import com.Shultrea.Rin.Main_Sector.somanyenchantments;
import com.Shultrea.Rin.Utility_Sector.EnchantmentsUtility;
import java.util.ArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class EnchantmentDebug extends Enchantment {
   public EnchantmentDebug() {
      super(Rarity.UNCOMMON, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
      this.setName("Debug");
      this.setRegistryName("Debug");
   }

   public int getMaxLevel() {
      return 10;
   }

   public int getMinEnchantability(int par1) {
      return 5 + 10 * (par1 - 1);
   }

   public int getMaxEnchantability(int par1) {
      return super.getMinEnchantability(par1) + 30;
   }

   public boolean canApplyTogether(Enchantment fTest) {
      return super.canApplyTogether(fTest);
   }

   public boolean canApply(ItemStack fTest) {
      return super.canApply(fTest);
   }

   public void register(EventBus bus) {
      bus.register(LivingAttackEvent.class, this::HandleEnchant);
   }

   public void HandleEnchant(LivingAttackEvent fEvent) {
      if (fEvent.getSource().damageType == "player" || fEvent.getSource().damageType == "mob") {
         if (fEvent.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)fEvent.getSource().getTrueSource();
            if (attacker != null) {
               ItemStack dmgSource = ((EntityLivingBase)fEvent.getSource().getTrueSource()).getHeldItemMainhand();
               if (dmgSource != null) {
                  int levelCleave = EnchantmentHelper.getEnchantmentLevel(Smc_030.Debug, dmgSource);
                  if (EnchantmentHelper.getEnchantmentLevel(Smc_030.Debug, dmgSource) > 0) {
                     float splashDamage = fEvent.getAmount() * (float)levelCleave * 0.25F;
                     AxisAlignedBB boundBox = new AxisAlignedBB(attacker.posX - 5.0D - (double)levelCleave, attacker.posY - 5.0D - (double)levelCleave, attacker.posZ - 5.0D - (double)levelCleave, attacker.posX + 5.0D + (double)levelCleave, attacker.posY + 5.0D + (double)levelCleave, attacker.posZ + 5.0D + (double)levelCleave);

                     for(Entity target : new ArrayList<Entity>(attacker.getEntityWorld().getEntitiesWithinAABBExcludingEntity(fEvent.getEntity(), boundBox))) {
                        if (target instanceof EntityLivingBase && target != attacker && target != fEvent.getEntityLiving() && target.getDistance(attacker) <= 3.5F) {
                           Vec3d attackerCheck = EnchantmentsUtility.Cleave(target.posX - attacker.posX, target.posY - attacker.posY, target.posZ - attacker.posZ);
                           double angle = Math.toDegrees(Math.acos(attackerCheck.normalize().dotProduct(attacker.getLookVec())));
                           if (angle < 60.0D + (double)(levelCleave * 10)) {
                              DamageSource source = null;
                              if (attacker instanceof EntityPlayer) {
                                 source = new EntityDamageSource("player", attacker);
                              }

                              target.attackEntityFrom(somanyenchantments.Cleave, splashDamage);
                              if (attacker instanceof EntityMob) {
                                 source = new EntityDamageSource("mob", attacker);
                              }

                              target.attackEntityFrom(somanyenchantments.Cleave, splashDamage);
                              if (source == null) {
                                 target.attackEntityFrom(DamageSource.GENERIC, splashDamage);
                              }

                              if (attacker instanceof EntityPlayer) {
                                 int modKnockback = 1;
                                 modKnockback = modKnockback + EnchantmentHelper.getKnockbackModifier(attacker);
                                 if (attacker.isSprinting()) {
                                    ++modKnockback;
                                 }

                                 Entity victim = fEvent.getEntityLiving();
                                 if (modKnockback > 0) {
                                    fEvent.getEntityLiving().knockBack(attacker, 0.4F * (float)modKnockback, attacker.posX - victim.posX, attacker.posZ - victim.posZ);
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
