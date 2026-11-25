package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityDodT extends EntityPStationary {
   private String paraToRelo;
   private EntityPDispatcher father;
   private EntityParasiteBase tele;
   private int stage;

   public EntityDodT(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 2.5F);
      this.experienceValue = 0;
      this.type = 40;
      this.buriedT = 4.0D;
      this.damageCap = SRPConfig.turretCap;
      this.pointCap = SRPConfig.turretPointCap;
      this.pointReduction = SRPConfig.turretPointRed;
      this.chanceLearn = SRPConfig.turretChanceLe;
      this.chanceLearnFire = SRPConfig.turretChanceLeFire;
      this.DamageTypeCap = SRPConfig.turretPointDamCap;
      this.MiniDamage = SRPConfig.turretMinDamage;
      this.regen = SRPConfig.turretRegen;
      this.valueEvDeath = 0;
   }

   public int getParasiteIDRegister() {
      return 74;
   }

   protected void initEntityAI() {
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote) {
         if (this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) > 256.0D) {
            this.world.setEntityState(this, (byte)51);
            this.up = true;
         }

         if (this.ticksExisted > 120 && this.paraToRelo == null && this.tele == null) {
            this.world.setEntityState(this, (byte)51);
            this.up = true;
         }

         if (!this.up && this.ticksExisted > 60 && !this.buried()) {
            if (this.paraToRelo != null) {
               EntityParasiteBase entityout = (EntityParasiteBase)EntityList.createEntityByIDFromName(new ResourceLocation(this.paraToRelo), this.world);
               entityout.copyLocationAndAnglesFrom(this);
               entityout.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(entityout)), (IEntityLivingData)null);
               this.world.spawnEntity(entityout);
               this.applyEffectsDod(entityout);
               this.paraToRelo = null;
               entityout.particleStatus((byte)8);
               entityout.particleStatus((byte)8);
               if (this.isBurning()) {
                  entityout.setHealth(entityout.getMaxHealth() * 0.5F);
                  entityout.setFire(8);
               }

               if (this.father != null && this.father.isEntityAlive()) {
                  entityout.setDodFatherID(this.father.getEntityId());
               }

               this.particleStatus((byte)8);
               this.particleStatus((byte)8);
               this.particleStatus((byte)8);
            } else if (this.tele != null) {
               this.tele.copyLocationAndAnglesFrom(this);
               this.tele.particleStatus((byte)7);
               this.particleStatus((byte)7);
               this.tele = null;
            }
         }
      }

   }

   protected void retreat(boolean dead) {
      if (this.up) {
         this.buried += 0.08D;
         if (this.buried > this.buriedT && !this.world.isRemote) {
            this.setDead();
         }
      }

   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (this.father != null && this.father.isEntityAlive()) {
         this.father.addParaBack(this.paraToRelo);
      }

   }

   public void setTele(String in, int s) {
      this.paraToRelo = in;
      this.stage = s;
   }

   public void setTele(EntityParasiteBase in) {
      this.tele = in;
   }

   public String getTele() {
      return this.paraToRelo;
   }

   protected void applyEffectsDod(EntityParasiteBase in) {
      String[] pots = SRPConfigMobs.dodsiEffects;
      switch(this.stage) {
      case 2:
         pots = SRPConfigMobs.dodsiiEffects;
         break;
      case 3:
         pots = SRPConfigMobs.dodsiiiEffects;
         break;
      case 4:
         pots = SRPConfigMobs.dodsivEffects;
      }

      if (pots.length != 0) {
         String[] here = new String[3];

         for(String i : pots) {
            here = i.split(";");
            int ticks = Integer.parseInt(here[0]);
            int amp = Integer.parseInt(here[2]);
            Potion potion = Potion.getPotionFromResourceLocation(here[1]);
            if (potion != null) {
               in.addPotionEffect(new PotionEffect(potion, ticks, amp));
            }
         }

      }
   }

   public float getEyeHeight() {
      return 1.8F;
   }

   public static void registerFixesTonro(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityDodT.class);
   }
}
