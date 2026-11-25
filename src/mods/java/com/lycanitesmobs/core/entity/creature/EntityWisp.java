package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWisp extends TameableCreatureEntity {
   EntityWisp playPartner = null;

   public EntityWisp(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
      this.setAttackCooldownMax(this.getRangedCooldown());
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.playPartner != null && !this.hasAttackTarget()) {
         this.faceEntity(this.playPartner, 360.0F, 360.0F);
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (!this.isPetType("familiar") && this.updateTick % 10L == 0L) {
            for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 2.0D)) {
               EntityLivingBase target = (EntityLivingBase)entityObj;
               if (target != this) {
                  if (target instanceof EntityWisp) {
                     if (this.playPartner == null) {
                        this.playPartner = (EntityWisp)target;
                        if (((EntityWisp)target).playPartner == null) {
                           ((EntityWisp)target).playPartner = this;
                        }
                     }
                  } else {
                     this.applyBuffs(target, 1, 1);
                     this.applyDebuffs(target, 1, 1);
                     if (target instanceof EntityZombie || target instanceof EntitySkeleton) {
                        target.setFire(1);
                     }

                     if (target instanceof BaseCreatureEntity) {
                        BaseCreatureEntity targetCreature = (BaseCreatureEntity)target;
                        if (targetCreature.daylightBurns()) {
                           targetCreature.setFire(1);
                        }
                     }
                  }
               }
            }
         }

         if (this.playPartner == null) {
            this.setAttackCooldownMax(this.getRangedCooldown());
            if (this.updateTick % 40L == 0L) {
               for(Object entityObj : this.getNearbyEntities(EntityWisp.class, (Predicate)null, 30.0D)) {
                  EntityWisp target = (EntityWisp)entityObj;
                  if (target != this && this.getAttackTarget() != target) {
                     this.playPartner = target;
                     if (target.playPartner == null) {
                        target.playPartner = this;
                     }
                     break;
                  }
               }
            }
         } else if (this.playPartner.isEntityAlive() && this.getAttackTarget() != this.playPartner && this.getDistance(this.playPartner) < 100.0F) {
            if (this.hasAttackTarget()) {
               this.setAttackCooldownMax(this.getRangedCooldown());
               if (this.getPlayerOwner() == this.playPartner.getPlayerOwner()) {
                  this.playPartner.setAttackTarget(this.getAttackTarget());
               }
            } else if (!this.isAttackOnCooldown() && !this.hasAttackTarget() && this.updateTick % (long)this.getRangedCooldown() == 0L) {
               this.setAttackCooldownMax(160);
               this.playPartner.setAttackCooldownMax(160);
               this.playPartner.triggerAttackCooldown();
               this.attackRanged(this.playPartner, this.getDistance(this.playPartner));
            }
         } else {
            this.playPartner = null;
         }
      }

   }

   public boolean rollWanderChance() {
      if (this.playPartner == null) {
         return super.rollWanderChance();
      } else if (this.isAttackOnCooldown()) {
         return false;
      } else {
         return this.getRNG().nextDouble() <= 0.002D;
      }
   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("lightball", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.5F, 1.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (targetEntity == this.playPartner) {
         return false;
      } else {
         return targetEntity instanceof EntityWisp && this.getPlayerOwner() == ((EntityWisp)targetEntity).getPlayerOwner() ? false : super.canAttackEntity(targetEntity);
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
