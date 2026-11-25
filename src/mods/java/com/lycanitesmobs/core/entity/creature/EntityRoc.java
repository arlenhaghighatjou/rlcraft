package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRoc extends RideableCreatureEntity implements IMob {
   public AttackMeleeGoal attackAI;
   public boolean creeperDropping = true;
   private int creeperDropCooldown = 0;

   public EntityRoc(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.flySoundSpeed = 20;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      if (this.creeperDropping) {
         this.targetTasks.addTask(this.nextFindTargetIndex++, (new FindAttackTargetGoal(this)).addTargets(EntityCreeper.class));
      }

      super.initEntityAI();
      this.attackAI = (new AttackMeleeGoal(this)).setLongMemory(false);
      this.tasks.addTask(this.nextCombatGoalIndex++, this.attackAI);
   }

   public void loadCreatureFlags() {
      this.creeperDropping = this.creatureInfo.getFlag("creeperDropping", this.creeperDropping);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.getControllingPassenger() == null) {
         this.attackAI.setEnabled(this.hasPickupEntity() ? this.getPickupEntity() instanceof EntityCreeper : this.creeperDropCooldown <= 0);
         if (this.creeperDropCooldown > 0) {
            --this.creeperDropCooldown;
         }

         if (this.hasPickupEntity()) {
            ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
            if (extendedEntity != null) {
               extendedEntity.setPickedUpByEntity(this);
            }

            if (this.getPickupEntity() instanceof EntityCreeper && this.hasAttackTarget() && !(this.getAttackTarget() instanceof EntityCreeper)) {
               double distance = (new Vec3d(this.posX, 0.0D, this.posZ)).distanceTo(new Vec3d(this.getAttackTarget().posX, 0.0D, this.getAttackTarget().posZ));
               if (distance <= 2.0D && this.posY > this.getAttackTarget().posY) {
                  this.getPickupEntity().setRevengeTarget(this.getAttackTarget());
                  this.dropPickupEntity();
                  this.creeperDropCooldown = 120;
               }
            }

            if (this.hasPickupEntity() && this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
               if (this.getPickupEntity() instanceof EntityPlayer) {
                  for(int distToGround = 0; distToGround < 8; ++distToGround) {
                     Block searchBlock = this.getEntityWorld().getBlockState(new BlockPos((int)this.posX, (int)this.posY - distToGround, (int)this.posZ)).getBlock();
                     if (searchBlock != null && searchBlock != Blocks.AIR) {
                        this.dropPickupEntity();
                        this.leap(1.0D, 2.0D);
                        break;
                     }
                  }
               } else if (!(this.getPickupEntity() instanceof EntityCreeper)) {
                  this.dropPickupEntity();
               }
            }
         }
      }

      if (!this.getEntityWorld().isRemote && this.getControllingPassenger() == null && this.getPickupEntity() instanceof EntityCreeper) {
         ((EntityCreeper)this.getPickupEntity()).setAttackTarget((EntityLivingBase)null);
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

   public double getFlightOffset() {
      return this.hasPickupEntity() ? 5.0D : super.getFlightOffset();
   }

   public boolean rollWanderChance() {
      if (this.isFlying()) {
         return this.getRNG().nextDouble() <= 0.25D;
      } else {
         return this.getRNG().nextDouble() <= 0.008D;
      }
   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase && this.getControllingPassenger() == null) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)target;
            if (this.canPickupEntity(entityLivingBase)) {
               this.pickupEntity(entityLivingBase);
            }

            if (entityLivingBase instanceof EntityCreeper) {
               entityLivingBase.setRevengeTarget((EntityLivingBase)null);
               ((EntityCreeper)entityLivingBase).setAttackTarget((EntityLivingBase)null);
               this.setAttackTarget((EntityLivingBase)null);
            }
         }

         return true;
      }
   }

   public boolean canAttackEntity(EntityLivingBase targetEntity) {
      if (this.isTamed()) {
         return super.canAttackEntity(targetEntity);
      } else {
         ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(targetEntity);
         if (extendedEntity != null && extendedEntity.pickedUpByEntity != null) {
            return false;
         } else if (!this.creeperDropping && targetEntity instanceof EntityCreeper) {
            return false;
         } else if (this.hasPickupEntity() && targetEntity instanceof EntityCreeper) {
            return false;
         } else {
            return this.creeperDropCooldown > 0 ? false : super.canAttackEntity(targetEntity);
         }
      }
   }

   public boolean isAggressive() {
      if (this.isTamed()) {
         return super.isAggressive();
      } else {
         return "".equals(this.spawnEventType) && this.getEntityWorld().isDaytime() && this.testLightLevel() >= 2 ? false : super.isAggressive();
      }
   }

   public boolean isFlying() {
      return true;
   }

   public void pickupEntity(EntityLivingBase entity) {
      super.pickupEntity(entity);
      if (this.getEntityWorld().getBlockState(this.getPosition()) != null && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
         this.leap(0.5D, 4.0D);
      }

   }

   public double[] getPickupOffset(Entity entity) {
      return entity != null ? new double[]{0.0D, (double)(1.0F - entity.height), 0.0D} : new double[]{0.0D, 0.0D, 0.0D};
   }

   public boolean canPickupEntity(EntityLivingBase entity) {
      return this.creeperDropCooldown <= 0 && !this.hasPickupEntity() ? super.canPickupEntity(entity) : false;
   }

   public boolean canAttackWithPickup() {
      return this.getPickupEntity() instanceof EntityCreeper ? true : super.canAttackWithPickup();
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

   public BlockPos getWanderPosition(BlockPos wanderPosition) {
      return this.hasPickupEntity() && this.getPickupEntity() instanceof EntityPlayer ? new BlockPos(wanderPosition.getX(), this.restrictYHeightFromGround(wanderPosition, 6, 14), wanderPosition.getZ()) : super.getWanderPosition(wanderPosition);
   }

   public void mountAbility(Entity rider) {
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
               if (this.getPickupEntity() instanceof EntityCreeper) {
                  ((EntityCreeper)this.getPickupEntity()).ignite();
               }

               this.dropPickupEntity();
            } else if (this.getStamina() >= this.getStaminaCost()) {
               EntityLivingBase nearestTarget = (EntityLivingBase)this.getNearestEntity(EntityLivingBase.class, (Class)null, 4.0D, false);
               if (this.canPickupEntity(nearestTarget)) {
                  this.pickupEntity(nearestTarget);
               }

               this.applyStaminaCost();
            }
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
