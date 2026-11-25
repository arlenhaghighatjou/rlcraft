package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public class EntityVorach extends TameableCreatureEntity implements IMob {
   public EntityVorach(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         float leeching = Math.max(1.0F, this.getAttackDamage(damageScale) / 2.0F);
         this.heal(leeching);
         return true;
      }
   }

   public float getAISpeedModifier() {
      if (this.hasAttackTarget() && this.getDistanceSq(this.getAttackTarget()) >= 60.0D) {
         return this.isLookingAtMe(this.getAttackTarget()) ? 0.0F : super.getAISpeedModifier() * 5.0F;
      } else {
         return super.getAISpeedModifier();
      }
   }

   public boolean canClimb() {
      return true;
   }

   public boolean canBurn() {
      return false;
   }

   public float getFallResistance() {
      return 10.0F;
   }
}
