package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIWatchClosestIgnoreRider;
import com.github.alexthe666.iceandfire.entity.ai.FlyingAITarget;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateAmphibious;
import com.github.alexthe666.iceandfire.entity.ai.SeaSerpentAIAttackMelee;
import com.github.alexthe666.iceandfire.entity.ai.SeaSerpentAIGetInWater;
import com.github.alexthe666.iceandfire.enums.EnumSeaSerpent;
import com.google.common.base.Predicate;
import java.util.Random;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.multipart.IMultipartEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySeaSerpent extends EntityAnimal implements IAnimatedEntity, IMultipartEntity, IVillagerFear, IAnimalFear {
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.VARINT);
   private static final DataParameter<Float> SCALE = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> JUMPING = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> BREATHING = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> ANCIENT = EntityDataManager.createKey(EntitySeaSerpent.class, DataSerializers.BOOLEAN);
   public int swimCycle;
   private int animationTick;
   private Animation currentAnimation;
   private EntityMutlipartPart[] segments = new EntityMutlipartPart[9];
   private float lastScale;
   private boolean isLandNavigator;
   private EntitySeaSerpent.SwimBehavior swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
   private boolean changedSwimBehavior = false;
   private int ticksCircling;
   public float orbitRadius = 0.0F;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer roll_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer tail_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer head_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer pitch_buffer;
   @Nullable
   public BlockPos orbitPos = null;
   public static final Animation ANIMATION_BITE = Animation.create(15);
   public static final Animation ANIMATION_SPEAK = Animation.create(15);
   public static final Animation ANIMATION_ROAR = Animation.create(40);
   private boolean isArcing = false;
   private float arcingYAdditive = 0.0F;
   public float jumpProgress = 0.0F;
   public float wantJumpProgress = 0.0F;
   public float jumpRot = 0.0F;
   public float breathProgress = 0.0F;
   private int ticksSinceJump = 0;
   private int ticksSinceRoar = 0;
   private int ticksJumping = 0;
   public static final int TIME_BETWEEN_JUMPS = 170;
   public static final int TIME_BETWEEN_ROARS = 300;
   private static final Predicate NOT_SEA_SERPENT = new Predicate<Entity>() {
      public boolean apply(@Nullable Entity entity) {
         return entity instanceof EntityLivingBase && !(entity instanceof EntitySeaSerpent) && DragonUtils.isAlive((EntityLivingBase)entity);
      }
   };
   public boolean attackDecision = false;
   private boolean isBreathing;
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "sea_serpent"));

   public EntitySeaSerpent(World worldIn) {
      super(worldIn);
      this.switchNavigator(true);
      this.setSize(0.5F, 0.5F);
      this.ignoreFrustumCheck = true;
      this.resetParts(1.0F);
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.roll_buffer = new IFChainBuffer();
         this.pitch_buffer = new IFChainBuffer();
         this.tail_buffer = new IFChainBuffer();
         this.head_buffer = new IFChainBuffer();
      }

   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntitySeaSerpent.AISwimBite());
      this.tasks.addTask(0, new EntitySeaSerpent.AISwimWander());
      this.tasks.addTask(0, new EntitySeaSerpent.AISwimCircle());
      this.tasks.addTask(1, new SeaSerpentAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(2, new SeaSerpentAIGetInWater(this, 1.0D));
      this.tasks.addTask(3, new EntityAIWatchClosestIgnoreRider(this, EntityLivingBase.class, 6.0F));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.targetTasks.addTask(2, new FlyingAITarget(this, EntityLivingBase.class, 0, true, false, NOT_SEA_SERPENT));
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveHelper = new EntityMoveHelper(this);
         this.navigator = new PathNavigateAmphibious(this, this.world);
         this.isLandNavigator = true;
      } else {
         this.moveHelper = new EntitySeaSerpent.SwimmingMoveHelper();
         this.navigator = new PathNavigateSwimmer(this, this.world);
         this.isLandNavigator = false;
      }

   }

   public boolean isDirectPathBetweenPoints(BlockPos pos) {
      RayTraceResult movingobjectposition = this.world.rayTraceBlocks(this.getPositionVector().addVector(0.0D, (double)this.height * 0.5D, 0.0D), new Vec3d((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D), false, true, false);
      return movingobjectposition == null || movingobjectposition.typeOfHit != Type.BLOCK;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.15D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)Math.min(2048, IceAndFire.CONFIG.dragonTargetSearchLength));
   }

   public void resetParts(float scale) {
      this.clearParts();
      this.segments = new EntityMutlipartPart[9];

      for(int i = 0; i < this.segments.length; ++i) {
         if (i > 3) {
            this.segments[i] = new EntityMutlipartPart(this, (2.0F - (float)(i + 1) * 0.55F) * scale, 0.0F, 0.0F, 0.5F * scale, 0.5F * scale, 1.0F);
         } else {
            this.segments[i] = new EntityMutlipartPart(this, (1.8F - (float)i * 0.5F) * scale, 0.0F, 0.0F, 0.45F * scale, 0.4F * scale, 1.0F);
         }
      }

   }

   public void onUpdateParts() {
      for(Entity entity : this.segments) {
         if (entity != null) {
            entity.onUpdate();
         }
      }

   }

   private void clearParts() {
      for(Entity entity : this.segments) {
         if (entity != null) {
            this.world.removeEntityDangerously(entity);
         }
      }

   }

   public void setDead() {
      this.clearParts();
      super.setDead();
   }

   public void setScaleForAge(boolean par1) {
      this.setScale(this.getSeaSerpentScale());
      if (this.getSeaSerpentScale() != this.lastScale) {
         this.resetParts(this.getSeaSerpentScale());
      }

      this.lastScale = this.getSeaSerpentScale();
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() != ANIMATION_BITE) {
         this.setAnimation(ANIMATION_BITE);
         return true;
      } else {
         return false;
      }
   }

   public void onUpdate() {
      super.onUpdate();
      this.setScaleForAge(true);
      this.onUpdateParts();
      if (this.isInWater()) {
         this.spawnParticlesAroundEntity(EnumParticleTypes.WATER_BUBBLE, this, (int)this.getSeaSerpentScale());

         for(Entity entity : this.segments) {
            this.spawnParticlesAroundEntity(EnumParticleTypes.WATER_BUBBLE, entity, (int)this.getSeaSerpentScale());
         }
      }

      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   private void spawnParticlesAroundEntity(EnumParticleTypes type, Entity entity, int count) {
      for(int i = 0; i < count; ++i) {
         double x = entity.posX + (double)(this.rand.nextFloat() * entity.width * 2.0F) - (double)entity.width;
         double y = entity.posY + 0.5D + (double)(this.rand.nextFloat() * entity.height);
         double z = entity.posZ + (double)(this.rand.nextFloat() * entity.width * 2.0F) - (double)entity.width;
         if (this.world.getBlockState(new BlockPos(x, y, z)).getMaterial() == Material.WATER) {
            this.world.spawnParticle(type, x, y, z, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   private void spawnSlamParticles(EnumParticleTypes type) {
      for(int i = 0; (float)i < this.getSeaSerpentScale() * 3.0F; ++i) {
         for(int i1 = 0; i1 < 20; ++i1) {
            double motionX = this.getRNG().nextGaussian() * 0.07D;
            double motionY = this.getRNG().nextGaussian() * 0.07D;
            double motionZ = this.getRNG().nextGaussian() * 0.07D;
            float radius = 1.25F * this.getSeaSerpentScale();
            float angle = 0.017453292F * this.renderYawOffset + (float)i1 * 1.0F;
            double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
            double extraY = 0.800000011920929D;
            double extraZ = (double)(radius * MathHelper.cos(angle));
            if (this.world.isRemote) {
               this.world.spawnParticle(type, true, this.posX + extraX, this.posY + extraY, this.posZ + extraZ, motionX, motionY, motionZ, new int[]{0});
            }
         }
      }

   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(SCALE, Float.valueOf(0.0F));
      this.dataManager.register(JUMPING, Boolean.valueOf(false));
      this.dataManager.register(BREATHING, Boolean.valueOf(false));
      this.dataManager.register(ANCIENT, Boolean.valueOf(false));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Variant", this.getVariant());
      compound.setInteger("TicksSinceRoar", this.ticksSinceRoar);
      compound.setFloat("Scale", this.getSeaSerpentScale());
      compound.setBoolean("JumpingOutOfWater", this.isJumpingOutOfWater());
      compound.setBoolean("AttackDecision", this.attackDecision);
      compound.setBoolean("Breathing", this.isBreathing());
      compound.setBoolean("Ancient", this.isAncient());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setVariant(compound.getInteger("Variant"));
      this.ticksSinceRoar = compound.getInteger("TicksSinceRoar");
      this.setSeaSerpentScale(compound.getFloat("Scale"));
      this.setJumpingOutOfWater(compound.getBoolean("JumpingOutOfWater"));
      this.attackDecision = compound.getBoolean("AttackDecision");
      this.setBreathing(compound.getBoolean("Breathing"));
      this.setAncient(compound.getBoolean("Ancient"));
   }

   private void setSeaSerpentScale(float scale) {
      this.dataManager.set(SCALE, Float.valueOf(scale));
      this.updateAttributes();
   }

   private void updateAttributes() {
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(Math.min(0.25D, 0.15D * (double)this.getSeaSerpentScale() * (double)this.getAncientModifier()));
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(Math.max(4.0D, IceAndFire.CONFIG.seaSerpentAttackStrength * (double)this.getSeaSerpentScale() * (double)this.getAncientModifier()));
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(Math.max(10.0D, IceAndFire.CONFIG.seaSerpentBaseHealth * (double)this.getSeaSerpentScale() * (double)this.getAncientModifier()));
      this.heal(30.0F * this.getSeaSerpentScale());
   }

   private float getAncientModifier() {
      return this.isAncient() ? 1.5F : 1.0F;
   }

   public float getSeaSerpentScale() {
      return ((Float)this.dataManager.get(SCALE)).floatValue().floatValue();
   }

   public int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue().intValue();
   }

   public boolean isJumpingOutOfWater() {
      return ((Boolean)this.dataManager.get(JUMPING)).booleanValue();
   }

   public boolean isAncient() {
      return ((Boolean)this.dataManager.get(ANCIENT)).booleanValue();
   }

   public boolean isBreathing() {
      if (this.world.isRemote) {
         boolean breathing = ((Boolean)this.dataManager.get(BREATHING)).booleanValue();
         this.isBreathing = breathing;
         return breathing;
      } else {
         return this.isBreathing;
      }
   }

   public void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public void setJumpingOutOfWater(boolean jump) {
      this.dataManager.set(JUMPING, Boolean.valueOf(jump));
   }

   public void setAncient(boolean ancient) {
      this.dataManager.set(ANCIENT, Boolean.valueOf(ancient));
   }

   public void setBreathing(boolean breathing) {
      this.dataManager.set(BREATHING, Boolean.valueOf(breathing));
      if (!this.world.isRemote) {
         this.isBreathing = breathing;
      }

   }

   public void fall(float distance, float damageMultiplier) {
   }

   public void onLivingUpdate() {
      if (!this.world.isRemote && this.isJumpingOutOfWater() && this.swimBehavior == EntitySeaSerpent.SwimBehavior.WANDER && this.shouldStopJumping()) {
         this.motionY -= 0.25D;
         if (this.isInWater()) {
            this.setJumpingOutOfWater(false);
            this.ticksSinceJump = 0;
         }
      }

      super.onLivingUpdate();
      boolean breathing = this.isBreathing() && this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_ROAR;
      boolean jumping = !this.isInWater() && !this.onGround && this.motionY >= 0.0D;
      boolean wantJumping = false;
      boolean ground = !this.isInWater() && this.onGround;
      boolean prevJumping = this.isJumpingOutOfWater();
      ++this.ticksSinceRoar;
      ++this.ticksSinceJump;
      if (this.ticksSinceRoar > 300 && this.isAtSurface() && this.getAnimation() != ANIMATION_BITE && this.jumpProgress == 0.0F && !this.isJumpingOutOfWater()) {
         this.setAnimation(ANIMATION_ROAR);
         this.ticksSinceRoar = 0;
      }

      if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 1) {
         this.playSound(ModSounds.SEA_SERPENT_ROAR, this.getSoundVolume() + 1.0F, 1.0F);
      }

      if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 5) {
         this.playSound(ModSounds.SEA_SERPENT_BITE, this.getSoundVolume(), 1.0F);
      }

      if (this.isJumpingOutOfWater()) {
         ++this.ticksJumping;
      } else {
         this.ticksJumping = 0;
      }

      if (this.isJumpingOutOfWater() && isWaterBlock(this.world, (new BlockPos(this)).up(2))) {
         this.setJumpingOutOfWater(false);
      }

      if (!this.isJumpingOutOfWater() && !isWaterBlock(this.world, (new BlockPos(this)).up()) && (this.ticksSinceJump > 170 || this.getAttackTarget() != null)) {
         this.ticksSinceJump = 0;
         this.setJumpingOutOfWater(true);
      }

      if (this.swimCycle < 38) {
         this.swimCycle += 2;
      } else {
         this.swimCycle = 0;
      }

      if (breathing && this.breathProgress < 20.0F) {
         this.breathProgress += 0.5F;
      } else if (!breathing && this.breathProgress > 0.0F) {
         this.breathProgress -= 0.5F;
      }

      if (jumping && this.jumpProgress < 10.0F) {
         this.jumpProgress += 0.5F;
      } else if (!jumping && this.jumpProgress > 0.0F) {
         this.jumpProgress -= 0.5F;
      }

      if (wantJumping && this.wantJumpProgress < 10.0F) {
         this.wantJumpProgress += 2.0F;
      } else if (!wantJumping && this.wantJumpProgress > 0.0F) {
         this.wantJumpProgress -= 2.0F;
      }

      if (this.isJumpingOutOfWater() && this.jumpRot < 1.0F) {
         this.jumpRot += 0.1F;
      } else if (!this.isJumpingOutOfWater() && this.jumpRot > 0.0F) {
         this.jumpRot -= 0.1F;
      }

      if (prevJumping != this.isJumpingOutOfWater() && !this.isJumpingOutOfWater()) {
         this.playSound(ModSounds.SEA_SERPENT_SPLASH, 5.0F, 0.75F);
         this.spawnSlamParticles(EnumParticleTypes.FIREWORKS_SPARK);
         this.spawnSlamParticles(EnumParticleTypes.WATER_BUBBLE);
         this.spawnSlamParticles(EnumParticleTypes.WATER_BUBBLE);
         this.spawnSlamParticles(EnumParticleTypes.WATER_BUBBLE);
         this.doSplashDamage();
      }

      if (!ground && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (ground && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      this.renderYawOffset = this.rotationYaw;
      this.rotationPitch = (float)this.motionY * 20.0F;
      if (this.world.isRemote) {
         this.pitch_buffer.calculateChainWaveBuffer(90.0F, 10, 10.0F, 0.5F, this);
         if (!jumping) {
            this.tail_buffer.calculateChainSwingBuffer(70.0F, 20, 5.0F, this);
            this.head_buffer.calculateChainSwingBuffer(70.0F, 20, 5.0F, this);
         }
      }

      if (this.changedSwimBehavior) {
         this.changedSwimBehavior = false;
      }

      if (!this.world.isRemote) {
         if (this.attackDecision) {
            this.setBreathing(false);
         }

         if (this.getAttackTarget() != null && this.getAnimation() != ANIMATION_ROAR) {
            if (this.attackDecision) {
               if (this.getDistanceSq(this.getAttackTarget()) > (double)(500.0F * this.getSeaSerpentScale())) {
                  this.attackDecision = false;
               }
            } else {
               if (!this.getAttackTarget().isInWater() || !this.isDirectPathBetweenPoints(this.getAttackTarget().getPosition()) || this.getDistanceSq(this.getAttackTarget()) < (double)(60.0F * this.getSeaSerpentScale())) {
                  this.attackDecision = true;
               }

               if (!this.attackDecision) {
                  this.shoot(this.getAttackTarget());
               }
            }
         } else {
            this.setBreathing(false);
         }

         if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.CIRCLE) {
            ++this.ticksCircling;
         } else {
            this.ticksCircling = 0;
         }

         if (this.getAttackTarget() != null) {
            if (this.isInWater()) {
               if (this.attackDecision) {
                  if (this.isPreyAtSurface() && this.getDistanceSq(this.getAttackTarget()) < (double)(200.0F * this.getSeaSerpentScale())) {
                     this.swimBehavior = EntitySeaSerpent.SwimBehavior.JUMP;
                  } else {
                     this.swimBehavior = EntitySeaSerpent.SwimBehavior.ATTACK;
                  }
               } else {
                  this.swimBehavior = EntitySeaSerpent.SwimBehavior.ATTACK;
               }
            } else if (this.onGround) {
               this.swimBehavior = EntitySeaSerpent.SwimBehavior.ATTACK;
            } else {
               this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
            }
         } else if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP && this.getAttackTarget() != null && this.getDistanceSq(this.getAttackTarget()) > (double)(200.0F * this.getSeaSerpentScale())) {
            this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
            this.ticksSinceJump = 0;
            this.setJumpingOutOfWater(false);
         }

         if (this.swimBehavior != EntitySeaSerpent.SwimBehavior.JUMP && this.swimBehavior != EntitySeaSerpent.SwimBehavior.ATTACK && this.ticksSinceJump > 170) {
            this.swimBehavior = EntitySeaSerpent.SwimBehavior.JUMP;
         }

         if (this.swimBehavior != EntitySeaSerpent.SwimBehavior.ATTACK) {
            this.arcingYAdditive = 0.0F;
         }

         if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP && this.motionY < 0.0D && !this.isInWater()) {
            this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
            this.ticksSinceJump = 0;
            this.setJumpingOutOfWater(false);
         }

         if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.ATTACK && this.getAttackTarget() != null && !this.getAttackTarget().isInWater()) {
            this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
            this.faceEntity(this.getAttackTarget(), 360.0F, 360.0F);
         }

         if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.ATTACK && (this.getAttackTarget() == null || !this.isDirectPathBetweenPoints(new BlockPos(this.getAttackTarget())))) {
            this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
         }
      }

      if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && (this.isTouchingMob(this.getAttackTarget()) || this.getDistanceSq(this.getAttackTarget()) < 50.0D)) {
         this.hurtMob(this.getAttackTarget());
      }

      this.breakBlock();
   }

   private void doSplashDamage() {
      double width = 2.0D * (double)this.getSeaSerpentScale();

      for(Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().grow(width, width * 0.5D, width), NOT_SEA_SERPENT)) {
         if (entity instanceof EntityLivingBase) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            this.destroyBoat(entity);
            double xRatio = this.posX - entity.posX;
            double zRatio = this.posZ - entity.posZ;
            float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
            float strength = 0.3F * this.getSeaSerpentScale();
            entity.motionX /= 2.0D;
            entity.motionZ /= 2.0D;
            entity.motionX -= xRatio / (double)f * (double)strength;
            entity.motionZ -= zRatio / (double)f * (double)strength;
            entity.motionY += (double)strength;
            if (this.motionY > 0.4000000059604645D) {
               this.motionY = 0.4000000059604645D;
            }
         }
      }

   }

   public void destroyBoat(Entity sailor) {
      if (sailor.getRidingEntity() != null && sailor.getRidingEntity() instanceof EntityBoat && !this.world.isRemote) {
         EntityBoat boat = (EntityBoat)sailor.getRidingEntity();
         boat.setDead();
         if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            for(int i = 0; i < 3; ++i) {
               boat.entityDropItem(new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 1, boat.getBoatType().getMetadata()), 0.0F);
            }

            for(int j = 0; j < 2; ++j) {
               boat.dropItemWithOffset(Items.STICK, 1, 0.0F);
            }
         }
      }

   }

   private boolean isPreyAtSurface() {
      if (this.getAttackTarget() != null) {
         BlockPos pos = new BlockPos(this.getAttackTarget());
         return !isWaterBlock(this.world, pos.up((int)Math.ceil((double)this.getAttackTarget().height)));
      } else {
         return false;
      }
   }

   private void hurtMob(EntityLivingBase entity) {
      if (this.getAnimation() == ANIMATION_BITE && entity != null && this.getAnimationTick() > 6) {
         this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         this.attackDecision = this.getRNG().nextBoolean();
      }

   }

   public void moveJumping() {
      float velocity = 0.5F;
      double x = (double)(-MathHelper.sin(this.rotationYaw * 0.017453292F) * MathHelper.cos(this.rotationPitch * 0.017453292F));
      double z = (double)(MathHelper.cos(this.rotationYaw * 0.017453292F) * MathHelper.cos(this.rotationPitch * 0.017453292F));
      float f = MathHelper.sqrt(x * x + z * z);
      x = x / (double)f;
      z = z / (double)f;
      x = x * (double)velocity;
      z = z * (double)velocity;
      this.motionX = x;
      this.motionZ = z;
   }

   private boolean isTouchingMob(Entity entity) {
      if (this.getEntityBoundingBox().expand((double)(this.getSeaSerpentScale() * 3.0F), (double)(this.getSeaSerpentScale() * 3.0F), (double)(this.getSeaSerpentScale() * 3.0F)).intersects(entity.getEntityBoundingBox())) {
         return true;
      } else {
         for(Entity segment : this.segments) {
            if (segment.getEntityBoundingBox().expand((double)(this.getSeaSerpentScale() * 2.0F), (double)(this.getSeaSerpentScale() * 2.0F), (double)(this.getSeaSerpentScale() * 2.0F)).intersects(entity.getEntityBoundingBox())) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean isInWater() {
      return super.isInWater() || this.isInsideOfMaterial(Material.WATER) || this.isInsideOfMaterial(Material.CORAL);
   }

   public void breakBlock() {
      if (IceAndFire.CONFIG.seaSerpentGriefing) {
         for(int a = (int)Math.round(this.getEntityBoundingBox().minX) - 2; a <= (int)Math.round(this.getEntityBoundingBox().maxX) + 2; ++a) {
            for(int b = (int)Math.round(this.getEntityBoundingBox().minY); b <= (int)Math.round(this.getEntityBoundingBox().maxY) + 2 && b <= 127; ++b) {
               for(int c = (int)Math.round(this.getEntityBoundingBox().minZ) - 2; c <= (int)Math.round(this.getEntityBoundingBox().maxZ) + 2; ++c) {
                  BlockPos pos = new BlockPos(a, b, c);
                  IBlockState state = this.world.getBlockState(pos);
                  Block block = state.getBlock();
                  if (state.getMaterial() != Material.AIR && !(block instanceof BlockLiquid) && state.getMaterial() == Material.PLANTS && block != Blocks.AIR && !this.world.isRemote) {
                     this.world.destroyBlock(pos, true);
                  }
               }
            }
         }
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setVariant(this.getRNG().nextInt(7));
      boolean ancient = this.getRNG().nextInt(16) == 1;
      if (ancient) {
         this.setAncient(true);
         this.setSeaSerpentScale(6.0F + this.getRNG().nextFloat() * 3.0F);
      } else {
         this.setSeaSerpentScale(1.5F + this.getRNG().nextFloat() * 4.0F);
      }

      return livingdata;
   }

   public void onWorldSpawn(Random random) {
      this.setVariant(random.nextInt(6));
      boolean ancient = random.nextInt(15) == 1;
      if (ancient) {
         this.setAncient(true);
         this.setSeaSerpentScale(6.0F + random.nextFloat() * 3.0F);
      } else {
         this.setSeaSerpentScale(1.5F + random.nextFloat() * 4.0F);
      }

   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return null;
   }

   public int getAnimationTick() {
      return this.animationTick;
   }

   public void setAnimationTick(int tick) {
      this.animationTick = tick;
   }

   public Animation getAnimation() {
      return this.currentAnimation;
   }

   public void setAnimation(Animation animation) {
      this.currentAnimation = animation;
   }

   public Animation[] getAnimations() {
      return new Animation[]{ANIMATION_BITE, ANIMATION_ROAR, ANIMATION_SPEAK};
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.SEA_SERPENT_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.SEA_SERPENT_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.SEA_SERPENT_DIE;
   }

   public void playLivingSound() {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playLivingSound();
   }

   protected void playHurtSound(DamageSource source) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_SPEAK);
      }

      super.playHurtSound(source);
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return true;
   }

   public boolean isBlinking() {
      return this.ticksExisted % 50 > 43;
   }

   private boolean canMove() {
      return true;
   }

   public boolean wantsToJump() {
      return this.ticksSinceJump > 170 && this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP;
   }

   private boolean shouldStopJumping() {
      return this.ticksJumping > 30 || !isWaterBlock(this.world, this.world.getHeight(new BlockPos(this)).down(1));
   }

   public static BlockPos getPositionRelativeToSeafloor(EntitySeaSerpent entity, World world, double x, double z, Random rand) {
      BlockPos topY = new BlockPos(x, entity.posY, z);

      BlockPos bottomY;
      for(bottomY = new BlockPos(x, entity.posY, z); isWaterBlock(world, topY) && topY.getY() < world.getHeight(); topY = topY.up()) {
         ;
      }

      while(isWaterBlock(world, bottomY) && bottomY.getY() > 0) {
         bottomY = bottomY.down();
      }

      if (entity.ticksSinceJump > 170) {
         return topY.up((int)Math.ceil((double)(3.0F * entity.getSeaSerpentScale())));
      } else if (entity.ticksSinceRoar <= 300 && entity.getAnimation() != ANIMATION_ROAR) {
         for(int tries = 0; tries < 5; ++tries) {
            BlockPos pos = new BlockPos(x, (double)(bottomY.getY() + 1 + rand.nextInt(Math.max(1, topY.getY() - bottomY.getY() - 2))), z);
            if (isWaterBlock(world, pos)) {
               return pos;
            }
         }

         return entity.getPosition();
      } else {
         return topY.down();
      }
   }

   public static BlockPos getPositionInOrbit(EntitySeaSerpent entity, World world, BlockPos orbit, Random rand) {
      float possibleOrbitRadius = entity.orbitRadius + 10.0F;
      float radius = 5.0F * entity.getSeaSerpentScale();
      float angle = 0.017453292F * possibleOrbitRadius;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos((double)orbit.getX() + extraX, (double)orbit.getY(), (double)orbit.getZ() + extraZ);
      entity.orbitRadius = possibleOrbitRadius;
      return radialPos;
   }

   public static BlockPos getPositionPreyArc(EntitySeaSerpent entity, BlockPos target, World world) {
      float radius = 10.0F * entity.getSeaSerpentScale();
      entity.renderYawOffset = entity.rotationYaw;
      float angle = 0.017453292F * entity.renderYawOffset;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      double signum = Math.signum((double)((float)target.getY() + 0.5F) - entity.posY);
      BlockPos pos = new BlockPos((double)target.getX() + extraX, (double)target.getY() + (double)entity.rand.nextInt(5) * signum, (double)target.getZ() + extraZ);
      entity.isArcing = true;
      return clampBlockPosToWater(entity, world, pos);
   }

   private static BlockPos clampBlockPosToWater(Entity entity, World world, BlockPos pos) {
      BlockPos topY = new BlockPos((double)pos.getX(), entity.posY, (double)pos.getZ());

      BlockPos bottomY;
      for(bottomY = new BlockPos((double)pos.getX(), entity.posY, (double)pos.getZ()); isWaterBlock(world, topY) && topY.getY() < world.getHeight(); topY = topY.up()) {
         ;
      }

      while(isWaterBlock(world, bottomY) && bottomY.getY() > 0) {
         bottomY = bottomY.down();
      }

      return new BlockPos(pos.getX(), MathHelper.clamp(pos.getY(), bottomY.getY() + 1, topY.getY() - 1), pos.getZ());
   }

   public static boolean isWaterBlock(World world, BlockPos pos) {
      return world.getBlockState(pos).getMaterial() == Material.WATER;
   }

   public boolean isAtSurface() {
      BlockPos pos = new BlockPos(this);
      return isWaterBlock(this.world, pos.down()) && !isWaterBlock(this.world, pos.up());
   }

   private void shoot(EntityLivingBase entity) {
      if (!this.attackDecision) {
         if (!this.isInWater()) {
            this.setBreathing(false);
            this.attackDecision = true;
         }

         if (this.isBreathing()) {
            if (this.ticksExisted % 40 == 0) {
               this.playSound(ModSounds.SEA_SERPENT_BREATH, 4.0F, 1.0F);
            }

            if (this.ticksExisted % 3 == 0) {
               this.rotationYaw = this.renderYawOffset;
               float f1 = 0.0F;
               float f2 = 0.0F;
               float f3 = 0.0F;
               float headPosX = f1 + (float)(this.segments[0].posX + (double)(0.3F * this.getSeaSerpentScale()) * Math.cos((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosZ = f2 + (float)(this.segments[0].posZ + (double)(0.3F * this.getSeaSerpentScale()) * Math.sin((double)(this.rotationYaw + 90.0F) * 3.141592653589793D / 180.0D));
               float headPosY = f3 + (float)(this.segments[0].posY + (double)(0.2F * this.getSeaSerpentScale()));
               double d2 = entity.posX - (double)headPosX;
               double d3 = entity.posY - (double)headPosY;
               double d4 = entity.posZ - (double)headPosZ;
               EntitySeaSerpentBubbles entitylargefireball = new EntitySeaSerpentBubbles(this.world, this, d2, d3, d4);
               float size = 0.8F;
               entitylargefireball.setPosition((double)headPosX, (double)headPosY, (double)headPosZ);
               if (!this.world.isRemote && !entity.isDead) {
                  this.world.spawnEntity(entitylargefireball);
               }

               entitylargefireball.setSizes(size, size);
               if (entity.isDead || entity == null) {
                  this.setBreathing(false);
                  this.attackDecision = this.getRNG().nextBoolean();
               }
            }
         } else {
            this.setBreathing(true);
         }
      }

      this.faceEntity(entity, 360.0F, 360.0F);
   }

   public EnumSeaSerpent getEnum() {
      switch(this.getVariant()) {
      case 1:
         return EnumSeaSerpent.BRONZE;
      case 2:
         return EnumSeaSerpent.DEEPBLUE;
      case 3:
         return EnumSeaSerpent.GREEN;
      case 4:
         return EnumSeaSerpent.PURPLE;
      case 5:
         return EnumSeaSerpent.RED;
      case 6:
         return EnumSeaSerpent.TEAL;
      default:
         return EnumSeaSerpent.BLUE;
      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP && this.isJumpingOutOfWater() && this.getAttackTarget() == null) {
         ;
      }

      if (this.isServerWorld()) {
         if (this.isInWater()) {
            this.moveRelative(strafe, vertical, forward, 0.1F);
            float f4 = 0.6F;
            float d0 = (float)EnchantmentHelper.getDepthStriderModifier(this);
            if (d0 > 3.0F) {
               d0 = 3.0F;
            }

            if (!this.onGround) {
               d0 *= 0.5F;
            }

            if (d0 > 0.0F) {
               f4 += (0.54600006F - f4) * d0 / 3.0F;
            }

            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f4;
            this.motionX *= 0.900000011920929D;
            this.motionY *= 0.900000011920929D;
            this.motionY *= (double)f4;
            this.motionZ *= 0.900000011920929D;
            this.motionZ *= (double)f4;
         } else {
            super.travel(strafe, vertical, forward);
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double deltaX = this.posX - this.prevPosX;
      double deltaZ = this.posZ - this.prevPosZ;
      double deltaY = this.posY - this.prevPosY;
      float delta = MathHelper.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 4.0F;
      if (delta > 1.0F) {
         delta = 1.0F;
      }

      this.limbSwingAmount += (delta - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public void onKillEntity(EntityLivingBase entity) {
      super.onKillEntity(entity);
      this.attackDecision = this.getRNG().nextBoolean();
   }

   class AISwimBite extends EntityAIBase {
      BlockPos target;
      boolean secondPhase = false;
      boolean isOver = false;
      public int max_distance = 1000;

      public AISwimBite() {
         this.setMutexBits(1);
      }

      public void resetTask() {
         this.target = null;
         this.secondPhase = false;
         this.isOver = false;
         EntitySeaSerpent.this.isArcing = false;
         EntitySeaSerpent.this.arcingYAdditive = 0.0F;
      }

      public boolean shouldExecute() {
         if (!EntitySeaSerpent.this.attackDecision && EntitySeaSerpent.this.getAttackTarget() != null && EntitySeaSerpent.this.getDistanceSq(EntitySeaSerpent.this.getAttackTarget()) < 300.0D) {
            return false;
         } else if ((EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.ATTACK || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) && EntitySeaSerpent.this.canMove() && EntitySeaSerpent.this.getAttackTarget() != null) {
            if (EntitySeaSerpent.this.isInWater() && EntitySeaSerpent.this.getAttackTarget() != null) {
               this.target = new BlockPos(EntitySeaSerpent.this.getAttackTarget());
               EntitySeaSerpent.this.orbitPos = null;
               this.secondPhase = false;
               EntitySeaSerpent.this.isArcing = false;
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
         return true;
      }

      public boolean shouldContinueExecuting() {
         if (this.isOver) {
            return false;
         } else if (!EntitySeaSerpent.this.attackDecision && EntitySeaSerpent.this.getAttackTarget() != null && EntitySeaSerpent.this.getDistanceSq(EntitySeaSerpent.this.getAttackTarget()) < 300.0D) {
            EntitySeaSerpent.this.moveHelper.action = Action.WAIT;
            this.resetTask();
            return false;
         } else if (this.secondPhase) {
            if (EntitySeaSerpent.this.getAttackTarget() != null && EntitySeaSerpent.this.getDistanceSq(this.target) <= (double)this.max_distance && !this.isOver) {
               return true;
            } else {
               EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
               this.resetTask();
               return false;
            }
         } else {
            return EntitySeaSerpent.this.getAttackTarget() != null;
         }
      }

      public void updateTask() {
         if (EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) {
            if (EntitySeaSerpent.this.getAttackTarget() != null && EntitySeaSerpent.this.isInWater()) {
               this.target = (new BlockPos(EntitySeaSerpent.this.getAttackTarget())).up((int)Math.ceil((double)(3.0F * EntitySeaSerpent.this.getSeaSerpentScale())));
            }
         } else {
            if (EntitySeaSerpent.this.getAttackTarget() != null && !EntitySeaSerpent.this.getAttackTarget().isDead) {
               double d0 = EntitySeaSerpent.this.getAttackTarget().posX - EntitySeaSerpent.this.posX;
               double d1 = EntitySeaSerpent.this.getAttackTarget().posZ - EntitySeaSerpent.this.posZ;
               double d2 = d0 * d0 + d1 * d1;
               d2 = (double)MathHelper.sqrt(d2);
               EntitySeaSerpent.this.arcingYAdditive = (float)(this.secondPhase ? 1 : -1) * (float)d2;
            } else {
               this.secondPhase = true;
            }

            if (!this.secondPhase) {
               this.target = new BlockPos(EntitySeaSerpent.this.getAttackTarget());
               if (!EntitySeaSerpent.this.attackDecision) {
                  if (EntitySeaSerpent.this.getAttackTarget() != null) {
                     if (EntitySeaSerpent.this.getDistanceSq(this.target) > (double)(10.0F * EntitySeaSerpent.this.getSeaSerpentScale())) {
                        EntitySeaSerpent.this.setBreathing(true);
                     } else {
                        EntitySeaSerpent.this.attackDecision = true;
                     }
                  }
               } else {
                  if (EntitySeaSerpent.this.getAttackTarget() != null && (EntitySeaSerpent.this.getDistanceSq(this.target) < (double)(30.0F * EntitySeaSerpent.this.getSeaSerpentScale()) || EntitySeaSerpent.this.isTouchingMob(EntitySeaSerpent.this.getAttackTarget()))) {
                     EntitySeaSerpent.this.setAnimation(EntitySeaSerpent.ANIMATION_BITE);
                  }

                  if (EntitySeaSerpent.this.getAttackTarget() == null || EntitySeaSerpent.this.getDistanceSq(this.target) < (double)(30.0F * EntitySeaSerpent.this.getSeaSerpentScale())) {
                     this.target = null;
                     this.secondPhase = true;
                  }
               }
            }

            if (this.secondPhase && EntitySeaSerpent.this.getAttackTarget() != null) {
               if (EntitySeaSerpent.this.getDistanceSq(EntitySeaSerpent.this.getAttackTarget()) <= (double)this.max_distance && (this.target == null || EntitySeaSerpent.this.getDistanceSq(this.target) >= 5.0D)) {
                  this.target = EntitySeaSerpent.getPositionPreyArc(EntitySeaSerpent.this, new BlockPos(EntitySeaSerpent.this.getAttackTarget()), EntitySeaSerpent.this.world);
               } else {
                  this.resetTask();
                  this.isOver = true;
               }
            }
         }

         if (this.target != null && (EntitySeaSerpent.isWaterBlock(EntitySeaSerpent.this.world, this.target) || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP)) {
            EntitySeaSerpent.this.moveHelper.setMoveTo((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 0.5D);
            if (EntitySeaSerpent.this.getAttackTarget() == null) {
               EntitySeaSerpent.this.getLookHelper().setLookPosition((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
         }

      }
   }

   class AISwimCircle extends EntityAIBase {
      BlockPos target;

      public AISwimCircle() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.CIRCLE && EntitySeaSerpent.this.canMove()) {
            if (!EntitySeaSerpent.this.getMoveHelper().isUpdating()) {
               EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
               return false;
            } else {
               if (EntitySeaSerpent.this.isInWater() && !EntitySeaSerpent.this.isJumpingOutOfWater()) {
                  BlockPos gen = this.generateTarget();
                  if (gen != null) {
                     EntitySeaSerpent.this.orbitPos = gen;
                     this.target = EntitySeaSerpent.getPositionInOrbit(EntitySeaSerpent.this, EntitySeaSerpent.this.world, EntitySeaSerpent.this.orbitPos, EntitySeaSerpent.this.rand);
                     return EntitySeaSerpent.this.isDirectPathBetweenPoints(this.target);
                  }

                  EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
               }

               return false;
            }
         } else {
            return false;
         }
      }

      protected BlockPos generateTarget() {
         for(int i = 0; i < 5; ++i) {
            BlockPos pos = EntitySeaSerpent.getPositionRelativeToSeafloor(EntitySeaSerpent.this, EntitySeaSerpent.this.world, EntitySeaSerpent.this.posX + (double)EntitySeaSerpent.this.rand.nextInt(30) - 15.0D, EntitySeaSerpent.this.posZ + (double)EntitySeaSerpent.this.rand.nextInt(30) - 15.0D, EntitySeaSerpent.this.rand);
            if (EntitySeaSerpent.isWaterBlock(EntitySeaSerpent.this.world, pos) && EntitySeaSerpent.this.isDirectPathBetweenPoints(pos)) {
               return pos;
            }
         }

         return null;
      }

      public boolean shouldContinueExecuting() {
         if (this.target != null && !EntitySeaSerpent.this.isDirectPathBetweenPoints(this.target)) {
            return false;
         } else {
            return EntitySeaSerpent.this.getAttackTarget() == null && EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.CIRCLE;
         }
      }

      public void updateTask() {
         if (EntitySeaSerpent.this.getDistance((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ()) < 5.0D) {
            this.target = EntitySeaSerpent.getPositionInOrbit(EntitySeaSerpent.this, EntitySeaSerpent.this.world, EntitySeaSerpent.this.orbitPos, EntitySeaSerpent.this.rand);
         }

         if (EntitySeaSerpent.isWaterBlock(EntitySeaSerpent.this.world, this.target) && EntitySeaSerpent.this.isDirectPathBetweenPoints(this.target)) {
            EntitySeaSerpent.this.moveHelper.setMoveTo((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 0.25D);
            if (EntitySeaSerpent.this.getAttackTarget() == null) {
               EntitySeaSerpent.this.getLookHelper().setLookPosition((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
         }

      }
   }

   public class AISwimWander extends EntityAIBase {
      BlockPos target;

      public AISwimWander() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if ((EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.WANDER || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) && EntitySeaSerpent.this.canMove() && EntitySeaSerpent.this.getAttackTarget() == null) {
            if (EntitySeaSerpent.this.isInWater()) {
               BlockPos gen = this.generateTarget();
               if (gen != null) {
                  this.target = gen;
                  EntitySeaSerpent.this.orbitPos = null;
                  return EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP || !EntitySeaSerpent.this.getMoveHelper().isUpdating();
               }
            }

            return false;
         } else {
            return false;
         }
      }

      protected BlockPos generateTarget() {
         if (EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) {
            BlockPos pos = EntitySeaSerpent.getPositionRelativeToSeafloor(EntitySeaSerpent.this, EntitySeaSerpent.this.world, EntitySeaSerpent.this.posX, EntitySeaSerpent.this.posZ, EntitySeaSerpent.this.rand);
            return pos.up(3 * (int)Math.ceil((double)EntitySeaSerpent.this.getSeaSerpentScale()));
         } else {
            for(int i = 0; i < 5; ++i) {
               BlockPos pos = EntitySeaSerpent.getPositionRelativeToSeafloor(EntitySeaSerpent.this, EntitySeaSerpent.this.world, EntitySeaSerpent.this.posX + (double)EntitySeaSerpent.this.rand.nextInt(30) - 15.0D, EntitySeaSerpent.this.posZ + (double)EntitySeaSerpent.this.rand.nextInt(30) - 15.0D, EntitySeaSerpent.this.rand);
               if (EntitySeaSerpent.isWaterBlock(EntitySeaSerpent.this.world, pos) && EntitySeaSerpent.this.isDirectPathBetweenPoints(pos) || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) {
                  return pos;
               }
            }

            return null;
         }
      }

      protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
         return true;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void startExecuting() {
         if (this.target == null) {
            this.target = this.generateTarget();
         }

         if (this.target != null && (EntitySeaSerpent.isWaterBlock(EntitySeaSerpent.this.world, this.target) || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) && EntitySeaSerpent.this.isDirectPathBetweenPoints(this.target)) {
            EntitySeaSerpent.this.moveHelper.setMoveTo((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 0.25D);
            if (EntitySeaSerpent.this.getAttackTarget() == null) {
               EntitySeaSerpent.this.getLookHelper().setLookPosition((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
         }

      }
   }

   static enum SwimBehavior {
      CIRCLE,
      WANDER,
      ATTACK,
      JUMP,
      NONE;

      private SwimBehavior() {
      }
   }

   public class SwimmingMoveHelper extends EntityMoveHelper {
      private EntitySeaSerpent serpent = EntitySeaSerpent.this;

      public SwimmingMoveHelper() {
         super(EntitySeaSerpent.this);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntitySeaSerpent.this.posX;
            double d1 = this.posY - EntitySeaSerpent.this.posY;
            double d2 = this.posZ - EntitySeaSerpent.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < 6.0D && EntitySeaSerpent.this.getAttackTarget() == null) {
               if (!EntitySeaSerpent.this.changedSwimBehavior && EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.WANDER && EntitySeaSerpent.this.rand.nextInt(20) == 0) {
                  EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.CIRCLE;
                  EntitySeaSerpent.this.changedSwimBehavior = true;
               }

               if (!EntitySeaSerpent.this.changedSwimBehavior && EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.CIRCLE && EntitySeaSerpent.this.rand.nextInt(5) == 0 && EntitySeaSerpent.this.ticksCircling > 150) {
                  EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
                  EntitySeaSerpent.this.changedSwimBehavior = true;
               }
            }

            if (EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP && !EntitySeaSerpent.this.isInWater() && !EntitySeaSerpent.this.onGround) {
               EntitySeaSerpent.this.ticksSinceJump = 0;
            }

            int dist = EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP ? 10 : 3;
            if (d3 < (double)dist && EntitySeaSerpent.this.getAttackTarget() == null || EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP && EntitySeaSerpent.this.shouldStopJumping() && EntitySeaSerpent.this.getAttackTarget() == null) {
               this.action = Action.WAIT;
               EntitySeaSerpent.this.motionX *= 0.5D;
               EntitySeaSerpent.this.motionZ *= 0.5D;
               if (EntitySeaSerpent.this.swimBehavior == EntitySeaSerpent.SwimBehavior.JUMP) {
                  EntitySeaSerpent.this.swimBehavior = EntitySeaSerpent.SwimBehavior.WANDER;
                  EntitySeaSerpent.this.ticksSinceJump = 0;
                  EntitySeaSerpent.this.setJumpingOutOfWater(false);
               }
            } else {
               EntitySeaSerpent.this.motionX += d0 / d3 * 0.5D * this.speed;
               EntitySeaSerpent.this.motionY += d1 / d3 * 0.5D * this.speed;
               EntitySeaSerpent.this.motionZ += d2 / d3 * 0.5D * this.speed;
               float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.29577951308232D));
               this.entity.rotationPitch = f1;
               if (!EntitySeaSerpent.this.isArcing) {
                  if (EntitySeaSerpent.this.getAttackTarget() == null) {
                     EntitySeaSerpent.this.rotationYaw = -((float)MathHelper.atan2(EntitySeaSerpent.this.motionX, EntitySeaSerpent.this.motionZ)) * 57.295776F;
                     EntitySeaSerpent.this.renderYawOffset = EntitySeaSerpent.this.rotationYaw;
                  } else if (EntitySeaSerpent.this.swimBehavior != EntitySeaSerpent.SwimBehavior.JUMP) {
                     double d4 = EntitySeaSerpent.this.getAttackTarget().posX - EntitySeaSerpent.this.posX;
                     double d5 = EntitySeaSerpent.this.getAttackTarget().posZ - EntitySeaSerpent.this.posZ;
                     EntitySeaSerpent.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                     EntitySeaSerpent.this.renderYawOffset = EntitySeaSerpent.this.rotationYaw;
                  }
               }
            }
         }

      }
   }
}
