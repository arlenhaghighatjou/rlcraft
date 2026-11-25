package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;

public class StealthGoal extends EntityAIBase {
   private BaseCreatureEntity host;
   private int stealthTimeMax = 20;
   private int stealthTimeMaxPrev = 20;
   private int stealthTime = 0;
   private int unstealthRate = 4;
   private boolean stealthMove = false;
   private boolean stealthAttack = false;
   private boolean unstealth = false;

   public StealthGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
      this.setMutexBits(0);
   }

   public StealthGoal setStealthTime(int time) {
      this.stealthTimeMax = time;
      return this;
   }

   public StealthGoal setUnstealthRate(int rate) {
      this.unstealthRate = rate;
      return this;
   }

   public StealthGoal setStealthMove(boolean flag) {
      this.stealthMove = flag;
      return this;
   }

   public StealthGoal setStealthAttack(boolean flag) {
      this.stealthAttack = flag;
      return this;
   }

   public boolean shouldExecute() {
      this.unstealth = false;
      if (this.host.getLeashed()) {
         this.unstealth = true;
      }

      if (!this.stealthMove) {
         if (!this.host.useDirectNavigator() && !this.host.getNavigator().noPath()) {
            this.unstealth = true;
         }

         if (this.host.useDirectNavigator() && !this.host.directNavigator.atTargetPosition()) {
            this.unstealth = true;
         }
      }

      if (!this.stealthAttack && this.host.getAttackTarget() != null) {
         this.unstealth = true;
      }

      if (!this.host.canStealth()) {
         this.unstealth = true;
      }

      return !this.unstealth;
   }

   public boolean shouldContinueExecuting() {
      if (this.host.getLeashed()) {
         this.unstealth = true;
      }

      if (!this.stealthMove) {
         if (!this.host.useDirectNavigator() && !this.host.getNavigator().noPath()) {
            this.unstealth = true;
         }

         if (this.host.useDirectNavigator() && !this.host.directNavigator.atTargetPosition()) {
            this.unstealth = true;
         }
      }

      if (!this.stealthAttack && this.host.getAttackTarget() != null) {
         this.unstealth = true;
      }

      if (!this.host.canStealth()) {
         this.unstealth = true;
      }

      if (this.unstealth && this.host.getStealth() <= 0.0F) {
         return false;
      } else {
         return this.stealthTimeMaxPrev == this.stealthTimeMax;
      }
   }

   public void startExecuting() {
      this.host.setStealth(0.0F);
      this.stealthTime = 0;
      this.stealthTimeMaxPrev = this.stealthTimeMax;
   }

   public void resetTask() {
      this.host.setStealth(0.0F);
      this.stealthTime = 0;
      this.stealthTimeMaxPrev = this.stealthTimeMax;
   }

   public void updateTask() {
      float nextStealth = (float)this.stealthTime / (float)this.stealthTimeMax;
      this.host.setStealth(nextStealth);
      if (!this.unstealth && this.stealthTime < this.stealthTimeMax) {
         ++this.stealthTime;
      } else if (this.unstealth && this.stealthTime > 0) {
         this.stealthTime -= this.unstealthRate;
      }

   }
}
