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

public class EntityDodSII extends EntityPDispatcher {
   public EntityDodSII(World worldIn) {
      super(worldIn);
      this.setSize(3.2F, 3.6F);
      this.ignoreFrustumCheck = true;
      this.experienceValue = SRPAttributes.XP_PRIMITIVE * 2;
      this.buried = 0.1D;
      this.setParasiteStatus(3);
      this.damageCap = SRPConfig.nexussiiCap;
      this.pointCap = SRPConfig.nexussiiPointCap;
      this.pointReduction = SRPConfig.nexussiiPointRed;
      this.chanceLearn = SRPConfig.nexussiiChanceLe;
      this.chanceLearnFire = SRPConfig.nexussiiChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussiiPointDamCap;
      this.totalP = 5;
      this.mobID = new int[5];
      this.mobPT = new int[5];
      this.stage = 2;
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.neededTime = this.setGT(SRPConfig.nexussiiMinGrowTime, SRPConfig.nexussiiMaxGrowTime);
      this.valueEvDeath = SRPConfig.nexussiiLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 77;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(4, new EntityAINexusGrow(this, 2, (byte)2));
      this.tasks.addTask(2, new EntityAIDodAttack(this, 2, (byte)32, 15.0F));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DODSII_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.DODSII_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DODSII_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussiiFollow * (double)SRPConfigMobs.dodsiiFollowRangeMult);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   public float getEyeHeight() {
      return 3.4F;
   }

   public static void registerFixesUnvo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityDodSII.class);
   }

   public float getBombDamage() {
      return (float)SRPAttributes.DODSII_ATTACK_DAMAGE;
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
