package com.lycanitesmobs.core.entity.goals.targeting;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public class RevengeRiderGoal extends FindAttackTargetGoal {
   private TameableCreatureEntity host;
   private int revengeTime;

   public RevengeRiderGoal(TameableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.tameTargeting = true;
   }

   public RevengeRiderGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public RevengeRiderGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public RevengeRiderGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public RevengeRiderGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.hasRiderTarget()) {
         return false;
      } else if (this.host.getRider() == null) {
         return false;
      } else if (this.getRiderRevengeTime() == this.revengeTime) {
         return false;
      } else {
         return this.isEntityTargetable(this.getRiderRevengeTarget(), false);
      }
   }

   public void startExecuting() {
      this.target = this.getRiderRevengeTarget();
      this.revengeTime = this.getRiderRevengeTime();

      try {
         if (this.callForHelp) {
            double d0 = this.getTargetDistance();

            for(BaseCreatureEntity possibleAlly : this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().grow(d0, 4.0D, d0), (input) -> {
               return input instanceof EntityLiving;
            })) {
               if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target)) {
                  possibleAlly.setAttackTarget(this.target);
               }
            }
         }
      } catch (Exception var6) {
         LycanitesMobs.logWarning("", "An exception occurred when selecting help targets in rider revenge, this has been skipped to prevent a crash.");
         var6.printStackTrace();
      }

      super.startExecuting();
   }

   public boolean shouldStopTargeting(EntityLivingBase target) {
      return target != this.getRiderRevengeTarget();
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      return true;
   }

   public EntityLivingBase getRiderRevengeTarget() {
      ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.host.getRider());
      return extendedEntity != null ? extendedEntity.lastAttackedEntity : null;
   }

   public int getRiderRevengeTime() {
      ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.host.getRider());
      return extendedEntity != null ? extendedEntity.lastAttackedTime : 0;
   }
}
