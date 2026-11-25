package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.projectile.EntityFireball;
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

public class EntityEmanaAdapted extends EntityPAdapted implements EntityCutomAttack, EntityCanShoot {
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityEmanaAdapted.class, DataSerializers.BYTE);
   private int count;

   public EntityEmanaAdapted(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityEmanaAdapted.AIMoveControl(this);
      this.setSize(1.3F, 2.9F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.borderOrb = -1;
      if (SRPConfigMobs.emanaMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
      }

      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 55;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.adaptedFollow));
      this.tasks.addTask(4, new EntityEmanaAdapted.AIChargeAttack());
      this.tasks.addTask(6, new EntityEmanaAdapted.AIMoveRandom());
      this.tasks.addTask(1, new EntityAIAttackProjectile(this, 60, 20, 2, true));
      this.tasks.addTask(2, new EntityAIAttackMeleeNotGround(this, 2.5D, 16.0D, 0.04D, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.EMANA_HEALTH + SRPAttributes.EMANA_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.EMANA_ARMOR + SRPAttributes.EMANA_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.EMANA_KD_RESISTANCE + SRPAttributes.EMANA_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.EMANA_A_MELLE);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      return this.attackEntityAsMob(entityIn);
   }

   public float getEyeHeight() {
      return 2.1F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityEmana(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.AEMANA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.AEMANA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.AEMANA_DEATH;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesEmana(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityEmanaAdapted.class);
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
      float pit = 1.0F;
      if (this.count == 3) {
         pit = 2.0F;
      }

      this.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, pit);
      if (this.count >= 3) {
         this.count = 0;
         return new EntityProjectileNade(this.world, this, accelX, accelY, accelZ, 4, 100);
      } else {
         EntityProjectileSpineball ball = new EntityProjectileSpineball(this.world, this, accelX, accelY, accelZ, SRPAttributes.EMANA_A_RANGED_DAMAGE);
         ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration * 2, SRPConfigMobs.emanaPoisonAmplifier + 1);
         ball.setGearDamage(SRPConfigMobs.emanaadaptedgeard);
         return ball;
      }
   }

   public void playProjSound() {
      ++this.count;
      if (this.count == 3) {
         this.world.setEntityState(this, (byte)100);
         float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
         this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
      } else {
         this.playSound(SRPSounds.AEMANA_SHOOTINGPOST, 2.0F, 1.0F);
      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityEmanaAdapted.this.getAttackTarget() != null && !EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.rand.nextInt(7) == 0) {
            return EntityEmanaAdapted.this.getDistanceSq(EntityEmanaAdapted.this.getAttackTarget()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean shouldContinueExecuting() {
         return EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.isCharging() && EntityEmanaAdapted.this.getAttackTarget() != null && EntityEmanaAdapted.this.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = EntityEmanaAdapted.this.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
         EntityEmanaAdapted.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.5D);
         EntityEmanaAdapted.this.setCharging(true);
      }

      public void resetTask() {
         EntityEmanaAdapted.this.setCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = EntityEmanaAdapted.this.getAttackTarget();
         if (entitylivingbase != null && entitylivingbase.isEntityAlive()) {
            if (EntityEmanaAdapted.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
               EntityEmanaAdapted.this.attackEntityAsMob(entitylivingbase);
               EntityEmanaAdapted.this.setCharging(false);
            } else {
               double d0 = EntityEmanaAdapted.this.getDistanceSq(entitylivingbase);
               if (d0 < 9.0D) {
                  Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
                  EntityEmanaAdapted.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.5D);
               }
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityEmanaAdapted vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityEmanaAdapted.this.posX;
            double d1 = this.posY - EntityEmanaAdapted.this.posY;
            double d2 = this.posZ - EntityEmanaAdapted.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityEmanaAdapted.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityEmanaAdapted.this.motionX *= 0.5D;
               EntityEmanaAdapted.this.motionY *= 0.5D;
               EntityEmanaAdapted.this.motionZ *= 0.5D;
            } else {
               EntityEmanaAdapted.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityEmanaAdapted.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityEmanaAdapted.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityEmanaAdapted.this.getAttackTarget() == null) {
                  EntityEmanaAdapted.this.rotationYaw = -((float)MathHelper.atan2(EntityEmanaAdapted.this.motionX, EntityEmanaAdapted.this.motionZ)) * 57.295776F;
                  EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw;
               } else {
                  double d4 = EntityEmanaAdapted.this.getAttackTarget().posX - EntityEmanaAdapted.this.posX;
                  double d5 = EntityEmanaAdapted.this.getAttackTarget().posZ - EntityEmanaAdapted.this.posZ;
                  EntityEmanaAdapted.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityEmanaAdapted.this.renderYawOffset = EntityEmanaAdapted.this.rotationYaw;
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
         return !EntityEmanaAdapted.this.getMoveHelper().isUpdating() && EntityEmanaAdapted.this.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityEmanaAdapted.this);

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityEmanaAdapted.this.rand.nextInt(15) - 7, EntityEmanaAdapted.this.rand.nextInt(11) - 5, EntityEmanaAdapted.this.rand.nextInt(15) - 7);
            if (EntityEmanaAdapted.this.world.isAirBlock(blockpos1)) {
               EntityEmanaAdapted.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);
               if (EntityEmanaAdapted.this.getAttackTarget() == null) {
                  EntityEmanaAdapted.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
