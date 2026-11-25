package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityWymo extends EntityPPrimitive {
   private float body = 0.0F;

   public EntityWymo(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 2.9F);
   }

   public int getParasiteIDRegister() {
      return 37;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 2, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ZETMO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ZETMO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.19D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.ZETMO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.ZETMO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getParasiteStatus() == 15) {
         this.setBODY(0.07F);
      } else {
         this.setBODY(-0.07F);
      }

      if (!this.world.isRemote && this.ticksExisted % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(this.world), true, true);
      }

   }

   public float getEyeHeight() {
      return 2.7F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityLesh(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public float getBODY() {
      return this.body;
   }

   public void setBODY(float in) {
      in = in + this.getBODY();
      if (in > 1.9F) {
         in = 1.9F;
      }

      if (in < 0.0F) {
         in = 0.0F;
      }

      this.body = in;
   }

   public static void registerFixesBano(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityWymo.class);
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
      if (!this.world.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(this.world), true, true);
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.ZETMO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ZETMO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ZETMO_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   static class AITentaclePull extends EntityAIBase {
      private final EntityWymo parentEntity;

      public AITentaclePull(EntityWymo ghast) {
         this.parentEntity = ghast;
      }

      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null || this.parentEntity.getBODY() != 0.0F;
      }

      public void startExecuting() {
      }

      public void resetTask() {
         this.parentEntity.setParasiteStatus(0);
      }

      public void updateTask() {
         if (this.parentEntity.getAttackTarget() == null) {
            this.parentEntity.setParasiteStatus(0);
         } else if (this.parentEntity.getAttackTarget().isDead) {
            this.parentEntity.setParasiteStatus(0);
         } else {
            EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
            if (entitylivingbase.getDistanceSq(this.parentEntity) < 169.0D && this.parentEntity.canEntityBeSeen(entitylivingbase)) {
               this.parentEntity.setParasiteStatus(15);
               if (entitylivingbase.getDistanceSq(this.parentEntity) < 64.0D && entitylivingbase.getDistanceSq(this.parentEntity) > 4.0D && this.parentEntity.ticksExisted % 20 == 0) {
                  Vec3d vec3d = this.parentEntity.getLook(1.0F);
                  float f = (float)MathHelper.atan2(entitylivingbase.posZ - this.parentEntity.posZ, entitylivingbase.posX - this.parentEntity.posX);
                  EntityDamage entityevokerfangs = new EntityDamage(this.parentEntity.world, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, this.parentEntity, 1.0F, true, 1.0F);
                  this.parentEntity.world.spawnEntity(entityevokerfangs);
               }
            } else {
               this.parentEntity.setParasiteStatus(0);
            }
         }

      }
   }
}
