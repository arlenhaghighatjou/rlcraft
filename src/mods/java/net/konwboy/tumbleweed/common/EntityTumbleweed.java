package net.konwboy.tumbleweed.common;

import com.google.common.base.Predicate;
import io.netty.buffer.ByteBuf;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.Type;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;

public class EntityTumbleweed extends Entity implements IEntityAdditionalSpawnData {
   public static final int FADE_TIME = 80;
   private static final int DESPAWN_RANGE = 110;
   private static final float BASE_SIZE = 0.75F;
   private static final double WIND_X = -0.0625D;
   private static final double WIND_Z = -0.0625D;
   private static final DataParameter<Integer> SIZE = EntityDataManager.createKey(EntityTumbleweed.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> CUSTOM_WIND_ENABLED = EntityDataManager.createKey(EntityTumbleweed.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Float> CUSTOM_WIND_X = EntityDataManager.createKey(EntityTumbleweed.class, DataSerializers.FLOAT);
   private static final DataParameter<Float> CUSTOM_WIND_Z = EntityDataManager.createKey(EntityTumbleweed.class, DataSerializers.FLOAT);
   private static final DataParameter<Boolean> FADING = EntityDataManager.createKey(EntityTumbleweed.class, DataSerializers.BOOLEAN);
   private int age;
   public int fadeAge;
   public boolean persistent;
   private double windMod;
   private int lifetime;
   private float angularX;
   private float angularZ;
   public float stretch = 1.0F;
   public float prevStretch = 1.0F;
   private boolean prevOnGround;
   private double prevMotionX;
   private double prevMotionY;
   private double prevMotionZ;
   @SideOnly(Side.CLIENT)
   public float rot1;
   @SideOnly(Side.CLIENT)
   public float rot2;
   @SideOnly(Side.CLIENT)
   public float rot3;
   @SideOnly(Side.CLIENT)
   public Quaternion quat;
   @SideOnly(Side.CLIENT)
   public Quaternion prevQuat;
   private static Field trackedEntityHashTable = fieldsOfType(EntityTracker.class, IntHashMap.class)[0];
   private static Field encodedPosX = fieldsOfType(EntityTrackerEntry.class, Long.TYPE)[0];
   private static Field encodedPosY = fieldsOfType(EntityTrackerEntry.class, Long.TYPE)[1];
   private static Field encodedPosZ = fieldsOfType(EntityTrackerEntry.class, Long.TYPE)[2];

   public EntityTumbleweed(World world) {
      super(world);
      this.entityCollisionReduction = 0.95F;
      this.preventEntitySpawning = true;
      this.setEntityId(this.getEntityId());
      if (this.world.isRemote) {
         this.initClient();
      }

   }

   @SideOnly(Side.CLIENT)
   private void initClient() {
      this.rot1 = 360.0F * this.world.rand.nextFloat();
      this.rot2 = 360.0F * this.world.rand.nextFloat();
      this.rot3 = 360.0F * this.world.rand.nextFloat();
      this.quat = new Quaternion();
      this.prevQuat = new Quaternion();
   }

   protected void entityInit() {
      this.dataManager.register(SIZE, Integer.valueOf(2));
      this.dataManager.register(CUSTOM_WIND_ENABLED, Boolean.valueOf(false));
      this.dataManager.register(CUSTOM_WIND_X, Float.valueOf(0.0F));
      this.dataManager.register(CUSTOM_WIND_Z, Float.valueOf(0.0F));
      this.dataManager.register(FADING, Boolean.valueOf(false));
      this.updateSize();
   }

   protected void writeEntityToNBT(NBTTagCompound nbt) {
      nbt.setInteger("Size", this.getSize());
      nbt.setBoolean("CustomWindEnabled", this.getCustomWindEnabled());
      nbt.setDouble("CustomWindX", this.getCustomWindX());
      nbt.setDouble("CustomWindZ", this.getCustomWindZ());
      nbt.setBoolean("Persistent", this.persistent);
      AxisAlignedBB bb = this.getEntityBoundingBox();
      nbt.setTag("AABB", this.newDoubleNBTList(new double[]{bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ}));
   }

   protected void readEntityFromNBT(NBTTagCompound nbt) {
      if (nbt.hasKey("Size")) {
         this.dataManager.set(SIZE, Integer.valueOf(nbt.getInteger("Size")));
      }

      this.dataManager.set(CUSTOM_WIND_ENABLED, Boolean.valueOf(nbt.getBoolean("CustomWindEnabled")));
      this.dataManager.set(CUSTOM_WIND_X, Float.valueOf(nbt.getFloat("CustomWindX")));
      this.dataManager.set(CUSTOM_WIND_Z, Float.valueOf(nbt.getFloat("CustomWindZ")));
      this.persistent = nbt.getBoolean("Persistent");
      if (nbt.hasKey("AABB")) {
         NBTTagList aabb = nbt.getTagList("AABB", 6);
         this.setEntityBoundingBox(new AxisAlignedBB(aabb.getDoubleAt(0), aabb.getDoubleAt(1), aabb.getDoubleAt(2), aabb.getDoubleAt(3), aabb.getDoubleAt(4), aabb.getDoubleAt(5)));
      }

   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (key.equals(SIZE)) {
         this.updateSize();
      }

   }

   private void updateSize() {
      float mcSize = 0.75F + (float)this.getSize() * 0.125F;
      if (this.world.isRemote) {
         mcSize -= 4.8828125E-4F;
      }

      this.setSize(mcSize, mcSize);
   }

   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return null;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean canBePushed() {
      return true;
   }

   public void setEntityId(int id) {
      super.setEntityId(id);
      Random rand = new Random((long)id);
      this.windMod = 1.05D - 0.1D * rand.nextDouble();
      this.lifetime = 2400 + rand.nextInt(200);
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.world.isRemote && this.ticksExisted == 1) {
         this.trackerHack();
      }

      if (this.world.isRemote) {
         this.preTickClient();
      }

      if (this.getRidingEntity() != null) {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
      } else {
         if (!this.isInWater()) {
            this.motionY -= 0.012D;
         }

         this.prevMotionX = this.motionX;
         this.prevMotionY = this.motionY;
         this.prevMotionZ = this.motionZ;
         this.prevOnGround = this.onGround;
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         double windX = this.getCustomWindEnabled() ? this.getCustomWindX() : -0.0625D * this.windMod;
         double windZ = this.getCustomWindEnabled() ? this.getCustomWindZ() : -0.0625D * this.windMod;
         if (this.isInWater()) {
            this.motionY += 0.02D;
            this.motionX *= 0.95D;
            this.motionZ *= 0.95D;
            windZ = 0.0D;
            windX = 0.0D;
         } else if (windX != 0.0D || windZ != 0.0D) {
            this.motionX = windX;
            this.motionZ = windZ;
         }

         if (this.world.isRemote) {
            this.tickClient();
         }

         if (this.onGround) {
            if (windX * windX + windZ * windZ >= 0.0025000000000000005D) {
               this.motionY = Math.max(-this.prevMotionY * 0.7D, 0.24D - (double)this.getSize() * 0.02D);
            } else {
               this.motionY = -this.prevMotionY * 0.7D;
            }
         }

         this.motionX *= 0.98D;
         this.motionY *= 0.98D;
         this.motionZ *= 0.98D;
         this.collideWithNearbyEntities();
         if (!this.world.isRemote) {
            this.age += !this.collidedHorizontally && !this.isInWater() ? 1 : 8;
            this.tryDespawn();
         }

         if (this.isFading()) {
            ++this.fadeAge;
            if (this.fadeAge > 80) {
               this.setDead();
            }
         }

      }
   }

   @SideOnly(Side.CLIENT)
   private void preTickClient() {
      this.prevStretch = this.stretch;
      this.stretch *= 1.2F;
      if (this.stretch > 1.0F) {
         this.stretch = 1.0F;
      }

      this.prevQuat = new Quaternion(this.quat);
   }

   @SideOnly(Side.CLIENT)
   private void tickClient() {
      if (this.prevOnGround != this.onGround) {
         this.stretch *= 0.75F;
      }

      float motionAngleX = (float)(-this.prevMotionX) / (this.width * 0.5F);
      float motionAngleZ = (float)this.prevMotionZ / (this.width * 0.5F);
      if (this.onGround) {
         this.angularX = motionAngleX;
         this.angularZ = motionAngleZ;
      }

      if (this.isInWater()) {
         this.angularX += motionAngleX * 0.2F;
         this.angularZ += motionAngleZ * 0.2F;
      }

      float resistance = this.isInWater() ? 0.9F : 0.96F;
      this.angularX *= resistance;
      this.angularZ *= resistance;
      Quaternion temp = new Quaternion();
      temp.setFromAxisAngle(new Vector4f(1.0F, 0.0F, 0.0F, this.angularZ));
      Quaternion.mul(temp, this.quat, this.quat);
      temp.setFromAxisAngle(new Vector4f(0.0F, 0.0F, 1.0F, this.angularX));
      Quaternion.mul(temp, this.quat, this.quat);
   }

   private void trackerHack() {
      EntityTrackerEntry entry = this.getTrackerEntry();
      if (entry != null && entry.updateCounter == 0) {
         entry.updateCounter += 30;
      }

   }

   private void tryDespawn() {
      if (this.persistent) {
         this.age = 0;
      } else {
         EntityPlayer player = this.world.getClosestPlayerToEntity(this, -1.0D);
         if (player != null && player.getDistanceSq(this) > 12100.0D) {
            this.setDead();
         }

         if (this.age > this.lifetime && this.fadeAge == 0) {
            this.dataManager.set(FADING, Boolean.valueOf(true));
         }

      }
   }

   public boolean isInRangeToRenderDist(double distance) {
      return distance < 16384.0D;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else {
         if (!this.isDead && !this.world.isRemote) {
            this.setDead();
            SoundType sound = SoundType.PLANT;
            this.playSound(sound.getBreakSound(), (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            if (TumbleweedConfig.enableDrops) {
               this.dropItem();
            }
         }

         return true;
      }
   }

   private void dropItem() {
      ItemStack item = TumbleweedConfig.Logic.getRandomItem();
      if (item != null) {
         EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, item);
         entityitem.motionX = 0.0D;
         entityitem.motionY = 0.2D;
         entityitem.motionZ = 0.0D;
         entityitem.setDefaultPickupDelay();
         this.world.spawnEntity(entityitem);
      }

   }

   public boolean hitByEntity(Entity entityIn) {
      return entityIn instanceof EntityPlayer && this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F);
   }

   protected void playStepSound(BlockPos pos, Block blockIn) {
      this.playSound(SoundEvents.BLOCK_GRASS_STEP, 0.15F, 1.0F);
   }

   public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance) {
      return world.rand.nextFloat() < 0.7F && world.getGameRules().getBoolean("mobGriefing") && TumbleweedConfig.damageCrops;
   }

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   private void collideWithNearbyEntities() {
      for(Entity entity : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(0.2D, 0.0D, 0.2D), Entity::canBePushed)) {
         if (!this.world.isRemote && entity instanceof EntityMinecart && ((EntityMinecart)entity).getType() == Type.RIDEABLE && entity.motionX * entity.motionX + entity.motionZ * entity.motionZ > 0.01D && entity.getPassengers().isEmpty() && this.getRidingEntity() == null) {
            this.startRiding(entity);
            this.motionY += 0.25D;
            this.velocityChanged = true;
         }

         entity.applyEntityCollision(this);
      }

   }

   public boolean isNotColliding() {
      return this.world.checkNoEntityCollision(this.getEntityBoundingBox(), this) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.world.containsAnyLiquid(this.getEntityBoundingBox());
   }

   public void setSize(int size) {
      this.dataManager.set(SIZE, Integer.valueOf(size));
   }

   public int getSize() {
      return ((Integer)this.dataManager.get(SIZE)).intValue();
   }

   public double getCustomWindX() {
      return (double)((Float)this.dataManager.get(CUSTOM_WIND_X)).floatValue();
   }

   public double getCustomWindZ() {
      return (double)((Float)this.dataManager.get(CUSTOM_WIND_Z)).floatValue();
   }

   public boolean getCustomWindEnabled() {
      return ((Boolean)this.dataManager.get(CUSTOM_WIND_ENABLED)).booleanValue();
   }

   public boolean isFading() {
      return ((Boolean)this.dataManager.get(FADING)).booleanValue();
   }

   private static Field[] fieldsOfType(Class inClass, Class type) {
      return (Field[])Arrays.stream(inClass.getDeclaredFields()).filter((f) -> {
         return f.getType() == type;
      }).toArray((x$0) -> {
         return new Field[x$0];
      });
   }

   public EntityTrackerEntry getTrackerEntry() {
      EntityTrackerEntry entry = null;

      try {
         entry = (EntityTrackerEntry)((IntHashMap)trackedEntityHashTable.get(((WorldServer)this.world).getEntityTracker())).lookup(this.getEntityId());
      } catch (IllegalAccessException var3) {
         ;
      }

      return entry;
   }

   public void writeSpawnData(ByteBuf buffer) {
      EntityTrackerEntry entry = this.getTrackerEntry();

      try {
         buffer.writeLong(((Long)encodedPosX.get(entry)).longValue());
         buffer.writeLong(((Long)encodedPosY.get(entry)).longValue());
         buffer.writeLong(((Long)encodedPosZ.get(entry)).longValue());
      } catch (IllegalAccessException var4) {
         ;
      }

   }

   public void readSpawnData(ByteBuf additionalData) {
      this.serverPosX = additionalData.readLong();
      this.serverPosY = additionalData.readLong();
      this.serverPosZ = additionalData.readLong();
   }

   static {
      trackedEntityHashTable.setAccessible(true);
      encodedPosX.setAccessible(true);
      encodedPosY.setAccessible(true);
      encodedPosZ.setAccessible(true);
   }
}
