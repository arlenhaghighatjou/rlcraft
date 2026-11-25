package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIParasiteFollow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.google.common.base.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityLesh extends EntityParasiteBase {
   private int noLegs;
   private int noTimes;
   private float tSize;
   private float aSize;
   private int delay;
   private boolean st1;
   private float tSpeed;
   private static final DataParameter<Float> SIZE = EntityDataManager.createKey(EntityLesh.class, DataSerializers.FLOAT);

   public EntityLesh(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 0.5F);
      this.tasks.removeTask(this.folow);
      this.noLegs = (1 + this.rand.nextInt(2)) * 2;
      this.noTimes = 1;
      this.st1 = false;
      this.tSize = 1.0F;
      this.aSize = 1.0F;
      this.fuseTime = 70;
      this.killcount = -10.0D;
      this.type = 100;
   }

   public int getParasiteIDRegister() {
      return 23;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(6, new EntityAIParasiteFollow(this, 1.3D, 10.0D, 2.0D, false));
      this.tasks.addTask(1, new EntityLesh.EntityAILeshCombine(this, 1.1D));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityLesh.class, false, false));
      this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityLivingBase.class, new Predicate<EntityLivingBase>() {
         public boolean apply(@Nullable EntityLivingBase entity) {
            return !(entity instanceof EntityWaterMob) && !(entity instanceof EntityCreeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof EntityAnimal);
         }
      }, 8.0F, 1.0D, 1.0D));
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(SIZE, Float.valueOf(this.tSize));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.infectedFollow);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAttackTarget() != null && this.ticksExisted % 20 == 0) {
         if (!this.getAttackTarget().isEntityAlive()) {
            this.setAttackTarget((EntityLivingBase)null);
         }

         if (this.getAttackTarget() instanceof EntityLesh) {
            EntityLesh meh = (EntityLesh)this.getAttackTarget();
            if (this.getTimes() + meh.getTimes() > 4) {
               this.setAttackTarget((EntityLivingBase)null);
            }
         }
      }

      if (this.gettSize() > this.getaSize()) {
         this.setaSize(0.01F);
         this.setSize(0.7F + (this.getaSize() - 1.0F), 0.5F + (this.getaSize() - 1.0F));
      }

      if (this.getTimes() >= 4 && this.ticksExisted % 20 == 0) {
         ++this.delay;
         this.st1 = true;
      }

      if (this.delay >= 4 && this.st1) {
         this.setSelfeState(1);
      }

      if (!this.world.isRemote && !this.isDead && this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth()) {
         this.setHealth(this.getHealth() + 0.007F);
      }

   }

   public void onUpdate() {
      if (this.isEntityAlive()) {
         this.lastActiveTime = this.timeSinceIgnited;
         this.dyingBurst(false, 2);
      }

      super.onUpdate();
   }

   protected void dyingBurst(boolean fromDeath, int value) {
      int i = this.getSelfeState();
      if (i > 0 && this.timeSinceIgnited == 0) {
         this.playSound(SRPSounds.FLESH_GROW, 10.0F, 1.0F);
      }

      this.timeSinceIgnited += i * value;
      if (this.timeSinceIgnited < 0) {
         this.timeSinceIgnited = 0;
      }

      if (this.timeSinceIgnited >= this.fuseTime) {
         this.timeSinceIgnited = this.fuseTime;
         this.selfExplode();
      }

   }

   protected void selfExplode() {
      if (!this.world.isRemote) {
         this.dead = true;
         this.setDead();
         this.spawnPrimitive(this, this.noLegs);
         this.playSound(SRPSounds.FLESH_PRIMITIVE, 1.0F, 1.0F);
      } else {
         this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
      }

   }

   public int getLegs() {
      return this.noLegs;
   }

   public void setLegs(int i, boolean plus) {
      if (plus) {
         this.noLegs += i;
      } else {
         this.noLegs = i;
      }
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (!(entityIn instanceof EntityLesh)) {
         return true;
      } else {
         ++this.delay;
         if (this.delay >= 3) {
            EntityLesh meh = (EntityLesh)entityIn;
            if (this.getaSize() >= meh.getaSize()) {
               if (this.getCustomNameTag() == null && meh.getCustomNameTag() != null) {
                  this.setCustomNameTag(meh.getCustomNameTag());
               }

               this.setLegs(meh.getLegs(), true);
               this.setTimes(meh.getTimes());
               meh.particleStatus((byte)6);
               meh.setDead();
               this.playSound(SRPSounds.FLESH_EAT, 1.0F, 1.0F);
               this.settSize(0.3F);
               this.resetDelay();
               this.settSpeed((float)(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getBaseValue() - 0.01D));
               this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)this.tSpeed);
               this.particleStatus((byte)5);
               this.playSound(SRPSounds.FLESH_GROW, 10.0F, 1.0F);
            }
         }

         return true;
      }
   }

   public int getTimes() {
      return this.noTimes;
   }

   public void setTimes(int in) {
      this.noTimes += in;
   }

   public float getEyeHeight() {
      return 0.3F;
   }

   public float gettSize() {
      return ((Float)this.dataManager.get(SIZE)).floatValue();
   }

   public void settSize(float in) {
      this.tSize += in;
      this.dataManager.set(SIZE, Float.valueOf(this.tSize));
   }

   public void settSpeed(float in) {
      this.tSpeed = in;
   }

   public float getaSize() {
      return this.aSize;
   }

   public void setaSize(float in) {
      this.aSize += in;
   }

   public void resetDelay() {
      this.delay = 0;
   }

   public static void registerFixesLodo(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityLesh.class);
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.FLESH_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SRPSounds.FLESH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.FLESH_DEATH;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("leshtotalsize", this.tSize);
      compound.setFloat("leshactualsize", this.aSize);
      compound.setInteger("leshlegs", this.noLegs);
      compound.setInteger("leshtimes", this.noTimes);
      compound.setFloat("leshspeed", this.tSpeed);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("leshtotalsize", 99)) {
         this.tSize = compound.getFloat("leshtotalsize");
         this.dataManager.set(SIZE, Float.valueOf(compound.getFloat("leshtotalsize")));
      }

      if (compound.hasKey("leshlegs", 99)) {
         this.noLegs = compound.getInteger("leshlegs");
      }

      if (compound.hasKey("leshtimes", 99)) {
         this.noTimes = compound.getInteger("leshtimes");
      }

      if (compound.hasKey("leshspeed", 99)) {
         this.tSpeed = compound.getFloat("leshspeed");
      }

   }

   @SideOnly(Side.CLIENT)
   public float getSelfeFlashIntensityS() {
      return this.aSize;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 4) {
         this.tSize += 0.3F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   private void spawnPrimitive(EntityLesh host, int code) {
      host.setDead();
      ParasiteEventEntity.merge(this, code, this.getCustomNameTag());
   }

   static class EntityAILeshCombine extends EntityAIBase {
      private final EntityLesh parent;

      public EntityAILeshCombine(EntityLesh animal, double speedIn) {
         this.parent = animal;
      }

      public boolean shouldExecute() {
         EntityLivingBase target = this.parent.getAttackTarget();
         if (target == null) {
            return true;
         } else {
            return !target.isEntityAlive();
         }
      }

      public void resetTask() {
      }

      public void updateTask() {
         if (this.parent.ticksExisted % 20 == 0) {
            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.parent.posX, this.parent.posY, this.parent.posZ, this.parent.posX + 1.0D, this.parent.posY + 1.0D, this.parent.posZ + 1.0D)).grow(this.parent.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue());

            for(EntityLesh mob : this.parent.world.getEntitiesWithinAABB(EntityLesh.class, axisalignedbb)) {
               if (mob != this.parent && this.parent.canEntityBeSeen(mob) && mob.isEntityAlive() && mob.getAttackingEntity() == null && mob.getTimes() + this.parent.getTimes() <= 4) {
                  this.parent.setAttackTarget(mob);
                  mob.setAttackTarget(this.parent);
                  return;
               }
            }

         }
      }
   }
}
