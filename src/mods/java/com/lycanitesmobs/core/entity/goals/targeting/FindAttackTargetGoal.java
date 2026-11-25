package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class FindAttackTargetGoal extends TargetingGoal {
   private List<Class<? extends Entity>> targetClasses = new ArrayList();
   protected boolean targetPlayers;
   private boolean requirePack = false;
   protected boolean tameTargeting = false;

   public FindAttackTargetGoal(BaseCreatureEntity setHost) {
      super(setHost);
      this.setMutexBits(1);
   }

   public FindAttackTargetGoal setCheckSight(boolean bool) {
      this.checkSight = bool;
      return this;
   }

   public FindAttackTargetGoal addTargets(Class<? extends Entity>... targets) {
      this.targetClasses.addAll(Arrays.asList(targets));

      for(Class<? extends Entity> targetType : targets) {
         this.host.setHostileTo(targetType);
         if (targetType.isAssignableFrom(EntityPlayer.class)) {
            this.targetPlayers = true;
         }
      }

      return this;
   }

   public FindAttackTargetGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindAttackTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindAttackTargetGoal setRange(double range) {
      this.targetingRange = range;
      return this;
   }

   public FindAttackTargetGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public FindAttackTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public FindAttackTargetGoal requiresPack() {
      this.requirePack = true;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host.getAttackTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      this.host.setAttackTarget(newTarget);
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (!this.targetClasses.isEmpty()) {
         boolean isTargetClass = false;

         for(Class<? extends Entity> targetClass : this.targetClasses) {
            if (targetClass.isAssignableFrom(target.getClass())) {
               isTargetClass = true;
               break;
            }
         }

         if (!isTargetClass) {
            return false;
         }
      }

      if (!this.tameTargeting && this.host.isTamed()) {
         return false;
      } else if (!this.host.canAttackClass(target.getClass())) {
         return false;
      } else if (!this.host.canAttackEntity(target)) {
         return false;
      } else {
         CreatureRelationshipEntry relationshipEntry = this.host.relationships.getEntry(target);
         if (relationshipEntry != null && !relationshipEntry.canHunt()) {
            return false;
         } else {
            return !this.requirePack || this.host.isInPack();
         }
      }
   }

   public boolean shouldExecute() {
      if (this.host.isAggressive() && !this.host.hasFixateTarget()) {
         if (this.targetPlayers) {
            if (this.host.updateTick % 5L != 0L) {
               return false;
            }
         } else if (this.host.updateTick % 40L != 0L) {
            return false;
         }

         this.target = null;
         double distance = this.getTargetDistance();
         double heightDistance = 4.0D + (double)this.host.height;
         if (this.host.useDirectNavigator()) {
            heightDistance = distance;
         }

         this.target = this.getNewTarget(distance, heightDistance, distance);
         if (this.target != null && this.host.rollAttackTargetChance(this.target)) {
            if (this.callForHelp) {
               this.callNearbyForHelp();
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean shouldStopTargeting(EntityLivingBase target) {
      return !this.isValidTarget(target);
   }

   public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
      if (this.targetPlayers) {
         EntityLivingBase newTarget = null;

         try {
            List<? extends EntityPlayer> players = this.host.getEntityWorld().getPlayers(EntityPlayer.class, this.targetSelector);
            if (!players.isEmpty()) {
               List<EntityPlayer> possibleTargets = new ArrayList();

               for(EntityPlayer player : players) {
                  if (this.isValidTarget(player)) {
                     possibleTargets.add(player);
                  }
               }

               if (!possibleTargets.isEmpty()) {
                  Collections.sort(possibleTargets, this.nearestSorter);
                  newTarget = (EntityLivingBase)possibleTargets.get(0);
               }
            }
         } catch (Exception var12) {
            LycanitesMobs.logWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
            var12.printStackTrace();
         }

         if (newTarget != null) {
            return newTarget;
         }
      }

      return this.host.updateTick % 40L == 0L ? super.getNewTarget(rangeX, rangeY, rangeZ) : null;
   }
}
