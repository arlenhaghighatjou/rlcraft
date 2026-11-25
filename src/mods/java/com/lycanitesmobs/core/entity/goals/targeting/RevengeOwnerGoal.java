package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;

public class RevengeOwnerGoal extends FindAttackTargetGoal {
   private TameableCreatureEntity host;
   private int revengeTime;

   public RevengeOwnerGoal(TameableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.tameTargeting = true;
   }

   public RevengeOwnerGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public RevengeOwnerGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public RevengeOwnerGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public RevengeOwnerGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.isTamed()) {
         return false;
      } else if (this.host.getOwner() == null) {
         return false;
      } else if (!(this.host.getOwner() instanceof EntityLivingBase)) {
         return false;
      } else {
         EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
         int i = owner.getRevengeTimer();
         if (i == this.revengeTime) {
            return false;
         } else {
            return this.isEntityTargetable(owner.getRevengeTarget(), false);
         }
      }
   }

   public void startExecuting() {
      EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
      this.target = owner.getRevengeTarget();
      this.revengeTime = owner.getRevengeTimer();
      if (this.callForHelp) {
         this.callNearbyForHelp();
      }

      super.startExecuting();
   }
}
