package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIAttackMeleeStatusAOE extends EntityAIAttackMeleeStatus {
   private double attack;

   public EntityAIAttackMeleeStatusAOE(EntityParasiteBase creature, double speedIn, boolean useLongMemory, double runningD, double attackD) {
      super(creature, speedIn, useLongMemory, runningD);
      this.attack = attackD * attackD;
   }

   protected void checkAndPerformAttack(EntityLivingBase target, double distance) {
      if (!this.isTargetParasite(target)) {
         if (distance <= this.attack && this.attacker.canEntityBeSeen(target)) {
            this.attacker.getNavigator().tryMoveToEntityLiving(target, 0.0D);
            if (this.attackTick <= 0) {
               this.attackTick = 20;
               EntityCutomAttack thisA = (EntityCutomAttack)this.attacker;
               thisA.attackEntityAsMobAOE(target);
            }
         }

      }
   }
}
