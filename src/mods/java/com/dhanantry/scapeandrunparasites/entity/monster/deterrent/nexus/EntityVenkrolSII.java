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

public class EntityVenkrolSII extends EntityPStationaryArchitect {
   public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsiilimit, 20 * SRPConfigMobs.venkrolsiiCooldown, 2, SRPConfigMobs.venkrolsiiCAMinimumV, (double)SRPConfigMobs.venkrolsiiCAExtraM);

   public EntityVenkrolSII(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 2.8F);
      this.buriedT = 4.4D;
      this.totalP = SRPConfigMobs.venkrolsiiTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsiilimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsiilimit];
      this.stage = 2;
      this.experienceValue = SRPAttributes.XP_PRIMITIVE * 2;
      if (SRPAttributes.rsBlockI) {
         this.tasks.addTask(3, new EntityAIBlockInfest(this, 2));
      }

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.setBODY(1.0F);
      this.tasks.addTask(2, this.summonV);
      this.damageCap = SRPConfig.nexussiiCap;
      this.pointCap = SRPConfig.nexussiiPointCap;
      this.pointReduction = SRPConfig.nexussiiPointRed;
      this.chanceLearn = SRPConfig.nexussiiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiiPointDamCap;
      this.neededTime = this.setGT(SRPConfig.nexussiiMinGrowTime, SRPConfig.nexussiiMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 18;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 2));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VENKROLSII_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROLSII_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VENKROLSII_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiiFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.world.isRemote && this.topParticles > 0 && this.ticksExisted % 5 == 0) {
         for(int i = 0; i <= 2; ++i) {
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
      return 2.7F;
   }

   public void setBODY(float in) {
      in = in + this.getBODY();
      if (in > 0.5F) {
         in = 0.5F;
      }

      if (in < 0.0F) {
         in = 0.0F;
      }

      this.body = in;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.VENKROLSII_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.VENKROLSII_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.VENKROLSII_DEATH;
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   public float getBombDamage() {
      return (float)SRPAttributes.VENKROLSII_ATTACK_DAMAGE;
   }
}
