package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityInfVillager extends EntityPInfected implements EntityCanMelt {
   private static final DataParameter<Float> HEIGH = EntityDataManager.createKey(EntityInfVillager.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> MELTING = EntityDataManager.createKey(EntityInfVillager.class, DataSerializers.BOOLEAN);
   private float aSize;
   private int sound;
   private int host;

   public EntityInfVillager(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 1.95F);
      this.aSize = 1.0F;
      this.sound = 0;
      this.canModRender = 1;
      this.type = 11;
      this.thisMelting = true;
   }

   public int getParasiteIDRegister() {
      return 27;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.08D));
      this.tasks.addTask(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7F, 1.5D, 3, 20, 0));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatus(this, 1.5D, false, 0.0D));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIGetFollowers(this, 1, 16));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFVILLAGER_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.INFVILLAGER_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.230000004172325D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.INFVILLAGER_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFVILLAGER_ATTACK_DAMAGE);
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
         this.setTHeigh(0.13F);
      }

      if (this.deathTime == 20 && !this.world.isRemote && this.rand.nextDouble() <= SRPAttributes.INFVILLAGER_HEADCHANCE) {
         EntityInfVillagerHead out = new EntityInfVillagerHead(this.world);
         out.copyLocationAndAnglesFrom(this);
         out.onInitialSpawn(this.world.getDifficultyForLocation(new BlockPos(this)), (IEntityLivingData)null);
         out.setSkin(this.getSkin());
         out.cannotDespawn(false);
         this.world.spawnEntity(out);
      }

   }

   public void onKillEntity(EntityLivingBase entityLivingIn) {
      if (!this.world.isRemote) {
         if (SRPConfigMobs.hostEnabled && entityLivingIn instanceof AbstractSkeleton) {
            ++this.host;
            if (this.host >= SRPConfigMobs.hostSkele) {
               this.particleStatus((byte)7);
               EntityHost host = new EntityHost(this.world);
               host.copyLocationAndAnglesFrom(this);
               host.onInitialSpawn(this.world.getDifficultyForLocation(this.getPosition()), (IEntityLivingData)null);
               this.world.spawnEntity(host);
               host.particleStatus((byte)7);
               this.setDead();
            }
         } else {
            super.onKillEntity(entityLivingIn);
         }
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      boolean flag = super.attackEntityAsMob(entityIn);
      if (flag && this.rand.nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof EntityLivingBase) {
         SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (EntityLivingBase)entityIn, 100, 0);
      }

      return flag;
   }

   public void melt() {
      this.setWait(1000);
      this.dataManager.set(HEIGH, Float.valueOf(1.95F));
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
            if ((double)this.getTHeigh() <= 0.7D || this.sound >= 127) {
               EntityLesh out = new EntityLesh(this.world);
               out.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
               if (this.getCustomNameTag() != null) {
                  out.setCustomNameTag(this.getCustomNameTag());
               }

               this.setDead();
               this.world.spawnEntity(out);
               out.setLegs(SRPAttributes.INFVILLAGER_V, false);
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

   public float getEyeHeight() {
      return 1.73F;
   }

   public static void registerFixesInfHuman(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfVillager.class);
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.INFECTEDHUMAN_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.INFECTEDHUMAN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.INFECTEDHUMAN_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("parasitehost", this.host);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasitehost", 99)) {
         this.host = compound.getInteger("parasitehost");
      }

   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      if (this.rand.nextDouble() < SRPConfig.variantChance) {
         this.setSkin(1);
      }

      return floo;
   }
}
