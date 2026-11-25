package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

public abstract class TargetingGoal extends EntityAIBase {
   protected BaseCreatureEntity host;
   protected EntityLivingBase target;
   protected Predicate<EntityLivingBase> targetSelector;
   protected Predicate<EntityLivingBase> allySelector;
   protected TargetSorterNearest nearestSorter;
   protected boolean checkSight = true;
   protected boolean nearbyOnly = false;
   protected boolean callForHelp = false;
   private int cantSeeTime;
   protected int cantSeeTimeMax = 60;
   protected double targetingRange = 0.0D;
   private int targetSearchStatus;
   private int targetSearchDelay;

   public TargetingGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.targetSelector = (entity) -> {
         if (entity == null) {
            return false;
         } else if ((double)entity.getDistance(this.host) > this.getTargetDistance() * (entity.isSneaking() && !this.host.trueSight ? 0.800000011920929D : 1.0D)) {
            return false;
         } else if (!this.isEntityTargetable(entity, false)) {
            return false;
         } else if (this.shouldCheckSight() && !entity.isGlowing() && !this.host.canEntityBeSeen(entity)) {
            return false;
         } else {
            if (this.shouldCheckSight() && !entity.isGlowing() && entity.isInvisible() && !this.host.trueSight) {
               if (!(entity instanceof EntityPlayer)) {
                  return false;
               }

               double d0 = this.getTargetDistance();
               if (entity.isSneaking()) {
                  d0 *= 0.800000011920929D;
               }

               float f = ((EntityPlayer)entity).getArmorVisibility();
               if (f < 0.1F) {
                  f = 0.1F;
               }

               d0 = d0 * (double)(0.7F * f);
               if ((double)entity.getDistance(this.host) > d0) {
                  return false;
               }
            }

            return true;
         }
      };
      this.allySelector = (entity) -> {
         if (entity == null) {
            return false;
         } else if ((double)entity.getDistance(this.host) > this.getTargetDistance()) {
            return false;
         } else if (!this.isAllyTarget(entity)) {
            return false;
         } else {
            return !this.shouldCheckSight() || entity.isGlowing() || this.host.canEntityBeSeen(entity);
         }
      };
      this.nearestSorter = new TargetSorterNearest(setHost);
   }

   public void startExecuting() {
      this.setTarget(this.target);
      this.targetSearchStatus = 0;
      this.targetSearchDelay = 0;
      this.cantSeeTime = 0;
   }

   public boolean shouldContinueExecuting() {
      if (this.getTarget() == null) {
         return false;
      } else if (!this.getTarget().isEntityAlive()) {
         return false;
      } else if (this.shouldStopTargeting(this.getTarget())) {
         return false;
      } else {
         double distance = this.getTargetDistance() + 2.0D;
         if ((double)this.host.getDistance(this.getTarget()) > distance) {
            return false;
         } else {
            if (this.shouldCheckSight()) {
               if (this.host.getEntitySenses().canSee(this.getTarget())) {
                  this.cantSeeTime = 0;
               } else if (++this.cantSeeTime > this.cantSeeTimeMax) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public boolean shouldStopTargeting(EntityLivingBase target) {
      return false;
   }

   public void resetTask() {
      this.setTarget((EntityLivingBase)null);
   }

   protected EntityLivingBase getTarget() {
      return null;
   }

   protected void setTarget(EntityLivingBase newTarget) {
   }

   public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
      EntityLivingBase newTarget = null;

      try {
         List<EntityLivingBase> possibleTargets = this.<EntityLivingBase>getPossibleTargets(EntityLivingBase.class, rangeX, rangeY, rangeZ);
         if (possibleTargets.isEmpty()) {
            return null;
         }

         Collections.sort(possibleTargets, this.nearestSorter);
         newTarget = (EntityLivingBase)possibleTargets.get(0);
      } catch (Exception var9) {
         LycanitesMobs.logWarning("", "An exception occurred when target selecting, this has been skipped to prevent a crash.");
         var9.printStackTrace();
      }

      return newTarget;
   }

   public <T extends EntityLivingBase> List<T> getPossibleTargets(Class<? extends T> clazz, double rangeX, double rangeY, double rangeZ) {
      return this.host.getEntityWorld().getEntitiesWithinAABB(clazz, this.host.getEntityBoundingBox().grow(rangeX, rangeY, rangeZ), this.targetSelector);
   }

   protected double getTargetDistance() {
      if (this.targetingRange > 0.0D) {
         return this.targetingRange;
      } else {
         IAttributeInstance attributeInstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
         return attributeInstance.getAttributeValue();
      }
   }

   public void callNearbyForHelp() {
      if (this.allySelector != null && this.target != null) {
         try {
            double targetDistance = this.getTargetDistance();

            for(EntityLiving possibleAlly : this.host.getEntityWorld().getEntitiesWithinAABB(BaseCreatureEntity.class, this.host.getEntityBoundingBox().grow(targetDistance, 4.0D, targetDistance), this.allySelector)) {
               if (!possibleAlly.isOnSameTeam(this.target) && possibleAlly.canAttackClass(this.target.getClass())) {
                  if (possibleAlly instanceof BaseCreatureEntity) {
                     BaseCreatureEntity possibleCreatureAlly = (BaseCreatureEntity)possibleAlly;
                     if (possibleCreatureAlly.getAttackTarget() == null && possibleCreatureAlly.canAttackEntity(this.target) && possibleCreatureAlly.shouldCreatureGroupRevenge(this.target)) {
                        possibleCreatureAlly.setRevengeTarget(this.target);
                     }
                  } else if (possibleAlly.getRevengeTarget() == null) {
                     possibleAlly.setRevengeTarget(this.target);
                  }
               }
            }
         } catch (Exception var7) {
            LycanitesMobs.logWarning("", "An exception occurred when calling for help, this has been skipped to prevent a crash.");
            var7.printStackTrace();
         }

      }
   }

   protected boolean isEntityTargetable(EntityLivingBase checkTarget, boolean targetCreative) {
      if (checkTarget == null) {
         return false;
      } else if (checkTarget == this.host) {
         return false;
      } else if (!checkTarget.isEntityAlive()) {
         return false;
      } else {
         if (checkTarget instanceof EntityPlayer) {
            if (!targetCreative && ((EntityPlayer)checkTarget).isCreative()) {
               return false;
            }

            if (((EntityPlayer)checkTarget).isSpectator()) {
               return false;
            }
         }

         if (!this.isValidTarget(checkTarget)) {
            return false;
         } else if (!this.host.positionNearHome(MathHelper.floor(checkTarget.posX), MathHelper.floor(checkTarget.posY), MathHelper.floor(checkTarget.posZ))) {
            return false;
         } else if (this.shouldCheckSight() && !checkTarget.isPotionActive(MobEffects.GLOWING) && !this.host.getEntitySenses().canSee(checkTarget)) {
            return false;
         } else {
            if (this.nearbyOnly) {
               if (--this.targetSearchDelay <= 0) {
                  this.targetSearchStatus = 0;
               }

               if (this.targetSearchStatus == 0) {
                  this.targetSearchStatus = this.isNearby(checkTarget) ? 1 : 2;
               }

               if (this.targetSearchStatus == 2) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   protected boolean shouldCheckSight() {
      return this.checkSight;
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      return true;
   }

   protected boolean isAllyTarget(EntityLivingBase checkTarget) {
      if (checkTarget == null) {
         return false;
      } else if (checkTarget == this.host) {
         return false;
      } else if (!checkTarget.isEntityAlive()) {
         return false;
      } else if (checkTarget instanceof EntityPlayer) {
         return false;
      } else {
         if (checkTarget instanceof BaseCreatureEntity) {
            if (!((BaseCreatureEntity)checkTarget).isProtective(this.host)) {
               return false;
            }
         } else if (checkTarget.getClass() != this.host.getClass()) {
            return false;
         }

         return !this.shouldCheckSight() || this.host.getEntitySenses().canSee(checkTarget);
      }
   }

   private boolean isNearby(EntityLivingBase target) {
      this.targetSearchDelay = 10 + this.host.getRNG().nextInt(5);
      Path path = this.host.getNavigator().getPathToEntityLiving(target);
      if (path == null) {
         return false;
      } else {
         PathPoint pathpoint = path.getFinalPathPoint();
         if (pathpoint == null) {
            return false;
         } else {
            int i = pathpoint.x - MathHelper.floor(target.posX);
            int j = pathpoint.z - MathHelper.floor(target.posZ);
            return (double)(i * i + j * j) <= 2.25D;
         }
      }
   }
}
