package com.lycanitesmobs.core.entity.goals;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class BaseGoal extends EntityAIBase {
   protected BaseCreatureEntity host;
   private GoalConditions goalConditions;
   private BaseCreatureEntity.TARGET_BITS targetBit = BaseCreatureEntity.TARGET_BITS.ATTACK;

   public BaseGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public BaseGoal setConditions(GoalConditions goalConditions) {
      this.goalConditions = goalConditions;
      return this;
   }

   public BaseGoal setTargetBit(BaseCreatureEntity.TARGET_BITS targetBit) {
      this.targetBit = targetBit;
      return this;
   }

   public boolean shouldExecute() {
      return this.host.isEntityAlive() && (this.goalConditions == null || this.goalConditions.isMet(this.host));
   }

   public EntityLivingBase getTarget() {
      if (this.targetBit == BaseCreatureEntity.TARGET_BITS.ATTACK) {
         return this.host.getAttackTarget();
      } else if (this.targetBit == BaseCreatureEntity.TARGET_BITS.AVOID) {
         return this.host.getAvoidTarget();
      } else if (this.targetBit == BaseCreatureEntity.TARGET_BITS.MASTER) {
         return this.host.getMasterTarget();
      } else if (this.targetBit == BaseCreatureEntity.TARGET_BITS.PARENT) {
         return this.host.getParentTarget();
      } else if (this.targetBit == BaseCreatureEntity.TARGET_BITS.RIDER) {
         return this.host.getRider();
      } else {
         return this.targetBit == BaseCreatureEntity.TARGET_BITS.PERCH ? this.host.getPerchTarget() : null;
      }
   }
}
