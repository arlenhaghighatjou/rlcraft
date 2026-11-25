package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAIAirTarget;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAIFlee;
import com.github.alexthe666.iceandfire.entity.ai.StymphalianBirdAITarget;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import java.util.UUID;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.oredict.OreDictionary;

public class EntityStymphalianBird extends EntityCreature implements IAnimatedEntity, IMob, IVillagerFear, IAnimalFear {
   private static final int FLIGHT_CHANCE_PER_TICK = 100;
   private int animationTick;
   private Animation currentAnimation;
   private static final DataParameter<Optional<UUID>> VICTOR_ENTITY = EntityDataManager.createKey(EntityStymphalianBird.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityStymphalianBird.class, DataSerializers.BOOLEAN);
   private EntityLivingBase victorEntity;
   private boolean isFlying;
   public float flyProgress;
   public BlockPos airTarget;
   private int flyTicks;
   private int launchTicks;
   public static Animation ANIMATION_PECK = Animation.create(20);
   public static Animation ANIMATION_SHOOT_ARROWS = Animation.create(30);
   public static Animation ANIMATION_SPEAK = Animation.create(10);
   public StymphalianBirdFlock flock;
   private boolean aiFlightLaunch = false;
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "stymphalian_bird"));
   protected static final Predicate<Entity> STYMPHALIAN_PREDICATE = new Predicate<Entity>() {
      public boolean apply(@Nullable Entity entity) {
         return entity instanceof EntityStymphalianBird;
      }
   };
   private int airBorneCounter;

   public EntityStymphalianBird(World worldIn) {
      super(worldIn);
      this.setSize(1.3F, 1.2F);
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new StymphalianBirdAIFlee(this, 10.0F));
      this.tasks.addTask(3, new EntityAIAttackMelee(this, 1.5D, false));
      this.tasks.addTask(5, new EntityAIWander(this, 1.0D));
      this.tasks.addTask(6, new StymphalianBirdAIAirTarget(this));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityLivingBase.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.targetTasks.addTask(2, new StymphalianBirdAITarget(this, EntityLivingBase.class, true));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(24.0D);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue((double)Math.min(2048, IceAndFire.CONFIG.stymphalianBirdTargetSearchLength));
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(4.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VICTOR_ENTITY, Optional.absent());
      this.dataManager.register(FLYING, Boolean.valueOf(false));
   }

   protected boolean canDespawn() {
      return false;
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 10 + this.world.rand.nextInt(5);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      }

   }

   public void writeEntityToNBT(NBTTagCompound tag) {
      super.writeEntityToNBT(tag);
      if (this.getVictorId() == null) {
         tag.setString("VictorUUID", "");
      } else {
         tag.setString("VictorUUID", this.getVictorId().toString());
      }

      tag.setBoolean("Flying", this.isFlying());
   }

   public void readEntityFromNBT(NBTTagCompound tag) {
      super.readEntityFromNBT(tag);
      String s;
      if (tag.hasKey("VictorUUID", 8)) {
         s = tag.getString("VictorUUID");
      } else {
         String s1 = tag.getString("Victor");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         try {
            this.setVictorId(UUID.fromString(s));
         } catch (Throwable var4) {
            ;
         }
      }

      this.setFlying(tag.getBoolean("Flying"));
   }

   public boolean isFlying() {
      return this.world.isRemote ? (this.isFlying = ((Boolean)this.dataManager.get(FLYING)).booleanValue()) : this.isFlying;
   }

   public void setFlying(boolean flying) {
      this.dataManager.set(FLYING, Boolean.valueOf(flying));
      if (!this.world.isRemote) {
         this.isFlying = flying;
      }

   }

   public void onDeath(DamageSource cause) {
      if (cause.getTrueSource() != null && cause.getTrueSource() instanceof EntityLivingBase && !this.world.isRemote) {
         this.setVictorId(cause.getTrueSource().getUniqueID());
         if (this.flock != null) {
            this.flock.setFearTarget((EntityLivingBase)cause.getTrueSource());
         }
      }

      super.onDeath(cause);
   }

   protected void onDeathUpdate() {
      super.onDeathUpdate();
      if (this.deathTime == 20 && !this.world.isRemote && IceAndFire.CONFIG.stymphalianBirdsOreDictDrops) {
         NonNullList<ItemStack> bronzeItems = OreDictionary.getOres("ingotBronze");
         NonNullList<ItemStack> copperItems = OreDictionary.getOres("ingotCopper");
         if (!bronzeItems.isEmpty()) {
            for(ItemStack bronzeIngot : bronzeItems) {
               if (bronzeIngot != ItemStack.EMPTY) {
                  ItemStack stack = bronzeIngot.copy();
                  stack.setCount(1 + this.getRNG().nextInt(3));
                  this.dropItemAt(stack, this.posX, this.posY + 0.5D, this.posZ);
                  break;
               }
            }
         }

         if (!copperItems.isEmpty()) {
            for(ItemStack copperIngot : copperItems) {
               if (copperIngot != ItemStack.EMPTY) {
                  ItemStack stack = copperIngot.copy();
                  stack.setCount(1 + this.getRNG().nextInt(3));
                  this.dropItemAt(stack, this.posX, this.posY + 0.5D, this.posZ);
                  break;
               }
            }
         }
      }

   }

   @Nullable
   private EntityItem dropItemAt(ItemStack stack, double x, double y, double z) {
      EntityItem entityitem = new EntityItem(this.world, x, y, z, stack);
      entityitem.setDefaultPickupDelay();
      if (this.captureDrops) {
         this.capturedDrops.add(entityitem);
      } else {
         this.world.spawnEntity(entityitem);
      }

      return entityitem;
   }

   @Nullable
   public UUID getVictorId() {
      return (UUID)((Optional)this.dataManager.get(VICTOR_ENTITY)).orNull();
   }

   public void setVictorId(@Nullable UUID uuid) {
      this.dataManager.set(VICTOR_ENTITY, Optional.fromNullable(uuid));
   }

   @Nullable
   public EntityLivingBase getVictor() {
      try {
         UUID uuid = this.getVictorId();
         return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
      } catch (IllegalArgumentException var21) {
         return null;
      }
   }

   public boolean isVictor(EntityLivingBase entityIn) {
      return entityIn == this.getVictor();
   }

   public void setVictor(EntityLivingBase player) {
      this.setVictorId(player.getUniqueID());
   }

   public boolean isTargetBlocked(Vec3d target) {
      if (target != null) {
         RayTraceResult rayTrace = this.world.rayTraceBlocks(new Vec3d(this.getPosition()), target, false);
         if (rayTrace != null && rayTrace.hitVec != null) {
            BlockPos pos = new BlockPos(rayTrace.hitVec);
            if (!this.world.isAirBlock(pos)) {
               return true;
            }

            return rayTrace != null && rayTrace.typeOfHit != Type.BLOCK;
         }
      }

      return false;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() == NO_ANIMATION) {
         this.setAnimation(ANIMATION_PECK);
      }

      return true;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getAttackTarget() != null && (this.getAttackTarget() instanceof EntityPlayer && ((EntityPlayer)this.getAttackTarget()).isCreative() || this.getVictor() != null && this.isVictor(this.getAttackTarget()))) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (this.flock == null) {
         StymphalianBirdFlock otherFlock = StymphalianBirdFlock.getNearbyFlock(this);
         if (otherFlock == null) {
            this.flock = StymphalianBirdFlock.createFlock(this);
         } else {
            this.flock = otherFlock;
            this.flock.addToFlock(this);
         }
      } else {
         if (!this.flock.isLeader(this)) {
            double dist = this.getDistanceSq(this.flock.getLeader());
            if (dist > 360.0D) {
               this.setFlying(true);
               this.navigator.clearPath();
               this.airTarget = StymphalianBirdAIAirTarget.getNearbyAirTarget(this.flock.getLeader());
               this.aiFlightLaunch = false;
            } else if (!this.flock.getLeader().isFlying()) {
               this.setFlying(false);
               this.airTarget = null;
               this.aiFlightLaunch = false;
            }

            if (this.onGround && dist < 40.0D && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
               this.setFlying(false);
            }
         }

         this.flock.update();
      }

      if (!this.world.isRemote && this.getAttackTarget() != null && !this.getAttackTarget().isDead) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (this.getAnimation() == ANIMATION_PECK && this.getAnimationTick() == 7) {
            if (dist < 1.5D) {
               this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            }

            if (this.onGround) {
               this.setFlying(false);
            }
         }

         if (this.getAnimation() != ANIMATION_PECK && this.getAnimation() != ANIMATION_SHOOT_ARROWS && dist > 3.0D && dist < 225.0D) {
            this.setAnimation(ANIMATION_SHOOT_ARROWS);
         }

         if (this.getAnimation() == ANIMATION_SHOOT_ARROWS) {
            EntityLivingBase target = this.getAttackTarget();
            this.faceEntity(target, 360.0F, 360.0F);
            if (this.isFlying()) {
               this.rotationYaw = this.renderYawOffset;
               if ((this.getAnimationTick() == 7 || this.getAnimationTick() == 14) && this.isDirectPathBetweenPoints(this, this.getPositionVector(), target.getPositionVector())) {
                  this.playSound(ModSounds.STYMPHALIAN_BIRD_ATTACK, 1.0F, 1.0F);

                  for(int i = 0; i < 4; ++i) {
                     float wingX = (float)(this.posX + 0.8999999761581421D * Math.cos((double)(this.rotationYaw + (float)(180 * (i % 2))) * 3.141592653589793D / 180.0D));
                     float wingZ = (float)(this.posZ + 0.8999999761581421D * Math.sin((double)(this.rotationYaw + (float)(180 * (i % 2))) * 3.141592653589793D / 180.0D));
                     float wingY = (float)(this.posY + 1.0D);
                     double d0 = target.posX - (double)wingX;
                     double d1 = target.getEntityBoundingBox().minY - (double)wingY;
                     double d2 = target.posZ - (double)wingZ;
                     double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                     EntityStymphalianFeather entityarrow = new EntityStymphalianFeather(this.world, this);
                     entityarrow.setPosition((double)wingX, (double)wingY, (double)wingZ);
                     entityarrow.shoot(d0, d1 + d3 * 0.10000000298023223D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getDifficultyId() * 4));
                     this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
                     this.world.spawnEntity(entityarrow);
                  }
               }
            } else {
               this.setFlying(true);
            }
         }
      }

      StoneEntityProperties properties = (StoneEntityProperties)EntityPropertiesHandler.INSTANCE.getProperties(this, StoneEntityProperties.class);
      boolean flying = this.isFlying() && !this.onGround || this.airBorneCounter > 10 || this.getAnimation() == ANIMATION_SHOOT_ARROWS;
      if (flying && this.flyProgress < 20.0F) {
         ++this.flyProgress;
      } else if (!flying && this.flyProgress > 0.0F) {
         --this.flyProgress;
      }

      if (!this.isFlying() && this.airTarget != null && this.onGround && !this.world.isRemote) {
         this.airTarget = null;
      }

      if (this.isFlying() && this.getAttackTarget() == null) {
         this.flyAround();
      } else if (this.getAttackTarget() != null) {
         this.flyTowardsTarget();
      }

      if (!this.world.isRemote && this.doesWantToLand() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
         this.setFlying(false);
         this.airTarget = null;
      }

      if (!this.world.isRemote && this.isOffsetPositionInLiquid(0.0D, 0.0D, 0.0D) && !this.isFlying()) {
         this.setFlying(true);
         this.launchTicks = 0;
         this.flyTicks = 0;
         this.aiFlightLaunch = true;
      }

      if (!this.world.isRemote && this.onGround && this.isFlying() && !this.aiFlightLaunch && this.getAnimation() != ANIMATION_SHOOT_ARROWS) {
         this.setFlying(false);
         this.airTarget = null;
      }

      if ((properties == null || properties != null && !properties.isStone) && !this.world.isRemote && (this.flock == null || this.flock != null && this.flock.isLeader(this)) && this.getRNG().nextInt(100) == 0 && !this.isFlying() && this.getPassengers().isEmpty() && !this.isChild() && this.onGround) {
         this.setFlying(true);
         this.launchTicks = 0;
         this.flyTicks = 0;
         this.aiFlightLaunch = true;
      }

      if (!this.world.isRemote) {
         if (this.aiFlightLaunch && this.launchTicks < 40) {
            ++this.launchTicks;
         } else {
            this.launchTicks = 0;
            this.aiFlightLaunch = false;
         }

         if (this.isFlying()) {
            ++this.flyTicks;
         } else {
            this.flyTicks = 0;
         }
      }

      if (!this.onGround) {
         ++this.airBorneCounter;
      } else {
         this.airBorneCounter = 0;
      }

      if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !this.isFlying() && !this.world.isRemote) {
         this.setFlying(true);
         this.aiFlightLaunch = true;
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
      if (this.posY > (double)IceAndFire.CONFIG.stymphalianBirdFlightHeight) {
         this.setPosition(this.posX, (double)IceAndFire.CONFIG.stymphalianBirdFlightHeight, this.posZ);
      }

   }

   public boolean isDirectPathBetweenPoints(Entity entity, Vec3d vec1, Vec3d vec2) {
      RayTraceResult movingobjectposition = entity.world.rayTraceBlocks(vec1, new Vec3d(vec2.x, vec2.y + (double)entity.height * 0.5D, vec2.z), false, true, false);
      return movingobjectposition == null || movingobjectposition.typeOfHit != Type.BLOCK;
   }

   private boolean isLeaderNotFlying() {
      return this.flock != null && this.flock.getLeader() != null && !this.flock.getLeader().isFlying();
   }

   public void flyAround() {
      if (this.airTarget != null && this.isFlying()) {
         if (!this.isTargetInAir() || this.flyTicks > 6000 || !this.isFlying()) {
            this.airTarget = null;
         }

         this.flyTowardsTarget();
      }

   }

   public void flyTowardsTarget() {
      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) > 3.0F) {
         double targetX = (double)this.airTarget.getX() + 0.5D - this.posX;
         double targetY = (double)Math.min(this.airTarget.getY(), 256) + 1.0D - this.posY;
         double targetZ = (double)this.airTarget.getZ() + 0.5D - this.posZ;
         this.motionX += (Math.signum(targetX) * 0.5D - this.motionX) * 0.100000000372529D * (double)this.getFlySpeed(false);
         this.motionY += (Math.signum(targetY) * 0.5D - this.motionY) * 0.100000000372529D * (double)this.getFlySpeed(true);
         this.motionZ += (Math.signum(targetZ) * 0.5D - this.motionZ) * 0.100000000372529D * (double)this.getFlySpeed(false);
         float angle = (float)(Math.atan2(this.motionZ, this.motionX) * 180.0D / 3.141592653589793D) - 90.0F;
         float rotation = MathHelper.wrapDegrees(angle - this.rotationYaw);
         this.moveForward = 0.5F;
         this.prevRotationYaw = this.rotationYaw;
         this.rotationYaw += rotation;
         if (!this.isFlying()) {
            this.setFlying(true);
         }
      } else {
         this.airTarget = null;
      }

      if (this.airTarget != null && this.isTargetInAir() && this.isFlying() && this.getDistanceSquared(new Vec3d((double)this.airTarget.getX(), this.posY, (double)this.airTarget.getZ())) < 3.0F && this.doesWantToLand()) {
         this.setFlying(false);
      }

   }

   private float getFlySpeed(boolean y) {
      float speed = 2.0F;
      if (this.flock != null && !this.flock.isLeader(this) && this.getDistanceSq(this.flock.getLeader()) > 10.0D) {
         speed = 4.0F;
      }

      if (this.getAnimation() == ANIMATION_SHOOT_ARROWS && !y) {
         speed = (float)((double)speed * 0.05D);
      }

      return speed;
   }

   public void fall(float distance, float damageMultiplier) {
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

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.STYMPHALIAN_BIRD_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.STYMPHALIAN_BIRD_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.STYMPHALIAN_BIRD_DIE;
   }

   public void setAttackTarget(EntityLivingBase entity) {
      if (!this.isVictor(entity) || entity == null) {
         super.setAttackTarget(entity);
         if (this.flock != null && this.flock.isLeader(this) && entity != null) {
            this.flock.onLeaderAttack(entity);
         }

      }
   }

   public float getDistanceSquared(Vec3d vec3d) {
      float f = (float)(this.posX - vec3d.x);
      float f1 = (float)(this.posY - vec3d.y);
      float f2 = (float)(this.posZ - vec3d.z);
      return f * f + f1 * f1 + f2 * f2;
   }

   protected boolean isTargetInAir() {
      return this.airTarget != null && (this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR || this.world.getBlockState(this.airTarget).getMaterial() == Material.AIR);
   }

   public boolean doesWantToLand() {
      if (this.flock != null && !this.flock.isLeader(this) && this.flock.getLeader() != null) {
         return this.flock.getLeader().doesWantToLand();
      } else {
         return this.flyTicks > 500 || this.flyTicks > 40 && this.flyProgress == 0.0F;
      }
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
      return new Animation[]{NO_ANIMATION, ANIMATION_PECK, ANIMATION_SHOOT_ARROWS, ANIMATION_SPEAK};
   }

   public boolean shouldAnimalsFear(Entity entity) {
      return IceAndFire.CONFIG.stympahlianBirdAttackAnimals;
   }
}
