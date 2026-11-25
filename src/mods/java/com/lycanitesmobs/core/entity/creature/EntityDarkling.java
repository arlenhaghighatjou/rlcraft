package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDarkling extends TameableCreatureEntity implements IMob {
   protected static final DataParameter<Integer> LATCH_TARGET = EntityDataManager.createKey(EntityDarkling.class, DataSerializers.VARINT);
   protected static final DataParameter<Float> LATCH_HEIGHT = EntityDataManager.createKey(EntityDarkling.class, DataSerializers.FLOAT);
   protected static final DataParameter<Float> LATCH_ANGLE = EntityDataManager.createKey(EntityDarkling.class, DataSerializers.FLOAT);
   EntityLivingBase latchEntity = null;
   int latchEntityID = 0;
   double latchHeight = 0.5D;
   double latchAngle = 90.0D;

   public EntityDarkling(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEAD;
      this.hasAttackSound = true;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextPriorityGoalIndex++, (new StealthGoal(this)).setStealthTime(20).setStealthAttack(true).setStealthMove(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(LATCH_TARGET, Integer.valueOf(0));
      this.dataManager.register(LATCH_HEIGHT, Float.valueOf((float)this.latchHeight));
      this.dataManager.register(LATCH_ANGLE, Float.valueOf((float)this.latchAngle));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.hasAttackTarget() && !this.hasLatchTarget() && this.onGround && !this.getEntityWorld().isRemote && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.6D, this.getAttackTarget());
      }

      if (this.hasLatchTarget()) {
         this.noClip = true;
         Vec3d latchPos = this.getFacingPositionDouble(this.getLatchTarget().posX, this.getLatchTarget().posY + (double)this.getLatchTarget().height * this.latchHeight, this.getLatchTarget().posZ, (double)this.getLatchTarget().width * 0.5D, this.latchAngle);
         this.setPosition(latchPos.x, latchPos.y, latchPos.z);
         double distanceX = this.getLatchTarget().posX - this.posX;
         double distanceZ = this.getLatchTarget().posZ - this.posZ;
         this.renderYawOffset = this.rotationYaw = -((float)MathHelper.atan2(distanceX, distanceZ)) * 57.295776F;
         if (!this.getEntityWorld().isRemote) {
            if (this.getLatchTarget().isEntityAlive() && !this.isInWater()) {
               this.setAttackTarget(this.getLatchTarget());
               if (this.updateTick % 40L == 0L) {
                  float damage = this.getAttackDamage(1.0D);
                  if (this.attackMelee(this.getLatchTarget(), (double)damage)) {
                     this.heal(damage * 2.0F);
                  }
               }
            } else {
               this.setPosition(this.getLatchTarget().posX, this.getLatchTarget().posY, this.getLatchTarget().posZ);
               this.setLatchTarget((EntityLivingBase)null);
               this.noClip = false;
            }
         } else {
            for(int i = 0; i < 2; ++i) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      } else {
         this.noClip = false;
      }

   }

   public EntityLivingBase getLatchTarget() {
      try {
         if (this.getEntityWorld().isRemote) {
            this.latchHeight = (double)((Float)this.dataManager.get(LATCH_HEIGHT)).floatValue();
            this.latchAngle = (double)((Float)this.dataManager.get(LATCH_ANGLE)).floatValue();
            int latchEntityID = ((Integer)this.getDataManager().get(LATCH_TARGET)).intValue();
            if (latchEntityID != this.latchEntityID) {
               this.latchEntity = null;
               this.latchEntityID = latchEntityID;
               if (latchEntityID != 0) {
                  Entity possilbeLatchEntity = this.getEntityWorld().getEntityByID(latchEntityID);
                  if (possilbeLatchEntity != null && possilbeLatchEntity instanceof EntityLivingBase) {
                     this.latchEntity = (EntityLivingBase)possilbeLatchEntity;
                  }
               }
            }
         }
      } catch (Exception var3) {
         ;
      }

      return this.latchEntity;
   }

   public void setLatchTarget(EntityLivingBase entity) {
      this.latchEntity = entity;
      if (!this.getEntityWorld().isRemote) {
         if (entity == null) {
            this.getDataManager().set(LATCH_TARGET, Integer.valueOf(0));
         } else if (ObjectManager.getEffect("repulsion") != null && entity.isPotionActive(ObjectManager.getEffect("repulsion"))) {
            this.latchEntity = null;
            this.getDataManager().set(LATCH_TARGET, Integer.valueOf(0));
         } else {
            this.getDataManager().set(LATCH_TARGET, Integer.valueOf(entity.getEntityId()));
            this.latchHeight = 0.25D + 0.75D * this.getRNG().nextDouble();
            this.latchAngle = 360.0D * this.getRNG().nextDouble();
            this.dataManager.set(LATCH_HEIGHT, Float.valueOf((float)this.latchHeight));
            this.dataManager.set(LATCH_ANGLE, Float.valueOf((float)this.latchAngle));
         }
      }
   }

   public boolean hasLatchTarget() {
      return this.getLatchTarget() != null;
   }

   public boolean canAttackClass(Class targetType) {
      return this.hasLatchTarget() ? false : super.canAttackClass(targetType);
   }

   public boolean attackMelee(Entity target, double damageScale) {
      double targetKnockbackResistance = 0.0D;
      if (target instanceof EntityLivingBase) {
         targetKnockbackResistance = ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue();
         ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      }

      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntityLivingBase) {
            ((EntityLivingBase)target).getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(targetKnockbackResistance);
         }

         if (!this.hasLatchTarget() && target instanceof EntityLivingBase && !this.isInWater()) {
            this.setLatchTarget((EntityLivingBase)target);
         }

         return true;
      }
   }

   public boolean canEntityBeSeen(Entity target) {
      return target == this.getLatchTarget() ? true : super.canEntityBeSeen(target);
   }

   public boolean canClimb() {
      return true;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canStealth() {
      if (this.getEntityWorld().isRemote) {
         return false;
      } else if (this.isMoving()) {
         return false;
      } else {
         return this.testLightLevel() <= 0;
      }
   }

   public void startStealth() {
      if (this.getEntityWorld().isRemote) {
         EnumParticleTypes particle = EnumParticleTypes.SPELL_WITCH;
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;

         for(int i = 0; i < 100; ++i) {
            this.getEntityWorld().spawnParticle(particle, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
         }
      }

      super.startStealth();
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("inWall") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public float getFallResistance() {
      return 10.0F;
   }
}
