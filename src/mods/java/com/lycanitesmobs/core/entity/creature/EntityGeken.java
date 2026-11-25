package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.World;

public class EntityGeken extends TameableCreatureEntity implements IMob {
   public EntityGeken(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.setupMob();
      this.attackPhaseMax = 3;
      this.setAttackCooldownMax(10);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote) {
         if (this.hasAttackTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(6.0F, 0.6D, this.getAttackTarget());
            }
         } else if (this.isMoving() && this.rand.nextInt(50) == 0) {
            this.leap(1.0D, 1.0D);
         }
      }

   }

   public int getMeleeCooldown() {
      return this.getAttackPhase() == 2 ? super.getMeleeCooldown() * 3 : super.getMeleeCooldown();
   }

   public int getRangedCooldown() {
      return this.getAttackPhase() == 2 ? super.getRangedCooldown() * 3 : super.getRangedCooldown();
   }

   public boolean canClimb() {
      return true;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
