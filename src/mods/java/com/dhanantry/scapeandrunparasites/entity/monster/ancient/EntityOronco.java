package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAncientSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAncientball;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;

public class EntityOronco extends EntityPAncient implements EntityCanShoot {
   private static final DataParameter<Boolean> URTEN = EntityDataManager.createKey(EntityOronco.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> ULTEN = EntityDataManager.createKey(EntityOronco.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> RATEN = EntityDataManager.createKey(EntityOronco.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> LATEN = EntityDataManager.createKey(EntityOronco.class, DataSerializers.BOOLEAN);
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOronco.class, DataSerializers.BYTE);
   private boolean health80;
   private boolean health60;
   private boolean health40;
   private boolean health20;
   private float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
   private int maxY = 20;
   private int minY = 7;
   private EntityAIFlightLimits flightLimit;
   private final BossInfoServer bossInfo;

   public EntityOronco(World worldIn) {
      super(worldIn);
      this.flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
      this.bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);
      this.setSize(4.0F, 4.0F);
      this.type = 62;
      this.borderOrb = -1;
      this.tasks.removeTask(this.folow);
      this.tasks.removeTask(this.aiWander);
      this.ignoreFrustumCheck = true;
      this.moveHelper = new EntityOronco.AIMoveControl(this);
      this.setNoGravity(true);
      this.health80 = true;
      this.health60 = true;
      this.health40 = true;
      this.health20 = true;
   }

   public int getParasiteIDRegister() {
      return 24;
   }

   protected void initEntityAI() {
      this.tasks.addTask(2, new EntityOronco.AIMoveRandom());
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(5, new EntityAIAttackProjectile(this, 60, 20, 3, true));
      this.tasks.addTask(4, new EntityAIAncientSummon(this, 20 * SRPConfigMobs.oroncoPodCooldown, SRPConfigMobs.oroncoPodNumber, new String[]{"srparasites:ancientpod;1;1"}));
      this.tasks.addTask(4, new EntityAIFlightLimits(this, this.minY, false));
      this.tasks.addTask(5, new EntityAIFlightAttack(this, 64.0D));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.ORONCO_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.ORONCO_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(SRPConfig.ancientFollow);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(URTEN, Boolean.valueOf(true));
      this.dataManager.register(ULTEN, Boolean.valueOf(true));
      this.dataManager.register(RATEN, Boolean.valueOf(true));
      this.dataManager.register(LATEN, Boolean.valueOf(true));
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.world.isRemote) {
         this.moveHelper.setMoveTo(this.posX, this.posY + 5.0D, this.posZ, 0.5D);
      }

      if (!this.world.isRemote && this.getAttackTarget() != null && this.ticksExisted % 30 == 0) {
         for(EntityLivingBase entitylivingbase : this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(3.0D, 3.0D, 3.0D))) {
            if (entitylivingbase != this && !(entitylivingbase instanceof EntityParasiteBase)) {
               float f = (float)MathHelper.atan2(entitylivingbase.posZ - this.posZ, entitylivingbase.posX - this.posX);
               EntityDamage damage = new EntityDamage(this.world, entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, f, this, 1.0F, false, 2.5F);
               this.world.spawnEntity(damage);
            }
         }
      }

   }

   public void onUpdate() {
      super.onUpdate();
      this.setNoGravity(true);
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
      if (flag && source != DamageSource.OUT_OF_WORLD) {
         if (source.getTrueSource() instanceof EntityPlayerMP) {
            this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
         }

         if ((double)this.getMaxHealth() * 0.8D >= (double)this.getHealth() && this.health80) {
            this.damageBody(this.rand.nextInt(4));
            this.health80 = false;
            this.setParasiteStatus(77);
            this.setWait(30);
         } else if ((double)this.getMaxHealth() * 0.6D >= (double)this.getHealth() && this.health60) {
            this.damageBody(this.rand.nextInt(4));
            this.health60 = false;
            this.setParasiteStatus(77);
            this.setWait(30);
         } else if ((double)this.getMaxHealth() * 0.4D >= (double)this.getHealth() && this.health40) {
            this.damageBody(this.rand.nextInt(4));
            this.health40 = false;
            this.setParasiteStatus(77);
            this.setWait(30);
         } else if ((double)this.getMaxHealth() * 0.2D >= (double)this.getHealth() && this.health20) {
            this.damageBody(this.rand.nextInt(4));
            this.health20 = false;
            this.setParasiteStatus(77);
            this.setWait(30);
         }
      }

      return flag;
   }

   private void damageBody(int in) {
      if (in == 0 && this.livingTENUR()) {
         this.livingTENUR(false);
         EntityOroncoTen ten = new EntityOroncoTen(this.world);
         ten.copyLocationAndAnglesFrom(this);
         this.world.spawnEntity(ten);
         ten.setSkin(1);
      } else if (in == 1 && this.livingTENUL()) {
         this.livingTENUL(false);
         EntityOroncoTen ten = new EntityOroncoTen(this.world);
         ten.copyLocationAndAnglesFrom(this);
         this.world.spawnEntity(ten);
         ten.setSkin(1);
      } else if (in == 2 && this.livingTENRA()) {
         this.livingTENRA(false);
         EntityOroncoTen ten = new EntityOroncoTen(this.world);
         ten.copyLocationAndAnglesFrom(this);
         this.world.spawnEntity(ten);
      } else if (in == 3 && this.livingTENLA()) {
         this.livingTENLA(false);
         EntityOroncoTen ten = new EntityOroncoTen(this.world);
         ten.copyLocationAndAnglesFrom(this);
         this.world.spawnEntity(ten);
      } else {
         in = (in + 1) % 4;
         this.damageBody(in);
      }

   }

   public int getHorizontalFaceSpeed() {
      return 3;
   }

   public float getDamageR() {
      return this.damageR;
   }

   public float getEyeHeight() {
      return 2.1F;
   }

   protected SoundEvent getAmbientSound() {
      return SRPSounds.ORONCO_GROWL;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.rand.nextBoolean() && this.getHitStatus() > 0 ? SRPSounds.MOBSILENCE : SRPSounds.ORONCO_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.ORONCO_DEATH;
   }

   public boolean scaryOrbEffect(EntityLivingBase in, int mobs) {
      boolean flag = super.scaryOrbEffect(in, mobs);
      if (flag) {
         ;
      }

      return flag;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   public static void registerFixesOronco(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityOronco.class);
   }

   public boolean livingTENUR() {
      return ((Boolean)this.dataManager.get(URTEN)).booleanValue();
   }

   public boolean livingTENUL() {
      return ((Boolean)this.dataManager.get(ULTEN)).booleanValue();
   }

   public boolean livingTENRA() {
      return ((Boolean)this.dataManager.get(RATEN)).booleanValue();
   }

   public boolean livingTENLA() {
      return ((Boolean)this.dataManager.get(LATEN)).booleanValue();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("urten", ((Boolean)this.dataManager.get(URTEN)).booleanValue());
      compound.setBoolean("ulten", ((Boolean)this.dataManager.get(ULTEN)).booleanValue());
      compound.setBoolean("raten", ((Boolean)this.dataManager.get(RATEN)).booleanValue());
      compound.setBoolean("laten", ((Boolean)this.dataManager.get(LATEN)).booleanValue());
      compound.setBoolean("healtheight", this.health80);
      compound.setBoolean("healthsix", this.health60);
      compound.setBoolean("healthfour", this.health40);
      compound.setBoolean("healthtwo", this.health20);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("urten", 99)) {
         this.livingTENUR(compound.getBoolean("urten"));
      }

      if (compound.hasKey("ulten", 99)) {
         this.livingTENUL(compound.getBoolean("ulten"));
      }

      if (compound.hasKey("raten", 99)) {
         this.livingTENRA(compound.getBoolean("raten"));
      }

      if (compound.hasKey("laten", 99)) {
         this.livingTENLA(compound.getBoolean("laten"));
      }

      if (compound.hasKey("healtheight", 99)) {
         this.health80 = compound.getBoolean("healtheight");
      }

      if (compound.hasKey("healthsix", 99)) {
         this.health60 = compound.getBoolean("healthsix");
      }

      if (compound.hasKey("healthfour", 99)) {
         this.health40 = compound.getBoolean("healthfour");
      }

      if (compound.hasKey("healthtwo", 99)) {
         this.health20 = compound.getBoolean("healthtwo");
      }

   }

   public void livingTENUR(boolean in) {
      this.dataManager.set(URTEN, Boolean.valueOf(in));
   }

   public void livingTENUL(boolean in) {
      this.dataManager.set(ULTEN, Boolean.valueOf(in));
   }

   public void livingTENRA(boolean in) {
      this.dataManager.set(RATEN, Boolean.valueOf(in));
   }

   public void livingTENLA(boolean in) {
      this.dataManager.set(LATEN, Boolean.valueOf(in));
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public boolean isOnLadder() {
      return false;
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   private boolean getVexFlag(int mask) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      return (i & mask) != 0;
   }

   private void setVexFlag(int mask, boolean value) {
      int i = ((Byte)this.dataManager.get(VEX_FLAGS)).byteValue();
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(VEX_FLAGS, Byte.valueOf((byte)(i & 255)));
   }

   public EntityFireball getProj(double accelX, double accelY, double accelZ) {
      this.playSound(SRPSounds.ORONCO_SHOOTINGREAL, 2.0F, 1.0F);
      return new EntityProjectileAncientball(this.world, this, accelX, accelY, accelZ);
   }

   public void playProjSound() {
      this.playSound(SRPSounds.ORONCO_SHOOTING, 4.0F, 1.0F);
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityOronco vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityOronco.this.posX;
            double d1 = this.posY - EntityOronco.this.posY;
            double d2 = this.posZ - EntityOronco.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityOronco.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityOronco.this.motionX *= 0.5D;
               EntityOronco.this.motionY *= 0.5D;
               EntityOronco.this.motionZ *= 0.5D;
            } else {
               EntityOronco.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityOronco.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityOronco.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityOronco.this.getAttackTarget() == null) {
                  EntityOronco.this.rotationYaw = -((float)MathHelper.atan2(EntityOronco.this.motionX, EntityOronco.this.motionZ)) * 57.295776F;
                  EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw;
               } else {
                  double d4 = EntityOronco.this.getAttackTarget().posX - EntityOronco.this.posX;
                  double d5 = EntityOronco.this.getAttackTarget().posZ - EntityOronco.this.posZ;
                  EntityOronco.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityOronco.this.renderYawOffset = EntityOronco.this.rotationYaw;
               }
            }
         }

      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         EntityMoveHelper entitymovehelper = EntityOronco.this.getMoveHelper();
         if (!entitymovehelper.isUpdating()) {
            return true;
         } else {
            double d0 = entitymovehelper.getX() - EntityOronco.this.posX;
            double d1 = entitymovehelper.getY() - EntityOronco.this.posY;
            double d2 = entitymovehelper.getZ() - EntityOronco.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         if (EntityOronco.this.getAttackTarget() != null) {
            BlockPos blockpos = new BlockPos(EntityOronco.this);
            byte flag = 1;
            double speed = 0.3D;
            if (EntityOronco.this.getDistanceSq(EntityOronco.this.getAttackTarget()) > 400.0D) {
               blockpos = new BlockPos(EntityOronco.this.getAttackTarget());
               flag = 2;
               speed += 0.1D;
            } else if (EntityOronco.this.getDistanceSq(EntityOronco.this.getAttackTarget()) < 100.0D) {
               blockpos = new BlockPos(EntityOronco.this.getAttackTarget());
               flag = 3;
               speed += 0.15D;
            }

            for(int i = 0; i < 3; ++i) {
               BlockPos blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(15) - 7, EntityOronco.this.rand.nextInt(11) - 5, EntityOronco.this.rand.nextInt(15) - 7);
               if (flag == 2) {
                  blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(12) - 2, EntityOronco.this.rand.nextInt(10) - 2, EntityOronco.this.rand.nextInt(12) - 2);
               } else if (flag == 3) {
                  blockpos1 = blockpos.add(EntityOronco.this.rand.nextInt(8) + 3, EntityOronco.this.rand.nextInt(7) + 4, EntityOronco.this.rand.nextInt(8) + 3);
               }

               if (EntityOronco.this.world.isAirBlock(blockpos1)) {
                  EntityOronco.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
                  if (EntityOronco.this.getAttackTarget() == null) {
                     EntityOronco.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                  }
                  break;
               }
            }
         } else {
            Random random = EntityOronco.this.getRNG();
            double d0 = EntityOronco.this.posX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = EntityOronco.this.posY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = EntityOronco.this.posZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            EntityOronco.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.3D);
         }

      }
   }
}
