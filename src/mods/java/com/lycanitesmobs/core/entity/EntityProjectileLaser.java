package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.Utilities;
import java.lang.reflect.Constructor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityProjectileLaser extends BaseProjectileEntity {
   public EntityLivingBase shootingEntity;
   public Entity followEntity;
   public int shootingEntityRef;
   public int shootingEntityID;
   public float projectileWidth;
   public float projectileHeight;
   public LaserEndProjectileEntity laserEnd;
   public int laserEndRef;
   public int laserEndID;
   public int laserTime;
   public int laserDelay;
   public float laserRange;
   public float laserWidth;
   public float laserLength;
   public int laserTimeID;
   public boolean useEntityAttackTarget;
   private double targetX;
   private double targetY;
   private double targetZ;
   public double offsetX;
   public double offsetY;
   public double offsetZ;
   public int offsetIDStart;
   protected static final DataParameter<Integer> SHOOTING_ENTITY_ID = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.VARINT);
   protected static final DataParameter<Integer> LASER_END_ID = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.VARINT);
   protected static final DataParameter<Integer> LASER_TIME = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.VARINT);
   protected static final DataParameter<Float> OFFSET_X = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> OFFSET_Y = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> OFFSET_Z = EntityDataManager.createKey(EntityProjectileLaser.class, DataSerializers.FLOAT);

   public EntityProjectileLaser(World world) {
      super(world);
      this.shootingEntityRef = -1;
      this.shootingEntityID = 11;
      this.projectileWidth = 0.2F;
      this.projectileHeight = 0.2F;
      this.laserEndRef = -1;
      this.laserEndID = 12;
      this.laserTime = 100;
      this.laserDelay = 20;
      this.laserLength = 10.0F;
      this.laserTimeID = 13;
      this.useEntityAttackTarget = true;
      this.offsetX = 0.0D;
      this.offsetY = 0.0D;
      this.offsetZ = 0.0D;
      this.offsetIDStart = 14;
      this.setStats();
      this.setTime(0);
   }

   public EntityProjectileLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
      super(world, par2, par4, par6);
      this.shootingEntityRef = -1;
      this.shootingEntityID = 11;
      this.projectileWidth = 0.2F;
      this.projectileHeight = 0.2F;
      this.laserEndRef = -1;
      this.laserEndID = 12;
      this.laserTime = 100;
      this.laserDelay = 20;
      this.laserLength = 10.0F;
      this.laserTimeID = 13;
      this.useEntityAttackTarget = true;
      this.offsetX = 0.0D;
      this.offsetY = 0.0D;
      this.offsetZ = 0.0D;
      this.offsetIDStart = 14;
      this.laserTime = setTime;
      this.laserDelay = setDelay;
      this.setStats();
   }

   public EntityProjectileLaser(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
      this(world, par2, par4, par6, setTime, setDelay);
      this.followEntity = followEntity;
   }

   public EntityProjectileLaser(World world, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
      this(world, par2EntityLivingBase, setTime, setDelay, (Entity)null);
   }

   public EntityProjectileLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay, Entity followEntity) {
      super(world, entityLiving);
      this.shootingEntityRef = -1;
      this.shootingEntityID = 11;
      this.projectileWidth = 0.2F;
      this.projectileHeight = 0.2F;
      this.laserEndRef = -1;
      this.laserEndID = 12;
      this.laserTime = 100;
      this.laserDelay = 20;
      this.laserLength = 10.0F;
      this.laserTimeID = 13;
      this.useEntityAttackTarget = true;
      this.offsetX = 0.0D;
      this.offsetY = 0.0D;
      this.offsetZ = 0.0D;
      this.offsetIDStart = 14;
      this.shootingEntity = entityLiving;
      this.laserTime = setTime;
      this.laserDelay = setDelay;
      this.setStats();
      this.followEntity = followEntity;
      this.syncOffset();
   }

   public void setStats() {
      this.setSize(this.projectileWidth, this.projectileHeight);
      this.setRange(16.0F);
      this.setLaserWidth(1.0F);
      this.knockbackChance = 0.0D;
      this.targetX = this.posX;
      this.targetY = this.posY;
      this.targetZ = this.posZ;
      this.dataManager.register(SHOOTING_ENTITY_ID, Integer.valueOf(this.shootingEntityRef));
      this.dataManager.register(LASER_END_ID, Integer.valueOf(this.laserEndRef));
      this.dataManager.register(LASER_TIME, Integer.valueOf(this.laserTime));
      this.dataManager.register(OFFSET_X, Float.valueOf((float)this.offsetX));
      this.dataManager.register(OFFSET_Y, Float.valueOf((float)this.offsetY));
      this.dataManager.register(OFFSET_Z, Float.valueOf((float)this.offsetZ));
      this.noClip = true;
   }

   public AxisAlignedBB getRenderBoundingBox() {
      if (this.laserEnd == null) {
         return super.getRenderBoundingBox();
      } else {
         double distance = (double)this.getDistance(this.laserEnd);
         return super.getRenderBoundingBox().expand(distance, distance, distance);
      }
   }

   public void setOffset(double x, double y, double z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      this.syncOffset();
   }

   public void onUpdate() {
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(LASER_TIME, Integer.valueOf(this.laserTime));
      } else {
         this.laserTime = ((Integer)this.dataManager.get(LASER_TIME)).intValue();
      }

      this.syncShootingEntity();
      if (!this.getEntityWorld().isRemote && this.shootingEntity != null) {
         Entity entityToFollow = this.shootingEntity;
         if (this.followEntity != null) {
            entityToFollow = this.followEntity;
         }

         double xPos = entityToFollow.posX + this.offsetX;
         double yPos = entityToFollow.posY - (double)(this.height / 2.0F) + this.offsetY;
         double zPos = entityToFollow.posZ + this.offsetZ;
         if (entityToFollow instanceof BaseCreatureEntity) {
            BaseCreatureEntity creatureToFollow = (BaseCreatureEntity)entityToFollow;
            xPos = (double)creatureToFollow.getFacingPosition(creatureToFollow, this.offsetX, (double)(creatureToFollow.rotationYaw + 90.0F)).getX();
            zPos = (double)creatureToFollow.getFacingPosition(creatureToFollow, this.offsetZ, (double)creatureToFollow.rotationYaw).getZ();
         }

         this.setPosition(xPos, yPos, zPos);
      }

      if (this.laserTime > 0) {
         this.updateEnd();
         --this.laserTime;
         double minX = 0.0D;
         double maxX = 0.0D;
         double minY = 0.0D;
         double maxY = 0.0D;
         double minZ = 0.0D;
         double maxZ = 0.0D;
         if (this.laserEnd != null) {
            if (this.posX - (double)this.width < this.laserEnd.posX - (double)this.laserEnd.width) {
               minX = this.posX - (double)this.width;
            } else {
               minX = this.laserEnd.posX - (double)this.laserEnd.width;
            }

            if (this.posX + (double)this.width > this.laserEnd.posX + (double)this.laserEnd.width) {
               maxX = this.posX + (double)this.width;
            } else {
               maxX = this.laserEnd.posX + (double)this.laserEnd.width;
            }

            if (this.posY - (double)this.height < this.laserEnd.posY - (double)this.laserEnd.height) {
               minY = this.posY - (double)this.height;
            } else {
               minY = this.laserEnd.posY - (double)this.laserEnd.height;
            }

            if (this.posY + (double)this.width > this.laserEnd.posY + (double)this.laserEnd.height) {
               maxY = this.posY + (double)this.height;
            } else {
               maxY = this.laserEnd.posY + (double)this.laserEnd.height;
            }

            if (this.posZ - (double)this.width < this.laserEnd.posZ - (double)this.laserEnd.width) {
               minZ = this.posZ - (double)this.width;
            } else {
               minZ = this.laserEnd.posZ - (double)this.laserEnd.width;
            }

            if (this.posZ + (double)this.width > this.laserEnd.posZ + (double)this.laserEnd.width) {
               maxZ = this.posZ + (double)this.width;
            } else {
               maxZ = this.laserEnd.posZ + (double)this.laserEnd.width;
            }
         } else {
            minX = this.posX - (double)this.width;
            maxX = this.posX + (double)this.width;
            minY = this.posY - (double)this.height;
            maxY = this.posY + (double)this.height;
            minZ = this.posZ - (double)this.width;
            maxZ = this.posZ + (double)this.width;
         }

         this.getEntityBoundingBox().expand(maxX - minX - (this.getEntityBoundingBox().maxX - this.getEntityBoundingBox().minX), maxY - minY - (this.getEntityBoundingBox().maxY - this.getEntityBoundingBox().minY), maxZ - minZ - (this.getEntityBoundingBox().maxZ - this.getEntityBoundingBox().minZ));
      } else if (!this.isDead) {
         this.setDead();
      }

   }

   public void updateEnd() {
      if (this.getEntityWorld().isRemote) {
         this.laserEndRef = ((Integer)this.dataManager.get(LASER_END_ID)).intValue();
         Entity possibleLaserEnd = null;
         if (this.laserEndRef != -1) {
            possibleLaserEnd = this.getEntityWorld().getEntityByID(this.laserEndRef);
         }

         if (possibleLaserEnd == null || !(possibleLaserEnd instanceof LaserEndProjectileEntity)) {
            this.laserEnd = null;
            return;
         }

         this.laserEnd = (LaserEndProjectileEntity)possibleLaserEnd;
      }

      if (this.laserEnd == null) {
         this.fireProjectile();
      }

      if (this.laserEnd == null) {
         this.laserEndRef = -1;
      } else {
         if (!this.getEntityWorld().isRemote) {
            this.laserEndRef = this.laserEnd.getEntityId();
         }

         boolean lockedLaser = false;
         if (this.shootingEntity != null && this.useEntityAttackTarget) {
            if (this.shootingEntity instanceof BaseCreatureEntity && ((BaseCreatureEntity)this.shootingEntity).getAttackTarget() != null) {
               EntityLivingBase attackTarget = ((BaseCreatureEntity)this.shootingEntity).getAttackTarget();
               this.targetX = attackTarget.posX;
               this.targetY = attackTarget.posY + (double)(attackTarget.height / 2.0F);
               this.targetZ = attackTarget.posZ;
               lockedLaser = true;
            } else {
               Vec3d lookDirection = this.shootingEntity.getLookVec();
               this.targetX = this.shootingEntity.posX + lookDirection.x * (double)this.laserRange;
               this.targetY = this.shootingEntity.posY + (double)this.shootingEntity.getEyeHeight() + lookDirection.y * (double)this.laserRange;
               this.targetZ = this.shootingEntity.posZ + lookDirection.z * (double)this.laserRange;
            }
         }

         RayTraceResult target = Utilities.raytrace(this.getEntityWorld(), this.getPositionVector(), new Vec3d(this.targetX, this.targetY, this.targetZ), false, true, this.laserWidth, Utilities.collidableExlcuding(this, this.shootingEntity, this.followEntity));
         double newTargetX = this.targetX;
         double newTargetY = this.targetY;
         double newTargetZ = this.targetZ;
         if (target != null && target.hitVec != null && !lockedLaser) {
            newTargetX = target.hitVec.x;
            newTargetY = target.hitVec.y;
            newTargetZ = target.hitVec.z;
         }

         this.laserEnd.onUpdateEnd(newTargetX, newTargetY, newTargetZ);
         if (this.laserTime % this.laserDelay == 0 && this.isEntityAlive() && target != null && target.entityHit != null && this.laserEnd.getDistance(target.entityHit) <= this.laserWidth * 10.0F) {
            boolean doDamage = true;
            if (target.entityHit instanceof EntityLivingBase) {
               doDamage = this.canDamage((EntityLivingBase)target.entityHit);
            }

            if (doDamage) {
               this.updateDamage(target.entityHit);
            }
         }
      }

      this.dataManager.set(LASER_END_ID, Integer.valueOf(this.laserEndRef));
      if (this.getBeamSound() != null) {
         this.playSound(this.getBeamSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
      }

   }

   public void setTime(int time) {
      this.laserTime = time;
   }

   public int getTime() {
      return this.laserTime;
   }

   public void fireProjectile() {
      World world = this.getEntityWorld();
      if (!world.isRemote) {
         try {
            if (this.shootingEntity == null) {
               Constructor constructor = this.getLaserEndClass().getDeclaredConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE, EntityProjectileLaser.class);
               constructor.setAccessible(true);
               this.laserEnd = (LaserEndProjectileEntity)constructor.newInstance(world, this.posX, this.posY, this.posZ, this);
            } else {
               Constructor constructor = this.getLaserEndClass().getDeclaredConstructor(World.class, EntityLivingBase.class, EntityProjectileLaser.class);
               constructor.setAccessible(true);
               this.laserEnd = (LaserEndProjectileEntity)constructor.newInstance(world, this.shootingEntity, this);
            }

            if (this.getLaunchSound() != null) {
               this.playSound(this.getLaunchSound(), 1.0F, 1.0F / (this.rand.nextFloat() * 0.4F + 0.8F));
            }

            world.spawnEntity(this.laserEnd);
         } catch (Exception var3) {
            System.out.println("[WARNING] [LycanitesMobs] EntityLaser was unable to instantiate the EntityLaserEnd.");
            var3.printStackTrace();
         }

      }
   }

   public void syncShootingEntity() {
      if (!this.getEntityWorld().isRemote) {
         if (this.shootingEntity == null) {
            this.shootingEntityRef = -1;
         } else {
            this.shootingEntityRef = this.shootingEntity.getEntityId();
         }

         this.dataManager.set(SHOOTING_ENTITY_ID, Integer.valueOf(this.shootingEntityRef));
      } else {
         this.shootingEntityRef = ((Integer)this.dataManager.get(SHOOTING_ENTITY_ID)).intValue();
         if (this.shootingEntityRef == -1) {
            this.shootingEntity = null;
         } else {
            Entity possibleShootingEntity = this.getEntityWorld().getEntityByID(this.shootingEntityRef);
            if (possibleShootingEntity != null && possibleShootingEntity instanceof EntityLivingBase) {
               this.shootingEntity = (EntityLivingBase)possibleShootingEntity;
            } else {
               this.shootingEntity = null;
            }
         }
      }

   }

   public void syncOffset() {
      if (!this.getEntityWorld().isRemote) {
         this.dataManager.set(OFFSET_X, Float.valueOf((float)this.offsetX));
         this.dataManager.set(OFFSET_Y, Float.valueOf((float)this.offsetY));
         this.dataManager.set(OFFSET_Z, Float.valueOf((float)this.offsetZ));
      } else {
         this.offsetX = (double)((Float)this.dataManager.get(OFFSET_X)).floatValue();
         this.offsetY = (double)((Float)this.dataManager.get(OFFSET_Y)).floatValue();
         this.offsetZ = (double)((Float)this.dataManager.get(OFFSET_Z)).floatValue();
      }

   }

   public LaserEndProjectileEntity getLaserEnd() {
      return this.laserEnd;
   }

   public Class getLaserEndClass() {
      return LaserEndProjectileEntity.class;
   }

   public void setTarget(double x, double y, double z) {
      this.targetX = x;
      this.targetY = y;
      this.targetZ = z;
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   protected void onImpact(RayTraceResult rayTraceResult) {
   }

   public boolean updateDamage(Entity target) {
      boolean attackSuccess = false;
      float damage = this.getDamage(target);
      double targetKnockbackResistance = 0.0D;
      if (this.knockbackChance < 1.0D && (this.knockbackChance <= 0.0D || this.rand.nextDouble() <= this.knockbackChance) && target instanceof EntityLivingBase) {
         targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
         ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      }

      if (this.getThrower() instanceof BaseCreatureEntity) {
         BaseCreatureEntity creatureThrower = (BaseCreatureEntity)this.getThrower();
         attackSuccess = creatureThrower.doRangedDamage(target, this, damage, this.isBlockedByEntity(target, this.getPositionVector()));
      } else {
         double pierceDamage = 1.0D;
         if ((double)damage <= pierceDamage) {
            attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), damage);
         } else {
            int hurtResistantTimeBefore = target.hurtResistantTime;
            target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()).setDamageBypassesArmor().setDamageIsAbsolute(), (float)pierceDamage);
            target.hurtResistantTime = hurtResistantTimeBefore;
            damage = (float)((double)damage - pierceDamage);
            attackSuccess = target.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), damage);
         }
      }

      if (target instanceof EntityLivingBase) {
         this.onDamage((EntityLivingBase)target, damage, attackSuccess);
      }

      if (this.knockbackChance < 1.0D && (this.knockbackChance <= 0.0D || this.rand.nextDouble() <= this.knockbackChance) && target instanceof EntityLivingBase) {
         ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
      }

      return attackSuccess;
   }

   public void setRange(float range) {
      this.laserRange = range;
   }

   public void setLaserWidth(float width) {
      this.laserWidth = width;
   }

   public float getLaserWidth() {
      return this.laserWidth;
   }

   public float getLaserAlpha() {
      return 0.25F + (float)(0.10000000149011612D * Math.sin((double)this.ticksExisted));
   }

   public ResourceLocation getBeamTexture() {
      return null;
   }

   public double[] getLengths() {
      return this.laserEnd == null ? new double[]{0.0D, 0.0D, 0.0D} : new double[]{this.laserEnd.posX - this.posX, this.laserEnd.posY - this.posY, this.laserEnd.posZ - this.posZ};
   }

   public float getLength() {
      return this.laserEnd == null ? 0.0F : this.getDistance(this.laserEnd);
   }

   public float[] getBeamAngles() {
      float[] angles = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
      if (this.laserEnd != null) {
         float dx = (float)(this.laserEnd.posX - this.posX);
         float dy = (float)(this.laserEnd.posY - this.posY);
         float dz = (float)(this.laserEnd.posZ - this.posZ);
         angles[0] = (float)Math.toDegrees(Math.atan2((double)dz, (double)dy)) - 90.0F;
         angles[1] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dz));
         angles[2] = (float)Math.toDegrees(Math.atan2((double)dx, (double)dy)) - 90.0F;
         float dr = (float)Math.sqrt((double)(dx * dx + dz * dz));
         angles[3] = (float)Math.toDegrees(Math.atan2((double)dr, (double)dy)) - 90.0F;
      }

      return angles;
   }

   public SoundEvent getLaunchSound() {
      return null;
   }

   public SoundEvent getBeamSound() {
      return null;
   }

   public void readEntityFromNBT(NBTTagCompound nbtTagCompound) {
      if (nbtTagCompound.hasKey("LaserTime")) {
         this.setTime(nbtTagCompound.getInteger("LaserTime"));
      }

      if (nbtTagCompound.hasKey("OffsetX")) {
         this.offsetX = nbtTagCompound.getDouble("OffsetX");
      }

      if (nbtTagCompound.hasKey("OffsetY")) {
         this.offsetY = nbtTagCompound.getDouble("OffsetY");
      }

      if (nbtTagCompound.hasKey("OffsetZ")) {
         this.offsetZ = nbtTagCompound.getDouble("OffsetZ");
      }

      super.readEntityFromNBT(nbtTagCompound);
   }

   public void writeEntityToNBT(NBTTagCompound nbtTagCompound) {
      nbtTagCompound.setInteger("LaserTime", this.laserTime);
      nbtTagCompound.setDouble("OffsetX", this.offsetX);
      nbtTagCompound.setDouble("OffsetY", this.offsetY);
      nbtTagCompound.setDouble("OffsetZ", this.offsetZ);
      super.writeEntityToNBT(nbtTagCompound);
   }
}
