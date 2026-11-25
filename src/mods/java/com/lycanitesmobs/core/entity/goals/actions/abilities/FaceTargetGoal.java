package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class FaceTargetGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private EntityLivingBase target;

   public FaceTargetGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public boolean shouldExecute() {
      if (!this.host.isEntityAlive()) {
         return false;
      } else {
         this.target = this.host.getAttackTarget();
         return this.target != null;
      }
   }

   public void updateTask() {
      this.host.getLookHelper().setLookPositionWithEntity(this.target, 10.0F, (float)this.host.getVerticalFaceSpeed());
   }
}
