package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.core.entity.ExtendedEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityQuetzodracl extends RideableCreatureEntity implements IMob, IGroupHeavy {
   protected AttackMeleeGoal attackAI;
   protected int waterTime = 0;
   protected boolean wantsToLand;
   protected boolean isLanded;

   public EntityQuetzodracl(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.flySoundSpeed = 20;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.attackAI = (new AttackMeleeGoal(this)).setLongMemory(false);
      this.tasks.addTask(this.nextCombatGoalIndex++, this.attackAI);
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

      if (!this.getEntityWorld().isRemote && this.getControllingPassenger() == null) {
         this.attackAI.setEnabled(!this.hasPickupEntity());
         if (!this.isInWater()) {
            this.waterTime = 0;
            if (this.hasPickupEntity()) {
               if (this.updateTick % (long)this.getMeleeCooldown() == 0L) {
                  this.attackMelee(this.getPickupEntity(), 1.0D);
               }

               ExtendedEntity extendedEntity = ExtendedEntity.getForEntity(this.getPickupEntity());
               if (extendedEntity != null) {
                  extendedEntity.setPickedUpByEntity(this);
               }

               if (this.ticksExisted % 100 == 0 && this.getRNG().nextBoolean()) {
                  this.dropPickupEntity();
               }
            }
         } else {
            ++this.waterTime;
            if (!this.hasPickupEntity() && this.getAir() > 40) {
               if (this.hasAttackTarget()) {
                  if (this.waterTime >= 320) {
                     this.waterTime = 80;
                     this.leap(0.5D, 2.0D);
                  }
               } else if (this.waterTime >= 160) {
                  this.waterTime = 80;
                  this.leap(0.5D, 2.0D);
               }
            } else if (this.waterTime >= 40) {
               this.waterTime = 0;
               this.leap(0.5D, 2.0D);
            }
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
         }

         return true;
      }
   }

   public boolean isFlying() {
      return !this.isLanded || this.hasPickupEntity();
   }

   public boolean isStrongSwimmer() {
      return false;
   }

   public void pickupEntity(EntityLivingBase entity) {
      super.pickupEntity(entity);
      if (this.getEntityWorld().getBlockState(this.getPosition()) != null && this.getEntityWorld().canBlockSeeSky(this.getPosition())) {
         this.leap(1.0D, 2.0D);
      }

   }

   public void dropPickupEntity() {
      if (this.hasPickupEntity() && ObjectManager.getEffect("weight") != null) {
         this.getPickupEntity().addPotionEffect(new PotionEffect(ObjectManager.getEffect("weight"), this.getEffectDuration(5), 1));
      }

      super.dropPickupEntity();
   }

   public double[] getPickupOffset(Entity entity) {
      return entity != null ? new double[]{0.0D, (double)(1.0F - entity.height), 0.0D} : new double[]{0.0D, 0.0D, 0.0D};
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
      if (!this.getEntityWorld().isRemote) {
         if (!this.abilityToggled) {
            if (this.hasPickupEntity()) {
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
