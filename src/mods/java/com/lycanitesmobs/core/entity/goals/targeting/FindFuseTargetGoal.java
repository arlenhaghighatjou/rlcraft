package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;

public class FindFuseTargetGoal extends TargetingGoal {
   private Class<IFusable> targetClass = IFusable.class;
   private int targetChance = 0;
   protected boolean tameTargeting = false;

   public FindFuseTargetGoal(BaseCreatureEntity setHost) {
      super(setHost);
      this.setMutexBits(1);
   }

   public FindFuseTargetGoal setChance(int setChance) {
      this.targetChance = setChance;
      return this;
   }

   public FindFuseTargetGoal setTargetClass(Class<IFusable> setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FindFuseTargetGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public FindFuseTargetGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindFuseTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindFuseTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public FindFuseTargetGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   protected EntityLivingBase getTarget() {
      return this.host instanceof IFusable ? (EntityLivingBase)((IFusable)this.host).getFusionTarget() : null;
   }

   protected void setTarget(EntityLivingBase newTarget) {
      if (this.host instanceof IFusable && newTarget instanceof IFusable) {
         ((IFusable)this.host).setFusionTarget((IFusable)newTarget);
      }

   }

   protected boolean isValidTarget(EntityLivingBase target) {
      if (this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass())) {
         return false;
      } else if (target.getClass() == this.host.getClass()) {
         return false;
      } else if (this.host instanceof IFusable && ((IFusable)this.host).getFusionClass((IFusable)target) != null) {
         if (this.host.isRareVariant()) {
            if (!(target instanceof BaseCreatureEntity)) {
               return false;
            }

            if (!((BaseCreatureEntity)target).isRareVariant()) {
               return false;
            }
         }

         return !(this.host instanceof TameableCreatureEntity) || !(target instanceof TameableCreatureEntity) || ((TameableCreatureEntity)this.host).getPlayerOwner() == ((TameableCreatureEntity)target).getPlayerOwner();
      } else {
         return false;
      }
   }

   public boolean shouldExecute() {
      if (!CreatureManager.getInstance().config.elementalFusion) {
         return false;
      } else if (this.host.updateTick % 20L != 0L) {
         return false;
      } else if (this.host.isPetType("familiar")) {
         return false;
      } else if (this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
         return false;
      } else {
         EntityLivingBase fuseTarget = this.getTarget();
         if (fuseTarget != null && !this.isValidTarget(fuseTarget)) {
            return false;
         } else {
            this.target = null;
            double distance = this.getTargetDistance();
            double heightDistance = 4.0D;
            if (this.host.useDirectNavigator()) {
               heightDistance = distance;
            }

            this.target = this.getNewTarget(distance, heightDistance, distance);
            if (this.callForHelp) {
               this.callNearbyForHelp();
            }

            return this.target != null;
         }
      }
   }
}
