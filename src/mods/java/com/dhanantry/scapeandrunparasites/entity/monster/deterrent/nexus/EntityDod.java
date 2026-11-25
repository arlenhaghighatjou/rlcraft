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

public class EntityDod extends EntityPDispatcher {
   public EntityDod(World worldIn) {
      super(worldIn);
      this.setSize(2.7F, 2.5F);
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;
      this.buried = 0.1D;
      this.setParasiteStatus(3);
      this.damageCap = SRPConfig.nexussiCap;
      this.pointCap = SRPConfig.nexussiPointCap;
      this.pointReduction = SRPConfig.nexussiPointRed;
      this.chanceLearn = SRPConfig.nexussiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiPointDamCap;
      this.totalP = 3;
      this.mobID = new int[3];
      this.mobPT = new int[3];
      this.stage = 1;
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 73;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 1, (byte)2));
      this.tasks.addTask(2, new EntityAIDodAttack(this, 1, (byte)16, 10.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DOD_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.DOD_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DOD_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiFollow * (double)SRPConfigMobs.dodsiFollowRangeMult);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public float getEyeHeight() {
      return 2.3F;
   }

   public static void registerFixesUnvo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityDod.class);
   }

   public float getBombDamage() {
      return (float)SRPAttributes.DOD_ATTACK_DAMAGE;
   }

   public boolean storeParasite(EntityParasiteBase in) {
      if (super.storeParasite(in)) {
         return true;
      } else if (this.storeLodo(in, true)) {
         return true;
      } else if (this.storeInf(in, false)) {
         return true;
      } else if (this.storeCrude(in, false)) {
         return true;
      } else if (this.storeMudo(in, false)) {
         return true;
      } else if (this.storeMangler(in, false)) {
         return true;
      } else {
         this.storeAll(in);
         return false;
      }
   }
}
