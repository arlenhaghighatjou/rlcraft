package com.lycanitesmobs.core.entity.goals.actions.abilities;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.goals.BaseGoal;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireProjectilesGoal extends BaseGoal {
   protected String projectileName;
   protected Class<? extends BaseProjectileEntity> projectileClass;
   protected float velocity = 1.6F;
   protected float inaccuracy = 0.0F;
   protected float scale = 1.0F;
   protected float angle = 0.0F;
   protected Vec3d offset = Vec3d.ZERO;
   private int fireRate = 60;
   private boolean allPlayers = false;
   private int randomCount = 0;
   protected int phase = -1;
   protected boolean overhead = false;
   private int abilityTime = 60;
   private Entity attackTarget;

   public FireProjectilesGoal(BaseCreatureEntity setHost) {
      super(setHost);
   }

   public FireProjectilesGoal setPhase(int phase) {
      this.phase = phase;
      return this;
   }

   public FireProjectilesGoal setProjectile(String projectileName) {
      this.projectileName = projectileName;
      return this;
   }

   public FireProjectilesGoal setProjectile(Class<? extends BaseProjectileEntity> projectileClass) {
      this.projectileClass = projectileClass;
      return this;
   }

   public FireProjectilesGoal setFireRate(int fireRate) {
      this.fireRate = fireRate;
      return this;
   }

   public FireProjectilesGoal setVelocity(float velocity) {
      this.velocity = velocity;
      return this;
   }

   public FireProjectilesGoal setScale(float scale) {
      this.scale = scale;
      return this;
   }

   public FireProjectilesGoal setInaccuracy(int inaccuracy) {
      this.inaccuracy = (float)inaccuracy;
      return this;
   }

   public FireProjectilesGoal setAngle(int angle) {
      this.angle = (float)angle;
      return this;
   }

   public FireProjectilesGoal setOffset(Vec3d offset) {
      this.offset = offset;
      return this;
   }

   public FireProjectilesGoal setOverhead(boolean overhead) {
      this.overhead = overhead;
      return this;
   }

   public FireProjectilesGoal setAllPlayers(boolean allPlayers) {
      this.allPlayers = allPlayers;
      return this;
   }

   public FireProjectilesGoal setRandomCount(int randomCount) {
      this.randomCount = randomCount;
      return this;
   }

   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else if (this.projectileName == null && this.projectileClass == null) {
         return false;
      } else {
         this.attackTarget = this.host.getAttackTarget();
         if (!this.allPlayers && this.randomCount <= 0 && this.attackTarget == null) {
            return false;
         } else {
            return this.phase < 0 || this.phase == this.host.getBattlePhase();
         }
      }
   }

   public void startExecuting() {
      this.abilityTime = 1;
   }

   public void resetTask() {
      this.attackTarget = null;
   }

   public void updateTask() {
      if (this.abilityTime++ % Math.round((float)(1.0D / this.host.getEntityAttribute(BaseCreatureEntity.RANGED_SPEED).getAttributeValue() * (double)this.fireRate)) == 0) {
         if (this.allPlayers) {
            for(EntityPlayer target : this.host.playerTargets) {
               if (!target.capabilities.disableDamage && !target.isSpectator()) {
                  this.fireProjectile(target);
               }
            }

         } else if (this.randomCount > 0) {
            for(int i = 0; i < this.randomCount; ++i) {
               if (this.projectileName != null) {
                  this.host.fireProjectile(this.projectileName, (Entity)null, this.host.getRNG().nextFloat() * 10.0F, this.host.getRNG().nextFloat() * this.angle, this.offset, this.velocity, this.scale, this.inaccuracy);
               } else if (this.projectileClass != null) {
                  this.host.fireProjectile(this.projectileClass, (Entity)null, this.host.getRNG().nextFloat() * 10.0F, this.host.getRNG().nextFloat() * this.angle, this.offset, this.velocity, this.scale, this.inaccuracy);
               }
            }

         } else {
            this.fireProjectile(this.attackTarget);
         }
      }
   }

   public BaseProjectileEntity fireProjectile(Entity target) {
      BaseProjectileEntity projectile = this.createProjectile();
      if (this.overhead) {
         this.fireProjectileOverhead(projectile);
      } else {
         this.host.fireProjectile(projectile, target, this.host.getDistance(target), this.angle, this.offset, this.velocity, this.scale, this.inaccuracy);
      }

      return projectile;
   }

   public BaseProjectileEntity createProjectile() {
      BaseProjectileEntity projectile = null;
      if (this.projectileName != null) {
         ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
         if (projectileInfo == null) {
            return projectile;
         }

         projectile = projectileInfo.createProjectile(this.host.getEntityWorld(), this.host);
      } else if (this.projectileClass != null) {
         try {
            projectile = (BaseProjectileEntity)this.projectileClass.getConstructor(World.class, EntityLivingBase.class).newInstance(this.host.getEntityWorld(), this.host);
         } catch (Exception var3) {
            LycanitesMobs.logWarning("", "Unable to create a projectile from the class: " + this.projectileClass);
         }
      }

      if (projectile != null) {
         projectile.setProjectileScale(this.scale);
      }

      return projectile;
   }

   public void fireProjectileOverhead(BaseProjectileEntity projectile) {
      Entity target = (Entity)(this.attackTarget != null ? this.attackTarget : this.host);
      projectile.shoot(0.0D, -1.0D, 0.0D, 0.5F, this.inaccuracy);
      projectile.setPosition((double)target.getPosition().getX(), (double)target.getPosition().getY() + this.offset.y + this.host.getRNG().nextDouble() * 3.0D, (double)target.getPosition().getZ());
      this.host.getEntityWorld().spawnEntity(projectile);
   }
}
