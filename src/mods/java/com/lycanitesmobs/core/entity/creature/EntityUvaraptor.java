package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityUvaraptor extends RideableCreatureEntity {
   public EntityUvaraptor(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsUnderground = false;
      this.hasAttackSound = true;
      this.hasJumpSound = true;
      this.spreadFire = false;
      this.canGrow = true;
      this.babySpawnChance = 0.01D;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote) {
         if (this.hasAttackTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(6.0F, 1.0D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(1.0D, 1.0D);
         }
      }

   }

   public float getAISpeedModifier() {
      return !this.onGround ? 5.0F : 1.0F;
   }

   public double getFallingMod() {
      return 0.9D;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.9D;
   }

   public void mountAbility(Entity rider) {
      if (!this.abilityToggled) {
         if (this.getStamina() >= this.getStaminaCost()) {
            this.playJumpSound();
            this.leap(1.0D, 2.0D);
            this.applyStaminaCost();
         }
      }
   }

   public float getStaminaCost() {
      return 20.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
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

   public boolean petControlsEnabled() {
      return true;
   }
}
