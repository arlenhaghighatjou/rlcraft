package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;

public class AvoidIfHitGoal extends FindAvoidTargetGoal {
   Class[] helpClasses = null;
   private int revengeTime;

   public AvoidIfHitGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public AvoidIfHitGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public AvoidIfHitGoal setHelpClasses(Class... setHelpClasses) {
      this.helpClasses = setHelpClasses;
      this.callForHelp = true;
      return this;
   }

   public AvoidIfHitGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public AvoidIfHitGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public AvoidIfHitGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public void resetTask() {
      this.setTarget((EntityLivingBase)null);
   }

   public boolean shouldExecute() {
      if (this.host.getRevengeTarget() == null) {
         return false;
      } else if (this.host.shouldCreatureGroupRevenge(this.host.getRevengeTarget())) {
         return false;
      } else {
         return this.host.getRevengeTimer() != this.revengeTime;
      }
   }

   public void startExecuting() {
      this.target = this.host.getRevengeTarget();
      this.revengeTime = this.host.getRevengeTimer();
      this.callNearbyForHelp();
      super.startExecuting();
   }
}
