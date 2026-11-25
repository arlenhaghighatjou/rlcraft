package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityUnvo extends EntityPStationary implements EntityCanShoot {
   public EntityUnvo(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 4.1F);
      this.experienceValue = SRPAttributes.XP_ADAPTED * 2;
      this.type = 40;
      this.buriedT = 5.1D;
      this.damageCap = SRPConfig.turretCap;
      this.pointCap = SRPConfig.turretPointCap;
      this.pointReduction = SRPConfig.turretPointRed;
      this.chanceLearn = SRPConfig.turretChanceLe;
      this.chanceLearnFire = SRPConfig.turretChanceLeFire;
      this.DamageTypeCap = SRPConfig.turretPointDamCap;
      this.MiniDamage = SRPConfig.turretMinDamage;
      this.regen = SRPConfig.turretRegen;
   }

   public int getParasiteIDRegister() {
      return 30;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(2, new EntityAIAttackProjectile(this, 20, 1, 3));
      this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.UNVO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.UNVO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.UNVO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.turretFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public double getYOffset() {
      return this.getRidingEntity() != null && this.getRidingEntity() instanceof EntityEsor ? 1.0D : super.getYOffset();
   }

   public float getEyeHeight() {
      return 3.6F;
   }

   public static void registerFixesUnvo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityUnvo.class);
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.UNVO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.UNVO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.UNVO_DEATH;
   }

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.EMANA_SHOOTING, 2.0F, 1.0F);
      EntityLivingBase entitylivingbase = this.getAttackTarget();
      accelY = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 4.0F) - (1.0D + this.posY + (double)(this.height / 2.0F));
      EntityProjectileSpineball ball = new EntityProjectileSpineball(this.world, this, accelX, accelY, accelZ, SRPAttributes.UNVO_RANGE_ATTACK_DAMAGE);
      ball.setDurationAmplifier(SRPConfigMobs.unvoPoisonDuration, SRPConfigMobs.unvoPoisonAmplifier);
      ball.setGearDamage(SRPConfigMobs.unvoGearD);
      return ball;
   }

   public void playProjSound() {
      this.playSound(SRPSounds.UNVO_SHOOTING, 2.0F, 1.0F);
   }
}
