package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileElviaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityElvia extends EntityPPreeminent implements EntityCanShoot {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityElvia.class, DataSerializers.BYTE);
   private int count;

   public EntityElvia(World worldIn) {
      super(worldIn);
      this.setSize(4.0F, 4.0F);
      this.moveHelper = new EntityElvia.AIMoveControl(this);
      this.setNoGravity(true);
      this.tasks.removeTask(this.aiWander);
      this.adaptationCap = 0.95F;
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 85;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.preeminentFollow, true, 3));
      this.tasks.addTask(5, new EntityAIAttackProjectile(this, 20, 10, 4, true));
      this.tasks.addTask(4, new EntityElvia.AIChargeAttack());
      this.tasks.addTask(6, new EntityElvia.AIMoveRandom());
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 10, false));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, 30, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ELVIA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ELVIA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.preeminentFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

      if (!this.world.isRemote && this.getAttackTarget() != null && this.ticksExisted % 10 == 0) {
         for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D))) {
            if (entitylivingbase != this && !(entitylivingbase instanceof EntityParasiteBase)) {
               float f = (float)MathHelper.atan2(entitylivingbase.posZ - this.posZ, entitylivingbase.posX - this.posX);
               EntityDamage damage = new EntityDamage(this.world, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, this, 1.0F, false, 2.5F);
               this.world.spawnEntity(damage);
            }
         }
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   protected void updateAITasks() {
      super.updateAITasks();
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
   }

   public int getHorizontalFaceSpeed() {
      return 3;
   }

   public float getEyeHeight() {
      return 2.1F;
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ELVIA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ELVIA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ELVIA_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.elviaOrbEffects, mobs);
      }

      return flag;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public static void registerFixesOronco(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityElvia.class);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean isOnLadder() {
      return false;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesButhol(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityElvia.class);
   }

   private boolean getVexFlag(int mask) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      return (i & mask) != 0;
   }

   private void setVexFlag(int mask, boolean value) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(VEX_FLAGS, Byte.valueOf((byte)(i & 255)));
   }

   public boolean isCharging() {
      return this.getVexFlag(1);
   }

   public void setCharging(boolean charging) {
      this.setVexFlag(1, charging);
   }

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      if (this.count >= 1) {
         this.count = 0;
         return new EntityProjectileNade(this.world, this, accelX, accelY, accelZ, 4, 60);
      } else {
         return new EntityProjectileElviaBall(this.world, this, accelX, accelY, accelZ);
      }
   }

   public void playProjSound() {
      ++this.count;
   }

   class AIBomb extends EntityAIBase {
      private final EntityLiving parent;
      private int ccc;

      public AIBomb(EntityLiving parentIn) {
         this.parent = parentIn;
         this.ccc = 0;
      }

      public boolean shouldExecute() {
         ++this.ccc;
         if (this.ccc >= 100) {
            this.ccc = 0;
            if (this.parent.getAttackTarget() != null) {
               EntityLivingBase target = this.parent.getAttackTarget();
               if (target.getDistanceSq(this.parent.posX, target.posY, this.parent.posZ) < 256.0D) {
                  return true;
               }
            }
         }

         return false;
      }

      public void updateTask() {
      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityElvia.this.getAttackTarget() != null && EntityElvia.this.rand.nextInt(5) == 0) {
            return EntityElvia.this.getDistanceSq(EntityElvia.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityElvia.this.getMoveHelper().isUpdating() && EntityElvia.this.isCharging() && EntityElvia.this.getAttackTarget() != null && EntityElvia.this.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityElvia.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
         EntityElvia.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 15.0D, vec3d.z, 0.7D);
         EntityElvia.this.setCharging(true);
      }

      public void resetTask() {
         EntityElvia.this.setCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityElvia.this.getAttackTarget();
         if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
            if (EntityElvia.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
               EntityElvia.this.attackEntityAsMob(entitylivingbase);
               EntityElvia.this.setCharging(false);
            } else {
               double d0 = EntityElvia.this.getDistanceSq(entitylivingbase);
               if (d0 < 9.0D) {
                  if (EntityElvia.this.canEntityBeSeen(entitylivingbase)) {
                     Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                     EntityElvia.this.moveHelper.setMoveTo(vec3d.x, vec3d.y + 20.0D, vec3d.z, 0.7D);
                  } else {
                     Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                     EntityElvia.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.1D);
                  }
               }
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityElvia vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityElvia.this.posX;
            double d1 = this.posY - EntityElvia.this.posY;
            double d2 = this.posZ - EntityElvia.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityElvia.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityElvia.this.motionX *= 0.5D;
               EntityElvia.this.motionY *= 0.5D;
               EntityElvia.this.motionZ *= 0.5D;
            } else {
               EntityElvia.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityElvia.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityElvia.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityElvia.this.getAttackTarget() == null) {
                  EntityElvia.this.rotationYaw = -((float)MathHelper.atan2(EntityElvia.this.motionX, EntityElvia.this.motionZ)) * 57.295776F;
                  EntityElvia.this.renderYawOffset = EntityElvia.this.rotationYaw;
               } else {
                  double d4 = EntityElvia.this.getAttackTarget().posX - EntityElvia.this.posX;
                  double d5 = EntityElvia.this.getAttackTarget().posZ - EntityElvia.this.posZ;
                  EntityElvia.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityElvia.this.renderYawOffset = EntityElvia.this.rotationYaw;
               }
            }
         }

      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         return !EntityElvia.this.getMoveHelper().isUpdating() && EntityElvia.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityElvia.this);
         byte flag = 1;
         double speed = 0.6D;
         if (EntityElvia.this.getAttackTarget() != null) {
            if (EntityElvia.this.getDistanceSq(EntityElvia.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityElvia.this.getAttackTarget());
               flag = 2;
               speed += 0.1D;
            } else if (EntityElvia.this.getDistanceSq(EntityElvia.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityElvia.this.getAttackTarget());
               flag = 3;
               speed += 0.15D;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityElvia.this.rand.nextInt(15) - 7, EntityElvia.this.rand.nextInt(11) - 5, EntityElvia.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityElvia.this.rand.nextInt(6) - 2, EntityElvia.this.rand.nextInt(7) - 2, EntityElvia.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityElvia.this.rand.nextInt(4) + 3, EntityElvia.this.rand.nextInt(5) + 4, EntityElvia.this.rand.nextInt(4) + 3);
            }

            if (EntityElvia.this.world.isAirBlock(blockpos1)) {
               EntityElvia.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityElvia.this.getAttackTarget() == null) {
                  EntityElvia.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 1.0D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
