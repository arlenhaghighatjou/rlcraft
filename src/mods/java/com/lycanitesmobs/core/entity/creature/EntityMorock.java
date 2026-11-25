package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMorock extends RideableCreatureEntity implements IMob, IGroupHeavy {
   protected boolean wantsToLand;
   protected boolean isLanded;

   public EntityMorock(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.flySoundSpeed = 20;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote) {
         if (this.isLanded) {
            this.wantsToLand = false;
            if (this.hasPickupEntity() || this.getControllingPassenger() != null || this.getLeashed() || this.isInWater() || !this.isTamed() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
               this.leap(1.0D, 1.0D);
               this.isLanded = false;
            }
         } else if (this.wantsToLand) {
            if (!this.isLanded && this.isSafeToLand()) {
               this.isLanded = true;
            }
         } else if (!this.hasPickupEntity() && !this.hasAttackTarget() && this.updateTick % 100L == 0L && this.getRNG().nextBoolean()) {
            this.wantsToLand = true;
         }

         if (!this.hasPickupEntity() && this.getControllingPassenger() == null && !this.hasAttackTarget() && !this.isInWater()) {
            if (this.isTamed() && !this.getLeashed()) {
               this.wantsToLand = true;
            }
         } else {
            this.wantsToLand = false;
         }
      }

      if (!this.isTamed() && !this.getEntityWorld().isRemote) {
         if (this.hasAttackTarget()) {
            if (this.rand.nextInt(10) == 0) {
               this.leap(6.0F, 0.0D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(2.0D, 0.0D);
         }
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

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      if (this.wantsToLand || !this.isLanded) {
         BlockPos groundPos;
         for(groundPos = wanderPosition.down(); groundPos.getY() > 0 && this.getEntityWorld().getBlockState(groundPos).getBlock() == Blocks.AIR; groundPos = groundPos.down()) {
            ;
         }

         if (this.getEntityWorld().getBlockState(groundPos).getMaterial().isSolid()) {
            return groundPos.up();
         }
      }

      if (this.hasPickupEntity() && this.getPickupEntity() instanceof EntityPlayer) {
         wanderPosition = new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ());
      }

      return wanderPosition;
   }

   public double getFlightOffset() {
      if (!this.wantsToLand) {
         super.getFlightOffset();
      }

      return 0.0D;
   }

   public boolean rollWanderChance() {
      if (this.isFlying()) {
         return this.getRNG().nextDouble() <= 0.25D;
      } else {
         return this.getRNG().nextDouble() <= 0.008D;
      }
   }

   public boolean isFlying() {
      return !this.isLanded || this.hasPickupEntity();
   }

   public boolean petControlsEnabled() {
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

   public double getMountedYOffset() {
      return (double)this.height * 0.8D;
   }

   public void mountAbility(Entity rider) {
      if (!this.abilityToggled) {
         if (this.getStamina() >= this.getStaminaCost()) {
            this.playJumpSound();
            this.leap(4.0D, 0.0D);
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
}
