package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityMudo extends EntityParasiteBase {
   private static final DataParameter<Byte> CLIMBING = EntityDataManager.createKey(EntityMudo.class, DataSerializers.BYTE);

   public EntityMudo(World worldIn) {
      super(worldIn);
      this.setSize(0.85F, 1.0F);
      this.type = 5;
      this.MiniDamage = SRPConfigMobs.mudoMinDamage;
      this.experienceValue = SRPAttributes.XP_LiTTLE;
   }

   public int getParasiteIDRegister() {
      return 12;
   }

   public void applyBonuses(int colony, int node, byte phase) {
      super.applyBonuses(colony, node, phase);
      this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityPlayer.class, 0, true, false, (Predicate)null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      if (SRPConfig.mobattacking) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 0, true, false, new Predicate<EntityLiving>() {
            public boolean apply(@Nullable EntityLiving entity) {
               return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
            }
         }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
      }

      if (SRPConfigSystems.useEvolution) {
         if (phase >= SRPConfigSystems.evolutionMudoAttack) {
            if (phase >= SRPConfigSystems.evolutionAssimilatedDehiding) {
               this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 10, true, false, new Predicate<EntityLiving>() {
                  public boolean apply(@Nullable EntityLiving entity) {
                     return !(entity instanceof EntityMob) && !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                  }
               }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
            } else {
               this.targetTasks.addTask(4, new EntityAINearestAttackableTargetStatus(this, EntityLiving.class, 10, true, false, new Predicate<EntityLiving>() {
                  public boolean apply(@Nullable EntityLiving entity) {
                     return !entity.isPotionActive(SRPPotions.COTH_E) && !(entity instanceof EntityMob) && !(entity instanceof EntityWaterMob) && !ParasiteEventEntity.checkEntity(entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                  }
               }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
            }
         } else {
            this.tasks.addTask(2, new EntityMudo.EntityAIMudoInfest(this, 1.0D));
            this.tasks.addTask(4, new EntityAIAvoidOrAttack(this, 0.0F));
            this.tasks.addTask(5, new EntityAIAvoidEntityStatus(this, EntityLiving.class, new Predicate<EntityLiving>() {
               public boolean apply(@Nullable EntityLiving entity) {
                  return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal) && !(entity instanceof EntityVillager);
               }
            }, 8.0F, 1.3D));
         }
      }

   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISkill(this, 40, 100, 5, true, 14));
      this.setskillLeapValues(0.7F, 2.5D, 0);
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, -1.0D, 10));
      this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      if (SRPConfigMobs.mudoAnimalAttacking && !SRPConfigSystems.useEvolution) {
         this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityAnimal.class, true));
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.srpTicks == 10 && !this.world.isRemote && this.killcount >= (double)SRPConfigMobs.mudoTunnelValue && this.phaseCreated < SRPConfigMobs.mudoTunnelPhase && this.rand.nextInt(30) == 0) {
         if (this.getAttackTarget() != null) {
            return;
         }

         if (this.world.getBlockState(this.getPosition()).getBlock() == Blocks.AIR && this.world.getBlockState(this.getPosition().down()).isFullBlock() && this.world.getBlockState(this.getPosition().down()).isFullCube()) {
            this.world.setBlockState(this.getPosition(), SRPBlocks.buglin.getDefaultState());
            this.killcount -= (double)SRPConfigMobs.mudoTunnelValue;
         }
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.MUDO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.MUDO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.MUDO_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.MUDO_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 1));
         if (!((EntityLivingBase)entityIn).isPotionActive(SRPPotions.COTH_E)) {
            ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
         }
      }

      return flag;
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (this.killcount >= 0.0D) {
         ++this.killcount;
      }

      if (!this.world.isRemote && SRPConfigSystems.useEvolution) {
         SRPWorldData data = SRPWorldData.get(this.world);
         data.setTotalKills(SRPConfigSystems.valueKill, true, this.world, true);
      }

      if (!entityLivingIn.isPotionActive(SRPPotions.COTH_E)) {
         entityLivingIn.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
      }

      ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getEntityData(), true, SRPConfigSystems.COTHVictimParasite);
      this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 80, 0, false, false));
      this.setWait(10);
      if (!this.world.isRemote && this.killcount > (double)SRPConfigMobs.mudoMangler && !this.isDead && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityNuuh(this.world), true, false);
      }

   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 60.0F) {
         super.fall(distance, damageMultiplier);
      }

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
         if (this.srpTicks == 10 && this.killcount > (double)SRPConfigMobs.mudoMangler && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNuuh(this.world), true, true);
         }
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
      return 0.8F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1 && SRPConfigMobs.lodoEnabled) {
               ParasiteEventEntity.spawnNext(this, new EntityLodo(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesMudo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityMudo.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.MUDO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.MUDO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.MUDO_DEATH;
   }

   static class EntityAIMudoInfest extends EntityAIBase {
      private final EntityMudo parent;
      private int count;

      public EntityAIMudoInfest(EntityMudo animal, double speedIn) {
         this.parent = animal;
         this.count = 0;
         this.setMutexBits(3);
      }

      public boolean shouldExecute() {
         ++this.count;
         if (this.count >= 20) {
            this.count = 0;
            return this.parent.getAttackTarget() == null;
         } else {
            return false;
         }
      }

      public void updateTask() {
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(10.0D, 2.0D, 10.0D);

         for(EntityLivingBase mob : this.parent.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb)) {
            if ((mob instanceof EntityAnimal || mob instanceof EntityVillager) && !mob.isPotionActive(SRPPotions.COTH_E) && this.parent.canEntityBeSeen(mob) && this.parent.getNavigator().tryMoveToEntityLiving(mob, 1.3D) && this.parent.getDistanceSq(mob) < 9.0D) {
               this.spawnLingeringCloud();
               this.parent.playSound(SRPSounds.MUDO_CLOUD, 2.0F, 1.0F);
            }
         }

      }

      private void spawnLingeringCloud() {
         EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.parent.world, this.parent.posX, this.parent.posY, this.parent.posZ);
         entityareaeffectcloud.setRadius(this.parent.width * 3.0F);
         entityareaeffectcloud.setRadiusOnUse(-0.5F);
         entityareaeffectcloud.setWaitTime(10);
         entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
         entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 1, false, false));
         this.parent.world.spawnEntity(entityareaeffectcloud);
      }
   }
}
