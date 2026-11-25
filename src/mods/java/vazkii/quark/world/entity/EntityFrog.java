package vazkii.quark.world.entity;

import com.google.common.collect.Sets;
import java.util.Calendar;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.base.util.CommonReflectiveAccessor;
import vazkii.quark.base.util.EntityOpacityHandler;
import vazkii.quark.world.entity.ai.EntityAIFavorBlock;
import vazkii.quark.world.entity.ai.EntityAIPassenger;
import vazkii.quark.world.entity.ai.EntityAITemptButNice;
import vazkii.quark.world.feature.Frogs;

public class EntityFrog extends EntityAnimal {
   public static final ResourceLocation FROG_LOOT_TABLE = new ResourceLocation("quark", "entities/frog");
   private static final DataParameter<Integer> TALK_TIME = EntityDataManager.createKey(EntityFrog.class, DataSerializers.VARINT);
   private static final DataParameter<Float> SIZE_MODIFIER = EntityDataManager.createKey(EntityFrog.class, DataSerializers.FLOAT);
   private static final Set<Item> TEMPTATION_ITEMS = Sets.newHashSet(new Item[]{Items.FISH, Items.SPIDER_EYE});
   private static final Set<Item> TEMPTATION_ITEMS_BUT_NICE = Sets.newHashSet(new Item[]{Items.FISH, Items.SPIDER_EYE, Items.CLOCK});
   public int spawnCd;
   public int spawnChain;
   public boolean isDuplicate;
   private int droppedLegs;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int currentMoveTypeDuration;

   public EntityFrog(World worldIn) {
      this(worldIn, 1.0F);
   }

