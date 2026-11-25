package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;

public class EntityVentoraptor extends RideableCreatureEntity {
   public EntityVentoraptor(World world) {
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
               this.leap(6.0F, 0.5D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(2.0D, 0.5D);
         }
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      if (rider.isPotionActive(MobEffects.WEAKNESS)) {
         rider.removePotionEffect(MobEffects.WEAKNESS);
      }

      if (rider.isPotionActive(MobEffects.SLOWNESS)) {
         rider.removePotionEffect(MobEffects.SLOWNESS);
      }

   }

   public float getAISpeedModifier() {
      return !this.onGround ? 5.0F : 1.0F;
   }

   public double getFallingMod() {
      return 0.98D;
   }

   public void mountAbility(Entity rider) {
      if (!this.abilityToggled) {
         if (this.getStamina() >= this.getStaminaCost()) {
            this.playJumpSound();
            this.leap(4.0D, 0.5D);
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
