package com.lycanitesmobs.core.info.projectile.behaviours;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.BaseProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProjectileBehaviourFireProjectiles extends ProjectileBehaviour {
   public String projectileName;
   public int tickRate = 10;
   public int persistentCount = 0;
   public int impactCount = 5;
   public float velocity = 1.2F;

   public ProjectileBehaviourFireProjectiles() {
   }

   public void loadFromJSON(JsonObject json) {
      this.projectileName = json.get("projectileName").getAsString();
      if (json.has("tickRate")) {
         this.tickRate = json.get("tickRate").getAsInt();
      }

      if (json.has("impactCount")) {
         this.impactCount = json.get("impactCount").getAsInt();
      }

      if (json.has("persistentCount")) {
         this.persistentCount = json.get("persistentCount").getAsInt();
      }

      if (json.has("velocity")) {
         this.velocity = json.get("velocity").getAsFloat();
      }

   }

   public void onProjectileUpdate(BaseProjectileEntity projectile) {
      if (this.tickRate >= 0 && projectile.updateTick % (long)this.tickRate == 0L && !projectile.getEntityWorld().isRemote) {
         if (this.persistentCount <= 0 || ((CustomProjectileEntity)projectile).spawnedProjectiles.size() < this.persistentCount) {
            this.createProjectile(projectile);
         }

      }
   }

   public void onProjectileImpact(BaseProjectileEntity projectile, World world, BlockPos pos) {
      if (!projectile.getEntityWorld().isRemote) {
         for(int i = 0; i < this.impactCount; ++i) {
            this.createProjectile(projectile);
         }

         for(BaseProjectileEntity spawnedProjectile : ((CustomProjectileEntity)projectile).spawnedProjectiles) {
            spawnedProjectile.setDead();
         }

      }
   }

   public BaseProjectileEntity createProjectile(BaseProjectileEntity projectile) {
      ProjectileInfo projectileInfo = ProjectileManager.getInstance().getProjectile(this.projectileName);
      if (projectileInfo == null) {
         return null;
      } else {
         BaseProjectileEntity childProjectile;
         if (projectile.getThrower() != null) {
            childProjectile = projectileInfo.createProjectile(projectile.getEntityWorld(), projectile.getThrower());
            childProjectile.setPosition(projectile.getPositionVector().x, projectile.getPositionVector().y, projectile.getPositionVector().z);
         } else {
            childProjectile = projectileInfo.createProjectile(projectile.getEntityWorld(), projectile.getPositionVector().x, projectile.getPositionVector().y, projectile.getPositionVector().z);
         }

         if (childProjectile instanceof CustomProjectileEntity) {
            ((CustomProjectileEntity)childProjectile).setParent(projectile);
         }

         if (this.persistentCount > 0 && childProjectile instanceof CustomProjectileEntity) {
            ((CustomProjectileEntity)childProjectile).laserAngle = 360.0F / (float)this.persistentCount * (float)((CustomProjectileEntity)projectile).spawnedProjectiles.size();
            ((CustomProjectileEntity)projectile).spawnedProjectiles.add(childProjectile);
         }

         double motionT = projectile.motionX + projectile.motionY + projectile.motionZ;
         if (projectile.motionX < 0.0D) {
            motionT -= projectile.motionX * 2.0D;
         }

         if (projectile.motionY < 0.0D) {
            motionT -= projectile.motionY * 2.0D;
         }

         if (projectile.motionZ < 0.0D) {
            motionT -= projectile.motionZ * 2.0D;
         }

         childProjectile.shoot(projectile.motionX / motionT + (projectile.getEntityWorld().rand.nextGaussian() - 0.5D), projectile.motionY / motionT + (projectile.getEntityWorld().rand.nextGaussian() - 0.5D), projectile.motionZ / motionT + (projectile.getEntityWorld().rand.nextGaussian() - 0.5D), this.velocity, 0.0F);
         projectile.playSound(childProjectile.getLaunchSound(), 1.0F, 1.0F / (projectile.getEntityWorld().rand.nextFloat() * 0.4F + 0.8F));
         projectile.getEntityWorld().spawnEntity(childProjectile);
         return childProjectile;
      }
   }
}
