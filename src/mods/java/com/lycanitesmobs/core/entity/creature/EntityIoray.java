package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.RideableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.entity.goals.actions.WanderGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityIoray extends RideableCreatureEntity implements IMob {
   WanderGoal wanderAI;
   BaseProjectileEntity projectile = null;
   BaseProjectileEntity abilityProjectile = null;

   public EntityIoray(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsOnLand = false;
      this.spawnsInWater = true;
      this.hasAttackSound = true;
      this.babySpawnChance = 0.0D;
      this.canGrow = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false).setMaxChaseDistanceSq(4.0F));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setStaminaTime(100).setRange(8.0F).setMinChaseDistance(4.0F).setMountedAttacking(false));
   }

   public void riderEffects(EntityLivingBase rider) {
      rider.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 105, 1));
      super.riderEffects(rider);
   }

   public float getBlockPathWeight(int x, int y, int z) {
      int waterWeight = 10;
      Block block = this.getEntityWorld().getBlockState(new BlockPos(x, y, z)).getBlock();
      if (block == Blocks.WATER) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getEntityWorld().isRaining() && this.getEntityWorld().canBlockSeeSky(new BlockPos(x, y, z))) {
         return (super.getBlockPathWeight(x, y, z) + 1.0F) * (float)(waterWeight + 1);
      } else if (this.getAttackTarget() != null) {
         return super.getBlockPathWeight(x, y, z);
      } else {
         return this.waterContact() ? -999999.0F : super.getBlockPathWeight(x, y, z);
      }
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean canWalk() {
      return false;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.6D;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBreatheAir() {
      return false;
   }

   public boolean canBurn() {
      return false;
   }

   public void attackRanged(Entity target, float range) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("waterjet");
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
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile("waterjet");
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

   public void onDismounted(Entity entity) {
      super.onDismounted(entity);
      if (entity != null && entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 100, 1));
      }

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
