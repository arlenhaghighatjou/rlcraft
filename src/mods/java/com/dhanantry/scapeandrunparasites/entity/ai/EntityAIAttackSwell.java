package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackSwell extends EntityAIBase {
   EntityParasiteBase swellingParasite;
   EntityLivingBase creeperAttackTarget;
   double distance;

   public EntityAIAttackSwell(EntityParasiteBase in) {
      this.swellingParasite = in;
      this.setMutexBits(1);
      this.distance = 9.0D;
   }

   public EntityAIAttackSwell(EntityParasiteBase in, double dis) {
      this(in);
      this.distance = dis;
      this.setMutexBits(0);
   }

   public boolean shouldExecute() {
      EntityLivingBase entitylivingbase = this.swellingParasite.getAttackTarget();
      return this.swellingParasite.getSelfeState() > 0 || entitylivingbase != null && this.swellingParasite.getDistanceSq(entitylivingbase) < this.distance;
   }

   public void startExecuting() {
      this.swellingParasite.getNavigator().clearPath();
      this.creeperAttackTarget = this.swellingParasite.getAttackTarget();
   }

   public void resetTask() {
      this.creeperAttackTarget = null;
   }

   public void updateTask() {
      if (this.creeperAttackTarget == null) {
         this.swellingParasite.setSelfeState(-1);
      } else if (this.swellingParasite.getDistanceSq(this.creeperAttackTarget) > 49.0D) {
         this.swellingParasite.setSelfeState(-1);
      } else if (!this.swellingParasite.getEntitySenses().canSee(this.creeperAttackTarget)) {
         this.swellingParasite.setSelfeState(-1);
      } else {
         this.swellingParasite.setSelfeState(1);
      }

   }
}
