package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockInfest;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityVenkrolSIII extends EntityPStationaryArchitect {
   public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsiiilimit, 20 * SRPConfigMobs.venkrolsiiiCooldown, 3, SRPConfigMobs.venkrolsiiiCAMinimumV, (double)SRPConfigMobs.venkrolsiiiCAExtraM);

   public EntityVenkrolSIII(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 5.1F);
      this.buriedT = 5.8D;
      this.totalP = SRPConfigMobs.venkrolsiiiTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsiiilimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsiiilimit];
      this.stage = 3;
      this.experienceValue = SRPAttributes.XP_ADAPTED * 2;
      if (SRPAttributes.rsBlockI) {
         this.tasks.addTask(3, new EntityAIBlockInfest(this, 3));
      }

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.setBODY(1.0F);
      this.tasks.addTask(2, this.summonV);
      this.damageCap = SRPConfig.nexussiiiCap;
      this.pointCap = SRPConfig.nexussiiiPointCap;
      this.pointReduction = SRPConfig.nexussiiiPointRed;
      this.chanceLearn = SRPConfig.nexussiiiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiiiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiiiPointDamCap;
      this.neededTime = this.setGT(SRPConfig.nexussiiiMinGrowTime, SRPConfig.nexussiiiMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiiiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 19;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 3));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VENKROLSIII_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSIII_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VENKROLSIII_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiiiFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.world.isRemote && this.topParticles > 0 && this.ticksExisted % 3 == 0) {
         for(int i = 0; i <= 1; ++i) {
            this.spawnParticlesTop(SRPEnumParticle.BIOMASS, 0, 0, 0);
         }
      }

      if (this.getParasiteStatus() == 0) {
         this.setBODY(0.04F);
      } else {
         this.setBODY(-0.04F);
      }

   }

   public float getEyeHeight() {
      return 4.9F;
   }

   public void setBODY(float in) {
      in = in + this.getBODY();
      if (in > 0.6F) {
         in = 0.6F;
      }

      if (in < 0.0F) {
         in = 0.0F;
      }

      this.body = in;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.VENKROLSIII_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.VENKROLSIII_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.VENKROLSIII_DEATH;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public float getBombDamage() {
      return (float)SRPAttributes.VENKROLSIII_ATTACK_DAMAGE;
   }
}
