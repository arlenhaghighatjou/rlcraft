package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityNogla extends EntityPPrimitive {
   private int attacking;
   private double targetX;
   private double targetY;
   private double targetZ;
   private boolean skillCharge;

   public EntityNogla(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 2.6F);
      this.stepHeight = 1.0F;
      this.skillCharge = false;
   }

   public int getParasiteIDRegister() {
      return 10;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.095D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.3D, false, 8.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(2, new EntityAISkill(this, 40, 32, 8, true, 1));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 2, 16));
      this.tasks.addTask(2, new EntityAIEvade(this, 55, 10, 4));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.NOGLA_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.NOGLA_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.31234D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.NOGLA_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.NOGLA_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.primitiveFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.world.isRemote && this.ticksExisted % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(this.world), true, true);
      }

   }

   protected void collideWithEntity(Entity entityIn) {
      super.collideWithEntity(entityIn);
      if (!this.world.isRemote) {
         if (entityIn instanceof EntityLivingBase && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
         }

      }
   }

   public float getEyeHeight() {
      return 2.4F;
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote) {
         if (SRPConfigWorld.coloniesActivated) {
            if (ParasiteEventWorld.numberofColonies(this.world) >= 1) {
               ParasiteEventEntity.checkColony(this.world, cause, this);
               ParasiteEventEntity.spawnNext(this, new EntityLesh(this.world), true, false);
            } else {
               super.onDeath(cause);
            }
         } else {
            super.onDeath(cause);
         }
      }

   }

   public static void registerFixesNogla(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityNogla.class);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && entityIn instanceof EntityLivingBase) {
         ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 100));
         switch(this.getSkin()) {
         case 5:
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (EntityLivingBase)entityIn, 40, 0);
            break;
         case 6:
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 40, 0);
         }
      }

      return flag;
   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      super.onKillEntity(entityLivingIn);
      this.particleStatus((byte)5);
      if (!this.world.isRemote && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
         ParasiteEventEntity.spawnNext(this, new EntityNoglaAdapted(this.world), true, true);
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.NOGLA_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.NOGLA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.NOGLA_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.noglaOrbEffects, mobs);
      }

      return flag;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SRPSounds.MONSTER_STEP, 0.15F, 1.0F);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         switch(this.rand.nextInt(3)) {
         case 0:
            this.setSkin(5);
            break;
         case 1:
            this.setSkin(6);
            break;
         case 2:
            this.setSkin(1);
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.NOGLA_HEALTH * 0.5D);
            this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.NOGLA_ATTACK_DAMAGE * 1.5D);
            this.setHealth((float)this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue());
         }
      }

      return floo;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      switch(id) {
      case 100:
         for(int i = 0; i <= 1; ++i) {
            this.spawnParticles(EnumParticleTypes.FLAME);
         }

         return;
      default:
         super.handleStatusUpdate(id);
      }
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillCharge;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillCharge = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.charge();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void charge() {
      ++this.attacking;
      if (this.attacking < 20) {
         this.world.setEntityState(this, (byte)100);
         if (this.attacking == 2) {
            float v = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 2.0F;
            this.playSound(this.getHurtSound(DamageSource.GENERIC), 4.0F, v);
         }

         EntityLivingBase entitylivingbase = this.getAttackTarget();
         if (entitylivingbase == null || !this.onGround || this.isInWater() || entitylivingbase.posY > this.posY && entitylivingbase.onGround) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            return;
         }

         if (!entitylivingbase.isEntityAlive()) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            return;
         }

         if (this.attacking <= 19) {
            double dis = (double)this.getDistance(entitylivingbase);
            this.setParasiteStatus(3);
            this.getNavigator().clearPath();
            this.targetX = this.posX + 15.0D * (entitylivingbase.posX - this.posX) / dis;
            this.targetY = this.posY + 15.0D * (entitylivingbase.posY - this.posY) / dis;
            this.targetZ = this.posZ + 15.0D * (entitylivingbase.posZ - this.posZ) / dis;
         }
      }

      if (this.attacking == 20) {
         this.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, 2.5D);
      }

      if (this.attacking >= 20) {
         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(2.0D, 0.0D, 2.0D))) {
            if (mob != this && !(mob instanceof EntityParasiteBase)) {
               float f = (float)MathHelper.atan2(mob.posZ - this.posZ, mob.posX - this.posX);
               EntityDamage damage = new EntityDamage(this.world, mob.posX, mob.posY, mob.posZ, f, this, 1.0F, false, 0.5F);
               this.world.spawnEntity(damage);
            }
         }
      }

      this.skillBreakBlocks();
      if (!this.onGround) {
         this.motionX *= 0.7D;
         this.motionZ *= 0.7D;
      }

      if (this.attacking >= 60 && this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
         this.attacking = 0;
         this.skillCharge = true;
         this.setParasiteStatus(2);
      }

   }
}
