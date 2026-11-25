package com.oblivioussp.spartanweaponry.util;

import com.oblivioussp.spartanweaponry.api.WeaponProperties;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import com.oblivioussp.spartanweaponry.item.IBlockingWeapon;
import com.oblivioussp.spartanweaponry.item.ItemSwordBase;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public class WeaponHelper {
   public WeaponHelper() {
   }

   public static boolean inflictAttackDamage(ItemSwordBase item, ItemStack stack, EntityPlayer player, Entity targetEntity, float reach) {
      if (targetEntity != null && player != null && targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(player)) {
         float damage = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
         if (damage <= 0.0F) {
            damage = 0.4F;
         }

         ItemStack weaponStack = player.getHeldItemMainhand();
         if (!player.isSneaking() && weaponStack.getItem() instanceof IBlockingWeapon || !(weaponStack.getItem() instanceof ItemSwordBase)) {
            return false;
         }

         float dmgModifier;
         if (targetEntity instanceof EntityLivingBase) {
            dmgModifier = EnchantmentHelper.getModifierForCreature(weaponStack, ((EntityLivingBase)targetEntity).getCreatureAttribute());
         } else {
            dmgModifier = EnchantmentHelper.getModifierForCreature(weaponStack, EnumCreatureAttribute.UNDEFINED);
         }

         int offhandCooldown = 0;
         float cooledAtkStrength = player.getCooledAttackStrength(0.5F);
         damage = damage * (0.2F + cooledAtkStrength * cooledAtkStrength * 0.8F);
         dmgModifier = dmgModifier * cooledAtkStrength;
         player.resetCooldown();
         if (damage > 0.0F || dmgModifier > 0.0F) {
            boolean flag = cooledAtkStrength > 0.9F;
            boolean flag1 = false;
            int i = 0;
            i = i + EnchantmentHelper.getKnockbackModifier(player);
            if (player.isSprinting() && flag) {
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
               ++i;
               flag1 = true;
            }

            boolean isCriticalHit = false;
            isCriticalHit = flag && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase && !player.isSprinting();
            CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(player, targetEntity, isCriticalHit, isCriticalHit ? 1.5F : 1.0F);
            isCriticalHit = hitResult != null;
            if (isCriticalHit) {
               damage *= hitResult.getDamageModifier();
            }

            damage = damage + dmgModifier;
            boolean flag3 = false;
            double d0 = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
            if (flag && !isCriticalHit && !flag1 && d0 < (double)player.getAIMoveSpeed()) {
               ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
               if (!itemstack.isEmpty() && itemstack.getItem() == item) {
                  flag3 = true;
               }
            }

            float f4 = 0.0F;
            boolean flag4 = false;
            int j = EnchantmentHelper.getFireAspectModifier(player);
            if (targetEntity instanceof EntityLivingBase) {
               f4 = ((EntityLivingBase)targetEntity).getHealth();
               if (j > 0 && !targetEntity.isBurning()) {
                  flag4 = true;
                  targetEntity.setFire(1);
               }
            }

            double d1 = targetEntity.motionX;
            double d2 = targetEntity.motionY;
            double d3 = targetEntity.motionZ;
            WeaponProperty amrPrcProp = item.getFirstWeaponPropertyWithType("armour_piercing");
            DamageSource dmgSrc = (DamageSource)(amrPrcProp != null ? new EntityDamageSourceArmorPiercing("player", player, amrPrcProp.getMagnitude() / 100.0F) : DamageSource.causePlayerDamage(player));
            boolean attacked = targetEntity.attackEntityFrom(dmgSrc, damage);
            if (attacked) {
               if (i > 0) {
                  if (targetEntity instanceof EntityLivingBase) {
                     ((EntityLivingBase)targetEntity).knockBack(player, (float)i * 0.5F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                  } else {
                     targetEntity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * (float)i * 0.5F));
                  }

                  player.motionX *= 0.6D;
                  player.motionZ *= 0.6D;
                  player.setSprinting(false);
               }

               WeaponProperty sweepProp = item.getFirstWeaponPropertyWithType("sweep_damage");
               if (flag3 && sweepProp != null) {
                  float sweepDamage = damage * (sweepProp.getMagnitude() / 100.0F);

                  for(EntityLivingBase entitylivingbase : player.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                     if (entitylivingbase != player && entitylivingbase != targetEntity && !player.isOnSameTeam(entitylivingbase) && player.getDistanceSq(entitylivingbase) < (double)(reach * reach)) {
                        entitylivingbase.knockBack(player, 0.4F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                        entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(player), sweepDamage);
                     }
                  }

                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                  player.spawnSweepParticles();
               }

               if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                  ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                  targetEntity.velocityChanged = false;
                  targetEntity.motionX = d1;
                  targetEntity.motionY = d2;
                  targetEntity.motionZ = d3;
               }

               if (isCriticalHit) {
                  player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                  player.onCriticalHit(targetEntity);
               }

               if (!isCriticalHit && !flag3) {
                  if (flag) {
                     player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                  } else {
                     player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                  }
               }

               if (dmgModifier > 0.0F) {
                  player.onEnchantmentCritical(targetEntity);
               }

               if (!player.world.isRemote && targetEntity instanceof EntityPlayer) {
                  EntityPlayer entityplayer = (EntityPlayer)targetEntity;
                  ItemStack itemstack2 = player.getHeldItemMainhand();
                  ItemStack itemstack3 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                  if (!itemstack2.isEmpty() && !itemstack3.isEmpty() && (itemstack2.getItem() instanceof ItemAxe || itemstack2.getItem() == item && item.canDisableShield(itemstack2, itemstack3, entityplayer, player)) && itemstack3.getItem() instanceof ItemShield) {
                     float f3 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(player) * 0.05F;
                     if (flag1) {
                        f3 += 0.75F;
                     }

                     if (player.world.rand.nextFloat() < f3) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack3.getItem(), 100);
                        player.world.setEntityState(entityplayer, (byte)30);
                     }
                  }
               }

               player.setLastAttackedEntity(targetEntity);
               if (targetEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, player);
               }

               EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
               ItemStack itemstack1 = player.getHeldItemMainhand();
               Entity entity = targetEntity;
               if (targetEntity instanceof MultiPartEntityPart) {
                  IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;
                  if (ientitymultipart instanceof EntityLivingBase) {
                     entity = (EntityLivingBase)ientitymultipart;
                  }
               }

               if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                  itemstack1.hitEntity((EntityLivingBase)entity, player);
                  if (itemstack1.getCount() <= 0) {
                     EnumHand hand = EnumHand.MAIN_HAND;
                     player.setHeldItem(hand, ItemStack.EMPTY);
                     ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, hand);
                  }
               }

               if (targetEntity instanceof EntityLivingBase) {
                  float f5 = f4 - ((EntityLivingBase)targetEntity).getHealth();
                  player.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                  if (j > 0) {
                     targetEntity.setFire(j * 4);
                  }

                  if (player.world instanceof WorldServer && f5 > 2.0F) {
                     int k = (int)((double)f5 * 0.5D);
                     ((WorldServer)player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D, new int[0]);
                  }
               }

               player.addExhaustion(0.3F);
            } else {
               player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
               if (flag4) {
                  targetEntity.extinguish();
               }
            }

            if (item.hasWeaponProperty(WeaponProperties.QUICK_STRIKE)) {
               targetEntity.hurtResistantTime = ConfigHandler.quickStrikeHurtResistTicks;
            }
         }
      }

      return true;
   }

   public static boolean stacktraceHasClass(String clazz) {
      for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
         if (element.getClassName().equals(clazz)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isOffhandFromStacktrace() {
      return stacktraceHasClass("bettercombat.mod.network.PacketOffhandAttack$Handler");
   }
}
