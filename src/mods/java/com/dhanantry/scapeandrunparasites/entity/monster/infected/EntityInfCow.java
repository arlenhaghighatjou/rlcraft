package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityInfCow extends EntityPInfected implements EntityCanMelt {
   private static final DataParameter<Float> HEIGH = EntityDataManager.createKey(EntityInfCow.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> MELTING = EntityDataManager.createKey(EntityInfCow.class, DataSerializers.BOOLEAN);
   private float aSize;
   private int sound;
   private int attacking;
   private double targetX;
   private double targetY;
   private double targetZ;
   private boolean skillCharge;

   public EntityInfCow(World worldIn) {
      super(worldIn);
      this.setSize(0.9F, 1.4F);
      this.aSize = 1.0F;
      this.canModRender = 1;
      this.type = 11;
      this.fuseTime = 40;
      this.skillCharge = false;
      this.thisMelting = true;
   }

   public int getParasiteIDRegister() {
      return 13;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.08D));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.5D, false, 0.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 1, 16));
      this.tasks.addTask(2, new EntityAISkill(this, 60, 32, 8, true, 1));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFCOW_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.INFCOW_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.20000000298023224D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.INFCOW_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFCOW_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.infectedFollow);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(HEIGH, Float.valueOf(0.0F));
      this.dataManager.register(MELTING, Boolean.valueOf(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.melting();
   }

   protected void onDeathUpdate() {
      super.onDeathUpdate();
      if (this.getTHeigh() < 1.57F && !this.world.isRemote) {
         this.setTHeigh(0.17F);
      }

      if (this.deathTime == 20 && !this.world.isRemote && this.rand.nextDouble() <= SRPAttributes.INFCOW_HEADCHANCE) {
         ParasiteEventEntity.spawnM(this, new String[]{"srparasites:sim_cowhead;1;1"}, 0, false, this.getCustomNameTag());
      }

   }

   public void melt() {
      this.setWait(1000);
      this.dataManager.set(HEIGH, Float.valueOf(1.4F));
      this.dataManager.set(MELTING, Boolean.valueOf(true));
   }

   public void melting() {
      if (this.isMelting()) {
         if (this.sound % 20 == 0) {
            this.playSound(SRPSounds.INFECTED_MELT, 1.0F, 1.0F);
         }

         ++this.sound;
         if ((double)this.getTHeigh() > 0.7D) {
            this.setaSize(-0.005F);
            this.setTHeigh(-0.01F);
            this.setSize(this.width, this.getTHeigh());
         }

         if (!this.world.isRemote) {
            if ((double)this.getTHeigh() <= 0.7D || this.sound >= 73) {
               EntityLesh out = new EntityLesh(this.world);
               out.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
               if (this.getCustomNameTag() != null) {
                  out.setCustomNameTag(this.getCustomNameTag());
               }

               this.setDead();
               this.world.spawnEntity(out);
               out.setLegs(SRPAttributes.INFCOW_V, false);
            }
         } else {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
         }
      }

   }

   public boolean isMelting() {
      return ((Boolean)this.dataManager.get(MELTING)).booleanValue();
   }

   public float getTHeigh() {
      return ((Float)this.dataManager.get(HEIGH)).floatValue();
   }

   public void setTHeigh(float in) {
      in = in + this.getTHeigh();
      this.dataManager.set(HEIGH, Float.valueOf(in));
   }

   public float getaSize() {
      return this.aSize;
   }

   public void setaSize(float in) {
      this.aSize += in;
   }

   @SideOnly(Side.CLIENT)
   public float getSelfeFlashIntensity2() {
      return this.aSize;
   }

   protected void selfExplode() {
      super.selfExplode();
      ParasiteEventEntity.spawnM(this, new String[]{SRPConfigMobs.infcowmob}, 0, false, this.getCustomNameTag());
   }

   public float getEyeHeight() {
      return 1.3F;
   }

   public static void registerFixesInfHuman(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfCow.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.INFECTEDCOW_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.INFECTEDCOW_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.INFECTEDCOW_DEATH;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SoundEvents.ENTITY_COW_STEP, 0.15F, 1.0F);
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
      this.MiniDamage = 0.0F;
      if (this.attacking < 40) {
         EntityLivingBase entitylivingbase = this.getAttackTarget();
         if (entitylivingbase == null || !this.onGround || this.isInWater() || entitylivingbase.posY > this.posY) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            this.MiniDamage = SRPConfig.infectedMinDamage;
            return;
         }

         if (!entitylivingbase.isEntityAlive()) {
            this.skillCharge = true;
            this.attacking = 0;
            this.setParasiteStatus(0);
            this.MiniDamage = SRPConfig.infectedMinDamage;
            return;
         }

         if (this.attacking <= 39) {
            double dis = (double)this.getDistance(entitylivingbase);
            this.setParasiteStatus(3);
            this.getNavigator().clearPath();
            this.targetX = this.posX + 15.0D * (entitylivingbase.posX - this.posX) / dis;
            this.targetY = this.posY + 15.0D * (entitylivingbase.posY - this.posY) / dis;
            this.targetZ = this.posZ + 15.0D * (entitylivingbase.posZ - this.posZ) / dis;
         }
      }

      if (this.attacking == 40) {
         this.getNavigator().tryMoveToXYZ(this.targetX, this.targetY, this.targetZ, 2.0D);
      }

      if (this.attacking >= 40) {
         for(EntityLivingBase mob : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
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

      if (this.attacking >= 80 && this.posX == this.prevPosX && this.posZ == this.prevPosZ) {
         this.attacking = 0;
         this.skillCharge = true;
         this.setParasiteStatus(2);
         this.MiniDamage = SRPConfig.infectedMinDamage;
      }

   }
}
