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
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityVenkrol extends EntityPStationaryArchitect {
   public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrollimit, 20 * SRPConfigMobs.venkrolCooldown, 1, SRPConfigMobs.venkrolCAMinimumV, (double)SRPConfigMobs.venkrolCAExtraM);

   public EntityVenkrol(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 1.5F);
      this.buriedT = 2.6D;
      this.totalP = SRPConfigMobs.venkrolTotalActiveMobs;
      this.mobID = new int[this.totalP + SRPConfigMobs.venkrollimit];
      this.mobPT = new int[this.totalP + SRPConfigMobs.venkrollimit];
      this.stage = 1;
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;
      if (SRPAttributes.rsBlockI) {
         this.tasks.addTask(3, new EntityAIBlockInfest(this, 1));
      }

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.setBODY(1.0F);
      this.tasks.addTask(2, this.summonV);
      this.damageCap = SRPConfig.nexussiCap;
      this.pointCap = SRPConfig.nexussiPointCap;
      this.pointReduction = SRPConfig.nexussiPointRed;
      this.chanceLearn = SRPConfig.nexussiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiPointDamCap;
      this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 16;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 1));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.VENKROL_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.VENKROL_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.VENKROL_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.world.isRemote && this.topParticles > 0 && this.ticksExisted % 10 == 0) {
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
      return 1.4F;
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

   public boolean getCanSpawnHere() {
      IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
      return this.getBlockPathWeight(new BlockPos(this.posX, this.getEntityBoundingBox().minY, this.posZ)) >= 0.0F && iblockstate.canEntitySpawn(this) && this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.VENKROLSI_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.VENKROLSI_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.VENKROLSI_DEATH;
   }

   public float getBombDamage() {
      return (float)SRPAttributes.VENKROL_ATTACK_DAMAGE;
   }
}
