package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityCockatrice extends RideableCreatureEntity implements IMob {
   protected WanderGoal wanderAI;
   protected AttackMeleeGoal attackAI;
   protected boolean wantsToLand;
   protected boolean isLanded;

   public EntityCockatrice(World world) {
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
      Class<? extends Entity> vespidType = CreatureManager.getInstance().getEntityClass("vespid");
      if (vespidType != null) {
         this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(vespidType));
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && !this.isAIDisabled()) {
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
               this.leap(2.0F, 1.0D, this.getAttackTarget());
            }
         } else if (this.rand.nextInt(50) == 0 && this.isMoving()) {
            this.leap(1.0D, 1.0D);
         }
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

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityVespid) {
            target.setDead();
         }

         return true;
      }
   }

   public void specialAttack() {
      double distance = 5.0D;
      List<EntityLivingBase> possibleTargets = this.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(distance, distance, distance), (possibleTargetx) -> {
         return possibleTarget.isEntityAlive() && possibleTarget != this && !this.isRidingOrBeingRiddenBy(possibleTarget) && !this.isOnSameTeam(possibleTarget) && this.canAttackClass(possibleTarget.getClass()) && this.canAttackEntity(possibleTarget);
      });
      if (!possibleTargets.isEmpty()) {
         for(EntityLivingBase possibleTarget : possibleTargets) {
            boolean doDamage = true;
            if (this.getRider() instanceof EntityPlayer && MinecraftForge.EVENT_BUS.post(new AttackEntityEvent((EntityPlayer)this.getRider(), possibleTarget))) {
               doDamage = false;
            }

            if (doDamage) {
               if (ObjectManager.getEffect("paralysis") != null) {
                  possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("paralysis"), this.getEffectDuration(5), 1));
               }

               if (ObjectManager.getEffect("aphagia") != null) {
                  possibleTarget.addPotionEffect(new PotionEffect(ObjectManager.getEffect("aphagia"), this.getEffectDuration(5), 1));
               } else {
                  possibleTarget.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 0));
               }
            }
         }
      }

      this.playAttackSound();
      this.triggerAttackCooldown();
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
      return (double)this.height * 0.7D;
   }

   public double getMountedZOffset() {
      return -((double)this.width) * 0.01D;
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.getStamina() >= this.getStaminaCost()) {
               this.specialAttack();
               this.applyStaminaCost();
            }
         }
      }
   }

   public float getStaminaCost() {
      return 100.0F;
   }

   public int getStaminaRecoveryWarmup() {
      return 100;
   }

   public float getStaminaRecoveryMax() {
      return 1.0F;
   }
}
