package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityHullAdapted extends EntityPAdapted {
   private int timer = 0;
   private static final DataParameter<Boolean> CAM = EntityDataManager.createKey(EntityHullAdapted.class, DataSerializers.BOOLEAN);

   public EntityHullAdapted(World worldIn) {
      super(worldIn);
      this.setSize(1.4F, 2.2F);
      this.stepHeight = 1.0F;
      this.adaptationCap = 0.95F;
   }

   public int getParasiteIDRegister() {
      return 52;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(CAM, Boolean.valueOf(false));
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.11D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(2, new EntityAIEvade(this, 25, 10, 4));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D, 6));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D));
      if (SRPConfig.parasiteGenResidue) {
         this.tasks.addTask(9, new EntityAIBlockResidue(this, 2));
      }

   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.HULL_HEALTH + SRPAttributes.HULL_A_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.HULL_ARMOR + SRPAttributes.HULL_A_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.HULL_ATTACK_DAMAGE + SRPAttributes.HULL_A_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.HULL_KD_RESISTANCE + SRPAttributes.HULL_A_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.adaptedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.srpTicks == 10 && !this.world.isRemote) {
         if (this.isPotionActive(MobEffects.INVISIBILITY)) {
            return;
         }

         float currentH = this.getHealth() / this.getMaxHealth();
         if (this.getSSS()) {
            this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 20, 1, false, false));
            this.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 40, 2, false, false));
            if (this.ticksExisted % 2 == 0) {
               this.playSound(SRPSounds.HULL_C, 0.2F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            if ((double)currentH < SRPConfigMobs.hulladaptedneededhealth) {
               this.setSSS(false);
            }
         } else if ((double)currentH >= SRPConfigMobs.hulladaptedneededhealth) {
            ++this.timer;
            if ((float)this.timer > SRPConfigMobs.hulladaptedneededtime) {
               this.setSSS(true);
               this.particleStatus((byte)6);
               this.timer = 0;
            }
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      this.setSSS(false);
      this.timer = 0;
      return super.attackEntityFrom(source, amount);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && this.getSSS()) {
         float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * SRPConfigMobs.hulladaptedstealthdamage;
         if (entityIn instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
         }

         entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);
         this.setSSS(false);
         this.timer = 0;
      }

      return flag;
   }

   public boolean attackEntityAsMobMinimum(EntityLivingBase entityIn) {
      boolean flag = super.attackEntityAsMobMinimum(entityIn);
      if (flag && this.getSSS()) {
         this.setSSS(false);
         this.timer = 0;
      }

      return flag;
   }

   public float getEyeHeight() {
      return 2.0F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityHull(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public boolean getSSS() {
      return ((Boolean)this.dataManager.get(CAM)).booleanValue();
   }

   public void setSSS(boolean in) {
      this.dataManager.set(CAM, Boolean.valueOf(in));
   }

   public static void registerFixesHull(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityHullAdapted.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() == 0 && !this.isPotionActive(MobEffects.INVISIBILITY) ? SRPSounds.AHULL_GROWL : SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.AHULL_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.AHULL_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.hulladaptedOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }
}
