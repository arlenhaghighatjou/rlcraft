package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityFeradon extends RideableCreatureEntity {
   protected boolean leapedAbilityQueued = false;
   protected boolean leapedAbilityReady = false;

   public EntityFeradon(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.spreadFire = false;
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
      super.onLivingUpdate();
      if (!this.isTamed() && this.onGround && !this.getEntityWorld().isRemote && this.hasAttackTarget() && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.5D, this.getAttackTarget());
      }

      if (this.leapedAbilityQueued && !this.onGround && !this.getEntityWorld().isRemote) {
         this.leapedAbilityQueued = false;
         this.leapedAbilityReady = true;
      }

      if (this.leapedAbilityReady && this.onGround && !this.getEntityWorld().isRemote) {
         this.leapedAbilityReady = false;
         double distance = 4.0D;
         List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), (possibleTarget) -> {
            return possibleTarget.isEntityAlive() && possibleTarget != this && !this.isRidingOrBeingRiddenBy(possibleTarget) && !this.isOnSameTeam(possibleTarget) && this.canAttackClass(possibleTarget.getClass()) && this.canAttackEntity(possibleTarget);
         });
         if (!possibleTargets.isEmpty()) {
            for(EntityLivingBase possibleTarget : possibleTargets) {
               boolean doDamage = true;
               if (this.getRider() instanceof EntityPlayer && MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
                  doDamage = false;
               }

               if (doDamage) {
                  possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 0));
               }
            }
         }

         this.playAttackSound();
      }

   }

   public void riderEffects(EntityLivingBase rider) {
      if (rider.isPotionActive(MobEffects.WEAKNESS)) {
         rider.removePotionEffect(MobEffects.WEAKNESS);
      }

      if (rider.isPotionActive(MobEffects.MINING_FATIGUE)) {
         rider.removePotionEffect(MobEffects.MINING_FATIGUE);
      }

   }

   public float getAISpeedModifier() {
      return !this.onGround ? 2.0F : 1.0F;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.9D;
   }

   public void leap(double distance, double leapHeight) {
      super.leap(distance, leapHeight);
      if (!this.getEntityWorld().isRemote) {
         this.leapedAbilityQueued = true;
      }

   }

   public void leap(float range, double leapHeight, Entity target) {
      super.leap(range, leapHeight, target);
      if (!this.getEntityWorld().isRemote) {
         this.leapedAbilityQueued = true;
      }

   }

   public void mountAbility(Entity rider) {
      if (this.onGround) {
         if (!this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
               this.playJumpSound();
               this.leap(4.0D, 0.5D);
               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 15.0F;
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
