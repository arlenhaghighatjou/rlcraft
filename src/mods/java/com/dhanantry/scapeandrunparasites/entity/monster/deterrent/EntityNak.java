package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityNak extends EntityPStationary {
   private float attackTimer;
   private boolean up2;
   private static final DataParameter<Integer> TARGET_ENTITY = EntityDataManager.createKey(EntityNak.class, DataSerializers.VARINT);
   private EntityLivingBase targetedEntity;
   private int tak;

   public EntityNak(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 2.5F);
      this.experienceValue = 0;
      this.type = 40;
      this.buriedT = 4.0D;
      this.damageCap = SRPConfig.turretCap;
      this.pointCap = SRPConfig.turretPointCap;
      this.pointReduction = SRPConfig.turretPointRed;
      this.chanceLearn = SRPConfig.turretChanceLe;
      this.chanceLearnFire = SRPConfig.turretChanceLeFire;
      this.DamageTypeCap = SRPConfig.turretPointDamCap;
      this.MiniDamage = SRPConfig.turretMinDamage;
      this.regen = SRPConfig.turretRegen;
      this.valueEvDeath = 0;
   }

   public int getParasiteIDRegister() {
      return 72;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TARGET_ENTITY, Integer.valueOf(0));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.NAK_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.NAK_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.NAK_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(6.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.isAIDisabled()) {
         if (this.up2) {
            this.attackTimer = (float)((double)this.attackTimer + 0.15D);
            if (this.attackTimer > 1.0F) {
               this.up = false;
            }
         } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.2D);
         }

         if (!this.up && !this.buried()) {
            if (!this.world.isRemote) {
               if (this.getAttackTarget() != null) {
                  if (this.getAttackTarget() instanceof EntityPlayer) {
                     EntityPlayer pa = (EntityPlayer)this.getAttackTarget();
                     if (pa.capabilities.isCreativeMode || pa.capabilities.disableDamage) {
                        this.setAttackTarget((EntityLivingBase)null);
                        this.setTargetedEntity(0);
                        this.setParasiteStatus(0);
                        return;
                     }
                  }

                  if (!this.getAttackTarget().isEntityAlive()) {
                     this.setAttackTarget((EntityLivingBase)null);
                     this.setTargetedEntity(0);
                     this.setParasiteStatus(0);
                  } else {
                     this.tak = 0;
                     if (this.canEntityBeSeen(this.getAttackTarget()) && this.getDistanceSq(this.getAttackTarget()) < 25.0D) {
                        this.setParasiteStatus(3);
                        this.getAttackTarget().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 80, 2, false, false));
                        this.setTargetedEntity(this.getAttackTarget().getEntityId());
                        this.getNavigator().tryMoveToEntityLiving(this.getAttackTarget(), 0.0D);
                     } else {
                        this.setParasiteStatus(2);
                        this.setTargetedEntity(0);
                     }
                  }
               } else {
                  ++this.tak;
                  if (this.tak >= 60) {
                     this.world.setEntityState(this, (byte)51);
                     this.up = true;
                  }

                  this.setParasiteStatus(0);
                  this.setTargetedEntity(0);
               }
            }

            if (this.getTargetedEntity() != null) {
               double dis = this.getDistanceSq(this.getTargetedEntity());
               if (dis < 25.0D && dis > 1.0D) {
                  EntityLivingBase target = this.getTargetedEntity();
                  target.dismountRidingEntity();
                  double str = 0.5D;
                  target.motionX += (Math.signum(this.posX - target.posX) * str - target.motionX) * str;
                  target.motionZ += (Math.signum(this.posZ - target.posZ) * str - target.motionZ) * str;
                  target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 20, 2, false, false));
               }
            }

         }
      }
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source.getImmediateSource() instanceof EntitySRPProjectile) {
         EntityLivingBase target = this.getTargetedEntity();
         if (target != null) {
            target.attackEntityFrom(source, amount * 2.0F);
         }

         return false;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   private void setTargetedEntity(int entityId) {
      this.dataManager.set(TARGET_ENTITY, Integer.valueOf(entityId));
   }

   public boolean hasTargetedEntity() {
      return ((Integer)this.dataManager.get(TARGET_ENTITY)).intValue() != 0;
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

   public float getEyeHeight() {
      return 1.8F;
   }

   public static void registerFixesTonro(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityNak.class);
   }

   @SideOnly(Side.CLIENT)
   public float getAttackTimer() {
      return this.attackTimer;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 12) {
         this.up = true;
         this.attackTimer = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }
}
