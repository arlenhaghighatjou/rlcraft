package rlmixins.entity.flare;

import cursedflames.bountifulbaubles.util.Util;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rlmixins.RLMixins;

public class EntityFlareNonAlbedo extends EntityArrow {
   protected boolean inGround;
   protected BlockPos tilePos;
   protected Block inTile;
   protected double tileX;
   protected double tileY;
   protected double tileZ;
   protected int xTile;
   protected int yTile;
   protected int zTile;
   protected int ticksInGround;
   private boolean isPlayingSound;

   public EntityFlareNonAlbedo(World world) {
      super(world);
      this.ticksInGround = 0;
      this.isPlayingSound = false;
   }

   public EntityFlareNonAlbedo(World world, double x, double y, double z) {
      super(world);
      this.ticksInGround = 0;
      this.isPlayingSound = false;
      this.setPosition(x, y, z);
   }

   public EntityFlareNonAlbedo(World world, EntityLivingBase thrower) {
      this(world, thrower.posX, thrower.posY + (double)thrower.getEyeHeight() - 0.10000000149011612D, thrower.posZ);
      this.shootingEntity = thrower;
   }

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      double f = (double)MathHelper.sqrt(x * x + y * y + z * z);
      x = x / f;
      y = y / f;
      z = z / f;
      x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      double f1 = (double)MathHelper.sqrt(x * x + z * z);
      this.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
      this.rotationPitch = (float)(MathHelper.atan2(y, f1) * 57.29577951308232D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
   }

   public void shoot(EntityLivingBase entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
      this.shootingEntity = entityThrower;
      float f = -MathHelper.sin(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
      float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * 0.017453292F);
      float f2 = MathHelper.cos(rotationYawIn * 0.017453292F) * MathHelper.cos(rotationPitchIn * 0.017453292F);
      this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
      this.motionX += entityThrower.motionX;
      this.motionZ += entityThrower.motionZ;
      if (!entityThrower.onGround) {
         this.motionY += entityThrower.motionY;
      }

   }

   protected void arrowHit(EntityLivingBase entity) {
      if (!(entity instanceof EntityEnderman)) {
         entity.setFire(5);
      }

   }

   protected void entityInit() {
      super.entityInit();
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.inGround = compound.getBoolean("inGround");
      if (compound.hasKey("tilePos")) {
         NBTTagCompound pos = compound.getCompoundTag("tilePos");
         this.tilePos = Util.blockPosFromNBT(pos);
      }

      if (compound.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(compound.getString("inTile"));
      }

      if (compound.hasUniqueId("thrower")) {
         UUID thrower = compound.getUniqueId("thrower");
         this.shootingEntity = this.world.getPlayerEntityByUUID(thrower);
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setBoolean("inGround", this.inGround);
      if (this.tilePos != null) {
         NBTTagCompound pos = Util.blockPosToNBT(this.tilePos);
         compound.setTag("tilePos", pos);
      }

      ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(this.inTile);
      compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
      if (this.shootingEntity != null) {
         compound.setUniqueId("thrower", this.shootingEntity.getUniqueID());
      }

   }

   protected float getGravityVelocity() {
      return 0.03F;
   }

   public void onUpdate() {
      super.onEntityUpdate();
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
         this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * 57.29577951308232D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

      int particleRate = this.inGround ? 10 : 2;
      if (this.world.isRemote) {
         Vec3d vel = (new Vec3d(0.0D, 0.0D, -1.0D)).rotatePitch((float)((double)this.rotationPitch * 0.017453292519943295D)).rotateYaw((float)((double)this.rotationYaw * 0.017453292519943295D));
         if (this.world.getTotalWorldTime() % (long)particleRate == 0L) {
            this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + vel.x * 0.3D, this.posY + vel.y * 0.3D, this.posZ + vel.z * 0.3D, vel.x * 0.055D, vel.y * 0.055D, vel.z * 0.055D, new int[0]);
         }

         if (!this.isPlayingSound) {
            this.isPlayingSound = true;
            RLMixins.PROXY.playSoundFlare(this);
         }
      }

      if (this.ticksExisted > 6000) {
         this.setDead();
      }

      if (this.inGround && this.tilePos != null) {
         if (this.world.getBlockState(this.tilePos).getBlock() == this.inTile) {
            ++this.ticksInGround;
            return;
         }

         this.inGround = false;
         this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
      }

      Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
      vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      Entity entity = null;
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
      double d0 = 0.0D;

      for(Entity entity1 : list) {
         if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksExisted >= 5)) {
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
            RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);
            if (raytraceresult1 != null) {
               double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
               if (d1 < d0 || d0 == 0.0D) {
                  entity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null) {
         if (raytraceresult.typeOfHit == Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
            this.setPortal(raytraceresult.getBlockPos());
         } else if (!ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onHit(raytraceresult);
         }
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);
      this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * 57.29577951308232D);
      double velMultiplier = 0.995D;
      float gravity = this.getGravityVelocity();
      if (this.isInWater()) {
         for(int j = 0; j < 4; ++j) {
            this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
         }

         velMultiplier = 0.8D;
      }

      this.motionX *= velMultiplier;
      this.motionY *= velMultiplier;
      this.motionZ *= velMultiplier;
      if (!this.hasNoGravity()) {
         if (this.motionY > 0.0D) {
            this.motionY -= (double)gravity;
         } else {
            double preY = this.motionY;
            this.motionY -= (double)gravity / 8.0D;
            this.motionX *= 0.95D;
            this.motionY *= 1.01D;
            this.motionZ *= 0.95D;
            if (this.motionY - preY > (double)(gravity / 4.0F)) {
               this.motionY = preY - (double)(gravity / 4.0F);
            }
         }
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      if (this.inGround) {
         ;
      }

   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         if (entity.world.isRemote && this.ticksExisted < 5) {
            return;
         }

         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         int i = MathHelper.ceil((double)f * 0.5D);
         DamageSource damagesource;
         if (this.shootingEntity == null) {
            damagesource = DamageSource.causeArrowDamage(this, this);
         } else {
            damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
         }

         if (entity.attackEntityFrom(damagesource, (float)i)) {
            if (entity instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
               this.arrowHit(entitylivingbase);
               if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
               }
            }

            this.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         }

         this.motionX *= -0.10000000149011612D;
         this.motionY *= -0.10000000149011612D;
         this.motionZ *= -0.10000000149011612D;
         this.rotationYaw += 180.0F;
         this.prevRotationYaw += 180.0F;
      } else {
         BlockPos blockpos = raytraceResultIn.getBlockPos();
         this.tilePos = blockpos;
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         this.inTile = iblockstate.getBlock();
         this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
         this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
         this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
         float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
         this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
         this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
         this.inGround = true;
         if (iblockstate.getMaterial() != Material.AIR) {
            this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
            if (this.ticksInGround < 120 && this.world.isAirBlock(this.tilePos.offset(raytraceResultIn.sideHit))) {
               this.world.setBlockState(this.tilePos.offset(raytraceResultIn.sideHit), Blocks.FIRE.getDefaultState(), 11);
            }
         }
      }

   }

   protected ItemStack getArrowStack() {
      return ItemStack.EMPTY;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
