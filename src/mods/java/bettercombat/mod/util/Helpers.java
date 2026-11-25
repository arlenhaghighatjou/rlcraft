package bettercombat.mod.util;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.event.RLCombatCriticalHitEvent;
import bettercombat.mod.event.RLCombatModifyDamageEvent;
import bettercombat.mod.event.RLCombatSweepEvent;
import bettercombat.mod.handler.EventHandlers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentSweepingEdge;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public final class Helpers {
   private Helpers() {
   }

   public static <T> void execNullable(@Nullable T obj, Consumer<T> onNonNull) {
      if (obj != null) {
         onNonNull.accept(obj);
      }

   }

   public static <T, R> R execNullable(@Nullable T obj, Function<T, R> onNonNull, R orElse) {
      return (R)(obj != null ? onNonNull.apply(obj) : orElse);
   }

   public static void clearOldModifiers(EntityLivingBase entity, ItemStack stack) {
      if (!stack.isEmpty() && entity != null) {
         Multimap<String, AttributeModifier> modifiersToRemove = HashMultimap.create();

         for(Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
            if (((String)modifier.getKey()).contains("attackDamage") || ((String)modifier.getKey()).contains("attackSpeed") || ((String)modifier.getKey()).contains("reachDistance")) {
               modifiersToRemove.put(modifier.getKey(), modifier.getValue());
            }
         }

         if (Loader.isModLoaded("qualitytools")) {
            QualityToolsHandler.clearOldModifiersQualityTools(entity, stack, modifiersToRemove);
         }

         if (!modifiersToRemove.isEmpty()) {
            entity.getAttributeMap().removeAttributeModifiers(modifiersToRemove);
         }
      }

   }

   public static void addNewModifiers(EntityLivingBase entity, ItemStack stack) {
      if (!stack.isEmpty() && entity != null) {
         for(Entry<String, AttributeModifier> modifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).entries()) {
            if (((String)modifier.getKey()).contains("attackDamage")) {
               IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
               if (!entityAttribute.hasModifier((AttributeModifier)modifier.getValue())) {
                  entityAttribute.applyModifier((AttributeModifier)modifier.getValue());
               }
            } else if (((String)modifier.getKey()).contains("attackSpeed")) {
               IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
               if (!entityAttribute.hasModifier((AttributeModifier)modifier.getValue())) {
                  entityAttribute.applyModifier((AttributeModifier)modifier.getValue());
               }
            } else if (((String)modifier.getKey()).contains("reachDistance")) {
               IAttributeInstance entityAttribute = entity.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE);
               if (!entityAttribute.hasModifier((AttributeModifier)modifier.getValue())) {
                  entityAttribute.applyModifier((AttributeModifier)modifier.getValue());
               }
            }
         }

         if (Loader.isModLoaded("qualitytools")) {
            QualityToolsHandler.addNewModifiersQualityTools(entity, stack);
         }
      }

   }

   public static float getOffhandDamage(EntityPlayer player) {
      float attack = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
      return attack * (ConfigurationHandler.weakerOffhand ? ConfigurationHandler.offHandEfficiency : 1.0F);
   }

   public static int getOffhandCooldown(EntityPlayer player) {
      float speed = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue();
      return (int)(1.0F / speed * 20.0F + 0.5F);
   }

   public static void attackTargetEntityItem(EntityPlayer player, Entity targetEntity, boolean offhand, double motionX, double motionY, double motionZ) {
      ItemStack weapon = offhand ? player.getHeldItemOffhand() : player.getHeldItemMainhand();
      if (!MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, targetEntity))) {
         if (weapon.isEmpty() || !weapon.getItem().onLeftClickEntity(weapon, player, targetEntity)) {
            if (!Loader.isModLoaded("reskillable") || !ReskillableHandler.shouldLockAttack(player, weapon)) {
               if (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(player)) {
                  int cooldown = 0;
                  float damage;
                  double reach;
                  if (offhand) {
                     clearOldModifiers(player, player.getHeldItemMainhand());
                     addNewModifiers(player, player.getHeldItemOffhand());
                     damage = getOffhandDamage(player);
                     cooldown = getOffhandCooldown(player);
                     reach = ReachFixUtil.getEntityReach(player, EnumHand.OFF_HAND);
                     clearOldModifiers(player, player.getHeldItemOffhand());
                     addNewModifiers(player, player.getHeldItemMainhand());
                  } else {
                     damage = (float)player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                     reach = ReachFixUtil.getEntityReach(player, EnumHand.MAIN_HAND);
                  }

                  float cMod;
                  if (targetEntity instanceof EntityLivingBase) {
                     cMod = EnchantmentHelper.getModifierForCreature(weapon, ((EntityLivingBase)targetEntity).getCreatureAttribute());
                  } else {
                     cMod = EnchantmentHelper.getModifierForCreature(weapon, EnumCreatureAttribute.UNDEFINED);
                  }

                  float cooledStr;
                  if (offhand) {
                     if (cooldown > 0) {
                        cooledStr = 1.0F - (float)((Integer)execNullable(player.getCapability(EventHandlers.TUTO_CAP, (EnumFacing)null), CapabilityOffhandCooldown::getOffhandCooldown, Integer.valueOf(0))).intValue() / (float)cooldown;
                     } else {
                        cooledStr = 1.0F;
                     }
                  } else {
                     cooledStr = player.getCooledAttackStrength(0.5F);
                  }

                  RLCombatModifyDamageEvent modifyResultPre = new RLCombatModifyDamageEvent.Pre(player, targetEntity, offhand, weapon, damage, cooledStr, motionX, motionY, motionZ);
                  MinecraftForge.EVENT_BUS.post(modifyResultPre);
                  damage = damage + modifyResultPre.getDamageModifier();
                  damage = damage * (0.2F + cooledStr * cooledStr * 0.8F);
                  cMod = cMod * cooledStr;
                  if (offhand) {
                     CapabilityOffhandCooldown coh = (CapabilityOffhandCooldown)player.getCapability(EventHandlers.TUTO_CAP, (EnumFacing)null);
                     if (coh != null) {
                        coh.setOffhandCooldown(cooldown);
                        coh.setOffhandBeginningCooldown(cooldown);
                        if (!player.world.isRemote) {
                           coh.sync();
                        }
                     }
                  } else {
                     player.resetCooldown();
                  }

                  if (damage > 0.0F || cMod > 0.0F) {
                     boolean isStrong = cooledStr > 0.9F;
                     boolean knockback = false;
                     int knockbackMod = EnchantmentHelper.getKnockbackModifier(player);
                     int fireAspect = EnchantmentHelper.getFireAspectModifier(player);
                     if (player.isSprinting() && isStrong) {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0F, 1.0F);
                        ++knockbackMod;
                        knockback = true;
                     }

                     boolean isCrit;
                     if (ConfigurationHandler.randomCrits) {
                        isCrit = player.getRNG().nextFloat() < ConfigurationHandler.critChance && !player.isSprinting() && (!ConfigurationHandler.requireEnergyToRandomCrit || isStrong);
                        if (!isCrit) {
                           isCrit = (!ConfigurationHandler.requireEnergyToJumpCrit || isStrong) && player.getDistance(targetEntity) < ConfigurationHandler.distanceToJumpCrit && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase && !player.isSprinting();
                        }
                     } else {
                        isCrit = isStrong && player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(MobEffects.BLINDNESS) && !player.isRiding() && targetEntity instanceof EntityLivingBase && !player.isSprinting();
                     }

                     RLCombatCriticalHitEvent hitResult = new RLCombatCriticalHitEvent(player, targetEntity, isCrit ? 1.5F : 1.0F, isCrit, offhand);
                     MinecraftForge.EVENT_BUS.post(hitResult);
                     if (hitResult.getResult() != Result.ALLOW && (!isCrit || hitResult.getResult() != Result.DEFAULT)) {
                        hitResult = null;
                     }

                     isCrit = hitResult != null;
                     if (isCrit) {
                        damage *= hitResult.getDamageModifier();
                     }

                     damage = damage + cMod;
                     RLCombatModifyDamageEvent.Post modifyResultPost = new RLCombatModifyDamageEvent.Post(player, targetEntity, offhand, weapon, damage, cooledStr, motionX, motionY, motionZ, DamageSource.causePlayerDamage(player));
                     MinecraftForge.EVENT_BUS.post(modifyResultPost);
                     damage = damage + modifyResultPost.getDamageModifier();
                     DamageSource dmgSource = modifyResultPost.getDamageSource();
                     boolean doSweepingIgnoreSword = false;
                     boolean doSweeping = false;
                     double tgtDistDelta = (double)(player.distanceWalkedModified - player.prevDistanceWalkedModified);
                     if (isStrong && !isCrit && !knockback && player.onGround && tgtDistDelta < (double)player.getAIMoveSpeed()) {
                        doSweepingIgnoreSword = true;
                        if (weapon.getItem() instanceof ItemSword) {
                           doSweeping = true;
                        }
                     }

                     float tgtHealth = 0.0F;
                     boolean burnInflicted = false;
                     if (targetEntity instanceof EntityLivingBase) {
                        tgtHealth = ((EntityLivingBase)targetEntity).getHealth();
                        if (fireAspect > 0 && !targetEntity.isBurning()) {
                           targetEntity.setFire(1);
                           burnInflicted = true;
                        }
                     }

                     double tgtMotionX = targetEntity.motionX;
                     double tgtMotionY = targetEntity.motionY;
                     double tgtMotionZ = targetEntity.motionZ;
                     boolean attacked;
                     if (offhand) {
                        Entity targetEntCap = targetEntity;
                        if (targetEntity instanceof MultiPartEntityPart) {
                           targetEntCap = (Entity)((MultiPartEntityPart)targetEntity).parent;
                        }

                        attacked = ((Boolean)execNullable(targetEntCap.getCapability(EventHandlers.SECONDHURTTIMER_CAP, (EnumFacing)null), (sht) -> {
                           return sht.attackEntityFromOffhand(targetEntity, dmgSource, attackDmgFinal);
                        }, Boolean.valueOf(false))).booleanValue();
                     } else {
                        attacked = targetEntity.attackEntityFrom(dmgSource, damage);
                     }

                     if (!attacked) {
                        player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0F, 1.0F);
                        if (burnInflicted) {
                           targetEntity.extinguish();
                        }
                     } else {
                        if (knockbackMod > 0) {
                           if (targetEntity instanceof EntityLivingBase) {
                              ((EntityLivingBase)targetEntity).knockBack(player, (float)knockbackMod * 0.5F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                           } else {
                              targetEntity.addVelocity((double)(-MathHelper.sin(player.rotationYaw * 0.017453292F) * (float)knockbackMod * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * 0.017453292F) * (float)knockbackMod * 0.5F));
                           }

                           player.motionX *= 0.6D;
                           player.motionZ *= 0.6D;
                           if (!ConfigurationHandler.moreSprint) {
                              player.setSprinting(false);
                           }
                        }

                        RLCombatSweepEvent sweepResult = new RLCombatSweepEvent(player, targetEntity, damage, offhand, weapon, doSweepingIgnoreSword, doSweeping, EnchantmentSweepingEdge.getSweepingDamageRatio(EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING, player.getHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND))), targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D), DamageSource.causePlayerDamage(player));
                        MinecraftForge.EVENT_BUS.post(sweepResult);
                        doSweeping = sweepResult.getDoSweep();
                        if (doSweeping) {
                           float sweepingDamage = 1.0F + sweepResult.getSweepModifier() * damage;
                           AxisAlignedBB sweepingAABB = sweepResult.getSweepingAABB();
                           DamageSource sweepingDamageSource = sweepResult.getSweepingDamageSource();

                           for(EntityLivingBase living : player.world.getEntitiesWithinAABB(EntityLivingBase.class, sweepingAABB)) {
                              if (living != player && living != targetEntity && !player.isOnSameTeam(living) && player.getDistanceSq(living) < reach * reach) {
                                 living.knockBack(player, 0.4F, (double)MathHelper.sin(player.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(player.rotationYaw * 0.017453292F)));
                                 if (offhand) {
                                    execNullable(living.getCapability(EventHandlers.SECONDHURTTIMER_CAP, (EnumFacing)null), (sht) -> {
                                       sht.attackEntityFromOffhand(living, sweepingDamageSource, sweepingDamage);
                                    });
                                 } else {
                                    living.attackEntityFrom(sweepingDamageSource, sweepingDamage);
                                 }
                              }
                           }

                           player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0F, 1.0F);
                           player.spawnSweepParticles();
                        }

                        if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                           ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                           targetEntity.velocityChanged = false;
                           targetEntity.motionX = tgtMotionX;
                           targetEntity.motionY = tgtMotionY;
                           targetEntity.motionZ = tgtMotionZ;
                        }

                        if (isCrit) {
                           if (offhand) {
                              player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0F, 1.0F);
                           }

                           player.onCriticalHit(targetEntity);
                        }

                        boolean playSound = true;
                        if (!weapon.isEmpty()) {
                           if (weapon.getItem() instanceof ItemSpade) {
                              playSound = false;
                           }

                           if (playSound) {
                              if (ConfigurationHandler.hitSound && (!ConfigurationHandler.critSound || !isCrit)) {
                                 player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, Sounds.SWORD_SLASH, player.getSoundCategory(), 1.0F, 1.0F);
                              }

                              if (ConfigurationHandler.critSound && isCrit) {
                                 player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, Sounds.CRITICAL_STRIKE, player.getSoundCategory(), 1.0F, 1.0F);
                              }
                           }
                        }

                        if (!isCrit && !doSweeping) {
                           if (isStrong) {
                              player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0F, 1.0F);
                           } else {
                              player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0F, 1.0F);
                           }
                        }

                        if (cMod > 0.0F) {
                           player.onEnchantmentCritical(targetEntity);
                        }

                        if (!player.world.isRemote && targetEntity instanceof EntityPlayer) {
                           EntityPlayer entityplayer = (EntityPlayer)targetEntity;
                           ItemStack activeItem = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                           if (weapon.getItem() instanceof ItemAxe && activeItem.getItem() instanceof ItemShield) {
                              float efficiency = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(player) * 0.05F;
                              if (knockback) {
                                 efficiency += 0.75F;
                              }

                              if (player.getRNG().nextFloat() < efficiency) {
                                 entityplayer.getCooldownTracker().setCooldown(activeItem.getItem(), 100);
                                 player.world.setEntityState(entityplayer, (byte)30);
                              }
                           }
                        }

                        player.setLastAttackedEntity(targetEntity);
                        if (targetEntity instanceof EntityLivingBase) {
                           EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, player);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(player, targetEntity);
                        Entity entity = targetEntity;
                        if (targetEntity instanceof MultiPartEntityPart) {
                           IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;
                           if (ientitymultipart instanceof EntityLivingBase) {
                              entity = (EntityLivingBase)ientitymultipart;
                           }
                        }

                        if (!weapon.isEmpty() && entity instanceof EntityLivingBase) {
                           ItemStack beforeHitCopy = weapon.copy();
                           weapon.hitEntity((EntityLivingBase)entity, player);
                           if (weapon.isEmpty()) {
                              player.setHeldItem(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ItemStack.EMPTY);
                              ForgeEventFactory.onPlayerDestroyItem(player, beforeHitCopy, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                           }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                           float healthDelta = tgtHealth - ((EntityLivingBase)targetEntity).getHealth();
                           player.addStat(StatList.DAMAGE_DEALT, Math.round(healthDelta * 10.0F));
                           if (fireAspect > 0) {
                              targetEntity.setFire(fireAspect * 4);
                           }

                           if (player.world instanceof WorldServer && healthDelta > 2.0F) {
                              int k = (int)((double)healthDelta * 0.5D);
                              ((WorldServer)player.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D, new int[0]);
                           }
                        }

                        player.addExhaustion(0.3F);
                     }

                     if (Loader.isModLoaded("spartanweaponry")) {
                        SpartanWeaponryHandler.handleSpartanQuickStrike(weapon, targetEntity);
                     }
                  }
               }

            }
         }
      }
   }

   @Nonnull
   public static <T> T getNull() {
      return (T)null;
   }
}
