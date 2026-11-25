package com.lycanitesmobs.core.entity.goals.actions;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class LookIdleGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private int idleTime;
   private int idleTimeMin = 20;
   private int idleTimeRange = 20;
   private double lookX;
   private double lookZ;

   public LookIdleGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(2);
   }

   public LookIdleGoal setTimeMin(int setTimeMin) {
      this.idleTimeMin = setTimeMin;
      return this;
   }

   public LookIdleGoal setTimeRange(int setTimeRange) {
      this.idleTimeRange = setTimeRange;
      return this;
   }

   public boolean shouldExecute() {
      return this.host.rollLookChance();
   }

   public boolean shouldContinueExecuting() {
      return this.idleTime >= 0;
   }

   public void startExecuting() {
      double d0 = 6.283185307179586D * this.host.getRNG().nextDouble();
      this.lookX = Math.cos(d0);
      this.lookZ = Math.sin(d0);
      this.idleTime = this.idleTimeMin + this.host.getRNG().nextInt(this.idleTimeRange);
   }

   public void updateTask() {
      --this.idleTime;
      this.host.getLookHelper().setLookPosition(this.host.posX + this.lookX, this.host.posY + (double)this.host.getEyeHeight(), this.host.posZ + this.lookZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
   }
}
