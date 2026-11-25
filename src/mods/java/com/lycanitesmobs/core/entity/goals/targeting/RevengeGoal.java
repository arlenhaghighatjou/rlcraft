package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.info.CreatureGroup;
import net.minecraft.entity.EntityLivingBase;

public class RevengeGoal extends FindAttackTargetGoal {
   Class<? extends EntityLivingBase>[] helpClasses = null;
   private int revengeTime;

   public RevengeGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public RevengeGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   public RevengeGoal setHelpClasses(Class<? extends EntityLivingBase>... setHelpClasses) {
      this.helpClasses = setHelpClasses;
      this.callForHelp = true;
      return this;
   }

   public RevengeGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public RevengeGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public RevengeGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return super.getTarget();
   }

   protected void setTarget(EntityLivingBase newTarget) {
      super.setTarget(newTarget);
   }

   public boolean shouldExecute() {
      if (this.host.getRevengeTarget() != null && this.isEntityTargetable(this.host.getRevengeTarget(), false)) {
         this.target = this.host.getRevengeTarget();
         if (this.host.getRevengeTimer() != this.revengeTime) {
            this.revengeTime = this.host.getRevengeTimer();
            this.callNearbyForHelp();
         }

         boolean shouldRevenge = this.host.creatureInfo.getGroups().isEmpty();
         boolean shouldPackHunt = false;

         for(CreatureGroup group : this.host.creatureInfo.getGroups()) {
            if (group.shouldRevenge(this.target)) {
               shouldRevenge = true;
            }

            if (group.shouldPackHunt(this.target)) {
               shouldPackHunt = true;
            }
         }

         return shouldRevenge || shouldPackHunt && this.host.isInPack();
      } else {
         return false;
      }
   }

   public void startExecuting() {
      super.startExecuting();
   }
}
