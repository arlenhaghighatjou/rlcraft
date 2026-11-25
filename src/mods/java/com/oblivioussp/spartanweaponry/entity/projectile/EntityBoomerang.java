package com.oblivioussp.spartanweaponry.entity.projectile;

import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityBoomerang extends EntityThrownWeapon {
   protected final String NBT_RETURN_POS = "returnPos";
   protected final String NBT_X = "x";
   protected final String NBT_Y = "y";
   protected final String NBT_Z = "z";
   protected final String NBT_IS_RETURNING = "isReturning";
   protected final String NBT_MAX_DISTANCE = "maxDistance";
   protected final double MAX_VELOCITY = 2.0D;
   protected final double VELOCITY_PER_TICK = 0.1D;
   public static final double MAX_DISTANCE_LIMIT = 5.0D;
   protected final double DEFAULT_DISTANCE = 5.0D;
   protected Vec3d returnPos = null;
   protected boolean isReturning = false;
   protected double maxDistance = 5.0D;
   protected int ticksUntilSound = 0;
   protected final int TICKS_PER_SOUND = 5;

   public EntityBoomerang(World world) {
      super(world);
      this.setNoGravity(true);
   }

   public EntityBoomerang(World world, double x, double y, double z) {
      super(world, x, y, z);
      this.setNoGravity(true);
   }

   public EntityBoomerang(World world, EntityLivingBase shooter) {
      super(world, shooter);
      this.setNoGravity(true);
      this.maxDistance = 5.0D;
      this.returnPos = new Vec3d(shooter.posX, shooter.posY + (double)shooter.getEyeHeight() * 0.9D - 0.1D, shooter.posZ);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      if (this.returnPos != null) {
         NBTTagCompound returnPosTag = new NBTTagCompound();
         returnPosTag.setDouble("x", this.returnPos.x);
         returnPosTag.setDouble("y", this.returnPos.y);
         returnPosTag.setDouble("z", this.returnPos.z);
         compound.setTag("returnPos", returnPosTag);
         compound.setDouble("maxDistance", this.maxDistance);
      } else {
         compound.removeTag("returnPos");
      }

      compound.setBoolean("isReturning", this.isReturning);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("returnPos")) {
         NBTTagCompound returnPosTag = compound.getCompoundTag("returnPos");
         if (returnPosTag != null && !returnPosTag.hasNoTags()) {
            double x = returnPosTag.getDouble("x");
            double y = returnPosTag.getDouble("y");
            double z = returnPosTag.getDouble("z");
            this.returnPos = new Vec3d(x, y, z);
         } else {
            this.returnPos = null;
         }

         this.maxDistance = compound.getDouble("maxDistance");
      } else {
         this.returnPos = null;
      }

      this.isReturning = compound.getBoolean("isReturning");
   }

   public void setMaxDistance(double distance) {
      this.maxDistance = distance;
   }

   public void onUpdate() {
      super.onUpdate();
      if (!this.inGround) {
         if (this.shootingEntity != null) {
            this.returnPos = new Vec3d(this.shootingEntity.posX, this.shootingEntity.posY + (double)this.shootingEntity.getEyeHeight() * 0.9D - 0.1D, this.shootingEntity.posZ);
         }

         double distance = -1.0D;
         if (this.returnPos != null) {
            distance = this.returnPos.distanceTo(this.getPositionVector());
         }

         if (this.hasNoGravity()) {
            if (distance < 1.0D && this.isReturning || this.inWater || this.returnPos == null) {
               this.setNoGravity(false);
            }

            if (distance > this.maxDistance && !this.isReturning) {
               this.isReturning = true;
            }

            if (this.isReturning && this.returnPos != null) {
               Vec3d distanceVec = this.getPositionVector().subtract(this.returnPos);
               double length = distanceVec.lengthVector();
               if (length < 5.0D) {
                  this.motionX = -distanceVec.x / length;
                  this.motionY = -distanceVec.y / length;
                  this.motionZ = -distanceVec.z / length;
               } else {
                  this.motionX -= 0.1D * (distanceVec.x / length);
                  this.motionY -= 0.1D * (distanceVec.y / length);
                  this.motionZ -= 0.1D * (distanceVec.z / length);
               }
            }

            --this.ticksUntilSound;
            if (this.ticksUntilSound <= 0) {
               this.ticksUntilSound = 5;
               if (!this.world.isRemote) {
                  this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundRegistry.BOOMERANG_FLIGHT, SoundCategory.NEUTRAL, 2.0F, 0.5F);
               }
            }
         }

         if (!this.world.isRemote && this.ticksExisted > 200) {
            if (this.pickupStatus == PickupStatus.ALLOWED) {
               this.dropAsItem();
            }

            this.setDead();
         }

         if (this.world.isRemote && !this.inGround) {
            this.world.spawnParticle(EnumParticleTypes.CRIT_MAGIC, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         }

      }
   }

   public void arrowHit(EntityLivingBase entity) {
      this.isReturning = true;
   }

   protected void onHit(RayTraceResult rayTrace) {
      if (rayTrace.typeOfHit == Type.BLOCK && this.hasNoGravity()) {
         BlockPos blockPos = rayTrace.getBlockPos();
         IBlockState blockState = this.world.getBlockState(blockPos);
         Vec3i faceNormali = rayTrace.sideHit.getDirectionVec();
         Vec3d faceNormalVec = new Vec3d((double)faceNormali.getX(), (double)faceNormali.getY(), (double)faceNormali.getZ());
         Vec3d motionVec = new Vec3d(this.motionX, this.motionY, this.motionZ);
         faceNormalVec.normalize();
         Vec3d reflectVec = faceNormalVec.scale(2.0D * motionVec.dotProduct(faceNormalVec)).subtract(motionVec);
         Vec3d bounceVec = reflectVec.scale(-0.75D);
         this.motionX = bounceVec.x;
         this.motionY = bounceVec.y;
         this.motionZ = bounceVec.z;
         this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, 1.0F, 2.2F / this.rand.nextFloat() * 0.2F + 0.9F);
         if (!this.world.isRemote) {
            ((WorldServer)this.world).spawnParticle(EnumParticleTypes.BLOCK_DUST, this.posX, this.posY, this.posZ, 5, 0.1D, 0.1D, 0.1D, 0.05D, new int[]{Block.getIdFromBlock(this.world.getBlockState(blockPos).getBlock())});
         }

         if (blockState.getMaterial() != Material.AIR) {
            blockState.getBlock().onEntityCollidedWithBlock(this.world, blockPos, blockState, this);
         }
      } else {
         super.onHit(rayTrace);
      }

   }

   protected boolean canBeCaughtInMidair(Entity shooter, Entity entityHit) {
      return shooter.isEntityEqual(entityHit);
   }

   public boolean isInGround() {
      return this.inGround;
   }
}
