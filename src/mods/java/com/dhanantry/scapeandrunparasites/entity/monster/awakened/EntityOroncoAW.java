package com.dhanantry.scapeandrunparasites.entity.monster.awakened;

import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayerMP;
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

public class EntityOroncoAW extends EntityPAncient {
   private static final DataParameter<Boolean> URTEN = EntityDataManager.createKey(EntityOroncoAW.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> ULTEN = EntityDataManager.createKey(EntityOroncoAW.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> RATEN = EntityDataManager.createKey(EntityOroncoAW.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> LATEN = EntityDataManager.createKey(EntityOroncoAW.class, DataSerializers.BOOLEAN);
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityOroncoAW.class, DataSerializers.BYTE);
   private boolean health80;
   private boolean health60;
   private boolean health40;
   private boolean health20;
   private float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
   private int maxY = 20;
   private int minY = 7;
   private EntityAIFlightLimits flightLimit;
   private final BossInfoServer bossInfo;

   public EntityOroncoAW(World worldIn) {
      super(worldIn);
      this.flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
      this.bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);
      this.setSize(4.0F, 4.0F);
      this.type = 62;
      this.tasks.removeTask(this.folow);
      this.tasks.removeTask(this.aiWander);
      this.ignoreFrustumCheck = true;
      this.moveHelper = new EntityOroncoAW.AIMoveControl(this);
      this.setNoGravity(true);
      this.health80 = true;
      this.health60 = true;
      this.health40 = true;
      this.health20 = true;
   }

   public int getParasiteIDRegister() {
      return 68;
   }

   protected void initEntityAI() {
      this.tasks.addTask(2, new EntityOroncoAW.AIMoveRandom());
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
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
         ;
      }

      return flag;
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
      return SRPSounds.ORONCO_HURT;
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
      EntityLiving.registerFixesMob(fixer, EntityOroncoAW.class);
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

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityOroncoAW vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityOroncoAW.this.posX;
            double d1 = this.posY - EntityOroncoAW.this.posY;
            double d2 = this.posZ - EntityOroncoAW.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityOroncoAW.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityOroncoAW.this.motionX *= 0.5D;
               EntityOroncoAW.this.motionY *= 0.5D;
               EntityOroncoAW.this.motionZ *= 0.5D;
            } else {
               EntityOroncoAW.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityOroncoAW.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityOroncoAW.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityOroncoAW.this.getAttackTarget() == null) {
                  EntityOroncoAW.this.rotationYaw = -((float)MathHelper.atan2(EntityOroncoAW.this.motionX, EntityOroncoAW.this.motionZ)) * 57.295776F;
                  EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw;
               } else {
                  double d4 = EntityOroncoAW.this.getAttackTarget().posX - EntityOroncoAW.this.posX;
                  double d5 = EntityOroncoAW.this.getAttackTarget().posZ - EntityOroncoAW.this.posZ;
                  EntityOroncoAW.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityOroncoAW.this.renderYawOffset = EntityOroncoAW.this.rotationYaw;
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
         EntityMoveHelper entitymovehelper = EntityOroncoAW.this.getMoveHelper();
         if (!entitymovehelper.isUpdating()) {
            return true;
         } else {
            double d0 = entitymovehelper.getX() - EntityOroncoAW.this.posX;
            double d1 = entitymovehelper.getY() - EntityOroncoAW.this.posY;
            double d2 = entitymovehelper.getZ() - EntityOroncoAW.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            return d3 < 1.0D || d3 > 3600.0D;
         }
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         if (EntityOroncoAW.this.getAttackTarget() != null) {
            BlockPos blockpos = new BlockPos(EntityOroncoAW.this);
            byte flag = 1;
            double speed = 0.3D;
            if (EntityOroncoAW.this.getDistanceSq(EntityOroncoAW.this.getAttackTarget()) > 400.0D) {
               blockpos = new BlockPos(EntityOroncoAW.this.getAttackTarget());
               flag = 2;
               speed += 0.1D;
            } else if (EntityOroncoAW.this.getDistanceSq(EntityOroncoAW.this.getAttackTarget()) < 100.0D) {
               blockpos = new BlockPos(EntityOroncoAW.this.getAttackTarget());
               flag = 3;
               speed += 0.15D;
            }

            for(int i = 0; i < 3; ++i) {
               BlockPos blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(15) - 7, EntityOroncoAW.this.rand.nextInt(11) - 5, EntityOroncoAW.this.rand.nextInt(15) - 7);
               if (flag == 2) {
                  blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(12) - 2, EntityOroncoAW.this.rand.nextInt(10) - 2, EntityOroncoAW.this.rand.nextInt(12) - 2);
               } else if (flag == 3) {
                  blockpos1 = blockpos.add(EntityOroncoAW.this.rand.nextInt(8) + 3, EntityOroncoAW.this.rand.nextInt(7) + 4, EntityOroncoAW.this.rand.nextInt(8) + 3);
               }

               if (EntityOroncoAW.this.world.isAirBlock(blockpos1)) {
                  EntityOroncoAW.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
                  if (EntityOroncoAW.this.getAttackTarget() == null) {
                     EntityOroncoAW.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                  }
                  break;
               }
            }
         } else {
            EaglercraftRandom random = EntityOroncoAW.this.getRNG();
            double d0 = EntityOroncoAW.this.posX + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d1 = EntityOroncoAW.this.posY + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double d2 = EntityOroncoAW.this.posZ + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            EntityOroncoAW.this.getMoveHelper().setMoveTo(d0, d1, d2, 0.3D);
         }

      }
   }
}
