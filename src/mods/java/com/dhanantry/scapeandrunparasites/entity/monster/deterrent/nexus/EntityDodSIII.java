package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDodAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityDodSIII extends EntityPDispatcher {
   public EntityDodSIII(World worldIn) {
      super(worldIn);
      this.setSize(3.9F, 4.8F);
      this.ignoreFrustumCheck = true;
      this.experienceValue = SRPAttributes.XP_ADAPTED * 2;
      this.buried = 0.1D;
      this.setParasiteStatus(3);
      this.damageCap = SRPConfig.nexussiiiCap;
      this.pointCap = SRPConfig.nexussiiiPointCap;
      this.pointReduction = SRPConfig.nexussiiiPointRed;
      this.chanceLearn = SRPConfig.nexussiiiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiiiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiiiPointDamCap;
      this.totalP = 5;
      this.mobID = new int[5];
      this.mobPT = new int[5];
      this.stage = 3;
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.neededTime = this.setGT(SRPConfig.nexussiiiMinGrowTime, SRPConfig.nexussiiiMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiiiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 78;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 3, (byte)2));
      this.tasks.addTask(2, new EntityAIDodAttack(this, 3, (byte)42, 20.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DODSIII_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.DODSIII_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DODSIII_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiiiFollow * (double)SRPConfigMobs.dodsiiiFollowRangeMult);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public float getEyeHeight() {
      return 4.6F;
   }

   public static void registerFixesUnvo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityDodSIII.class);
   }

   public float getBombDamage() {
      return (float)SRPAttributes.DODSIII_ATTACK_DAMAGE;
   }

   public boolean storeParasite(EntityParasiteBase in) {
      if (super.storeParasite(in)) {
         return true;
      } else if (this.storeLodo(in, true)) {
         return true;
      } else if (this.storeInf(in, true)) {
         return true;
      } else if (this.storeCrude(in, true)) {
         return true;
      } else if (this.storeMudo(in, true)) {
         return true;
      } else if (this.storeMangler(in, true)) {
         return true;
      } else {
         this.storeAll(in);
         return false;
      }
   }
}
