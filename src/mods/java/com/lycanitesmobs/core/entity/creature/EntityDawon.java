package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.world.World;

public class EntityDawon extends TameableCreatureEntity {
   public EntityDawon(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.attackCooldownMax = 15;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPigZombie.class).setSpeed(1.5D).setDamageScale(8.0D).setRange(2.5D));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setSpeed(1.5D));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote) {
         if (this.hasAttackTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(16.0F, 0.2D, this.getAttackTarget());
            }
         } else if (this.isMoving() && this.rand.nextInt(50) == 0) {
            this.leap(2.0D, 0.5D);
         }
      }

   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 100.0F;
   }
}