   public EntityFrog(World worldIn, float sizeModifier) {
      super(worldIn);
      this.spawnCd = -1;
      this.spawnChain = 30;
      this.droppedLegs = -1;
      if (sizeModifier != 1.0F) {
         this.setSize(0.65F * sizeModifier, 0.5F * sizeModifier);
         this.dataManager.set(SIZE_MODIFIER, Float.valueOf(sizeModifier));
      }

      this.jumpHelper = new EntityFrog.FrogJumpHelper();
      this.moveHelper = new EntityFrog.FrogMoveHelper();
      this.setMovementSpeed(0.0D);
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(TALK_TIME, Integer.valueOf(0));
      this.dataManager.register(SIZE_MODIFIER, Float.valueOf(1.0F));
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAIPassenger(this));
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityFrog.AIPanic(1.25D));
      this.tasks.addTask(3, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(4, new EntityAITemptButNice(this, 1.2D, false, TEMPTATION_ITEMS, TEMPTATION_ITEMS_BUT_NICE));
      this.tasks.addTask(5, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(6, new EntityAIFavorBlock(this, 1.0D, Blocks.WATERLILY));
      this.tasks.addTask(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.5F));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(9, new EntityAILookIdle(this));
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
      this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public float getEyeHeight() {
      return 0.1F * this.getSizeModifier();
   }

   public boolean isEntityInsideOpaqueBlock() {
      return EntityOpacityHandler.isEntityInsideOpaqueBlock(this);
   }

   public int getTalkTime() {
      return ((Integer)this.dataManager.get(TALK_TIME)).intValue();
   }

   public float getSizeModifier() {
      return ((Float)this.dataManager.get(SIZE_MODIFIER)).floatValue();
   }

   public void onUpdate() {
      float sizeModifier = this.getSizeModifier();
      if (this.height != sizeModifier * 0.5F) {
         this.setSize(0.65F * sizeModifier, 0.5F * sizeModifier);
      }

      super.onUpdate();
      int talkTime = this.getTalkTime();
      if (talkTime > 0) {
         this.dataManager.set(TALK_TIME, Integer.valueOf(talkTime - 1));
      }

      if (Frogs.frogsDoTheFunny && this.spawnCd > 0 && this.spawnChain > 0) {
         --this.spawnCd;
         if (this.spawnCd == 0 && !this.world.isRemote) {
            float multiplier = 0.8F;
            EntityFrog newFrog = new EntityFrog(this.world);
            newFrog.setPosition(this.posX, this.posY, this.posZ);
            newFrog.motionX = (Math.random() - 0.5D) * (double)multiplier;
            newFrog.motionY = (Math.random() - 0.5D) * (double)multiplier;
            newFrog.motionZ = (Math.random() - 0.5D) * (double)multiplier;
            newFrog.isDuplicate = true;
            newFrog.spawnCd = 2;
            newFrog.spawnChain = this.spawnChain - 1;
            this.world.spawnEntity(newFrog);
            this.spawnChain = 0;
         }
      }

      this.prevRotationYaw = this.prevRotationYawHead;
      this.rotationYaw = this.rotationYawHead;
   }

   protected boolean canDropLoot() {
      return !this.isDuplicate && super.canDropLoot();
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return FROG_LOOT_TABLE;
   }

   protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, @Nonnull DamageSource source) {
      this.droppedLegs = 0;
      super.dropLoot(wasRecentlyHit, lootingModifier, source);
      this.droppedLegs = -1;
   }

   @Nullable
   public EntityItem entityDropItem(ItemStack stack, float offsetY) {
      if (this.droppedLegs >= 0) {
         int count = Math.max(4 - this.droppedLegs, 0);
         this.droppedLegs += stack.getCount();
         if (stack.getCount() > count) {
            ItemStack copy = stack.copy();
            copy.shrink(count);
            copy.getOrCreateSubCompound("display").setString("LocName", "item.quark:frog_maybe_leg.name");
            stack = stack.copy();
            stack.shrink(copy.getCount());
            super.entityDropItem(copy, offsetY);
         }
      }

      return super.entityDropItem(stack, offsetY);
   }

   public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
      if (super.processInteract(player, hand)) {
         return true;
      } else {
         Calendar calendar = this.world.getCurrentDate();
         if (Frogs.frogsDoTheFunny && calendar.get(7) == 4) {
            if (!this.world.isRemote && this.spawnChain > 0 && !this.isDuplicate) {
               this.spawnCd = 50;
               this.dataManager.set(TALK_TIME, Integer.valueOf(80));
               this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, QuarkSounds.ENTITY_FROG_WEDNESDAY, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public EntityAgeable createChild(@Nonnull EntityAgeable otherParent) {
      if (this.isDuplicate) {
         return null;
      } else {
         float sizeMod = this.getSizeModifier();
         if (otherParent instanceof EntityFrog) {
            if (((EntityFrog)otherParent).isDuplicate) {
               return null;
            }

            sizeMod = sizeMod + ((EntityFrog)otherParent).getSizeModifier();
            sizeMod = sizeMod / 2.0F;
         }

         double regression = this.rand.nextGaussian() / 20.0D;
         regression = regression * Math.abs(((double)sizeMod + regression) / (double)sizeMod);
         return new EntityFrog(this.world, MathHelper.clamp(sizeMod + (float)regression, 0.25F, 2.0F));
      }
   }

   public boolean isBreedingItem(ItemStack stack) {
      Calendar calendar = this.world.getCurrentDate();
      return !stack.isEmpty() && (Frogs.frogsDoTheFunny && calendar.get(7) == 4 ? TEMPTATION_ITEMS_BUT_NICE : TEMPTATION_ITEMS).contains(stack.getItem());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.spawnCd = compound.getInteger("Cooldown");
      if (compound.hasKey("Chain")) {
         this.spawnChain = compound.getInteger("Chain");
      }

      this.dataManager.set(TALK_TIME, Integer.valueOf(compound.getInteger("DudeAmount")));
      float sizeModifier = compound.hasKey("FrogAmount") ? compound.getFloat("FrogAmount") : 1.0F;
      this.dataManager.set(SIZE_MODIFIER, Float.valueOf(sizeModifier));
      this.setSize(0.65F * sizeModifier, 0.5F * sizeModifier);
      this.isDuplicate = compound.getBoolean("FakeFrog");
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("FrogAmount", this.getSizeModifier());
      compound.setInteger("Cooldown", this.spawnCd);
      compound.setInteger("Chain", this.spawnChain);
      compound.setInteger("DudeAmount", this.getTalkTime());
      compound.setBoolean("FakeFrog", this.isDuplicate);
   }

   protected SoundEvent getAmbientSound() {
      return QuarkSounds.ENTITY_FROG_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return QuarkSounds.ENTITY_FROG_HURT;
   }

   protected SoundEvent getDeathSound() {
      return QuarkSounds.ENTITY_FROG_DIE;
   }

   protected SoundEvent getJumpSound() {
      return QuarkSounds.ENTITY_FROG_JUMP;
   }

   public void updateAITasks() {
      if (this.currentMoveTypeDuration > 0) {
         --this.currentMoveTypeDuration;
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         EntityFrog.FrogJumpHelper jumpHelper = (EntityFrog.FrogJumpHelper)this.jumpHelper;
         if (!jumpHelper.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               Path path = this.navigator.getPath();
               Vec3d vec3d = new Vec3d(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                  vec3d = path.getPosition(this);
               }

               this.calculateRotationYaw(vec3d.x, vec3d.z);
               this.startJumping();
            }
         } else if (!jumpHelper.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public void spawnRunningParticles() {
   }

   private void calculateRotationYaw(double x, double z) {
      this.rotationYaw = (float)(MathHelper.atan2(z - this.posZ, x - this.posX) * 57.29577951308232D) - 90.0F;
   }

   private void enableJumpControl() {
      ((EntityFrog.FrogJumpHelper)this.jumpHelper).setCanJump(true);
   }

   private void disableJumpControl() {
      ((EntityFrog.FrogJumpHelper)this.jumpHelper).setCanJump(false);
   }

   private void updateMoveTypeDuration() {
      if (this.moveHelper.getSpeed() < 2.2D) {
         this.currentMoveTypeDuration = 10;
      } else {
         this.currentMoveTypeDuration = 1;
      }

   }

   private void checkLandingDelay() {
      this.updateMoveTypeDuration();
      this.disableJumpControl();
   }

   public void onLivingUpdate() {
      EntityLiving steed = null;
      double speed = 1.0D;
      Path path = null;
      if (this.isRiding() && this.getRidingEntity() instanceof EntityLiving) {
         steed = (EntityLiving)this.getRidingEntity();
         speed = CommonReflectiveAccessor.getSpeed(steed.getNavigator());
         path = steed.getNavigator().getPath();
         this.getMoveHelper().read(steed.getMoveHelper());
      }

      super.onLivingUpdate();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

      if (steed != null) {
         steed.getNavigator().setPath(path, speed);
      }

   }

   protected void jump() {
      super.jump();
      double d0 = this.moveHelper.getSpeed();
      if (d0 > 0.0D) {
         double d1 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d1 < 0.01D) {
            this.moveRelative(0.0F, 0.0F, 1.0F, 0.1F);
         }
      }

      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)1);
      }

   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigator().setSpeed(newSpeed);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
   }

   public void setJumping(boolean jumping) {
      super.setJumping(jumping);
      if (jumping) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 1) {
         this.createRunningParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public class AIPanic extends EntityAIPanic {
      public AIPanic(double speedIn) {
         super(EntityFrog.this, speedIn);
      }

      public void updateTask() {
         super.updateTask();
         EntityFrog.this.setMovementSpeed(this.speed);
      }
   }

   public class FrogJumpHelper extends EntityJumpHelper {
      private boolean canJump;

      public FrogJumpHelper() {
         super(EntityFrog.this);
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean canJumpIn) {
         this.canJump = canJumpIn;
      }

      public void doJump() {
         if (this.isJumping) {
            EntityFrog.this.startJumping();
            this.isJumping = false;
         }

      }
   }

   public class FrogMoveHelper extends EntityMoveHelper {
      private double nextJumpSpeed;

      public FrogMoveHelper() {
         super(EntityFrog.this);
      }

      public void onUpdateMoveHelper() {
         if (EntityFrog.this.onGround && !EntityFrog.this.isJumping && !((EntityFrog.FrogJumpHelper)EntityFrog.this.jumpHelper).getIsJumping()) {
            EntityFrog.this.setMovementSpeed(0.0D);
         } else if (this.isUpdating()) {
            EntityFrog.this.setMovementSpeed(this.nextJumpSpeed);
         }

         super.onUpdateMoveHelper();
      }

      public void setMoveTo(double x, double y, double z, double speedIn) {
         if (EntityFrog.this.isInWater()) {
            speedIn = 1.5D;
         }

         super.setMoveTo(x, y, z, speedIn);
         if (speedIn > 0.0D) {
            this.nextJumpSpeed = speedIn;
         }

      }
   }
}
