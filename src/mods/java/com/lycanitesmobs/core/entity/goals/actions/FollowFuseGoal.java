package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.Entity;

public class FollowFuseGoal extends FollowGoal {
   BaseCreatureEntity host;
   double fuseRange = 2.0D;

   public FollowFuseGoal(BaseCreatureEntity setHost) {
      super(setHost);
      this.host = setHost;
      this.strayDistance = 0.0D;
   }

   public boolean shouldExecute() {
      return this.host.isBoss() ? false : super.shouldExecute();
   }

   public FollowFuseGoal setSpeed(double setSpeed) {
      this.speed = setSpeed;
      return this;
   }

   public FollowFuseGoal setTargetClass(Class setTargetClass) {
      this.targetClass = setTargetClass;
      return this;
   }

   public FollowFuseGoal setStrayDistance(double setDist) {
      this.strayDistance = setDist;
      return this;
   }

   public FollowFuseGoal setLostDistance(double setDist) {
      this.lostDistance = setDist;
      return this;
   }

   public FollowFuseGoal setFuseRange(double setDist) {
      this.fuseRange = setDist;
      return this;
   }

   public Entity getTarget() {
      return this.host instanceof IFusable ? (Entity)((IFusable)this.host).getFusionTarget() : null;
   }

   public void setTarget(Entity entity) {
      if (this.host instanceof IFusable && entity instanceof IFusable) {
         ((IFusable)this.host).setFusionTarget((IFusable)entity);
      }

   }

   public void onTargetDistance(double distance, Entity followTarget) {
      if (distance <= this.fuseRange) {
         if (this.host instanceof IFusable && followTarget instanceof IFusable) {
            Class fusionClass = ((IFusable)this.host).getFusionClass((IFusable)followTarget);
            if (fusionClass == null) {
               return;
            }

            this.host.transform(fusionClass, followTarget, true);
         }

      }
   }
}
