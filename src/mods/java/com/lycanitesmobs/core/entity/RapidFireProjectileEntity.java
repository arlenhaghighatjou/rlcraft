package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.core.LycanitesClasses;
import com.lycanitesmobs.core.info.projectile.ProjectileInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RapidFireProjectileEntity extends BaseProjectileEntity {
   public EntityLivingBase shootingEntity;
   private float projectileWidth = 0.2F;
   private float projectileHeight = 0.2F;
   private Class projectileClass;
   private ProjectileInfo projectileInfo;
   private int rapidTime = 100;
   private int rapidDelay = 5;
   public double offsetX = 0.0D;
   public double offsetY = 0.0D;
   public double offsetZ = 0.0D;

   public RapidFireProjectileEntity(Class entityClass, World world, int setTime, int setDelay) {
      super(world);
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.projectileClass = entityClass;
      this.rapidTime = setTime;
      this.rapidDelay = setDelay;
      this.noClip = true;
   }

   public RapidFireProjectileEntity(Class entityClass, World world, double par2, double par4, double par6, int setTime, int setDelay) {
      super(world, par2, par4, par6);
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.projectileClass = entityClass;
      this.rapidTime = setTime;
      this.rapidDelay = setDelay;
      this.noClip = true;
   }

   public RapidFireProjectileEntity(Class entityClass, World world, EntityLivingBase entityLivingBase, int setTime, int setDelay) {
      super(world, entityLivingBase);
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.projectileClass = entityClass;
      this.shootingEntity = entityLivingBase;
      this.offsetX = this.posX - entityLivingBase.posX;
      this.offsetY = this.posY - entityLivingBase.posY;
      this.offsetZ = this.posZ - entityLivingBase.posZ;
      this.rapidTime = setTime;
      this.rapidDelay = setDelay;
      this.noClip = true;
   }

   public RapidFireProjectileEntity(ProjectileInfo projectileInfo, World world, double par2, double par4, double par6, int setTime, int setDelay) {
      super(world, par2, par4, par6);
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.projectileInfo = projectileInfo;
      this.rapidTime = setTime;
      this.rapidDelay = setDelay;
      this.noClip = true;
   }

   public RapidFireProjectileEntity(ProjectileInfo projectileInfo, World world, EntityLivingBase entityLivingBase, int setTime, int setDelay) {
      super(world, entityLivingBase);
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.projectileInfo = projectileInfo;
      this.shootingEntity = entityLivingBase;
      this.offsetX = this.posX - entityLivingBase.posX;
      this.offsetY = this.posY - entityLivingBase.posY;
      this.offsetZ = this.posZ - entityLivingBase.posZ;
      this.rapidTime = setTime;
      this.rapidDelay = setDelay;
      this.noClip = true;
   }

   public void onUpdate() {
      if (this.shootingEntity != null) {
         this.posX = this.shootingEntity.posX + this.offsetX;
         this.posY = this.shootingEntity.posY + this.offsetY;
         this.posZ = this.shootingEntity.posZ + this.offsetZ;
      }

      if (this.rapidTime > 0) {
         if (this.projectileClass == null && this.projectileInfo == null) {
            this.rapidTime = 0;
            return;
         }

         if (this.rapidTime % this.rapidDelay == 0) {
            this.fireProjectile();
         }

         --this.rapidTime;
      } else if (!this.isDead) {
         this.setDead();
      }

   }

   public void addTime(int addTime) {
      this.rapidTime += addTime;
   }

   public void fireProjectile() {
      World world = this.getEntityWorld();
      if (!world.isRemote) {
         try {
            IProjectile projectile;
            if (this.shootingEntity == null) {
               if (this.projectileInfo != null) {
                  projectile = this.projectileInfo.createProjectile(this.getEntityWorld(), this.posX, this.posY, this.posZ);
                  projectile.shoot(this.motionX, this.motionY, this.motionZ, (float)this.projectileInfo.velocity, 0.0F);
               } else {
                  projectile = (IProjectile)LycanitesClasses.create(this.projectileClass, world, this.posX, this.posY, this.posZ);
                  projectile.shoot(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
               }
            } else {
               if (this.projectileInfo != null) {
                  projectile = this.projectileInfo.createProjectile(this.getEntityWorld(), this.shootingEntity);
                  projectile.shoot(this.motionX, this.motionY, this.motionZ, (float)this.projectileInfo.velocity, 0.0F);
               } else {
                  projectile = (IProjectile)LycanitesClasses.create(this.projectileClass, world, this.shootingEntity);
                  projectile.shoot(this.motionX, this.motionY, this.motionZ, 1.0F, 1.0F);
               }

               if (projectile instanceof EntityThrowable) {
                  EntityThrowable entityThrowable = (EntityThrowable)projectile;
                  entityThrowable.setPosition(this.shootingEntity.posX + this.offsetX, this.shootingEntity.posY + this.offsetY, this.shootingEntity.posZ + this.offsetZ);
               }
            }

            if (projectile instanceof BaseProjectileEntity) {
               ((BaseProjectileEntity)projectile).setProjectileScale(this.projectileScale);
            }

            world.spawnEntity((Entity)projectile);
         } catch (Exception var4) {
            System.out.println("[WARNING] [LycanitesMobs] EntityRapidFire was unable to instantiate the given projectile class.");
            var4.printStackTrace();
         }

      }
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public void setPosition(double x, double y, double z) {
      super.setPosition(x, y, z);
      if (this.shootingEntity != null) {
         this.offsetX = x - this.shootingEntity.posX;
         this.offsetY = y - this.shootingEntity.posY;
         this.offsetZ = z - this.shootingEntity.posZ;
      }

   }

   protected void onImpact(RayTraceResult rayTraceResult) {
   }

   public ResourceLocation getTexture() {
      return null;
   }
}
