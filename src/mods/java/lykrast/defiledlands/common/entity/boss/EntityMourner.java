package lykrast.defiledlands.common.entity.boss;

import java.util.Random;
import javax.annotation.Nullable;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityMourner extends EntityMob implements IEntityDefiled {
   public static final ResourceLocation LOOT = new ResourceLocation("defiledlands", "entities/the_mourner");
   private static final DataParameter<Integer> INVULNERABILITY_TIME = EntityDataManager.createKey(EntityMourner.class, DataSerializers.VARINT);
   protected static final DataParameter<Byte> MOURNER_FLAGS = EntityDataManager.createKey(EntityMourner.class, DataSerializers.BYTE);
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.PURPLE, Overlay.PROGRESS)).setDarkenSky(true);
   public static final int ATTACK_FIREBALLS = 0;
   public static final int ATTACK_SHULKER = 1;
   public static final int ATTACK_GHAST = 2;

   public EntityMourner(World worldIn) {
      super(worldIn);
      this.setSize(0.7F, 2.4F);
      this.experienceValue = 200;
      this.isImmuneToFire = true;
      this.moveHelper = new EntityMourner.AIMoveControl(this);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityMourner.AIDoNothing());
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, new EntityMourner.AIChargeAttack(this));
      this.tasks.addTask(4, new EntityMourner.AIRangedAttack(this));
      this.tasks.addTask(8, new EntityMourner.AIMoveRandom(this));
      this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, false));
      this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false, new Class[0]));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(128.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(16.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(400.0D);
      this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(INVULNERABILITY_TIME, Integer.valueOf(0));
      this.dataManager.register(MOURNER_FLAGS, Byte.valueOf((byte)0));
   }

   public void move(MoverType type, double x, double y, double z) {
      super.move(type, x, y, z);
      this.doBlockCollisions();
   }

   public void onUpdate() {
      this.noClip = true;
      super.onUpdate();
      this.noClip = false;
      this.setNoGravity(true);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getInvulTime() > 0) {
         this.motionY = 0.01D;
         if (this.world.isRemote) {
            for(int i1 = 0; i1 < 3; ++i1) {
               this.world.spawnParticle(EnumParticleTypes.SPELL_MOB, this.posX + this.rand.nextGaussian(), this.posY + (double)(this.rand.nextFloat() * 3.3F), this.posZ + this.rand.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D, new int[0]);
            }
         } else {
            int i = this.getInvulTime();
            if (i > 60 && i % 40 == 0 || i <= 60 && i % 10 == 0) {
               int y = (int)this.posY + this.rand.nextInt(11);
               MutableBlockPos pos = new MutableBlockPos((int)this.posX + this.rand.nextInt(15) - 7, y, (int)this.posZ + this.rand.nextInt(15) - 7);

               while(pos.getY() > 0 && y - pos.getY() < 16 && this.world.isAirBlock(pos)) {
                  pos.setY(pos.getY() - 1);
               }

               this.world.addWeatherEffect(new EntityLightningBolt(this.world, (double)pos.getX(), (double)(pos.getY() + 1), (double)pos.getZ(), true));
            }
         }
      }

      if (this.world.isRemote && this.isFiring()) {
         EnumParticleTypes particle = EnumParticleTypes.BARRIER;
         switch(this.getCurrentAttack()) {
         case 0:
            particle = EnumParticleTypes.FLAME;
            break;
         case 1:
            particle = EnumParticleTypes.END_ROD;
            break;
         case 2:
            particle = EnumParticleTypes.SMOKE_LARGE;
         }

         for(int i = 0; i < 2; ++i) {
            this.world.spawnParticle(particle, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   protected void updateAITasks() {
      if (this.getInvulTime() > 0) {
         int j1 = this.getInvulTime() - 1;
         if (j1 <= 0) {
            this.world.newExplosion(this, this.posX, this.posY + (double)this.getEyeHeight(), this.posZ, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
            this.playSound(SoundEvents.ENTITY_WITHER_SPAWN, 10.0F, 1.0F);
         }

         this.setInvulTime(j1);
      } else {
         super.updateAITasks();
         this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (super.attackEntityAsMob(entityIn)) {
         int force = 6;
         entityIn.motionY += 0.4D;
         if (entityIn instanceof EntityLivingBase) {
            ((EntityLivingBase)entityIn).knockBack(this, (float)force * 0.5F, (double)MathHelper.sin(this.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(this.rotationYaw * 0.017453292F)));
         } else {
            entityIn.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 0.017453292F) * (float)force * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * (float)force * 0.5F));
         }

         return true;
      } else {
         return false;
      }
   }

   public void ignite() {
      this.setInvulTime(200);
   }

   public float getExplosionResistance(Explosion explosionIn, World worldIn, BlockPos pos, IBlockState blockStateIn) {
      float f = super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn);
      Block block = blockStateIn.getBlock();
      if (EntityWither.canDestroyBlock(block) && ForgeEventFactory.onEntityDestroyBlock(this, pos, blockStateIn)) {
         f = Math.min(2.0F, f);
      }

      return f;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else if (source != DamageSource.DROWN && !(source.getTrueSource() instanceof EntityMourner)) {
         return this.getInvulTime() > 0 && source != DamageSource.OUT_OF_WORLD ? false : super.attackEntityFrom(source, amount);
      } else {
         return false;
      }
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      if (this.deathTime >= 180 && this.deathTime <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.posX + (double)f, this.posY + 2.0D + (double)f1, this.posZ + (double)f2, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      boolean flag = this.world.getGameRules().getBoolean("doMobLoot");
      int i = this.experienceValue;
      if (!this.world.isRemote) {
         if (this.deathTime > 150 && this.deathTime % 5 == 0 && flag) {
            this.dropExperience(MathHelper.floor((float)i * 0.08F));
         }

         if (this.deathTime == 1) {
            this.playSound(SoundEvents.ENTITY_ENDERDRAGON_DEATH, 10.0F, 1.0F);
         }
      }

      this.motionY = 0.01D;
      if (this.deathTime >= 200) {
         if (!this.world.isRemote) {
            if (flag) {
               this.dropExperience(MathHelper.floor((float)i * 0.2F));
            }

            this.setDead();
         }

         for(int k = 0; k < 20; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1, new int[0]);
         }
      }

   }

   private void dropExperience(int p_184668_1_) {
      while(p_184668_1_ > 0) {
         int i = EntityXPOrb.getXPSplit(p_184668_1_);
         p_184668_1_ -= i;
         this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, i));
      }

   }

   public void addTrackingPlayer(EntityPlayerMP player) {
      super.addTrackingPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public boolean isNonBoss() {
      return false;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Invul", this.getInvulTime());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setInvulTime(compound.getInteger("Invul"));
      if (this.hasCustomName()) {
         this.bossInfo.setName(this.getDisplayName());
      }

   }

   private boolean getMournerFlag(int mask) {
      int i = ((Byte)this.dataManager.get(MOURNER_FLAGS)).byteValue();
      return (i & mask) != 0;
   }

   private void setMournerFlag(int mask, boolean value) {
      int i = ((Byte)this.dataManager.get(MOURNER_FLAGS)).byteValue();
      if (value) {
         i = i | mask;
      } else {
         i = i & ~mask;
      }

      this.dataManager.set(MOURNER_FLAGS, Byte.valueOf((byte)(i & 255)));
   }

   public boolean isCharging() {
      return this.getMournerFlag(1);
   }

   public void setIsCharging(boolean value) {
      this.setMournerFlag(1, value);
   }

   public boolean isFiring() {
      return this.getMournerFlag(2);
   }

   public void setIsFiring(boolean value) {
      this.setMournerFlag(2, value);
   }

   public int getCurrentAttack() {
      int i = ((Byte)this.dataManager.get(MOURNER_FLAGS)).byteValue();
      return i >>> 2;
   }

   public void setCurrentAttack(int value) {
      int i = ((Byte)this.dataManager.get(MOURNER_FLAGS)).byteValue();
      i = i & 3;
      i = i | value << 2;
      this.dataManager.set(MOURNER_FLAGS, Byte.valueOf((byte)(i & 255)));
   }

   public int getRageFactor() {
      float h = this.getHealth();
      if (h < 100.0F) {
         return 3;
      } else {
         return h < 200.0F ? 2 : 1;
      }
   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public EnumCreatureAttribute getCreatureAttribute() {
      return EnumCreatureAttribute.UNDEAD;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.ENTITY_WITHER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_WITHER_DEATH;
   }

   public int getInvulTime() {
      return ((Integer)this.dataManager.get(INVULNERABILITY_TIME)).intValue();
   }

   public void setInvulTime(int time) {
      this.dataManager.set(INVULNERABILITY_TIME, Integer.valueOf(time));
   }

   protected boolean attemptTeleportAir(double x, double y, double z) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean flag = false;
      BlockPos blockpos = new BlockPos(this);
      World world = this.world;
      Random random = this.getRNG();
      if (world.isBlockLoaded(blockpos)) {
         this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
         if (world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(this.getEntityBoundingBox())) {
            flag = true;
         }
      }

      if (!flag) {
         this.setPositionAndUpdate(d0, d1, d2);
         return false;
      } else {
         for(int j = 0; j < 128; ++j) {
            double d6 = (double)j / 127.0D;
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d3 = d0 + (this.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            double d4 = d1 + (this.posY - d1) * d6 + random.nextDouble() * (double)this.height;
            double d5 = d2 + (this.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
         }

         if (this instanceof EntityCreature) {
            this.getNavigator().clearPath();
         }

         return true;
      }
   }

   static class AIChargeAttack extends EntityAIBase {
      private EntityMourner mourner;

      public AIChargeAttack(EntityMourner mourner) {
         this.setMutexBits(1);
         this.mourner = mourner;
      }

      public boolean shouldExecute() {
         return this.mourner.getAttackTarget() != null && this.mourner.getAttackTarget().isEntityAlive() && this.mourner.getDistanceSq(this.mourner.getAttackTarget()) <= 16.0D;
      }

      public boolean shouldContinueExecuting() {
         return this.mourner.getMoveHelper().isUpdating() && this.mourner.isCharging() && this.mourner.getAttackTarget() != null && this.mourner.getAttackTarget().isEntityAlive() && this.mourner.getDistanceSq(this.mourner.getAttackTarget()) <= 25.0D;
      }

      public void startExecuting() {
         EntityLivingBase entitylivingbase = this.mourner.getAttackTarget();
         Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
         this.mourner.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 0.5D * (double)(this.mourner.getRageFactor() + 1));
         this.mourner.setIsCharging(true);
         this.mourner.setIsFiring(false);
         this.mourner.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
      }

      public void resetTask() {
         this.mourner.setIsCharging(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = this.mourner.getAttackTarget();
         if (this.mourner.getEntityBoundingBox().grow(0.5D).intersects(entitylivingbase.getEntityBoundingBox())) {
            this.mourner.attackEntityAsMob(entitylivingbase);
            this.mourner.swingArm(EnumHand.MAIN_HAND);
            this.mourner.setIsCharging(false);
         } else {
            Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
            this.mourner.moveHelper.setMoveTo(vec3d.x, vec3d.y - 1.0D, vec3d.z, 0.5D * (double)(this.mourner.getRageFactor() + 1));
         }

      }
   }

   class AIDoNothing extends EntityAIBase {
      public AIDoNothing() {
         this.setMutexBits(7);
      }

      public boolean shouldExecute() {
         return EntityMourner.this.getInvulTime() > 0;
      }
   }

   static class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityMourner mourner) {
         super(mourner);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - this.entity.posX;
            double d1 = this.posY - this.entity.posY;
            double d2 = this.posZ - this.entity.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < this.entity.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               this.entity.motionX *= 0.5D;
               this.entity.motionY *= 0.5D;
               this.entity.motionZ *= 0.5D;
            } else if (d3 >= 20.0D && ((EntityMourner)this.entity).attemptTeleportAir(this.posX, this.posY, this.posZ)) {
               this.entity.world.playSound((EntityPlayer)null, this.entity.prevPosX, this.entity.prevPosY, this.entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, this.entity.getSoundCategory(), 1.0F, 1.0F);
               this.entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            } else {
               this.entity.motionX += d0 / d3 * 0.05D * this.speed;
               this.entity.motionY += d1 / d3 * 0.05D * this.speed;
               this.entity.motionZ += d2 / d3 * 0.05D * this.speed;
               if (this.entity.getAttackTarget() == null) {
                  this.entity.rotationYaw = -((float)MathHelper.atan2(this.entity.motionX, this.entity.motionZ)) * 57.295776F;
                  this.entity.renderYawOffset = this.entity.rotationYaw;
               } else {
                  double d4 = this.entity.getAttackTarget().posX - this.entity.posX;
                  double d5 = this.entity.getAttackTarget().posZ - this.entity.posZ;
                  this.entity.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                  this.entity.renderYawOffset = this.entity.rotationYaw;
               }
            }
         }

      }
   }

   static class AIMoveRandom extends EntityAIBase {
      private EntityMourner mourner;

      public AIMoveRandom(EntityMourner mourner) {
         this.setMutexBits(1);
         this.mourner = mourner;
      }

      public boolean shouldExecute() {
         return !this.mourner.getMoveHelper().isUpdating() && this.mourner.rand.nextInt(7) == 0;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         BlockPos blockpos;
         if (this.mourner.getAttackTarget() != null && this.mourner.getAttackTarget().isEntityAlive()) {
            blockpos = new BlockPos(this.mourner.getAttackTarget());
         } else {
            blockpos = new BlockPos(this.mourner);
         }

         for(int i = 0; i < 3; ++i) {
            BlockPos blockpos1 = blockpos.add(this.mourner.rand.nextInt(15) - 7, this.mourner.rand.nextInt(11) - 5, this.mourner.rand.nextInt(15) - 7);
            if (this.mourner.world.isAirBlock(blockpos1)) {
               this.mourner.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D * (double)this.mourner.getRageFactor());
               if (this.mourner.getAttackTarget() == null) {
                  this.mourner.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   static class AIRangedAttack extends EntityAIBase {
      private EntityMourner mourner;
      private int attackStep;
      private int attackTime;

      public AIRangedAttack(EntityMourner mourner) {
         this.setMutexBits(2);
         this.mourner = mourner;
      }

      public boolean shouldExecute() {
         return this.mourner.getAttackTarget() != null && !this.mourner.getMoveHelper().isUpdating() && !this.mourner.isCharging() && this.mourner.rand.nextInt(9 - this.mourner.getRageFactor() * 2) == 0;
      }

      public boolean shouldContinueExecuting() {
         return this.attackStep > 0 && this.mourner.isFiring() && this.mourner.getAttackTarget() != null && this.mourner.getAttackTarget().isEntityAlive();
      }

      public void startExecuting() {
         this.attackStep = 2 + this.mourner.getRageFactor() * 3;
         this.attackTime = 60;
         this.mourner.setIsFiring(true);
         this.mourner.setCurrentAttack(this.mourner.rand.nextInt(3));
      }

      public void resetTask() {
         this.mourner.setIsFiring(false);
      }

      public void updateTask() {
         EntityLivingBase entitylivingbase = this.mourner.getAttackTarget();
         --this.attackTime;
         if (this.attackTime <= 0) {
            --this.attackStep;
            this.attackTime = 3;
            switch(this.mourner.getCurrentAttack()) {
            case 0:
               double d0 = this.mourner.getDistanceSq(entitylivingbase);
               double d1 = entitylivingbase.posX - this.mourner.posX;
               double var10001 = (double)entitylivingbase.height;
               double d2 = entitylivingbase.getEntityBoundingBox().minY + var10001 / 2.0D - (this.mourner.posY + (double)this.mourner.height / 2.0D);
               double d3 = entitylivingbase.posZ - this.mourner.posZ;
               float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
               this.mourner.world.playEvent((EntityPlayer)null, 1018, new BlockPos((int)this.mourner.posX, (int)this.mourner.posY, (int)this.mourner.posZ), 0);
               EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.mourner.world, this.mourner, d1 + this.mourner.getRNG().nextGaussian() * (double)f, d2, d3 + this.mourner.getRNG().nextGaussian() * (double)f);
               entitysmallfireball.posY = this.mourner.posY + (double)this.mourner.height / 2.0D + 0.5D;
               this.mourner.world.spawnEntity(entitysmallfireball);
               break;
            case 1:
               Axis axis = Axis.values()[this.mourner.rand.nextInt(3)];
               EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(this.mourner.world, this.mourner, entitylivingbase, axis);
               entityshulkerbullet.posY += (double)this.mourner.height / 2.0D + 0.5D;
               this.mourner.world.spawnEntity(entityshulkerbullet);
               this.mourner.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (this.mourner.rand.nextFloat() - this.mourner.rand.nextFloat()) * 0.2F + 1.0F);
               break;
            case 2:
               Vec3d vec3d = this.mourner.getLook(1.0F);
               double d2 = entitylivingbase.posX - (this.mourner.posX + vec3d.x * 4.0D);
               double d3 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (0.5D + this.mourner.posY + (double)(this.mourner.height / 2.0F));
               double d4 = entitylivingbase.posZ - (this.mourner.posZ + vec3d.z * 4.0D);
               this.mourner.world.playEvent((EntityPlayer)null, 1016, new BlockPos(this.mourner), 0);
               EntityLargeFireball entitylargefireball = new EntityLargeFireball(this.mourner.world, this.mourner, d2, d3, d4);
               entitylargefireball.explosionPower = this.mourner.getRageFactor();
               entitylargefireball.posX = this.mourner.posX;
               entitylargefireball.posY = this.mourner.posY + (double)this.mourner.height + 0.5D;
               entitylargefireball.posZ = this.mourner.posZ;
               this.mourner.world.spawnEntity(entitylargefireball);
               --this.attackStep;
               this.attackTime = 10;
            }
         }

         this.mourner.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0F, 10.0F);
         super.updateTask();
      }
   }
}
