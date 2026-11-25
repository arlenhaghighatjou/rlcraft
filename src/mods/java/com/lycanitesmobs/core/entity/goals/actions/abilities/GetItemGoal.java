package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.TargetSorterNearest;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;

public class GetItemGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private EntityItem target;
   private Predicate<EntityItem> targetSelector;
   private TargetSorterNearest targetSorter;
   private double distanceMax = 1024.0D;
   double speed = 1.0D;
   private boolean checkSight = true;
   private int cantSeeTime = 0;
   protected int cantSeeTimeMax = 60;
   private int updateRate = 0;
   private int recheckTime = 0;
   public boolean tamedLooting = true;

   public GetItemGoal(BaseCreatureEntity setHost) {
      this.setMutexBits(3);
      this.host = setHost;
      this.targetSelector = (input) -> {
         return true;
      };
      this.targetSorter = new TargetSorterNearest(setHost);
   }

   public GetItemGoal setDistanceMax(double setDouble) {
      this.distanceMax = setDouble * setDouble;
      return this;
   }

   public GetItemGoal setSpeed(double setDouble) {
      this.speed = setDouble;
      return this;
   }

   public GetItemGoal setCheckSight(boolean setBool) {
      this.checkSight = setBool;
      return this;
   }

   public GetItemGoal setTamedLooting(boolean bool) {
      this.tamedLooting = bool;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.canPickupItems()) {
         return false;
      } else if (this.recheckTime++ < 40) {
         return false;
      } else {
         this.recheckTime = 0;
         if (!this.tamedLooting && this.host instanceof TameableCreatureEntity && this.host.isTamed()) {
            return false;
         } else {
            double heightDistance = 4.0D;
            if (this.host.useDirectNavigator()) {
               heightDistance = this.distanceMax;
            }

            List<EntityItem> possibleTargets = this.host.getEntityWorld().getEntitiesWithinAABB(EntityItem.class, this.host.getEntityBoundingBox().grow(this.distanceMax, heightDistance, this.distanceMax), this.targetSelector);
            if (possibleTargets.isEmpty()) {
               return false;
            } else {
               Collections.sort(possibleTargets, this.targetSorter);
               this.target = (EntityItem)possibleTargets.get(0);
               return this.shouldContinueExecuting();
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.target == null) {
         return false;
      } else if (!this.target.isEntityAlive()) {
         return false;
      } else {
         double distance = (double)this.host.getDistance(this.target);
         if (distance > this.distanceMax) {
            return false;
         } else {
            if (this.checkSight) {
               if (this.host.getEntitySenses().canSee(this.target)) {
                  this.cantSeeTime = 0;
               } else if (++this.cantSeeTime > this.cantSeeTimeMax) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public void resetTask() {
      this.target = null;
      this.host.clearMovement();
   }

   public void startExecuting() {
      this.updateRate = 0;
   }

   public void updateTask() {
      if (this.updateRate-- <= 0) {
         this.updateRate = 20;
         if (!this.host.useDirectNavigator()) {
            this.host.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
         } else {
            this.host.directNavigator.setTargetPosition(new BlockPos((int)this.target.posX, (int)this.target.posY, (int)this.target.posZ), this.speed);
         }
      }

   }
}
