package noppes.vc;

import java.util.Optional;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.client.VCSounds;

public class EntityHolyHandGrenade extends EntityThrowable {
   private static final DataParameter<Boolean> Gravity = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> Arrow = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> Is3d = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> Glows = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> Rotating = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> Sticks = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> Velocity = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.VARINT);
   private static final DataParameter<Integer> Particle = EntityDataManager.createKey(EntityHolyHandGrenade.class, DataSerializers.VARINT);
   private BlockPos tilePos = BlockPos.ORIGIN;
   private Block inTile;
   public boolean inGround = false;
   private int inData = 0;
   public boolean canBePickedUp = false;
   public boolean destroyedOnEntityHit = true;
   private EntityLivingBase thrower;
   private String throwerName = null;
   private int ticksInGround;
   public int ticksInAir = 0;
   private double accelerationX;
   private double accelerationY;
   private double accelerationZ;
   public float damage = 5.0F;
   public int punch = 0;
   public boolean accelerate = false;
   public boolean explosiveDamage = true;
   public int explosiveRadius = 0;
   public int duration = 5;
   public int timeToLive = 1200;

   public EntityHolyHandGrenade(World par1World) {
      super(par1World);
      this.setSize(0.2F, 0.2F);
   }

   protected void entityInit() {
      this.dataManager.register(Velocity, Integer.valueOf(10));
      this.dataManager.register(Particle, Integer.valueOf(0));
      this.dataManager.register(Gravity, Boolean.valueOf(false));
      this.dataManager.register(Glows, Boolean.valueOf(false));
      this.dataManager.register(Arrow, Boolean.valueOf(false));
      this.dataManager.register(Is3d, Boolean.valueOf(false));
      this.dataManager.register(Rotating, Boolean.valueOf(false));
      this.dataManager.register(Sticks, Boolean.valueOf(false));
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double par1) {
      double d1 = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0D;
      d1 = d1 * 64.0D;
      return par1 < d1 * d1;
   }

   public EntityHolyHandGrenade(World par1World, EntityLivingBase par2EntityLiving, ItemStack item) {
      super(par1World);
      this.thrower = par2EntityLiving;
      if (this.thrower != null) {
         this.throwerName = this.thrower.getUniqueID().toString();
      }

      this.dataManager.set(Arrow, Boolean.valueOf(this.getItem() == Items.ARROW));
      this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY + (double)par2EntityLiving.getEyeHeight(), par2EntityLiving.posZ, par2EntityLiving.rotationYaw, par2EntityLiving.rotationPitch);
      this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * 0.1F);
      this.posY -= 0.10000000149011612D;
      this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * 0.1F);
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public void shoot(double par1, double par3, double par5, float par7, float par8) {
      float f2 = MathHelper.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
      float f3 = MathHelper.sqrt(par1 * par1 + par5 * par5);
      float yaw = (float)(Math.atan2(par1, par5) * 180.0D / 3.141592653589793D);
      float pitch = this.hasGravity() ? par7 : (float)(Math.atan2(par3, (double)f3) * 180.0D / 3.141592653589793D);
      this.prevRotationYaw = this.rotationYaw = yaw;
      this.prevRotationPitch = this.rotationPitch = pitch;
      this.motionX = (double)(MathHelper.sin(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F));
      this.motionZ = (double)(MathHelper.cos(yaw / 180.0F * 3.1415927F) * MathHelper.cos(pitch / 180.0F * 3.1415927F));
      this.motionY = (double)MathHelper.sin((pitch + 1.0F) / 180.0F * 3.1415927F);
      this.motionX += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
      this.motionZ += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
      this.motionY += this.rand.nextGaussian() * 0.007499999832361937D * (double)par8;
      this.motionX *= (double)this.getSpeed();
      this.motionZ *= (double)this.getSpeed();
      this.motionY *= (double)this.getSpeed();
      this.accelerationX = par1 / (double)f2 * 0.1D;
      this.accelerationY = par3 / (double)f2 * 0.1D;
      this.accelerationZ = par5 / (double)f2 * 0.1D;
      this.ticksInGround = 0;
   }

   public float getAngleForXYZ(double varX, double varY, double varZ, double horiDist, boolean arc) {
      float g = this.getGravityVelocity();
      float var1 = this.getSpeed() * this.getSpeed();
      double var2 = (double)g * horiDist;
      double var3 = (double)g * horiDist * horiDist + 2.0D * varY * (double)var1;
      double var4 = (double)(var1 * var1) - (double)g * var3;
      if (var4 < 0.0D) {
         return 30.0F;
      } else {
         float var6 = arc ? var1 + MathHelper.sqrt(var4) : var1 - MathHelper.sqrt(var4);
         float var7 = (float)(Math.atan2((double)var6, var2) * 180.0D / 3.141592653589793D);
         return var7;
      }
   }

   public void shoot(float speed) {
      double varX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F));
      double varZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * 3.1415927F) * MathHelper.cos(this.rotationPitch / 180.0F * 3.1415927F));
      double varY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * 3.1415927F));
      this.shoot(varX, varY, varZ, -this.rotationPitch, speed);
   }

   @SideOnly(Side.CLIENT)
   public void setPositionAndRotationDirect(double par1, double par3, double par5, float par7, float par8, int par9, boolean bo) {
      if (!this.world.isRemote || !this.inGround) {
         this.setPosition(par1, par3, par5);
         this.setRotation(par7, par8);
      }
   }

   public void onUpdate() {
      super.onEntityUpdate();
      IBlockState state = null;

      try {
         state = this.world.getBlockState(this.tilePos);
      } catch (Exception var11) {
         return;
      }

      Block block = state.getBlock();
      if ((this.isArrow() || this.sticksToWalls()) && this.tilePos != BlockPos.ORIGIN) {
         AxisAlignedBB axisalignedbb = state.getCollisionBoundingBox(this.world, this.tilePos);
         if (axisalignedbb != null && axisalignedbb.contains(new Vec3d(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.inGround) {
         int j = block.getMetaFromState(state);
         if (block == this.inTile && j == this.inData) {
            ++this.ticksInGround;
            if (this.ticksInGround == this.timeToLive) {
               this.setDead();
               boolean flag = ForgeEventFactory.getMobGriefingEvent(this.world, this);
               this.world.createExplosion(this, this.posX, this.posY, this.posZ, VariedCommodities.HolyHandGrenadeStrength, flag);
            }
         } else {
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }
      } else {
         ++this.ticksInAir;
         if (this.ticksInAir == 1200) {
            this.setDead();
         }

         Vec3d vec3 = new Vec3d(this.posX, this.posY, this.posZ);
         Vec3d vec31 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         RayTraceResult movingobjectposition = this.world.rayTraceBlocks(vec3, vec31, false, true, false);
         if (movingobjectposition != null) {
            if (movingobjectposition.typeOfHit == Type.BLOCK && this.world.getBlockState(movingobjectposition.getBlockPos()).getBlock() == Blocks.PORTAL) {
               this.setPortal(movingobjectposition.getBlockPos());
            } else {
               this.dataManager.set(Rotating, Boolean.valueOf(false));
               this.onImpact(movingobjectposition);
            }
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

         for(this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / 3.141592653589793D); this.rotationYaw - this.prevRotationYaw < -180.0F; this.prevRotationYaw -= 360.0F) {
            ;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.prevRotationPitch = this.rotationPitch;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw);
         if (this.isRotating()) {
            this.rotationPitch += this.getSpeed() * -60.0F;
         }

         float f2 = this.getMotionFactor();
         float f3 = this.getGravityVelocity();
         if (this.isInWater()) {
            if (this.world.isRemote) {
               for(int k = 0; k < 4; ++k) {
                  float f4 = 0.25F;
                  this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ, new int[0]);
               }
            }

            f2 = 0.8F;
         }

         this.motionX *= (double)f2;
         this.motionY *= (double)f2;
         this.motionZ *= (double)f2;
         if (this.hasGravity()) {
            this.motionY -= (double)f3;
         }

         if (this.accelerate) {
            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }

   }

   public static void playSound(Entity entity, SoundEvent sound, float volume, float pitch) {
      if (!entity.world.isRemote && sound != null && sound.getRegistryName() != null) {
         SPacketCustomSound packet = new SPacketCustomSound(sound.getRegistryName().toString(), SoundCategory.NEUTRAL, entity.posX, entity.posY, entity.posZ, volume, pitch);
         Optional.ofNullable(entity.getServer()).ifPresent((server) -> {
            server.getPlayerList().sendToAllNearExcept((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, volume > 1.0F ? (double)(16.0F * volume) : 16.0D, entity.world.provider.getDimension(), packet);
         });
      }

   }

   public boolean isBlock() {
      ItemStack item = this.getItemDisplay();
      return item.isEmpty() ? false : item.getItem() instanceof ItemBlock;
   }

   private Item getItem() {
      ItemStack item = this.getItemDisplay();
      return item.isEmpty() ? Items.AIR : item.getItem();
   }

   protected float getMotionFactor() {
      return this.accelerate ? 0.95F : 1.0F;
   }

   protected void onImpact(RayTraceResult movingobjectposition) {
      if (movingobjectposition.entityHit != null) {
         this.motionX *= -0.10000000149011612D;
         this.motionY *= -0.10000000149011612D;
         this.motionZ *= -0.10000000149011612D;
         this.rotationYaw += 180.0F;
         this.prevRotationYaw += 180.0F;
         this.ticksInAir = 0;
      } else {
         this.tilePos = movingobjectposition.getBlockPos();

         try {
            IBlockState state = this.world.getBlockState(this.tilePos);
            this.inTile = state.getBlock();
            this.inData = this.inTile.getMetaFromState(state);
            if (this.inTile != null) {
               this.inTile.onEntityCollidedWithBlock(this.world, this.tilePos, state, this);
            }

            playSound(this, VCSounds.HALLELUJAH, 4.0F, 1.0F);
         } catch (Exception var3) {
            ;
         }

         this.motionX = (double)((float)(movingobjectposition.hitVec.x - this.posX));
         this.motionY = (double)((float)(movingobjectposition.hitVec.y - this.posY));
         this.motionZ = (double)((float)(movingobjectposition.hitVec.z - this.posZ));
         float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
         this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
         this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
         this.inGround = true;
         if (!this.hasGravity()) {
            this.dataManager.set(Gravity, Boolean.valueOf(true));
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
      par1NBTTagCompound.setShort("xTile", (short)this.tilePos.getX());
      par1NBTTagCompound.setShort("yTile", (short)this.tilePos.getY());
      par1NBTTagCompound.setShort("zTile", (short)this.tilePos.getZ());
      par1NBTTagCompound.setByte("inTile", (byte)Block.getIdFromBlock(this.inTile));
      par1NBTTagCompound.setByte("inData", (byte)this.inData);
      par1NBTTagCompound.setByte("shake", (byte)this.throwableShake);
      par1NBTTagCompound.setBoolean("inGround", this.inGround);
      par1NBTTagCompound.setBoolean("isArrow", this.isArrow());
      par1NBTTagCompound.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
      par1NBTTagCompound.setBoolean("canBePickedUp", this.canBePickedUp);
      if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof EntityPlayer) {
         this.throwerName = this.thrower.getUniqueID().toString();
      }

      par1NBTTagCompound.setString("ownerName", this.throwerName == null ? "" : this.throwerName);
      par1NBTTagCompound.setTag("Item", this.getItemDisplay().writeToNBT(new NBTTagCompound()));
      par1NBTTagCompound.setFloat("damagev2", this.damage);
      par1NBTTagCompound.setInteger("punch", this.punch);
      par1NBTTagCompound.setInteger("velocity", ((Integer)this.dataManager.get(Velocity)).intValue());
      par1NBTTagCompound.setInteger("explosiveRadius", this.explosiveRadius);
      par1NBTTagCompound.setInteger("effectDuration", this.duration);
      par1NBTTagCompound.setBoolean("gravity", this.hasGravity());
      par1NBTTagCompound.setBoolean("accelerate", this.accelerate);
      par1NBTTagCompound.setBoolean("glows", ((Boolean)this.dataManager.get(Glows)).booleanValue());
      par1NBTTagCompound.setInteger("trailenum", ((Integer)this.dataManager.get(Particle)).intValue());
      par1NBTTagCompound.setBoolean("Render3D", ((Boolean)this.dataManager.get(Is3d)).booleanValue());
      par1NBTTagCompound.setBoolean("Spins", ((Boolean)this.dataManager.get(Rotating)).booleanValue());
      par1NBTTagCompound.setBoolean("Sticks", ((Boolean)this.dataManager.get(Sticks)).booleanValue());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.tilePos = new BlockPos(compound.getShort("xTile"), compound.getShort("yTile"), compound.getShort("zTile"));
      this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
      this.inData = compound.getByte("inData") & 255;
      this.throwableShake = compound.getByte("shake") & 255;
      this.inGround = compound.getByte("inGround") == 1;
      this.dataManager.set(Arrow, Boolean.valueOf(compound.getBoolean("isArrow")));
      this.throwerName = compound.getString("ownerName");
      this.canBePickedUp = compound.getBoolean("canBePickedUp");
      this.damage = compound.getFloat("damagev2");
      this.punch = compound.getInteger("punch");
      this.explosiveRadius = compound.getInteger("explosiveRadius");
      this.duration = compound.getInteger("effectDuration");
      this.accelerate = compound.getBoolean("accelerate");
      this.dataManager.set(Particle, Integer.valueOf(compound.getInteger("trailenum")));
      this.dataManager.set(Glows, Boolean.valueOf(compound.getBoolean("glows")));
      this.dataManager.set(Velocity, Integer.valueOf(compound.getInteger("velocity")));
      this.dataManager.set(Gravity, Boolean.valueOf(compound.getBoolean("gravity")));
      this.dataManager.set(Is3d, Boolean.valueOf(compound.getBoolean("Render3D")));
      this.dataManager.set(Rotating, Boolean.valueOf(compound.getBoolean("Spins")));
      this.dataManager.set(Sticks, Boolean.valueOf(compound.getBoolean("Sticks")));
      if (this.throwerName != null && this.throwerName.length() == 0) {
         this.throwerName = null;
      }

      if (compound.hasKey("direction")) {
         NBTTagList nbttaglist = compound.getTagList("direction", 6);
         this.motionX = nbttaglist.getDoubleAt(0);
         this.motionY = nbttaglist.getDoubleAt(1);
         this.motionZ = nbttaglist.getDoubleAt(2);
      }

   }

   public EntityLivingBase getThrower() {
      if (this.throwerName != null && !this.throwerName.isEmpty()) {
         try {
            EaglercraftUUID uuid = EaglercraftUUID.fromString(this.throwerName);
            if (this.thrower == null && uuid != null) {
               this.thrower = this.world.getPlayerEntityByUUID(uuid);
            }
         } catch (IllegalArgumentException var2) {
            ;
         }

         return this.thrower;
      } else {
         return null;
      }
   }

   public void setParticleEffect(int type) {
      this.dataManager.set(Particle, Integer.valueOf(type));
   }

   public void setHasGravity(boolean bo) {
      this.dataManager.set(Gravity, Boolean.valueOf(bo));
   }

   public void setIs3D(boolean bo) {
      this.dataManager.set(Is3d, Boolean.valueOf(bo));
   }

   public void setStickInWall(boolean bo) {
      this.dataManager.set(Sticks, Boolean.valueOf(bo));
   }

   public ItemStack getItemDisplay() {
      return new ItemStack(VCWeapons.holy_hand_grenade);
   }

   public float getBrightness() {
      return ((Boolean)this.dataManager.get(Glows)).booleanValue() ? 1.0F : super.getBrightness();
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return ((Boolean)this.dataManager.get(Glows)).booleanValue() ? 15728880 : super.getBrightnessForRender();
   }

   public boolean hasGravity() {
      return ((Boolean)this.dataManager.get(Gravity)).booleanValue();
   }

   public void setSpeed(int speed) {
      this.dataManager.set(Velocity, Integer.valueOf(speed));
   }

   public float getSpeed() {
      return (float)((Integer)this.dataManager.get(Velocity)).intValue() / 10.0F;
   }

   public boolean isArrow() {
      return ((Boolean)this.dataManager.get(Arrow)).booleanValue();
   }

   public void setRotating(boolean bo) {
      this.dataManager.set(Rotating, Boolean.valueOf(bo));
   }

   public boolean isRotating() {
      return ((Boolean)this.dataManager.get(Rotating)).booleanValue();
   }

   public boolean glows() {
      return ((Boolean)this.dataManager.get(Glows)).booleanValue();
   }

   public boolean is3D() {
      return ((Boolean)this.dataManager.get(Is3d)).booleanValue() || this.isBlock();
   }

   public boolean sticksToWalls() {
      return this.is3D() && ((Boolean)this.dataManager.get(Sticks)).booleanValue();
   }

   public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public ITextComponent getDisplayName() {
      return (ITextComponent)(!this.getItemDisplay().isEmpty() ? new TextComponentTranslation(this.getItemDisplay().getDisplayName(), new Object[0]) : super.getDisplayName());
   }

   public interface IProjectileCallback {
      boolean onImpact(EntityHolyHandGrenade var1, BlockPos var2, Entity var3);
   }

   public interface IProjectileDeadCallback {
      boolean onImpact(EntityHolyHandGrenade var1);
   }
}
