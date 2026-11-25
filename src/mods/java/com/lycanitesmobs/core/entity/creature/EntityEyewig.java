package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.world.World;

public class EntityEyewig extends RideableCreatureEntity {
   BaseProjectileEntity projectile = null;
   BaseProjectileEntity abilityProjectile = null;

   public EntityEyewig(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.ARTHROPOD;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false));
   }

   public boolean isPushedByWater() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("poisonray");
      if (projectileInfo != null) {
         if (this.projectile != null && this.projectile.isEntityAlive()) {
            this.projectile.projectileLife = 20;
         } else {
            this.projectile = null;
         }

         if (this.projectile == null) {
            this.projectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
            this.playSound(this.projectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            this.getEntityWorld().spawnEntity(this.projectile);
         }

         super.attackRanged(target, range);
      }
   }

   public void mountAbility(Entity rider) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("poisonray");
      if (projectileInfo != null) {
         if (!this.getEntityWorld().isRemote) {
            if (this.getStamina() >= this.getStaminaRecoveryMax() * 2.0F) {
               if (this.hasAttackTarget()) {
                  this.setAttackTarget((EntityLivingBase)null);
               }

               if (this.abilityProjectile != null && this.abilityProjectile.isEntityAlive()) {
                  this.abilityProjectile.projectileLife = 20;
               } else {
                  this.abilityProjectile = null;
               }

               if (this.abilityProjectile == null) {
                  if (this.getControllingPassenger() == null || !(this.getControllingPassenger() instanceof EntityLivingBase)) {
                     return;
                  }

                  this.abilityProjectile = projectileInfo.createProjectile(this.getEntityWorld(), this);
                  this.playSound(this.abilityProjectile.getLaunchSound(), 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                  this.getEntityWorld().spawnEntity(this.abilityProjectile);
               }

               this.applyStaminaCost();
            }
         }
      }
   }

   public boolean isAggressive() {
      if (this.isTamed()) {
         return super.isAggressive();
      } else if (this.getEntityWorld() != null && this.getEntityWorld().isDaytime()) {
         return this.testLightLevel() < 2;
      } else {
         return super.isAggressive();
      }
   }

   public boolean canClimb() {
      return true;
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public float getFallResistance() {
      return 10.0F;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }
}
