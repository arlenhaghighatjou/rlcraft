package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WeaponMeleeScythe extends WeaponToolMeleeBase {
   public WeaponMeleeScythe(ToolMaterial material, String name, double attackspeed, float range, float attackD, boolean fear, byte id) {
      super(material, name, attackspeed, range, attackD, fear, id);
   }

   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      super.onCreated(stack, worldIn, playerIn);
   }

   public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
      boolean flag = super.hitEntity(stack, target, attacker);
      if (flag && attacker instanceof EntityPlayer) {
         int aoe = 3;
         EntityPlayer player = (EntityPlayer)attacker;
         if (this.calling) {
            aoe = 6;
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(player.posX, player.posY, player.posZ, player.posX + 1.0D, player.posY + 1.0D, player.posZ + 1.0D)).grow((double)aoe);

            for(EntityLivingBase mob : target.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
               if (mob != target && mob != player) {
                  this.hitTarget(stack, mob, player);
               }
            }
         }

         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(target.posX, target.posY, target.posZ, target.posX + 1.0D, target.posY + 1.0D, target.posZ + 1.0D)).grow((double)aoe);

         for(EntityLivingBase mob : target.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if (mob != target && mob != player) {
               this.hitTarget(stack, mob, player);
            }
         }
      }

      return flag;
   }

   private void hitTarget(ItemStack stack, Entity targetEntity, EntityPlayer attacker) {
      if (ForgeHooks.onPlayerAttackTarget(attacker, targetEntity)) {
         if (targetEntity.canBeAttackedWithItem() && !targetEntity.hitByEntity(attacker)) {
            float f = (float)attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
            float f1;
            if (targetEntity instanceof EntityLivingBase) {
               f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
            } else {
               f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), EnumCreatureAttribute.UNDEFINED);
            }

            float f2 = attacker.getCooledAttackStrength(0.5F);
            f = f * (0.2F + f2 * f2 * 0.8F);
            f1 = f1 * f2;
            attacker.resetCooldown();
            if (f > 0.0F || f1 > 0.0F) {
               boolean flag = f2 > 0.9F;
               boolean flag1 = false;
               int i = 0;
               i = i + EnchantmentHelper.getKnockbackModifier(attacker);
               if (attacker.isSprinting() && flag) {
                  attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, attacker.getSoundCategory(), 1.0F, 1.0F);
                  ++i;
                  flag1 = true;
               }

               boolean flag2 = flag && attacker.fallDistance > 0.0F && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(MobEffects.BLINDNESS) && !attacker.isRiding() && targetEntity instanceof EntityLivingBase;
               flag2 = flag2 && !attacker.isSprinting();
               CriticalHitEvent hitResult = ForgeHooks.getCriticalHit(attacker, targetEntity, flag2, flag2 ? 1.5F : 1.0F);
               flag2 = hitResult != null;
               if (flag2) {
                  f *= hitResult.getDamageModifier();
               }

               f = f + f1;
               boolean flag3 = false;
               double d0 = (double)(attacker.distanceWalkedModified - attacker.prevDistanceWalkedModified);
               if (flag && !flag2 && !flag1 && attacker.onGround && d0 < (double)attacker.getAIMoveSpeed()) {
                  ItemStack itemstack = attacker.getHeldItem(EnumHand.MAIN_HAND);
                  if (itemstack.getItem() instanceof ItemSword) {
                     flag3 = true;
                  }
               }

               float f4 = 0.0F;
               boolean flag4 = false;
               int j = EnchantmentHelper.getFireAspectModifier(attacker);
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
               boolean flag5 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(attacker), f);
               if (flag5) {
                  if (i > 0) {
                     if (targetEntity instanceof EntityLivingBase) {
                        ((EntityLivingBase)targetEntity).knockBack(attacker, (float)i * 0.5F, (double)MathHelper.sin(attacker.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(attacker.rotationYaw * 0.017453292F)));
                     } else {
                        targetEntity.addVelocity((double)(-MathHelper.sin(attacker.rotationYaw * 0.017453292F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(attacker.rotationYaw * 0.017453292F) * (float)i * 0.5F));
                     }

                     attacker.motionX *= 0.6D;
                     attacker.motionZ *= 0.6D;
                     attacker.setSprinting(false);
                  }

                  if (flag3) {
                     float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(attacker) * f;

                     for(EntityLivingBase entitylivingbase : attacker.world.getEntitiesWithinAABB(EntityLivingBase.class, targetEntity.getEntityBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                        if (entitylivingbase != attacker && entitylivingbase != targetEntity && !attacker.isOnSameTeam(entitylivingbase) && attacker.getDistanceSq(entitylivingbase) < 9.0D) {
                           entitylivingbase.knockBack(attacker, 0.4F, (double)MathHelper.sin(attacker.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(attacker.rotationYaw * 0.017453292F)));
                           entitylivingbase.attackEntityFrom(DamageSource.causePlayerDamage(attacker), f3);
                        }
                     }

                     attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, attacker.getSoundCategory(), 1.0F, 1.0F);
                     attacker.spawnSweepParticles();
                  }

                  if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                     ((EntityPlayerMP)targetEntity).connection.sendPacket(new SPacketEntityVelocity(targetEntity));
                     targetEntity.velocityChanged = false;
                     targetEntity.motionX = d1;
                     targetEntity.motionY = d2;
                     targetEntity.motionZ = d3;
                  }

                  if (flag2) {
                     attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, attacker.getSoundCategory(), 1.0F, 1.0F);
                     attacker.onCriticalHit(targetEntity);
                  }

                  if (!flag2 && !flag3) {
                     if (flag) {
                        attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, attacker.getSoundCategory(), 1.0F, 1.0F);
                     } else {
                        attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, attacker.getSoundCategory(), 1.0F, 1.0F);
                     }
                  }

                  if (f1 > 0.0F) {
                     attacker.onEnchantmentCritical(targetEntity);
                  }

                  attacker.setLastAttackedEntity(targetEntity);
                  if (targetEntity instanceof EntityLivingBase) {
                     EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, attacker);
                  }

                  EnchantmentHelper.applyArthropodEnchantments(attacker, targetEntity);
                  ItemStack itemstack1 = attacker.getHeldItemMainhand();
                  Entity entity = targetEntity;
                  if (targetEntity instanceof MultiPartEntityPart) {
                     IEntityMultiPart ientitymultipart = ((MultiPartEntityPart)targetEntity).parent;
                     if (ientitymultipart instanceof EntityLivingBase) {
                        entity = (EntityLivingBase)ientitymultipart;
                     }
                  }

                  if (!itemstack1.isEmpty() && entity instanceof EntityLivingBase) {
                     ItemStack beforeHitCopy = itemstack1.copy();
                     stack.damageItem(1, attacker);
                     if (itemstack1.isEmpty()) {
                        ForgeEventFactory.onPlayerDestroyItem(attacker, beforeHitCopy, EnumHand.MAIN_HAND);
                        attacker.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                     }
                  }

                  if (targetEntity instanceof EntityLivingBase) {
                     float f5 = f4 - ((EntityLivingBase)targetEntity).getHealth();
                     attacker.addStat(StatList.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                     if (j > 0) {
                        targetEntity.setFire(j * 4);
                     }

                     if (attacker.world instanceof WorldServer && f5 > 2.0F) {
                        int k = (int)((double)f5 * 0.5D);
                        ((WorldServer)attacker.world).spawnParticle(EnumParticleTypes.DAMAGE_INDICATOR, targetEntity.posX, targetEntity.posY + (double)(targetEntity.height * 0.5F), targetEntity.posZ, k, 0.1D, 0.0D, 0.1D, 0.2D, new int[0]);
                     }
                  }

                  attacker.addExhaustion(0.1F);
               } else {
                  attacker.world.playSound((EntityPlayer)null, attacker.posX, attacker.posY, attacker.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, attacker.getSoundCategory(), 1.0F, 1.0F);
                  if (flag4) {
                     targetEntity.extinguish();
                  }
               }
            }
         }

      }
   }

   public EnumRarity getRarity(ItemStack stack) {
      return super.getRarity(stack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(TextFormatting.YELLOW + I18n.format("tootip.srparasites.weaponm." + this.idTool, new Object[0]));
      tooltip.add(TextFormatting.RED + I18n.format("tootip.srparasites.weaponm." + this.idTool * 10, new Object[0]));
      if (this.calling && SRPConfigSystems.useOneMind) {
         tooltip.add(TextFormatting.BLACK + I18n.format("tootip.srparasites.weaponm." + this.idTool * 100, new Object[0]));
      }

   }
}
