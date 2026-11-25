package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityInfWolfHead extends EntityPInfected {
   public EntityInfWolfHead(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 0.6F);
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 21;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 40, 100, 3, true, 14));
      this.setskillLeapValues(0.7F, 1.5D, 0);
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, -1.0D));
      this.tasks.addTask(4, new EntityAIAvoidOrAttack(this, 0.5F));
      this.tasks.addTask(5, new EntityAIAvoidEntityStatus(this, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase entity) {
            return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityCreeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal);
         }
      }, 8.0F, 1.3D));
      this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityInhooM.class, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFWOLF_HEADHEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFWOLF_HEADDAMAGE);
   }

   public void setInWeb() {
   }

   public float getEyeHeight() {
      return 0.3F;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (entityIn instanceof EntityInhooM && entityIn.isEntityAlive() && this.isEntityAlive()) {
         ParasiteEventEntity.spawnNext(this, new EntityInfWolf(this.world), true, false);
         ((EntityParasiteBase)entityIn).particleStatus((byte)7);
         entityIn.setDead();
         return true;
      } else {
         return super.attackEntityAsMob(entityIn);
      }
   }

   public boolean canAttackClass(Class<? extends EntityLivingBase> cls) {
      if (cls != EntityPlayer.class && cls != EntityPlayerMP.class) {
         String name = null;

         try {
            name = EntityList.getKey(cls).toString();
         } catch (Exception var4) {
            return true;
         }

         if (name == null) {
            return true;
         } else if (name.contains("srparasites") && cls != EntityInhooM.class) {
            return false;
         } else {
            return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
         }
      } else {
         return true;
      }
   }

   public static void registerFixesInfWolfHead(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfWolfHead.class);
   }

   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier * 0.3F);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.INFECTEDHEAD_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.INFECTEDHEAD_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.INFECTEDHEAD_DEATH;
   }
}
