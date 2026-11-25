package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityZoataur extends RideableCreatureEntity implements IMob {
   public EntityZoataur(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsUnderground = true;
      this.hasAttackSound = true;
      this.spreadFire = true;
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   public void onLivingUpdate() {
      if (!this.getEntityWorld().isRemote && this.isBlocking() && this.hasAttackTarget()) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      super.onLivingUpdate();
   }

   public void onDamage(DamageSource damageSrc, float damage) {
      if (!this.hasRiderTarget() && this.getRNG().nextDouble() > 0.75D && this.getHealth() / this.getMaxHealth() > 0.25F) {
         this.setBlocking();
      }

      super.onDamage(damageSrc, damage);
   }

   public void setBlocking() {
      this.currentBlockingTime = this.blockingTime + this.getRNG().nextInt(this.blockingTime / 2);
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

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("cactus") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (this.getStamina() >= this.getStaminaCost()) {
            this.currentBlockingTime = 10;
            this.applyStaminaCost();
         }
      }
   }

   public float getStaminaCost() {
      return 0.5F;
   }

   public int getStaminaRecoveryWarmup() {
      return 40;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }
}
