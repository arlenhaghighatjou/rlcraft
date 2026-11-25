package com.lycanitesmobs.core.entity.goals.targeting;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class CopyMasterAttackTargetGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private boolean tameTargeting = false;

   public CopyMasterAttackTargetGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(1);
   }

   public CopyMasterAttackTargetGoal setTameTargetting(boolean setTargetting) {
      this.tameTargeting = setTargetting;
      return this;
   }

   public boolean shouldExecute() {
      if (this.host.getAttackTarget() != null && !this.host.getAttackTarget().isEntityAlive()) {
         return false;
      } else {
         return this.host.getMasterAttackTarget() != null;
      }
   }

   public void updateTask() {
      if (this.host.getAttackTarget() == null) {
         EntityLivingBase target = this.host.getMasterAttackTarget();
         if (this.isTargetValid(target)) {
            this.host.setAttackTarget(target);
         }
      }

   }

   private boolean isTargetValid(EntityLivingBase target) {
      if (target == null) {
         return false;
      } else if (!target.isEntityAlive()) {
         return false;
      } else if (target == this.host) {
         return false;
      } else if (!this.host.canAttackClass(target.getClass())) {
         return false;
      } else {
         return this.host.canAttackEntity(target);
      }
   }

   public void resetTask() {
      this.host.setAttackTarget((EntityLivingBase)null);
   }
}
