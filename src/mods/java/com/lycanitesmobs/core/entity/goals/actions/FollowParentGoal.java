package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class FollowParentGoal extends FollowGoal {
   AgeableCreatureEntity host;

   public FollowParentGoal(AgeableCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
   }

   public FollowParentGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public FollowParentGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FollowParentGoal setStrayDistance(double setDist) {
      this.strayDistance = setDist;
      return this;
   }

   public FollowParentGoal setLostDistance(double setDist) {
      this.lostDistance = setDist;
      return this;
   }

   public FollowParentGoal setFollowBehind(double setDist) {
      this.behindDistance = setDist;
      return this;
   }

   public Entity getTarget() {
      return this.host.getParentTarget();
   }

   public void setTarget(Entity entity) {
      if (entity instanceof EntityLivingBase) {
         this.host.setParentTarget((EntityLivingBase)entity);
      }

   }

   public boolean shouldExecute() {
      return !this.host.shouldFollowParent() ? false : super.shouldExecute();
   }

   public boolean shouldContinueExecuting() {
      return !this.host.shouldFollowParent() ? false : super.shouldContinueExecuting();
   }
}
