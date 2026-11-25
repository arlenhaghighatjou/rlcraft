package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.model.IFChainBuffer;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModKeys;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIAttackMelee;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIFleePlayer;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIFollowOwner;
import com.github.alexthe666.iceandfire.entity.ai.AmphithereAIHurtByTarget;
import com.github.alexthe666.iceandfire.entity.ai.EntityAIWatchClosestIgnoreRider;
import com.github.alexthe666.iceandfire.entity.ai.PathNavigateFlyingCreature;
import com.github.alexthe666.iceandfire.message.MessageDragonControl;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.AnimationHandler;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAmphithere extends EntityTameable implements IAnimatedEntity, IPhasesThroughBlock, IFlapable, IDragonFlute {
   private int animationTick;
   private Animation currentAnimation;
   private static final DataParameter<Integer> VARIANT = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> FLAP_TICKS = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
   private static final DataParameter<Byte> CONTROL_STATE = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> COMMAND = EntityDataManager.createKey(EntityAmphithere.class, DataSerializers.VARINT);
   public float flapProgress;
   private int flapTicks = 0;
   public float groundProgress = 0.0F;
   public float sitProgress = 0.0F;
   public float diveProgress = 0.0F;
   private int flightCooldown = 0;
   private int ticksFlying = 0;
   private boolean isFlying;
   private boolean isLandNavigator = true;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer roll_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer tail_buffer;
   @SideOnly(Side.CLIENT)
   public IFChainBuffer pitch_buffer;
   protected EntityAmphithere.FlightBehavior flightBehavior = EntityAmphithere.FlightBehavior.WANDER;
   private boolean changedFlightBehavior = false;
   @Nullable
   public BlockPos orbitPos = null;
   public float orbitRadius = 0.0F;
   private int ticksStill = 0;
   private int ridingTime = 0;
   public boolean isFallen;
   public static Animation ANIMATION_BITE = Animation.create(15);
   public static Animation ANIMATION_BITE_RIDER = Animation.create(15);
   public static Animation ANIMATION_WING_BLAST = Animation.create(30);
   public static Animation ANIMATION_TAIL_WHIP = Animation.create(30);
   public static Animation ANIMATION_SPEAK = Animation.create(10);
   private boolean isSitting;
   public BlockPos homePos;
   public boolean hasHomePosition = false;
   protected int ticksCircling = 0;
   public static final ResourceLocation LOOT = LootTableList.register(new ResourceLocation("iceandfire", "amphithere"));

   public EntityAmphithere(World worldIn) {
      super(worldIn);
      this.setSize(2.5F, 1.25F);
      this.stepHeight = 1.0F;
      if (FMLCommonHandler.instance().getSide().isClient()) {
         this.roll_buffer = new IFChainBuffer();
         this.pitch_buffer = new IFChainBuffer();
         this.tail_buffer = new IFChainBuffer();
      }

      this.switchNavigator(true);
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   public float getBlockPathWeight(BlockPos pos) {
      if (this.isFlying()) {
         return this.world.isAirBlock(pos) ? 10.0F : 0.0F;
      } else {
         return super.getBlockPathWeight(pos);
      }
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      boolean flag = itemstack.getItem() == Items.NAME_TAG || itemstack.getItem() == Items.LEAD;
      if (flag) {
         itemstack.interactWithEntity(player, this, hand);
         return true;
      } else if (itemstack != null && itemstack.getItem() == Items.COOKIE) {
         if (this.getGrowingAge() == 0 && !this.isInLove()) {
            this.setSitting(false);
            this.setInLove(player);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
            if (!player.isCreative()) {
               itemstack.shrink(1);
            }
         }

         return true;
      } else if (itemstack != null && itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == EnumDyeColor.BROWN.getDyeDamage()) {
         this.heal(5.0F);
         this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
         if (!player.isCreative()) {
            itemstack.shrink(1);
         }

         return true;
      } else {
         if (!super.processInteract(player, hand)) {
            if (itemstack != null && itemstack.getItem() == ModItems.dragon_stick && this.isOwner(player)) {
               if (player.isSneaking()) {
                  BlockPos pos = new BlockPos(this);
                  this.homePos = pos;
                  this.hasHomePosition = true;
                  player.sendStatusMessage(new TextComponentTranslation("amphithere.command.new_home", new Object[]{this.homePos.getX(), this.homePos.getY(), this.homePos.getZ()}), true);
                  return true;
               }

               return true;
            }

            if (player.isSneaking() && this.isOwner(player)) {
               if (player.getHeldItem(hand).isEmpty()) {
                  this.setCommand(this.getCommand() + 1);
                  if (this.getCommand() > 2) {
                     this.setCommand(0);
                  }

                  player.sendStatusMessage(new TextComponentTranslation("amphithere.command." + this.getCommand(), new Object[0]), true);
                  this.playSound(SoundEvents.ENTITY_ZOMBIE_INFECT, 1.0F, 1.0F);
                  return true;
               }

               return true;
            }

            if (!this.isTamed() || this.isOwner(player)) {
               player.startRiding(this);
               return true;
            }
         }

         return true;
      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LOOT;
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, this.aiSit = new EntityAISit(this));
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new AmphithereAIAttackMelee(this, 1.0D, true));
      this.tasks.addTask(2, new AmphithereAIFollowOwner(this, 1.0D, 10.0F, 2.0F));
      this.tasks.addTask(3, new AmphithereAIFleePlayer(this, 32.0F, 0.8D, 1.8D));
      this.tasks.addTask(3, new EntityAmphithere.AIFlyWander());
      this.tasks.addTask(3, new EntityAmphithere.AIFlyCircle());
      this.tasks.addTask(3, new EntityAmphithere.AILandWander(this, 1.0D));
      this.tasks.addTask(4, new EntityAIWatchClosestIgnoreRider(this, EntityLivingBase.class, 6.0F));
      this.tasks.addTask(4, new EntityAIMate(this, 1.0D));
      this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
      this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
      this.targetTasks.addTask(3, new AmphithereAIHurtByTarget(this, false, new Class[0]));
   }

   public boolean isStill() {
      return Math.abs(this.motionX) < 0.05D && Math.abs(this.motionZ) < 0.05D;
   }

   private void switchNavigator(boolean onLand) {
      if (onLand) {
         this.moveHelper = new EntityMoveHelper(this);
         this.navigator = new PathNavigateClimber(this, this.world);
         this.isLandNavigator = true;
      } else {
         this.moveHelper = new EntityAmphithere.FlyMoveHelper(this);
         this.navigator = new PathNavigateFlyingCreature(this, this.world);
         this.isLandNavigator = false;
      }

   }

   public boolean onLeaves() {
      IBlockState state = this.world.getBlockState(this.getPosition().down());
      return state.getBlock().isLeaves(state, this.world, this.getPosition().down());
   }

   public boolean attackEntityFrom(DamageSource source, float damage) {
      if (!this.isTamed() && this.isFlying() && !this.onGround && source.isProjectile() && !this.world.isRemote) {
         this.isFallen = true;
      }

      return super.attackEntityFrom(source, damage);
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      if (this.isPassenger(passenger) && this.isTamed()) {
         this.rotationYaw = passenger.rotationYaw;
      }

      if (!this.world.isRemote && !this.isTamed() && passenger instanceof EntityPlayer && this.getAnimation() == NO_ANIMATION && this.rand.nextInt(15) == 0) {
         this.setAnimation(ANIMATION_BITE_RIDER);
      }

      if (!this.world.isRemote && this.getAnimation() == ANIMATION_BITE_RIDER && this.getAnimationTick() == 6 && !this.isTamed()) {
         passenger.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
      }

      float pitch_forward = 0.0F;
      if (this.rotationPitch > 0.0F && this.isFlying()) {
         pitch_forward = this.rotationPitch / 45.0F * 0.45F;
      } else {
         pitch_forward = 0.0F;
      }

      float scaled_ground = this.groundProgress * 0.1F;
      float radius = (this.isTamed() ? 0.5F : 0.3F) - scaled_ground * 0.5F + pitch_forward;
      float angle = 0.017453292F * this.renderYawOffset;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      passenger.setPosition(this.posX + extraX, this.posY + 0.699999988079071D - (double)(scaled_ground * 0.14F) + (double)pitch_forward, this.posZ + extraZ);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == Items.COOKIE;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.isInLove()) {
         this.setFlying(false);
      }

      if (this.isSitting() && this.getAttackTarget() != null) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      boolean flapping = this.isFlapping();
      boolean flying = this.isFlying() && !this.onGround || !this.onGround && !this.onLeaves();
      boolean diving = flying && this.motionY <= -0.10000000149011612D || this.isFallen;
      boolean sitting = this.isSitting() && !this.isFlying();
      boolean notGrounded = flying || this.getAnimation() == ANIMATION_WING_BLAST;
      if (!this.world.isRemote) {
         if (this.isSitting() && (this.getCommand() != 1 || this.getControllingPassenger() != null)) {
            this.setSitting(false);
         }

         if (!this.isSitting() && this.getCommand() == 1 && this.getControllingPassenger() == null) {
            this.setSitting(true);
         }

         if (this.isSitting()) {
            this.getNavigator().clearPath();
            this.getMoveHelper().action = Action.WAIT;
         }

         if (flying) {
            ++this.ticksFlying;
         } else {
            this.ticksFlying = 0;
         }
      }

      if (sitting && this.sitProgress < 20.0F) {
         this.sitProgress += 0.5F;
      } else if (!sitting && this.sitProgress > 0.0F) {
         this.sitProgress -= 0.5F;
      }

      if (this.flightCooldown > 0) {
         --this.flightCooldown;
      }

      if (!this.world.isRemote) {
         if (this.flightBehavior == EntityAmphithere.FlightBehavior.CIRCLE) {
            ++this.ticksCircling;
         } else {
            this.ticksCircling = 0;
         }
      }

      if (this.getUntamedRider() != null && !this.isTamed()) {
         ++this.ridingTime;
      }

      if (this.getUntamedRider() == null) {
         this.ridingTime = 0;
      }

      if (!this.isTamed() && this.ridingTime > IceAndFire.CONFIG.amphithereTameTime && this.getUntamedRider() != null && this.getUntamedRider() instanceof EntityPlayer) {
         this.world.setEntityState(this, (byte)45);
         this.setTamedBy((EntityPlayer)this.getUntamedRider());
      }

      if (this.world.isRemote) {
         this.updateClientControls();
      }

      if (this.isStill()) {
         ++this.ticksStill;
      } else {
         this.ticksStill = 0;
      }

      if (!this.isFlying() && !this.isChild() && this.onGround && this.rand.nextInt(200) == 0 && this.flightCooldown == 0 && this.getPassengers().isEmpty() && !this.isAIDisabled() && this.canMove()) {
         this.motionY += 0.5D;
         this.setFlying(true);
      }

      if (this.getControllingPassenger() != null && this.isFlying() && !this.onGround) {
         this.rotationPitch = this.getControllingPassenger().rotationPitch / 2.0F;
         if (this.getControllingPassenger().rotationPitch > 25.0F && this.motionY > -1.0D) {
            if (this.motionY > 0.0D) {
               this.motionY = 0.0D;
            }

            this.motionY -= 0.1D;
         }

         if (this.getControllingPassenger().rotationPitch < -25.0F && this.motionY < 1.0D) {
            if (this.motionY < 0.0D) {
               this.motionY = 0.0D;
            }

            this.motionY += 0.1D;
         }
      }

      if (notGrounded && this.groundProgress > 0.0F) {
         this.groundProgress -= 2.0F;
      } else if (!notGrounded && this.groundProgress < 20.0F) {
         this.groundProgress += 2.0F;
      }

      if (diving && this.diveProgress < 20.0F) {
         ++this.diveProgress;
      } else if (!diving && this.diveProgress > 0.0F) {
         --this.diveProgress;
      }

      if (this.isFlying()) {
         this.motionY += 0.08D;
      }

      if (this.isFallen && this.flightBehavior != EntityAmphithere.FlightBehavior.NONE) {
         this.flightBehavior = EntityAmphithere.FlightBehavior.NONE;
      }

      if (this.flightBehavior == EntityAmphithere.FlightBehavior.NONE && this.getControllingPassenger() == null) {
         this.motionY -= 0.30000001192092896D;
      }

      if (this.isFlying() && !this.onGround && this.isFallen && this.getControllingPassenger() == null) {
         this.motionY -= 0.20000000298023224D;
         this.rotationPitch = Math.max(this.rotationPitch + 5.0F, 75.0F);
      }

      if (this.isFallen && this.onGround) {
         this.setFlying(false);
         if (this.isTamed()) {
            this.flightCooldown = 50;
         } else {
            this.flightCooldown = 12000;
         }

         this.isFallen = false;
      }

      if (flying && this.isLandNavigator) {
         this.switchNavigator(false);
      }

      if (!flying && !this.isLandNavigator) {
         this.switchNavigator(true);
      }

      if ((this.hasHomePosition || this.getCommand() == 2) && this.flightBehavior == EntityAmphithere.FlightBehavior.WANDER) {
         this.flightBehavior = EntityAmphithere.FlightBehavior.CIRCLE;
      }

      if (flapping && this.flapProgress < 10.0F) {
         ++this.flapProgress;
      } else if (!flapping && this.flapProgress > 0.0F) {
         --this.flapProgress;
      }

      if (this.flapTicks > 0) {
         --this.flapTicks;
      }

      this.renderYawOffset = this.rotationYaw;
      if (this.world.isRemote) {
         if (!this.onGround) {
            this.roll_buffer.calculateChainFlapBuffer(this.isBeingRidden() ? 55.0F : 90.0F, 3, 10.0F, 0.5F, this);
            this.pitch_buffer.calculateChainWaveBuffer(90.0F, 10, 10.0F, 0.5F, this);
         }

         this.tail_buffer.calculateChainSwingBuffer(70.0F, 20, 5.0F, this);
      }

      if (this.changedFlightBehavior) {
         this.changedFlightBehavior = false;
      }

      if (!flapping && (this.motionY > 0.15000000596046448D || this.motionY > 0.0D && this.ticksExisted % 200 == 0) && !this.onGround) {
         this.flapWings();
      }

      AnimationHandler.INSTANCE.updateAnimations(this);
   }

   public boolean canBeSteered() {
      return true;
   }

   public boolean isFlapping() {
      return this.flapTicks > 0;
   }

   public void setCommand(int command) {
      this.dataManager.set(COMMAND, Integer.valueOf(command));
      if (command == 1) {
         this.setSitting(true);
      } else {
         this.setSitting(false);
      }

   }

   public int getCommand() {
      return ((Integer)this.dataManager.get(COMMAND)).intValue().intValue();
   }

   public void flapWings() {
      this.flapTicks = 20;
   }

   public boolean isSitting() {
      if (this.world.isRemote) {
         boolean isSitting = (((Byte)this.dataManager.get(TAMED)).byteValue() & 1) != 0;
         this.isSitting = isSitting;
         return isSitting;
      } else {
         return this.isSitting;
      }
   }

   public void setSitting(boolean sitting) {
      if (!this.world.isRemote) {
         this.isSitting = sitting;
      }

      byte b0 = ((Byte)this.dataManager.get(TAMED)).byteValue();
      if (sitting) {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 | 1)));
      } else {
         this.dataManager.set(TAMED, Byte.valueOf((byte)(b0 & -2)));
      }

   }

   @Nullable
   public Entity getControllingPassenger() {
      for(Entity passenger : this.getPassengers()) {
         if (passenger instanceof EntityPlayer && this.getAttackTarget() != passenger) {
            EntityPlayer player = (EntityPlayer)passenger;
            if (this.isTamed() && this.getOwnerId() != null && this.getOwnerId().equals(player.getUniqueID())) {
               return player;
            }
         }
      }

      return null;
   }

   @Nullable
   public Entity getUntamedRider() {
      for(Entity passenger : this.getPassengers()) {
         if (passenger instanceof EntityPlayer) {
            return passenger;
         }
      }

      return null;
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.4D);
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(IceAndFire.CONFIG.amphithereMaxHealth);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
      this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(IceAndFire.CONFIG.amphithereAttackStrength);
      this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(32.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(VARIANT, Integer.valueOf(0));
      this.dataManager.register(FLYING, Boolean.valueOf(false));
      this.dataManager.register(FLAP_TICKS, Integer.valueOf(0));
      this.dataManager.register(CONTROL_STATE, Byte.valueOf((byte)0));
      this.dataManager.register(COMMAND, Integer.valueOf(0));
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("Variant", this.getVariant());
      compound.setBoolean("Flying", this.isFlying());
      compound.setInteger("FlightCooldown", this.flightCooldown);
      compound.setInteger("RidingTime", this.ridingTime);
      compound.setBoolean("HasHomePosition", this.hasHomePosition);
      if (this.homePos != null && this.hasHomePosition) {
         compound.setInteger("HomeAreaX", this.homePos.getX());
         compound.setInteger("HomeAreaY", this.homePos.getY());
         compound.setInteger("HomeAreaZ", this.homePos.getZ());
      }

      compound.setInteger("Command", this.getCommand());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.setVariant(compound.getInteger("Variant"));
      this.setFlying(compound.getBoolean("Flying"));
      this.flightCooldown = compound.getInteger("FlightCooldown");
      this.ridingTime = compound.getInteger("RidingTime");
      this.hasHomePosition = compound.getBoolean("HasHomePosition");
      if (this.hasHomePosition && compound.getInteger("HomeAreaX") != 0 && compound.getInteger("HomeAreaY") != 0 && compound.getInteger("HomeAreaZ") != 0) {
         this.homePos = new BlockPos(compound.getInteger("HomeAreaX"), compound.getInteger("HomeAreaY"), compound.getInteger("HomeAreaZ"));
      }

      this.setCommand(compound.getInteger("Command"));
   }

   public boolean isRidingPlayer(EntityPlayer player) {
      return this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger().getUniqueID().equals(player.getUniqueID()) && this.isTamed();
   }

   public boolean getCanSpawnHere() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getEntityBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      BlockPos blockpos = new BlockPos(i, j, k);
      this.world.getBlockState(blockpos.down()).getBlock();
      return this.world.canBlockSeeSky(blockpos.up());
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.getAnimation() == ANIMATION_BITE && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 10.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getAnimation() == ANIMATION_WING_BLAST && this.getAnimationTick() == 5) {
         this.playSound(ModSounds.AMPHITHERE_GUST, 1.0F, 1.0F);
      }

      if ((this.getAnimation() == ANIMATION_BITE || this.getAnimation() == ANIMATION_BITE_RIDER) && this.getAnimationTick() == 1) {
         this.playSound(ModSounds.AMPHITHERE_BITE, 1.0F, 1.0F);
      }

      if (this.getAnimation() == ANIMATION_WING_BLAST && this.getAttackTarget() != null && this.getAnimationTick() > 5 && this.getAnimationTick() < 22) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 25.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() / 2));
            this.getAttackTarget().isAirBorne = true;
            float f = MathHelper.sqrt(this.motionX * this.motionX * 0.20000000298023224D + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224D);
            EntityLivingBase var10000 = this.getAttackTarget();
            var10000.motionX /= 2.0D;
            var10000 = this.getAttackTarget();
            var10000.motionZ /= 2.0D;
            var10000 = this.getAttackTarget();
            var10000.motionX -= 0.5D / (double)f;
            var10000 = this.getAttackTarget();
            var10000.motionZ -= 0.5D / (double)f;
            if (this.getAttackTarget().onGround) {
               var10000 = this.getAttackTarget();
               var10000.motionY /= 2.0D;
               var10000 = this.getAttackTarget();
               var10000.motionY += 4.0D;
               if (this.getAttackTarget().motionY > 0.4000000059604645D) {
                  this.getAttackTarget().motionY = 0.4000000059604645D;
               }
            }
         }
      }

      if (this.getAnimation() == ANIMATION_TAIL_WHIP && this.getAttackTarget() != null && this.getAnimationTick() == 7) {
         double dist = this.getDistanceSq(this.getAttackTarget());
         if (dist < 10.0D) {
            this.getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
            this.getAttackTarget().isAirBorne = true;
            float f = MathHelper.sqrt(0.5D);
            EntityLivingBase var14 = this.getAttackTarget();
            var14.motionX /= 2.0D;
            var14 = this.getAttackTarget();
            var14.motionZ /= 2.0D;
            var14 = this.getAttackTarget();
            var14.motionX -= 0.5D / (double)f * 4.0D;
            var14 = this.getAttackTarget();
            var14.motionZ -= 0.5D / (double)f * 4.0D;
            if (this.getAttackTarget().onGround) {
               var14 = this.getAttackTarget();
               var14.motionY /= 2.0D;
               var14 = this.getAttackTarget();
               var14.motionY += 4.0D;
               if (this.getAttackTarget().motionY > 0.4000000059604645D) {
                  this.getAttackTarget().motionY = 0.4000000059604645D;
               }
            }
         }
      }

      if (this.world.isRemote) {
         this.updateClientControls();
      }

      if (this.up() && !this.world.isRemote && !this.isFlying()) {
         ++this.motionY;
         this.setFlying(true);
      }

      if (this.onGround && this.getControllingPassenger() != null) {
         this.setFlying(false);
      }

      if (this.dismount() && this.isFlying() && this.onGround) {
         this.setFlying(false);
      }

      if (this.getUntamedRider() != null && this.getUntamedRider().isSneaking()) {
         MiscPlayerProperties properties = (MiscPlayerProperties)EntityPropertiesHandler.INSTANCE.getProperties(this.getUntamedRider(), MiscPlayerProperties.class);
         if (properties != null) {
            properties.hasDismountedDragon = true;
         }

         this.getUntamedRider().dismountRidingEntity();
      }

      if (this.attack() && this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer) {
         EntityLivingBase target = DragonUtils.riderLookingAtEntity(this, (EntityPlayer)this.getControllingPassenger(), 2.5D);
         if (this.getAnimation() != ANIMATION_BITE) {
            this.setAnimation(ANIMATION_BITE);
         }

         if (target != null) {
            target.attackEntityFrom(DamageSource.causeMobDamage(this), (float)((int)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()));
         }
      }

      if (this.getAttackTarget() != null && this.isOwner(this.getAttackTarget())) {
         this.setAttackTarget((EntityLivingBase)null);
      }

      if (this.getAttackTarget() != null && this.onGround && this.isFlying() && this.ticksFlying > 40) {
         this.setFlying(false);
      }

   }

   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getAnimation() != ANIMATION_BITE && this.getAnimation() != ANIMATION_TAIL_WHIP && this.getAnimation() != ANIMATION_WING_BLAST && this.getControllingPassenger() == null) {
         if (this.rand.nextBoolean()) {
            this.setAnimation(ANIMATION_BITE);
         } else {
            this.setAnimation(!this.getRNG().nextBoolean() && !this.isFlying() ? ANIMATION_TAIL_WHIP : ANIMATION_WING_BLAST);
         }

         return true;
      } else {
         return false;
      }
   }

   @SideOnly(Side.CLIENT)
   protected void updateClientControls() {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.isRidingPlayer(mc.player)) {
         byte previousState = this.getControlState();
         this.up(mc.gameSettings.keyBindJump.isKeyDown());
         this.down(ModKeys.dragon_down.isKeyDown());
         this.attack(ModKeys.dragon_strike.isKeyDown());
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

      if (this.getRidingEntity() != null && this.getRidingEntity() == mc.player) {
         byte previousState = this.getControlState();
         this.dismount(mc.gameSettings.keyBindSneak.isKeyDown());
         byte controlState = this.getControlState();
         if (controlState != previousState) {
            IceAndFire.NETWORK_WRAPPER.sendToServer(new MessageDragonControl(this.getEntityId(), controlState, this.posX, this.posY, this.posZ));
         }
      }

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

   public int getVariant() {
      return ((Integer)this.dataManager.get(VARIANT)).intValue().intValue();
   }

   public void setVariant(int variant) {
      this.dataManager.set(VARIANT, Integer.valueOf(variant));
   }

   public boolean up() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() & 1) == 1;
   }

   public boolean down() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 1 & 1) == 1;
   }

   public boolean attack() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 2 & 1) == 1;
   }

   public boolean dismount() {
      return (((Byte)this.dataManager.get(CONTROL_STATE)).byteValue() >> 3 & 1) == 1;
   }

   public void up(boolean up) {
      this.setStateField(0, up);
   }

   public void down(boolean down) {
      this.setStateField(1, down);
   }

   public void attack(boolean attack) {
      this.setStateField(2, attack);
   }

   public void dismount(boolean dismount) {
      this.setStateField(3, dismount);
   }

   private void setStateField(int i, boolean newState) {
      byte prevState = ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
      if (newState) {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState | 1 << i)));
      } else {
         this.dataManager.set(CONTROL_STATE, Byte.valueOf((byte)(prevState & ~(1 << i))));
      }

   }

   public byte getControlState() {
      return ((Byte)this.dataManager.get(CONTROL_STATE)).byteValue();
   }

   public void setControlState(byte state) {
      this.dataManager.set(CONTROL_STATE, Byte.valueOf(state));
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return ModSounds.AMPHITHERE_IDLE;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource source) {
      return ModSounds.AMPHITHERE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return ModSounds.AMPHITHERE_DIE;
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
      return new Animation[]{ANIMATION_BITE, ANIMATION_BITE_RIDER, ANIMATION_WING_BLAST, ANIMATION_TAIL_WHIP, ANIMATION_SPEAK};
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

   public boolean isBlinking() {
      return this.ticksExisted % 50 > 40;
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      EntityAmphithere amphithere = new EntityAmphithere(this.world);
      amphithere.setVariant(this.getRNG().nextInt(5));
      return amphithere;
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 15 + this.world.rand.nextInt(10);
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      this.setVariant(this.getRNG().nextInt(5));
      return livingdata;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public static BlockPos getPositionRelativetoGround(Entity entity, World world, double x, double z, EaglercraftRandom rand) {
      BlockPos pos = new BlockPos(x, entity.posY, z);

      for(int yDown = 0; yDown < 6 + rand.nextInt(6); ++yDown) {
         if (!world.isAirBlock(pos.down(yDown))) {
            return pos.up(yDown);
         }
      }

      return pos;
   }

   public static BlockPos getPositionInOrbit(EntityAmphithere entity, World world, BlockPos orbit, EaglercraftRandom rand) {
      float possibleOrbitRadius = entity.orbitRadius + 10.0F;
      float radius = 10.0F;
      if (entity.getCommand() == 2) {
         if (entity.getOwner() != null) {
            orbit = entity.getOwner().getPosition().up(7);
            radius = 5.0F;
         }
      } else if (entity.hasHomePosition) {
         orbit = entity.homePos.up(30);
         radius = 30.0F;
      }

      float angle = 0.017453292F * possibleOrbitRadius;
      double extraX = (double)(radius * MathHelper.sin((float)(3.141592653589793D + (double)angle)));
      double extraZ = (double)(radius * MathHelper.cos(angle));
      BlockPos radialPos = new BlockPos((double)orbit.getX() + extraX, (double)orbit.getY(), (double)orbit.getZ() + extraZ);
      entity.orbitRadius = possibleOrbitRadius;
      return radialPos;
   }

   public boolean canPhaseThroughBlock(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().isLeaves(world.getBlockState(pos), world, pos);
   }

   public void travel(float strafe, float forward, float vertical) {
      if (!this.canMove() && !this.isBeingRidden()) {
         strafe = 0.0F;
         forward = 0.0F;
         super.travel(strafe, forward, vertical);
      } else {
         if (this.isBeingRidden() && this.canBeSteered()) {
            EntityLivingBase controller = (EntityLivingBase)this.getControllingPassenger();
            if (controller != null) {
               strafe = controller.moveStrafing * 0.5F;
               forward = controller.moveForward;
               if (!this.onGround) {
                  strafe = 0.0F;
                  forward = 1.5F;
               }

               if (forward <= 0.0F) {
                  forward *= 0.25F;
               }

               if (this.isFlying()) {
                  this.motionX *= 1.06D;
                  this.motionZ *= 1.06D;
               }

               this.jumpMovementFactor = 0.05F;
               this.setAIMoveSpeed(this.onGround ? (float)this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() : 2.0F);
               vertical = 0.0F;
               super.travel(strafe, 0.0F, forward);
               return;
            }
         }

         super.travel(strafe, forward, vertical);
      }
   }

   public boolean canMove() {
      return this.getControllingPassenger() == null && this.sitProgress == 0.0F && !this.isSitting();
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 45) {
         this.playEffect();
      } else {
         super.handleStatusUpdate(id);
      }

   }

   protected void playEffect() {
      EnumParticleTypes enumparticletypes = EnumParticleTypes.HEART;

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(enumparticletypes, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }

   public void onHearFlute(EntityPlayer player) {
      if (!this.onGround && this.isTamed()) {
         this.isFallen = true;
      }

   }

   class AIFlyCircle extends EntityAIBase {
      BlockPos target;

      public AIFlyCircle() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityAmphithere.this.flightBehavior == EntityAmphithere.FlightBehavior.CIRCLE && EntityAmphithere.this.canMove()) {
            if (EntityAmphithere.this.isFlying()) {
               EntityAmphithere.this.orbitPos = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.posX + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.posZ + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.rand);
               this.target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.orbitPos, EntityAmphithere.this.rand);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
         RayTraceResult raytraceresult = EntityAmphithere.this.world.rayTraceBlocks(new Vec3d((double)posVec31.getX() + 0.5D, (double)posVec31.getY() + 0.5D, (double)posVec31.getZ() + 0.5D), new Vec3d((double)posVec32.getX() + 0.5D, (double)posVec32.getY() + (double)EntityAmphithere.this.height * 0.5D, (double)posVec32.getZ() + 0.5D), false, true, false);
         return raytraceresult == null || raytraceresult.typeOfHit == Type.MISS;
      }

      public boolean shouldContinueExecuting() {
         return EntityAmphithere.this.getAttackTarget() == null && EntityAmphithere.this.flightBehavior == EntityAmphithere.FlightBehavior.CIRCLE;
      }

      public void updateTask() {
         if (EntityAmphithere.this.getDistance((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ()) < 5.0D) {
            this.target = EntityAmphithere.getPositionInOrbit(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.orbitPos, EntityAmphithere.this.rand);
         }

         if (EntityAmphithere.this.world.isAirBlock(this.target)) {
            EntityAmphithere.this.moveHelper.setMoveTo((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 0.25D);
            if (EntityAmphithere.this.getAttackTarget() == null) {
               EntityAmphithere.this.getLookHelper().setLookPosition((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
         }

      }
   }

   class AIFlyWander extends EntityAIBase {
      BlockPos target;

      public AIFlyWander() {
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (EntityAmphithere.this.flightBehavior == EntityAmphithere.FlightBehavior.WANDER && EntityAmphithere.this.canMove()) {
            if (!EntityAmphithere.this.isFlying()) {
               return false;
            } else {
               this.target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.posX + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.posZ + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.rand);
               EntityAmphithere.this.orbitPos = null;
               return !EntityAmphithere.this.getMoveHelper().isUpdating() || EntityAmphithere.this.ticksStill >= 50;
            }
         } else {
            return false;
         }
      }

      protected boolean isDirectPathBetweenPoints(BlockPos posVec31, BlockPos posVec32) {
         return true;
      }

      public boolean shouldContinueExecuting() {
         return false;
      }

      public void updateTask() {
         this.target = EntityAmphithere.getPositionRelativetoGround(EntityAmphithere.this, EntityAmphithere.this.world, EntityAmphithere.this.posX + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.posZ + (double)EntityAmphithere.this.rand.nextInt(30) - 15.0D, EntityAmphithere.this.rand);
         if (EntityAmphithere.this.world.isAirBlock(this.target)) {
            EntityAmphithere.this.moveHelper.setMoveTo((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 0.25D);
            if (EntityAmphithere.this.getAttackTarget() == null) {
               EntityAmphithere.this.getLookHelper().setLookPosition((double)this.target.getX() + 0.5D, (double)this.target.getY() + 0.5D, (double)this.target.getZ() + 0.5D, 180.0F, 20.0F);
            }
         }

      }
   }

   class AILandWander extends EntityAIWander {
      public AILandWander(EntityCreature creature, double speed) {
         super(creature, speed, 10);
      }

      public boolean shouldExecute() {
         return this.entity.onGround && super.shouldExecute() && ((EntityAmphithere)this.entity).canMove();
      }
   }

   public static enum FlightBehavior {
      CIRCLE,
      WANDER,
      NONE;

      private FlightBehavior() {
      }
   }

   class FlyMoveHelper extends EntityMoveHelper {
      public FlyMoveHelper(EntityAmphithere entity) {
         super(entity);
         this.speed = 1.75D;
      }

      public void onUpdateMoveHelper() {
         if (EntityAmphithere.this.canMove()) {
            if (this.action == Action.MOVE_TO) {
               double d0 = this.posX - EntityAmphithere.this.posX;
               double d1 = this.posY - EntityAmphithere.this.posY;
               double d2 = this.posZ - EntityAmphithere.this.posZ;
               double d3 = d0 * d0 + d1 * d1 + d2 * d2;
               d3 = (double)MathHelper.sqrt(d3);
               if (d3 < 6.0D && EntityAmphithere.this.getAttackTarget() == null) {
                  if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == EntityAmphithere.FlightBehavior.WANDER && EntityAmphithere.this.rand.nextInt(30) == 0) {
                     EntityAmphithere.this.flightBehavior = EntityAmphithere.FlightBehavior.CIRCLE;
                     EntityAmphithere.this.changedFlightBehavior = true;
                  }

                  if (!EntityAmphithere.this.changedFlightBehavior && EntityAmphithere.this.flightBehavior == EntityAmphithere.FlightBehavior.CIRCLE && EntityAmphithere.this.rand.nextInt(5) == 0 && EntityAmphithere.this.ticksCircling > 150) {
                     EntityAmphithere.this.flightBehavior = EntityAmphithere.FlightBehavior.WANDER;
                     EntityAmphithere.this.changedFlightBehavior = true;
                  }

                  if (EntityAmphithere.this.hasHomePosition && EntityAmphithere.this.flightBehavior != EntityAmphithere.FlightBehavior.NONE || EntityAmphithere.this.getCommand() == 2) {
                     EntityAmphithere.this.flightBehavior = EntityAmphithere.FlightBehavior.CIRCLE;
                  }
               }

               if (d3 < 1.0D && EntityAmphithere.this.getAttackTarget() == null) {
                  EntityAmphithere.this.motionX *= 0.5D;
                  EntityAmphithere.this.motionY *= 0.5D;
                  EntityAmphithere.this.motionZ *= 0.5D;
               } else {
                  EntityAmphithere.this.motionX += d0 / d3 * 0.5D * this.speed;
                  EntityAmphithere.this.motionY += d1 / d3 * 0.5D * this.speed;
                  EntityAmphithere.this.motionZ += d2 / d3 * 0.5D * this.speed;
                  float f1 = (float)(-(MathHelper.atan2(d1, d3) * 57.29577951308232D));
                  this.entity.rotationPitch = f1;
                  if (EntityAmphithere.this.getAttackTarget() == null) {
                     EntityAmphithere.this.rotationYaw = -((float)MathHelper.atan2(EntityAmphithere.this.motionX, EntityAmphithere.this.motionZ)) * 57.295776F;
                     EntityAmphithere.this.renderYawOffset = EntityAmphithere.this.rotationYaw;
                  } else {
                     double d4 = EntityAmphithere.this.getAttackTarget().posX - EntityAmphithere.this.posX;
                     double d5 = EntityAmphithere.this.getAttackTarget().posZ - EntityAmphithere.this.posZ;
                     EntityAmphithere.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
                     EntityAmphithere.this.renderYawOffset = EntityAmphithere.this.rotationYaw;
                  }
               }
            }

         }
      }
   }
}
