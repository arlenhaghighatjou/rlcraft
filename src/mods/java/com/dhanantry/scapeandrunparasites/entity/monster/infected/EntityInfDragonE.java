package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPPacketEntityBodyDead;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityInfDragonE extends EntityPInfected implements EntityCutomAttack, EntityBodyParts {
   protected static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityInfDragonE.class, DataSerializers.BOOLEAN);
   private int flying;
   private float aaa;
   private float sss;
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);
   private EntityBody leftTendril;
   private EntityBody rightTendril;
   private EntityBody head;
   private float leftTendrilHealth;
   private float rightTendrilHealth;
   private float headlHealth;
   protected static final DataParameter<Byte> VEX_FLAGS = EntityDataManager.createKey(EntityInfDragonE.class, DataSerializers.BYTE);
   private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityInfDragonE.class, DataSerializers.BOOLEAN);
   private int limit;
   private boolean skillFlame;
   private double tttX;
   private double tttY;
   private double tttZ;
   private double tttH;
   private double tttHH;

   public EntityInfDragonE(World worldIn) {
      super(worldIn);
      this.setSize(1.9F, 3.8F);
      this.stepHeight = 1.0F;
      this.canModRender = 0;
      this.type = 14;
      this.killcount = -10.0D;
      this.tasks.removeTask(this.folow);
      this.flying = 0;
      this.skillFlame = false;
      this.leftTendril = new EntityBody(this, 2.6F, 2.5F, 1.0F, 3.1F, 2.8F, 1, 1, true);
      this.rightTendril = new EntityBody(this, 2.6F, 2.5F, 1.0F, 3.1F, 2.8F, -1, 2, true);
      this.head = new EntityBody(this, 2.2F, 2.2F, 1.0F, 4.0F, 2.0F, -1, 3, false, 0.2F);
      this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.headlHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
      this.ignoreFrustumCheck = true;
   }

   public int getParasiteIDRegister() {
      return 64;
   }

   protected void initEntityAI() {
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.tasks.addTask(0, new EntityAISwimmingDiving(this, 0.08D));
      this.tasks.addTask(3, new EntityAIAttackMeleeStatusAOE(this, 1.9D, false, 8.0D, 6.0D));
      this.tasks.addTask(6, new EntityInfDragonE.AIMoveRandom());
      this.tasks.addTask(6, new EntityInfDragonE.AIFireballAttack(this));
      this.tasks.addTask(3, new EntityAIFlightAttack(this, 64.0D));
      this.tasks.addTask(2, new EntityAISkill(this, 60, 32, 7, true, 1));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(SRPAttributes.INFDRAGONE_HEALTH);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(SRPAttributes.INFDRAGONE_ARMOR);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.27D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(SRPAttributes.INFDRAGONE_KD_RESISTANCE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.INFDRAGONE_ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
   }

   public void onLivingUpdate() {
      if (!this.isAIDisabled()) {
         super.onLivingUpdate();
         this.killcount = -10.0D;
         if (this.headlHealth > 0.0F) {
            this.head.onUpdate();
         }

         if (this.leftTendrilHealth > 0.0F) {
            this.leftTendril.onUpdate();
         }

         if (this.rightTendrilHealth > 0.0F) {
            this.rightTendril.onUpdate();
         }

         if (!this.world.isRemote && this.ticksExisted % 20 == 0 && this.rand.nextInt(9) == 0 && !this.getFlyingState()) {
            this.changeStateTo(true);
         } else {
            if (this.flying >= 1) {
               ++this.flying;
            }

            if (this.getFlyingState()) {
               this.aaa += 0.08F;
               this.sss += 0.782F;
               if (this.sss >= 24.0F) {
                  this.playSound(SoundEvents.ENTITY_ENDERDRAGON_FLAP, 5.0F, 0.8F + this.rand.nextFloat() * 0.3F);
                  this.sss = 0.0F;
               }

               if (this.onGround && !this.world.isRemote && this.flying > 40) {
                  this.changeStateTo(false);
               }
            } else {
               this.aaa = 0.08F;
               this.sss = 0.0F;
            }

         }
      }
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.getFlyingState()) {
         this.setNoGravity(true);
      } else {
         this.setNoGravity(false);
      }

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

   public float getaaa() {
      return this.aaa;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      boolean flag = super.attackEntityFrom(source, amount);
      if (flag) {
         if (source.getTrueSource() != null && source.getTrueSource() instanceof EntityPlayerMP) {
            this.bossInfo.addPlayer((EntityPlayerMP)source.getTrueSource());
         }

         if (this.rand.nextInt(12) == 0 && !this.getFlyingState()) {
            this.changeStateTo(true);
         }
      }

      return flag;
   }

   public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
      if (this.world.isRemote) {
         return false;
      } else {
         boolean flag = this.attackEntityFrom(source, amount);
         if (!flag) {
            return false;
         } else {
            if (this.leftTendril.getId() == id) {
               this.leftTendrilHealth -= amount;
               if (this.leftTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(7);
                  tendril.copyLocationAndAnglesFrom(this.leftTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.leftTendril);
                  this.world.setEntityState(this, (byte)11);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
                  this.changeStateTo(false);
               }
            } else if (this.rightTendril.getId() == id) {
               this.rightTendrilHealth -= amount;
               if (this.rightTendrilHealth <= 0.0F) {
                  EntityTendril tendril = new EntityTendril(this.world);
                  tendril.setSkin(8);
                  tendril.copyLocationAndAnglesFrom(this.rightTendril);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.rightTendril);
                  this.world.setEntityState(this, (byte)22);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
                  this.changeStateTo(false);
               }
            } else if (this.head.getId() == id) {
               this.headlHealth -= amount;
               if (this.headlHealth <= 0.0F) {
                  EntityInfDragonEHead tendril = new EntityInfDragonEHead(this.world);
                  tendril.setSkin(5);
                  tendril.copyLocationAndAnglesFrom(this.head);
                  this.world.spawnEntity(tendril);
                  this.world.removeEntityDangerously(this.head);
                  this.world.setEntityState(this, (byte)33);
                  SRPMain.network.sendToAll(new SRPPacketEntityBodyDead(this.getEntityId(), id));
               }
            }

            return flag;
         }
      }
   }

   public void setBodyPartDead(int id) {
      if (this.leftTendril.getId() == id) {
         this.world.removeEntityDangerously(this.leftTendril);
      } else if (this.rightTendril.getId() == id) {
         this.world.removeEntityDangerously(this.rightTendril);
      } else if (this.head.getId() == id) {
         this.world.removeEntityDangerously(this.head);
      }

   }

   public void setDead() {
      if (this.head != null) {
         this.world.removeEntityDangerously(this.head);
      }

      if (this.leftTendril != null) {
         this.world.removeEntityDangerously(this.leftTendril);
      }

      if (this.rightTendril != null) {
         this.world.removeEntityDangerously(this.rightTendril);
      }

      super.setDead();
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public void changeStateTo(boolean fly) {
      if (this.limit < 1) {
         if (fly) {
            if (!this.getFlyingState()) {
               if (this.leftTendrilHealth <= 0.0F || this.rightTendrilHealth <= 0.0F) {
                  return;
               }

               this.moveHelper = new EntityInfDragonE.AIMoveControl(this);
               this.setParasiteStatus(3);
               this.dataManager.set(FLYING, Boolean.valueOf(true));
               ++this.motionY;
               this.aaa += 0.08F;
               this.flying = 1;
               this.sss = 19.85F;
            }
         } else if (this.getFlyingState()) {
            this.moveHelper = new EntityMoveHelper(this);
            this.setParasiteStatus(0);
            this.dataManager.set(FLYING, Boolean.valueOf(false));
            this.flying = 0;
            this.aaa = 0.0F;
            this.sss = 0.0F;
         }

      }
   }

   public float getEyeHeight() {
      return 1.75F;
   }

   public static void registerFixesDorpa(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfDragonE.class);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      return super.attackEntityAsMob(entityIn);
   }

   public boolean attackEntityAsMobAOE(Entity entityIn) {
      return this.attackEntityAsMob(entityIn);
   }

   protected void selfExplode() {
   }

   protected void spawnGore() {
   }

   protected SoundEvent getAmbientSound() {
      return this.getParasiteStatus() != 0 ? SRPSounds.MOBSILENCE : SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ENDERDRAGON_HURT;
   }

   protected float getSoundVolume() {
      return 5.0F;
   }

   protected SoundEvent getDeathSound() {
      return SRPSounds.MOBSILENCE;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SPIDER_STEP;
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      IEntityLivingData floo = super.onInitialSpawn(difficulty, livingdata);
      return floo;
   }

   public boolean getFlyingState() {
      return ((Boolean)this.dataManager.get(FLYING)).booleanValue();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("parasiteleftTendril", this.leftTendrilHealth);
      compound.setFloat("parasiterightTendril", this.rightTendrilHealth);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("parasiteleftTendril", 99)) {
         this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
         if (this.leftTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)11);
         }
      }

      if (compound.hasKey("parasiterightTendril", 99)) {
         this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
         if (this.rightTendrilHealth <= 0.0F) {
            this.world.setEntityState(this, (byte)22);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public float getLeft() {
      return this.leftTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public float getRight() {
      return this.rightTendrilHealth;
   }

   @SideOnly(Side.CLIENT)
   public float getHead() {
      return this.headlHealth;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 11) {
         this.leftTendrilHealth = 0.0F;
      } else if (id == 22) {
         this.rightTendrilHealth = 0.0F;
      } else if (id == 33) {
         this.headlHealth = 0.0F;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VEX_FLAGS, Byte.valueOf((byte)0));
      this.dataManager.register(FLYING, Boolean.valueOf(false));
      this.dataManager.register(ATTACKING, Boolean.valueOf(false));
   }

   public static void registerFixesEmana(DataFixer fixer) {
      EntityLiving.registerFixesMob(fixer, EntityInfDragonE.class);
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

   @SideOnly(Side.CLIENT)
   public boolean isAttacking() {
      return ((Boolean)this.dataManager.get(ATTACKING)).booleanValue();
   }

   public void setAttacking(boolean attacking) {
      this.dataManager.set(ATTACKING, Boolean.valueOf(attacking));
   }

   public boolean getFinished(byte attID) {
      switch(attID) {
      case 1:
         return this.skillFlame;
      default:
         return super.getFinished(attID);
      }
   }

   public void setFinished(byte attID, boolean in) {
      switch(attID) {
      case 1:
         this.skillFlame = in;
         return;
      default:
         super.setFinished(attID, in);
      }
   }

   public void doSpecialSkill(byte id) {
      switch(id) {
      case 1:
         this.flame();
         return;
      default:
         super.doSpecialSkill(id);
      }
   }

   private void flame() {
      if (!this.getFlyingState() && this.headlHealth > 0.0F) {
         if (this.limit == 0) {
            EntityLivingBase entitylivingbase = this.getAttackTarget();
            if (entitylivingbase == null) {
               this.skillFlame = true;
               this.limit = 0;
               return;
            }

            this.tttX = entitylivingbase.posX;
            this.tttY = entitylivingbase.posY;
            this.tttZ = entitylivingbase.posZ;
            this.tttH = entitylivingbase.getEntityBoundingBox().minY;
            this.tttHH = (double)entitylivingbase.height;
         }

         ++this.limit;
         this.setParasiteStatus(10);
         this.getNavigator().tryMoveToXYZ(this.tttX, this.tttY, this.tttZ, 0.0D);
         this.resetIdleTime();
         if (this.ticksExisted % 10 == 0) {
            double d1 = 4.0D;
            Vec3d vec3d = this.getLook(1.0F);
            double d2 = this.tttX - (this.posX + vec3d.x * 4.0D);
            double d3 = this.tttH + this.tttHH / 4.0D - (0.5D + this.posY + (double)(this.height / 4.0F));
            double d4 = this.tttZ - (this.posZ + vec3d.z * 4.0D);
            this.world.playEvent((EntityPlayer)null, 1016, new BlockPos(this), 0);
            EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(this.world, this, d2, d3, d4);
            entitylargefireball.posX = this.posX + vec3d.x * 4.0D;
            entitylargefireball.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
            entitylargefireball.posZ = this.posZ + vec3d.z * 4.0D;
            this.world.spawnEntity(entitylargefireball);
            if (this.limit >= 60) {
               this.skillFlame = true;
               this.setParasiteStatus(0);
               this.limit = 0;
            }

         }
      } else {
         this.skillFlame = true;
         this.limit = 0;
      }
   }

   static class AIFireballAttack extends EntityAIBase {
      private final EntityInfDragonE parentEntity;
      public int attackTimer;

      public AIFireballAttack(EntityInfDragonE ghast) {
         this.parentEntity = ghast;
      }

      public boolean shouldExecute() {
         return this.parentEntity.getAttackTarget() != null && this.parentEntity.getFlyingState() && this.parentEntity.headlHealth > 0.0F;
      }

      public void startExecuting() {
         this.attackTimer = 0;
      }

      public void resetTask() {
         this.parentEntity.setAttacking(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = this.parentEntity.getAttackTarget();
         double d0 = 64.0D;
         if (entitylivingbase != null) {
            if (entitylivingbase.getDistanceSq(this.parentEntity) < 4096.0D && this.parentEntity.canEntityBeSeen(entitylivingbase)) {
               World world = this.parentEntity.world;
               ++this.attackTimer;
               if (this.parentEntity.isPotionActive(SRPPotions.RAGE_E)) {
                  ++this.attackTimer;
               }

               this.parentEntity.resetIdleTime();
               if (this.attackTimer == 10) {
                  ;
               }

               if (this.attackTimer == 20) {
                  double d1 = 4.0D;
                  Vec3d vec3d = this.parentEntity.getLook(1.0F);
                  double d2 = entitylivingbase.posX - (this.parentEntity.posX + vec3d.x * 4.0D);
                  double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (0.5D + this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F));
                  double d4 = entitylivingbase.posZ - (this.parentEntity.posZ + vec3d.z * 4.0D);
                  world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.parentEntity), 0);
                  EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(world, this.parentEntity, d2, d3, d4);
                  entitylargefireball.posX = this.parentEntity.posX + vec3d.x * 4.0D;
                  entitylargefireball.posY = this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D;
                  entitylargefireball.posZ = this.parentEntity.posZ + vec3d.z * 4.0D;
                  world.spawnEntity(entitylargefireball);
                  this.attackTimer = -5;

                  for(int i = 0; i <= 2; ++i) {
                     this.parentEntity.world.spawnParticle(EnumParticleTypes.FLAME, this.parentEntity.posX + vec3d.x * 4.0D, this.parentEntity.posY + (double)(this.parentEntity.height / 2.0F) + 0.5D, this.parentEntity.posZ + vec3d.z * 4.0D, 0.0D, -1.0D, 0.0D, new int[0]);
                  }
               }
            } else if (this.attackTimer > 0) {
               --this.attackTimer;
            }

            this.parentEntity.setAttacking(this.attackTimer > 10);
         }
      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityInfDragonE vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityInfDragonE.this.posX;
            double d1 = this.posY - EntityInfDragonE.this.posY;
            double d2 = this.posZ - EntityInfDragonE.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityInfDragonE.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityInfDragonE.this.motionX *= 0.5D;
               EntityInfDragonE.this.motionY *= 0.5D;
               EntityInfDragonE.this.motionZ *= 0.5D;
            } else {
               EntityInfDragonE.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityInfDragonE.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityInfDragonE.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityInfDragonE.this.getAttackTarget() == null) {
                  EntityInfDragonE.this.rotationYaw = -((float)MathHelper.atan2(EntityInfDragonE.this.motionX, EntityInfDragonE.this.motionZ)) * 57.295776F;
                  EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw;
               } else {
                  double d4 = EntityInfDragonE.this.getAttackTarget().posX - EntityInfDragonE.this.posX;
                  double d5 = EntityInfDragonE.this.getAttackTarget().posZ - EntityInfDragonE.this.posZ;
                  EntityInfDragonE.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  EntityInfDragonE.this.renderYawOffset = EntityInfDragonE.this.rotationYaw;
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
         return EntityInfDragonE.this.rand.nextInt(7) == 0 && EntityInfDragonE.this.getFlyingState();
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos = new BlockPos(EntityInfDragonE.this);
         byte flag = 1;
         double speed = 0.5D;
         if (EntityInfDragonE.this.getAttackTarget() != null) {
            if (EntityInfDragonE.this.getDistanceSq(EntityInfDragonE.this.getAttackTarget()) > 100.0D) {
               blockpos = new BlockPos(EntityInfDragonE.this.getAttackTarget());
               flag = 2;
            } else if (EntityInfDragonE.this.getDistanceSq(EntityInfDragonE.this.getAttackTarget()) < 36.0D) {
               blockpos = new BlockPos(EntityInfDragonE.this.getAttackTarget());
               flag = 3;
               speed += 0.25D;
            }
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(15) - 7, EntityInfDragonE.this.rand.nextInt(11) - 5, EntityInfDragonE.this.rand.nextInt(15) - 7);
            if (flag == 2) {
               blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(6) - 2, EntityInfDragonE.this.rand.nextInt(7) - 2, EntityInfDragonE.this.rand.nextInt(6) - 2);
            } else if (flag == 3) {
               blockpos1 = blockpos.add(EntityInfDragonE.this.rand.nextInt(4) + 3, EntityInfDragonE.this.rand.nextInt(5) + 4, EntityInfDragonE.this.rand.nextInt(4) + 3);
            }

            if (EntityInfDragonE.this.world.isAirBlock(blockpos1)) {
               EntityInfDragonE.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, speed);
               if (EntityInfDragonE.this.getAttackTarget() == null) {
                  EntityInfDragonE.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }
}
