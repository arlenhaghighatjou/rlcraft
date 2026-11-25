package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.EntityMoveHelper.Action;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityProjectileHomming extends EntityLiving {
   private float damage;
   private EntityLivingBase owner;
   private Entity target;
   @Nullable
   private EaglercraftUUID ownerUniqueId;
   private BlockPos ownerBlockPos;
   @Nullable
   private EaglercraftUUID targetUniqueId;
   private BlockPos targetBlockPos;

   public EntityProjectileHomming(World worldIn) {
      super(worldIn);
      this.setSize(0.3125F, 0.3125F);
      this.noClip = true;
      this.moveHelper = new EntityProjectileHomming.AIMoveControl(this);
      this.setNoGravity(true);
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   @SideOnly(Side.CLIENT)
   public EntityProjectileHomming(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
      this(worldIn);
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
      this.motionX = motionXIn;
      this.motionY = motionYIn;
      this.motionZ = motionZIn;
   }

   public EntityProjectileHomming(World worldIn, EntityLivingBase ownerIn, Entity targetIn, float damage) {
      this(worldIn);
      this.owner = ownerIn;
      BlockPos blockpos = new BlockPos(ownerIn);
      double d0 = (double)blockpos.getX() + 0.5D;
      double d1 = (double)blockpos.getY() + 0.5D;
      double d2 = (double)blockpos.getZ() + 0.5D;
      this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
      this.target = targetIn;
      this.damage = damage;
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      if (this.owner != null) {
         BlockPos blockpos = new BlockPos(this.owner);
         NBTTagCompound nbttagcompound = NBTUtil.createUUIDTag(this.owner.getUniqueID());
         nbttagcompound.setInteger("X", blockpos.getX());
         nbttagcompound.setInteger("Y", blockpos.getY());
         nbttagcompound.setInteger("Z", blockpos.getZ());
         compound.setTag("Owner", nbttagcompound);
      }

      if (this.target != null) {
         BlockPos blockpos1 = new BlockPos(this.target);
         NBTTagCompound nbttagcompound1 = NBTUtil.createUUIDTag(this.target.getUniqueID());
         nbttagcompound1.setInteger("X", blockpos1.getX());
         nbttagcompound1.setInteger("Y", blockpos1.getY());
         nbttagcompound1.setInteger("Z", blockpos1.getZ());
         compound.setTag("Target", nbttagcompound1);
      }

   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("Owner", 10)) {
         NBTTagCompound nbttagcompound = compound.getCompoundTag("Owner");
         this.ownerUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound);
         this.ownerBlockPos = new BlockPos(nbttagcompound.getInteger("X"), nbttagcompound.getInteger("Y"), nbttagcompound.getInteger("Z"));
      }

      if (compound.hasKey("Target", 10)) {
         NBTTagCompound nbttagcompound1 = compound.getCompoundTag("Target");
         this.targetUniqueId = NBTUtil.getUUIDFromTag(nbttagcompound1);
         this.targetBlockPos = new BlockPos(nbttagcompound1.getInteger("X"), nbttagcompound1.getInteger("Y"), nbttagcompound1.getInteger("Z"));
      }

   }

   public void onUpdate() {
      if (!this.world.isRemote && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
         this.setDead();
      } else {
         super.onUpdate();
         if (!this.world.isRemote) {
            if (this.target == null && this.targetUniqueId != null) {
               this.setDead();
            }

            if (this.owner == null && this.ownerUniqueId != null) {
               this.setDead();
            }

            if (this.target != null && !this.target.isDead) {
               this.moveHelper.setMoveTo(this.target.posX, this.target.posY - (double)this.target.height * 1.5D, this.target.posZ, 1.5D);
            }

            AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D)).grow(2.0D);
            List<EntityLivingBase> moblist = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
            boolean flag = false;

            for(EntityLivingBase mob : moblist) {
               if (mob != null && !(mob instanceof EntityParasiteBase) && !(mob instanceof EntityProjectileHomming)) {
                  DamageSource damagesource;
                  if (this.owner == null) {
                     damagesource = DamageSource.causeThrownDamage(this, this);
                  } else {
                     damagesource = DamageSource.causeThrownDamage(this, this.owner);
                  }

                  if (!flag) {
                     flag = mob.attackEntityFrom(damagesource, this.damage);
                  }
               }
            }

            if (flag) {
               this.setDead();
            }

            if (this.ticksExisted > 200) {
               this.setDead();
            }
         }
      }

   }

   public boolean isBurning() {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 16384.0D;
   }

   protected void bulletHit(RayTraceResult result) {
      if (result.entityHit == null) {
         ((WorldServer)this.world).spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 7, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
      } else {
         if (!(result.entityHit instanceof EntityParasiteBase)) {
            DamageSource damagesource;
            if (this.owner == null) {
               damagesource = DamageSource.causeThrownDamage(this, this);
            } else {
               damagesource = DamageSource.causeThrownDamage(this, this.owner);
            }

            boolean flag = result.entityHit.attackEntityFrom(damagesource, this.damage);
            if (flag && result.entityHit instanceof EntityLivingBase) {
               ;
            }
         }

         this.setDead();
      }

   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (!this.world.isRemote) {
         this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((WorldServer)this.world).spawnParticle(EnumParticleTypes.CRIT, this.posX, this.posY, this.posZ, 15, 0.2D, 0.2D, 0.2D, 0.0D, new int[0]);
         this.setDead();
      }

      return true;
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityProjectileHomming vex) {
         super(vex);
      }

      public void onUpdateMoveHelper() {
         if (this.action == Action.MOVE_TO) {
            double d0 = this.posX - EntityProjectileHomming.this.posX;
            double d1 = this.posY - EntityProjectileHomming.this.posY;
            double d2 = this.posZ - EntityProjectileHomming.this.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            d3 = (double)MathHelper.sqrt(d3);
            if (d3 < EntityProjectileHomming.this.getEntityBoundingBox().getAverageEdgeLength()) {
               this.action = Action.WAIT;
               EntityProjectileHomming.this.motionX *= 0.5D;
               EntityProjectileHomming.this.motionY *= 0.5D;
               EntityProjectileHomming.this.motionZ *= 0.5D;
            } else {
               EntityProjectileHomming.this.motionX += d0 / d3 * 0.05D * this.speed;
               EntityProjectileHomming.this.motionY += d1 / d3 * 0.05D * this.speed;
               EntityProjectileHomming.this.motionZ += d2 / d3 * 0.05D * this.speed;
               if (EntityProjectileHomming.this.target == null) {
                  EntityProjectileHomming.this.rotationYaw = -((float)MathHelper.atan2(EntityProjectileHomming.this.motionX, EntityProjectileHomming.this.motionZ)) * 57.295776F;
               } else {
                  double d4 = EntityProjectileHomming.this.target.posX - EntityProjectileHomming.this.posX;
                  double d5 = EntityProjectileHomming.this.target.posZ - EntityProjectileHomming.this.posZ;
                  EntityProjectileHomming.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * 57.295776F;
               }
            }
         }

      }
   }
}
