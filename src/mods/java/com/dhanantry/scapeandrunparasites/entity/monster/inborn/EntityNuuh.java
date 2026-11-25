package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class EntityNuuh extends EntityPMalleable {
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityNuuh.class, DataSerializers.BYTE);

   public EntityNuuh(World worldIn) {
      super(worldIn);
      this.setSize(1.0F, 1.0F);
      this.borderOrb = -1;
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, false, (Predicate)null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
      }

      this.experienceValue = SRPAttributes.XP_PURE;
      this.type = 51;
      this.foodSteal = SRPConfig.pureFoodSteal;
      this.pointCap = SRPConfig.purePointCap;
      this.pointReduction = SRPConfig.purePointRed;
      this.chanceLearn = SRPConfig.pureChanceLe;
      this.chanceLearnFire = SRPConfig.pureChanceLeFire;
      this.DamageTypeCap = SRPConfig.purePointDamCap;
      this.MiniDamage = SRPConfigMobs.nuuhMinDamage;
      this.regen = SRPConfig.pureRegen * SRPConfig.globalHealthMultiplier;
      this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
      this.regenEff = 3;
      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 76;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 20, 100, 5, true, 14));
      this.setskillLeapValues(0.8F, 2.0D, 0);
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.12D));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, -1.0D, 6));
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(2, new EntityAIEvadeDash(this, 10, 2, 1, 1.0D, 15));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.NUUH_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.NUUH_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.37D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.NUUH_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.NUUH_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 3));
         if (entityIn instanceof EntityLivingBase && this.rand.nextDouble() < (double)SRPConfigSystems.cothPure) {
            EntityLivingBase target = (EntityLivingBase)entityIn;
            if (!target.isPotionActive(SRPPotions.COTH_E)) {
               if (target.isPotionActive(SRPPotions.EPEL_E)) {
                  if (this.rand.nextDouble() >= SRPConfigSystems.epelChanceCOTH) {
                     target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
                  }
               } else {
                  target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
               }
            }
         }
      }

      return flag;
   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 200.0F) {
         super.fall(distance, damageMultiplier);
      }

   }

   public void setInWeb() {
   }

   public double getMountedYOffset() {
      return (double)(this.height * 0.5F);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new PathNavigateClimber(this, worldIn);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote) {
         this.setBesideClimbableBlock(this.collidedHorizontally);
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CLIMBING, Byte.valueOf((byte)0));
   }

   public boolean isOnLadder() {
      return this.isBesideClimbableBlock();
   }

   public boolean isBesideClimbableBlock() {
      return (((Byte)this.dataManager.get(CLIMBING)).byteValue() & 1) != 0;
   }

   public void setBesideClimbableBlock(boolean climbing) {
      byte b0 = ((Byte)this.dataManager.get(CLIMBING)).byteValue();
      if (climbing) {
         b0 = (byte)(b0 | 1);
      } else {
         b0 = (byte)(b0 & -2);
      }

      this.dataManager.set(CLIMBING, Byte.valueOf(b0));
   }

   public float getEyeHeight() {
      return 0.9F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityMudo(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesMudo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityNuuh.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.MOBSILENCE;
   }
}
