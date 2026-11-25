package com.lycanitesmobs.core.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LaserEndProjectileEntity extends BaseProjectileEntity {
   private double targetX;
   private double targetY;
   private double targetZ;
   public EntityLivingBase shootingEntity;
   public BaseProjectileEntity laserEntity;
   private double projectileSpeed;
   protected static final DataParameter<Float> POS_X = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> POS_Y = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> POS_Z = EntityDataManager.createKey(LaserEndProjectileEntity.class, DataSerializers.FLOAT);

   public LaserEndProjectileEntity(World world) {
      super(world);
      this.setStats();
   }

   public LaserEndProjectileEntity(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
      super(world, par2, par4, par6);
      this.laserEntity = laser;
      this.setStats();
   }

   public LaserEndProjectileEntity(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
      super(world, shooter);
      this.shootingEntity = shooter;
      this.laserEntity = laser;
      this.setStats();
   }

   public void setStats() {
      this.setSpeed(1.0D);
      if (this.laserEntity != null) {
         this.targetX = this.laserEntity.posX;
         this.targetY = this.laserEntity.posY;
         this.targetZ = this.laserEntity.posZ;
      }

      this.dataManager.register(POS_X, Float.valueOf((float)this.posX));
      this.dataManager.register(POS_Y, Float.valueOf((float)this.posY));
      this.dataManager.register(POS_Z, Float.valueOf((float)this.posZ));
      this.noClip = true;
   }

   public void onUpdate() {
      if (this.getEntityWorld().isRemote) {
         this.posX = (double)((Float)this.dataManager.get(POS_X)).floatValue();
         this.posY = (double)((Float)this.dataManager.get(POS_Y)).floatValue();
         this.posZ = (double)((Float)this.dataManager.get(POS_Z)).floatValue();
      } else {
         if ((this.laserEntity == null || !this.laserEntity.isEntityAlive()) && !this.isDead) {
            this.setDead();
         }

         if (this.isEntityAlive()) {
            this.moveToTarget();
         }

         this.dataManager.set(POS_X, Float.valueOf((float)this.posX));
         this.dataManager.set(POS_Y, Float.valueOf((float)this.posY));
         this.dataManager.set(POS_Z, Float.valueOf((float)this.posZ));
      }
   }

   public void onUpdateEnd(double newTargetX, double newTargetY, double newTargetZ) {
      if (!this.getEntityWorld().isRemote) {
         this.targetX = newTargetX;
         this.targetY = newTargetY;
         this.targetZ = newTargetZ;
         if (this.getLaunchSound() != null) {
            this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
         }

      }
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public void moveToTarget() {
      this.setPosition(this.targetX, this.targetY, this.targetZ);
   }

   public double moveCoordToTarget(double coord, double targetCoord, double originCoord) {
      double distance = targetCoord - coord;
      double moveSpeed = this.projectileSpeed;
      if (distance > 0.0D) {
         if (distance < moveSpeed + 1.0D) {
            moveSpeed = distance;
         }

         return targetCoord - originCoord > coord - originCoord ? coord + moveSpeed : targetCoord;
      } else if (distance < 0.0D) {
         if (distance > -moveSpeed - 1.0D) {
            moveSpeed = -distance;
         }

         return targetCoord - originCoord < coord - originCoord ? coord - moveSpeed : targetCoord;
      } else {
         return targetCoord;
      }
   }

   protected void onImpact(RayTraceResult rayTraceResult) {
   }

   public void setSpeed(double speed) {
      this.projectileSpeed = speed;
   }

   public ResourceLocation getTexture() {
      return null;
   }
}
