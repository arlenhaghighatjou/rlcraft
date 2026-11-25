package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.World;

public class EntityTpumpkyn extends TameableCreatureEntity {
   public EntityTpumpkyn(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
   }

   public boolean rollLookChance() {
      return false;
   }

   public boolean rollWanderChance() {
      return this.getRNG().nextDouble() <= 5.0E-4D;
   }
}
