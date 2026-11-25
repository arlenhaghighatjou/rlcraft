package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLivingBase;

public class FindGroupAvoidTargetGoal extends FindAvoidTargetGoal {
   public FindGroupAvoidTargetGoal(BaseCreatureEntity host) {
      super(host);
   }

   public FindGroupAvoidTargetGoal setChance(int setChance) {
      this.targetChance = setChance;
      return this;
   }

   public FindGroupAvoidTargetGoal setSightCheck(boolean setSightCheck) {
      this.checkSight = setSightCheck;
      return this;
   }

   public FindGroupAvoidTargetGoal setOnlyNearby(boolean setNearby) {
      this.nearbyOnly = setNearby;
      return this;
   }

   public FindGroupAvoidTargetGoal setCantSeeTimeMax(int setCantSeeTimeMax) {
      this.cantSeeTimeMax = setCantSeeTimeMax;
      return this;
   }

   public FindGroupAvoidTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public FindGroupAvoidTargetGoal setHelpCall(boolean setHelp) {
      this.callForHelp = setHelp;
      return this;
   }

   protected boolean isValidTarget(EntityLivingBase target) {
      return target instanceof TameableCreatureEntity && ((TameableCreatureEntity)target).isTamed() ? false : this.host.shouldCreatureGroupFlee(target);
   }
}
