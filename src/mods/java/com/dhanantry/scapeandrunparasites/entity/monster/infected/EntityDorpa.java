package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDorpa extends EntityPInfected implements EntityCanShoot {
   public EntityDorpa(World worldIn) {
      super(worldIn);
      this.setSize(1.9F, 2.1F);
      this.stepHeight = 1.0F;
      this.canModRender = 1;
      this.type = 14;
      this.killcount = -10.0D;
   }

   public int getParasiteIDRegister() {
      return 2;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.5D, false, 0.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIAttackProjectile(this, 60, 15, 3));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 1, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DORPA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.DORPA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.DORPA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DORPA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.infectedFollow);
   }

   public void setInWeb() {
   }

   public float getEyeHeight() {
      return 1.75F;
   }

   public static void registerFixesDorpa(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityDorpa.class);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getSkin() == 1) {
         boolean flag = super.attackEntityAsMob(entityIn);
         if (flag && entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 40));
         }

         return flag;
      } else {
         return super.attackEntityAsMob(entityIn);
      }
   }

   protected void selfExplode() {
      super.selfExplode();
      ParasiteEventEntity.spawnM(this, new String[]{SRPConfigMobs.dorpamob}, 0, false, this.getCustomNameTag());
   }

   protected void spawnGore() {
   }

   @SideOnly(Side.CLIENT)
   public void spawnEffectsGore() {
      for(int i = 0; i <= 60; ++i) {
         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 127);
         }

         if (i % 5 == 0) {
            this.spawnParticles(SRPEnumParticle.GSPLASH, 1, -1, -1);
         }
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.INFECTEDSPIDER_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.INFECTEDSPIDER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.INFECTEDSPIDER_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SRPSounds.INFECTEDSPIDER_STEP;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.world.rand.nextInt(3) == 0) {
         this.setSkin(1);
         this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DORPA_HEALTH * 0.5D);
         this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DORPA_ATTACK_DAMAGE * 1.25D);
         this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
      }

      return floo;
   }

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.DORPA_RANGE, 2.0F, 1.0F);
      EntityLivingBase entitylivingbase = this.getAttackTarget();
      accelY = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 3.0F) - (1.0D + this.posY + (double)(this.height / 2.0F));
      return new EntityProjectileWebball(this.world, this, accelX, accelY, accelZ);
   }

   public void playProjSound() {
   }
}
