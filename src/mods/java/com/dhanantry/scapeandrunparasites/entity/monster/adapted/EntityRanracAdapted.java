package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRanracAdapted extends EntityPAdapted implements EntityBodyParts {
   private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityRanracAdapted.class, DataSerializers.VARINT);
   private EntityLivingBase targetedEntity;
   private EntityBody head;
   private int pulling;
   private boolean canPull;
   private boolean leapTo;
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityRanracAdapted.class, DataSerializers.BYTE);
   private int border;
   private boolean skillpulling;

   public EntityRanracAdapted(World worldIn) {
      super(worldIn);
      this.setSize(1.901F, 2.85F);
      this.stepHeight = 1.0F;
      this.head = new EntityBody(this, 1.8F, 1.7F, 2.0F, 2.0F, 1.7F, 1, 1, false, 0.2F);
      this.ignoreFrustumCheck = true;
      this.canPull = true;
   }

   public int getParasiteIDRegister() {
      return 58;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAISkill(this, 10, 300, 7, true, 1));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D));
      if (SRPConfig.parasiteGenResidue) {
         this.tasks.addTask(9, new EntityAIBlockResidue(this, 2));
      }

      this.tasks.addTask(6, new EntityAIGetFollowers(this, 3, 32));
      this.tasks.addTask(2, new EntityAIEvade(this, 25, 10, 4));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TARGET_ENTITY, Integer.valueOf(0));
      this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.RANRAC_HEALTH + SRPAttributes.RANRAC_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.RANRAC_ARMOR + SRPAttributes.RANRAC_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.33D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.RANRAC_KD_RESISTANCE + SRPAttributes.RANRAC_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.RANRAC_ATTACK_DAMAGE + SRPAttributes.RANRAC_A_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      if (!this.world.isRemote) {
         if (!this.canPull) {
            --this.pulling;
            if (this.pulling == 0) {
               this.canPull = true;
            }
         }

         if (this.getAttackTarget() != null) {
            if (!this.getAttackTarget().isEntityAlive()) {
               this.setAttackTarget((EntityLivingBase)null);
               this.setTargetedEntity(0);
               this.setParasiteStatus(0);
            } else if (this.canEntityBeSeen(this.getAttackTarget()) && this.getDistanceSq(this.getAttackTarget()) > 36.0D && this.canPull) {
               ++this.pulling;
               if (this.pulling > 140) {
                  this.setParasiteStatus(2);
                  this.setTargetedEntity(0);
                  this.canPull = false;
               }
            } else {
               if (this.leapTo) {
                  int i = this.getRNG().nextInt(2) == 0 ? 1 : -1;
                  this.moveStrafing = (float)i;
                  double dd0 = this.getAttackTarget().posX - this.posX;
                  double dd1 = this.getAttackTarget().posZ - this.posZ;
                  float f = MathHelper.sqrt(dd0 * dd0 + dd1 * dd1);
                  this.motionX += dd0 / (double)f * 2.77D * 0.800000011920929D + this.motionX * 0.20000000298023224D;
                  this.motionZ += dd1 / (double)f * 2.77D * 0.800000011920929D + this.motionZ * 0.20000000298023224D;
                  this.motionY = 0.2D + (double)this.height * 0.1D;
                  this.getNavigator().clearPath();
                  this.leapTo = false;
                  this.pulling += 20;
                  this.canPull = false;
               }

               this.setParasiteStatus(2);
               this.setTargetedEntity(0);
            }
         } else {
            this.setParasiteStatus(0);
            this.setTargetedEntity(0);
         }
      }

      if (this.getTargetedEntity() != null && this.getDistanceSq(this.getTargetedEntity()) > 36.0D) {
         EntityLivingBase target = this.getTargetedEntity();
         target.dismountRidingEntity();
         double str = 0.6D;
         target.motionX += (Math.signum(this.posX - target.posX) * str - target.motionX) * str;
         target.motionZ += (Math.signum(this.posZ - target.posZ) * str - target.motionZ) * str;
         str = 0.015D;
         if (target.posY > this.posY + 1.0D) {
            target.motionY -= (Math.signum(this.posY - target.posY) * str - target.motionY) * str;
         } else if (target.posY < this.posY) {
            target.motionY += (Math.signum(this.posY - target.posY) * str - target.motionY) * str;
         }
      }

      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   public boolean isBesideClimbableBlock() {
      return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, Byte.valueOf(b0));
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateClimberStatus(this, worldIn);
   }

   public void setTargetedEntity(int entityId) {
      if (this.canPull || entityId == 0) {
         if (entityId != 0) {
            this.leapTo = true;
         } else {
            this.leapTo = false;
         }

         this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
      }
   }

   public boolean hasTargetedEntity() {
      if (!this.canPull) {
         return false;
      } else {
         return ((Integer)this.dataManager.get(TARGET_ENTITY)).intValue() != 0;
      }
   }

   @Nullable
   public EntityLivingBase getTargetedEntity() {
      if (!this.hasTargetedEntity()) {
         return null;
      } else if (this.world.isRemote) {
         if (this.targetedEntity != null) {
            return this.targetedEntity;
         } else {
            Entity entity = this.world.getEntityByID(((Integer)this.dataManager.get(TARGET_ENTITY)).intValue());
            if (entity instanceof EntityLivingBase) {
               this.targetedEntity = (EntityLivingBase)entity;
               return this.targetedEntity;
            } else {
               return null;
            }
         }
      } else {
         return this.getAttackTarget();
      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (TARGET_ENTITY.equals(key)) {
         this.targetedEntity = null;
      }

   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag) {
         if (entityIn instanceof EntityLivingBase) {
            switch(this.getSkin()) {
            case 5:
               SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
               break;
            case 6:
               SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
            }
         }

         this.canPull = false;
         this.pulling = 80;
      }

      return flag;
   }

   public void setBodyPartDead(int id) {
   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 100, 0);
         }

      }
   }

   public float getEyeHeight() {
      return 1.5F;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.arachnidaadaptedOrbEffects, mobs);
      }

      return flag;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityRanrac(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(2)) {
         case 0:
            this.setSkin(5);
            break;
         case 1:
            this.setSkin(6);
         }
      }

      return floo;
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   public static void registerFixesBano(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityRanracAdapted.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ARANRAC_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ARANRAC_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ARANRAC_DEATH;
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillpulling;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillpulling = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.pullingE();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void pullingE() {
      if (this.onGround && this.canPull) {
         if (this.hasTargetedEntity()) {
            this.skillpulling = true;
            this.border = 0;
         } else {
            this.setParasiteStatus(11);
            this.getNavigator().clearPath();
            if (this.border == 0) {
               ;
            }

            if (this.border <= 2) {
               ;
            }

            if (this.ticksExisted % 20 == 0) {
               ++this.border;
               if (this.getAttackTarget() == null) {
                  this.skillpulling = true;
                  this.setParasiteStatus(0);
                  this.border = 0;
               } else if (!this.canEntityBeSeen(this.getAttackTarget())) {
                  this.skillpulling = true;
                  this.setParasiteStatus(0);
                  this.border = 0;
               } else {
                  EntityLivingBase entitylivingbase = this.getAttackTarget();
                  Vec3d vec3d = this.getLook(1.0F);
                  double d2 = entitylivingbase.posX - (this.posX + vec3d.x);
                  double d3 = entitylivingbase.getEntityBoundingBox().maxY + (double)(entitylivingbase.height + 0.8F) - (0.5D + this.posY + (double)(this.height / 1.0F));
                  double d4 = entitylivingbase.posZ - (this.posZ + vec3d.z);
                  EntityProjectilePullball entitylargefireball = new EntityProjectilePullball(this.world, this, d2, d3, d4, (byte)2);
                  entitylargefireball.posX = this.posX + vec3d.x;
                  entitylargefireball.posY = this.posY + (double)this.getEyeHeight() - 0.2D;
                  entitylargefireball.posZ = this.posZ + vec3d.z;
                  this.world.spawnEntity(entitylargefireball);
                  if (this.border > 6) {
                     this.skillpulling = true;
                     this.setParasiteStatus(0);
                     this.border = 0;
                     this.pulling = 60;
                  }

               }
            }
         }
      } else {
         this.skillpulling = true;
         this.setParasiteStatus(0);
         this.border = 0;
      }
   }

   public void resetPullSkill() {
      this.skillpulling = true;
      this.border = 0;
   }
}
