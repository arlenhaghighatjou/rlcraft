package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityAlafha extends EntityPPure implements EntityBodyParts, EntityCutomAttack, EntityCanShoot {
   private EntityBody head;
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityAlafha.class, DataSerializers.BYTE);

   public EntityAlafha(World worldIn) {
      super(worldIn);
      this.moveHelper = new EntityAlafha.AIMoveControl(this);
      this.setSize(1.9F, 2.6F);
      this.setNoGravity(true);
      this.tasks.removeTask(this.folow);
      this.ignoreFrustumCheck = true;
      this.borderOrb = -1;
      this.totalP = SRPConfigMobs.alafhaTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.alafhaLimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.alafhaLimit];

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      if (SRPConfigMobs.alafhaMaxY != 256) {
         this.tasks.addTask(3, new EntityAIFlightLimits(this, SRPConfigMobs.alafhaMaxY, true));
      }

      this.head = new EntityBody(this, 1.2F, 1.2F, 1.0F, 3.0F, 0.0F, -1, 1, false, 0.2F);
      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 9;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
      this.tasks.addTask(6, new EntityAlafha.AIMoveRandom());
      this.tasks.addTask(5, new EntityAlafha.EntityAIAirVomitSummon(this, SRPConfigMobs.alafhaSummoningCooldown, SRPConfigMobs.alafhaLimit, SRPConfigMobs.alafhaMobList, (byte)2));
      this.tasks.addTask(1, new EntityAIAttackProjectile(this, 20, 10, 4));
      this.tasks.addTask(2, new EntityAIAttackMeleeNotGround(this, 4.5D, 16.0D, 0.045D, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ALAFHA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ALAFHA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ALAFHA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.pureFollow);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ALAFHA_MELLE);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

      this.head.onUpdate();
   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setBodyPartDead(int id) {
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      return this.attackEntityAsMob(entityIn);
   }

   public float getEyeHeight() {
      return 1.6F;
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.ALAFHA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ALAFHA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ALAFHA_DEATH;
   }

   protected float getSoundVolume() {
      return 2.0F;
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      return super.onInitialSpawn(difficulty, livingdata);
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public static void registerFixesAlafha(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityAlafha.class);
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

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.ALAFHA_SHOOTING, 2.0F, 1.0F);
      return new EntityProjectileAlafhaBall(this.world, this, accelX, accelY, accelZ);
   }

   public void playProjSound() {
      this.playSound(SRPSounds.ALAFHA_SHOOTINGPOST, 2.0F, 1.0F);
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityAlafha vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityAlafha.this.posX;
            double d1 = this.posY - EntityAlafha.this.posY;
            double d2 = this.posZ - EntityAlafha.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityAlafha.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityAlafha.this.motionX *= 0.5D;
               EntityAlafha.this.motionY *= 0.5D;
               EntityAlafha.this.motionZ *= 0.5D;
            } else {
               EntityAlafha.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityAlafha.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityAlafha.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityAlafha.this.getAttackTarget() == null) {
                  EntityAlafha.this.rotationYaw = -((float)MathHelper.atan2(EntityAlafha.this.motionX, EntityAlafha.this.motionZ)) * 57.295776F;
                  EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw;
               } else {
                  double d4 = EntityAlafha.this.getAttackTarget().posX - EntityAlafha.this.posX;
                  double d5 = EntityAlafha.this.getAttackTarget().posZ - EntityAlafha.this.posZ;
                  EntityAlafha.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityAlafha.this.renderYawOffset = EntityAlafha.this.rotationYaw;
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
         EntityMoveHelper entitymovehelper = EntityAlafha.this.getMoveHelper();
         if (!entitymovehelper.isUpdating()) {
            return true;
         } else {
            double d0 = entitymovehelper.getX() - EntityAlafha.this.posX;
            double d1 = entitymovehelper.getY() - EntityAlafha.this.posY;
            double d2 = entitymovehelper.getZ() - EntityAlafha.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         if (EntityAlafha.this.getAttackTarget() != null) {
            BlockPos blockpos = new BlockPos(EntityAlafha.this);
            byte flag = 1;
            double speed = 0.11D;
            if (EntityAlafha.this.getDistanceSq(EntityAlafha.this.getAttackTarget()) > 400.0D) {
               blockpos = new BlockPos(EntityAlafha.this.getAttackTarget());
               flag = 2;
               speed += 0.11D;
            } else if (EntityAlafha.this.getDistanceSq(EntityAlafha.this.getAttackTarget()) < 100.0D) {
               blockpos = new BlockPos(EntityAlafha.this.getAttackTarget());
               flag = 3;
               speed += 0.11D;
            }

            for(int i = 0; i < 3; ++i) {
               BlockPos blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(15) - 7, EntityAlafha.this.rand.nextInt(9) - 5, EntityAlafha.this.rand.nextInt(15) - 7);
               if (flag == 2) {
                  blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(6) - 2, EntityAlafha.this.rand.nextInt(7) - 2, EntityAlafha.this.rand.nextInt(6) - 2);
               } else if (flag == 3) {
                  blockpos1 = blockpos.add(EntityAlafha.this.rand.nextInt(4) + 3, EntityAlafha.this.rand.nextInt(5) + 4, EntityAlafha.this.rand.nextInt(4) + 3);
               }

               if (EntityAlafha.this.world.isAirBlock(blockpos1)) {
                  EntityAlafha.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
                  if (EntityAlafha.this.getAttackTarget() == null) {
                     EntityAlafha.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                  }
                  break;
               }
            }
         } else {
            EaglercraftRandom random = EntityAlafha.this.getRNG();
            double d0 = EntityAlafha.this.posX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = EntityAlafha.this.posY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = EntityAlafha.this.posZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            EntityAlafha.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.5D);
         }

      }
   }

   static class EntityAIAirVomitSummon extends EntityAIBase {
      private final EntityParasiteBase parentEntity;
      private int limit = 0;
      private int sCooldown;
      private int sLimit;
      private String[] sMobs;
      private int attackTimer = 0;
      private int attacking = 0;
      private double targetX;
      private double targetY;
      private double targetZ;
      private byte sType;

      public EntityAIAirVomitSummon(EntityParasiteBase summoner, int cooldown, int limit, String[] mobList, byte sound) {
         this.parentEntity = summoner;
         this.sCooldown = cooldown;
         this.sLimit = limit;
         this.sMobs = mobList;
         this.sType = sound;
      }

      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null || this.attacking >= 1;
      }

      public void startExecuting() {
      }

      public void resetTask() {
      }

      public void updateTask() {
         if (this.attacking >= 1) {
            ++this.attacking;
            this.parentEntity.setParasiteStatus(10);
            this.parentEntity.getNavigator().clearPath();
            if (this.attacking == 2) {
               if (this.sType == 1) {
                  this.parentEntity.playSound(SRPSounds.CANRA_SPECIAL, 3.0F, 1.0F);
               } else if (this.sType == 2) {
                  this.parentEntity.playSound(SRPSounds.ACANRA_SPECIAL, 3.0F, 1.0F);
               }
            }

            if (this.attacking % 20 == 0 && this.attacking >= 40) {
               EntityCanSummon parent = (EntityCanSummon)this.parentEntity;
               parent.checkID();
               if (parent.getActualParasites() < parent.getTotalParasites() && this.limit < this.sLimit && ParasiteEventEntity.spawnBiomassFromProjectile(this.parentEntity, this.sMobs, this.parentEntity.getAttackTarget())) {
                  ++this.limit;
                  this.parentEntity.particleStatus((byte)8);
               }
            }

            if (this.attacking >= 80 || this.limit >= this.sLimit) {
               this.attacking = 0;
               this.attackTimer = 0;
               this.limit = 0;
               this.parentEntity.setParasiteStatus(2);
            }
         } else if (this.parentEntity.getAttackTarget() == null) {
            if (this.attackTimer > 0) {
               --this.attackTimer;
            }
         } else if (this.parentEntity.getAttackTarget().isDead) {
            if (this.attackTimer > 0) {
               --this.attackTimer;
            }
         } else {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            if (this.parentEntity.canEntityBeSeen(entitylivingbase) && this.parentEntity.getDistanceSq(entitylivingbase) < 256.0D && entitylivingbase.onGround) {
               ++this.attackTimer;
               if (this.attackTimer >= this.sCooldown && this.attacking == 0) {
                  ++this.attacking;
                  this.targetX = this.parentEntity.posX;
                  this.targetY = this.parentEntity.posY;
                  this.targetZ = this.parentEntity.posZ;
               }
            } else if (this.attackTimer > 0 && this.attackTimer > 0) {
               --this.attackTimer;
            }
         }

      }
   }
}
