package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAncientSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDodAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;

public class EntityDodSIV extends EntityPDispatcher implements EntityBodyParts {
   private EntityBodyModel head;
   private int ticksss;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);

   public EntityDodSIV(World worldIn) {
      super(worldIn);
      this.setSize(4.7F, 5.5F);
      this.ignoreFrustumCheck = true;
      this.experienceValue = SRPAttributes.XP_ADAPTED * 4;
      this.buried = 0.1D;
      this.setParasiteStatus(3);
      this.damageCap = SRPConfig.nexussivCap;
      this.pointCap = SRPConfig.nexussivPointCap;
      this.pointReduction = SRPConfig.nexussivPointRed;
      this.chanceLearn = SRPConfig.nexussivChanceLe;
      this.chanceLearnFire = SRPConfig.nexussivChanceLeFire;
      this.DamageTypeCap = SRPConfig.nexussivPointDamCap;
      this.totalP = 3;
      this.mobID = new int[3];
      this.mobPT = new int[3];
      this.stage = 4;
      this.experienceValue = SRPAttributes.XP_INFECTED * 2;

      for(int i = 0; i < this.mobID.length; ++i) {
         this.mobID[i] = -777;
      }

      this.head = new EntityBodyModel(this, 1.2F, 2.5F, 1.0F, 0.0F, 6.3F, -1, 1, false, 0.2F);
      this.head.setSkin(1);
      this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
   }

   public int getParasiteIDRegister() {
      return 79;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(2, new EntityAIDodAttack(this, 4, (byte)52, 30.0F));
      this.tasks.addTask(4, new EntityAIAncientSummon(this, 120, 4, new String[]{"srparasites:ancientpod;1;1"}));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.DODSIV_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.DODSIV_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DODSIV_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.nexussivFollow * (double)SRPConfigMobs.dodsivFollowRangeMult);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.head.onUpdate();
      this.placeColony();
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.rand.nextBoolean()) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, this, 80, 0);
      }

      return this.attackEntityFrom(source, amount * 3.0F);
   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      super.setDead();
   }

   public float getEyeHeight() {
      return 2.5F;
   }

   private void placeColony() {
      if (this.rand.nextInt(10) == 0) {
         if (this.ticksExisted >= 1200) {
            if (!this.world.isRemote) {
               ++this.ticksss;
               if (this.ticksss >= 200) {
                  this.ticksss = 0;
                  if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
                     this.ticksss = -1000;
                  } else {
                     int range = 7;
                     int attemp = 3;
                     int mini = 5;

                     while(attemp > 0) {
                        --attemp;
                        double randomx = (double)this.rand.nextInt(range);
                        double randomz = (double)this.rand.nextInt(range);
                        double negative = (double)this.rand.nextInt(2);
                        if (negative == 0.0D) {
                           randomx = randomx * -1.0D - (double)mini;
                        } else {
                           randomx = randomx + (double)mini;
                        }

                        negative = (double)this.rand.nextInt(2);
                        if (negative == 0.0D) {
                           randomz = randomz * -1.0D - (double)mini;
                        } else {
                           randomz = randomz + (double)mini;
                        }

                        BlockPos pos = new BlockPos(this.posX + randomx, this.posY, this.posZ + randomz);
                        pos = ParasiteEventEntity.getFloor(this.world, pos, 5);
                        if (pos != null && ParasiteEventWorld.placeColonyInWorld(this.world, pos) == 1) {
                           this.ticksss = -1000;
                           return;
                        }
                     }

                     this.ticksss = -100;
                  }
               }
            }
         }
      }
   }

   protected void updateAITasks() {
      super.updateAITasks();
      this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag && source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayerMP) {
         this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
      }

      return flag;
   }

   public void setBodyPartDead(int id) {
   }

   public float getBombDamage() {
      return (float)SRPAttributes.DODSIV_ATTACK_DAMAGE;
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
