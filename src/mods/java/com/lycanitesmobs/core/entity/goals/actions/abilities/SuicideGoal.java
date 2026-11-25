package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class SuicideGoal extends EntityAIBase {
   BaseCreatureEntity host;
   protected int countdown = 200;
   protected int phase = -1;

   public SuicideGoal(BaseCreatureEntity setHost) {
      this.host = setHost;
   }

   public SuicideGoal setCountdown(int countdown) {
      this.countdown = countdown;
      return this;
   }

   public SuicideGoal setPhase(int phase) {
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

   public void updateTask() {
      if (this.countdown-- <= 0) {
         DamageSource damageSource = new EntityDamageSource("mob", this.host);
         damageSource.setDamageIsAbsolute();
         damageSource.setDamageBypassesArmor();
         this.host.attackEntityFrom(damageSource, this.host.getHealth());
      }
   }
}
