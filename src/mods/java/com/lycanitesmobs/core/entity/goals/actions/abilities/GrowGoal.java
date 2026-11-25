package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class GrowGoal extends EntityAIBase {
   BaseCreatureEntity host;
   protected int tickRate = 20;
   protected float growthAmount = 0.1F;
   protected int phase = -1;
   public int abilityTime = 0;

   public GrowGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public GrowGoal setTickRate(int tickRate) {
      this.tickRate = tickRate;
      return this;
   }

   public GrowGoal setGrowthAmount(float growthAmount) {
      this.growthAmount = growthAmount;
      return this;
   }

   public GrowGoal setPhase(int phase) {
      this.phase = phase;
      return this;
   }

   public boolean shouldExecute() {
      if (!this.host.isEntityAlive()) {
         return false;
      } else {
         return this.phase < 0 || this.phase == this.host.getBattlePhase();
      }
   }

   public void startExecuting() {
      this.abilityTime = 0;
   }

   public void updateTask() {
      if (this.abilityTime++ % this.tickRate == 0) {
         this.host.setSizeScale(this.host.sizeScale + (double)this.growthAmount);
      }
   }
}
