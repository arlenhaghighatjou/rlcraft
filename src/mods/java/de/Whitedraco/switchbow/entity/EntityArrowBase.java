package de.Whitedraco.switchbow.entity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityArrowBase extends EntityArrow {
   public int xTile;
   public int yTile;
   public int zTile;
   public Block inTile;
   public int inData;
   public boolean inGround;
   public int timeInGround;
   public int ticksInGround;
   public int ticksInAir;
   public double damage = 2.0D;
   public int knockbackStrength;
   public boolean underwater = false;

   public EntityArrowBase(World worldIn) {
      super(worldIn);
   }

   public EntityArrowBase(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowBase(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
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

      BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (iblockstate.getMaterial() != Material.AIR) {
         AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);
         if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
            this.inGround = true;
         }
      }

      if (this.arrowShake > 0) {
         --this.arrowShake;
      }

      if (this.inGround) {
         int j = block.getMetaFromState(iblockstate);
         this.arrowHitGround(new BlockPos(this.xTile, this.yTile, this.zTile));
         if (block == this.inTile && j == this.inData) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               this.setDead();
            }
         } else {
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         }

         ++this.timeInGround;
      } else {
         this.timeInGround = 0;
         ++this.ticksInAir;
         this.detectinAir();
         Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
         Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
         vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
         vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (raytraceresult != null) {
            vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
         }

         Entity entity = this.findEntityOnPath(vec3d1, vec3d);
         if (entity != null) {
            raytraceresult = new RayTraceResult(entity);
         }

         if (raytraceresult != null && raytraceresult.entityHit != null && raytraceresult.entityHit instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;
            if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer)) {
               raytraceresult = null;
            }
         }

         if (raytraceresult != null) {
            this.onHit(raytraceresult);
         }

         if (this.getIsCritical()) {
            for(int k = 0; k < 4; ++k) {
               this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ, new int[0]);
            }
         }

         this.particleOnFlyArrow();
         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);

         for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f4) * 57.29577951308232D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            ;
         }

         while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
         }

         while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
         }

         this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
         this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
         float f1 = 0.99F;
         float f2 = 0.05F;
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            if (!this.underwater) {
               f1 = 0.6F;
            }
         }

         if (this.isWet()) {
            this.extinguish();
         }

         this.motionX *= (double)f1;
         this.motionY *= (double)f1;
         this.motionZ *= (double)f1;
         if (!this.hasNoGravity()) {
            this.motionY -= 0.05000000074505806D;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.doBlockCollisions();
      }

   }

   public void detectinAir() {
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         int i = MathHelper.ceil((double)f * this.damage);
         if (this.getIsCritical()) {
            i += this.rand.nextInt(i / 2 + 2);
         }

         DamageSource damagesource;
         if (this.shootingEntity == null) {
            damagesource = DamageSource.causeArrowDamage(this, this);
         } else {
            damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
         }

         if (this.isBurning() && !(entity instanceof EntityEnderman)) {
            entity.setFire(5);
         }

         if (entity.attackEntityFrom(damagesource, (float)i)) {
            if (entity instanceof EntityLivingBase) {
               EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
               if (!this.world.isRemote) {
                  entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
               }

               if (this.knockbackStrength > 0) {
                  float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                  if (f1 > 0.0F) {
                     entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
                  }
               }

               if (this.shootingEntity instanceof EntityLivingBase) {
                  EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                  EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entitylivingbase);
               }

               this.arrowHit(entitylivingbase);
               if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
               }
            }

            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            if (!(entity instanceof EntityEnderman)) {
               this.setDead();
            }
         } else {
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
               if (this.pickupStatus == PickupStatus.ALLOWED) {
                  this.entityDropItem(this.getArrowStack(), 0.1F);
               }

               this.setDead();
            }
         }
      } else {
         BlockPos blockpos = raytraceResultIn.getBlockPos();
         this.xTile = blockpos.getX();
         this.yTile = blockpos.getY();
         this.zTile = blockpos.getZ();
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         this.inTile = iblockstate.getBlock();
         this.inData = this.inTile.getMetaFromState(iblockstate);
         this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
         this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
         this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
         float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
         this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
         this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
         this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.arrowShake = 7;
         this.setIsCritical(false);
         if (iblockstate.getMaterial() != Material.AIR) {
            this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
         }
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   protected void particleOnFlyArrow() {
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("Underwater", this.underwater);
      compound.setDouble("damage", this.damage);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.damage = compound.getDouble("damage");
      this.underwater = compound.getBoolean("Underwater");
   }

   protected abstract ItemStack getArrowStack();

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.world.isRemote && this.inGround && this.arrowShake <= 0) {
         boolean flag = this.pickupStatus == PickupStatus.ALLOWED || this.pickupStatus == PickupStatus.CREATIVE_ONLY && entityIn.capabilities.isCreativeMode;
         if (this.pickupStatus == PickupStatus.ALLOWED && !entityIn.inventory.addItemStackToInventory(this.getArrowStack())) {
            flag = false;
         }

         if (flag) {
            this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            entityIn.onItemPickup(this, 1);
            this.setDead();
         }
      }

   }

   public void setKnockbackStrength(int knockbackStrengthIn) {
      this.knockbackStrength = knockbackStrengthIn;
   }

   public void setDamage(double damageIn) {
      this.damage = damageIn;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setUnderwatertotrue() {
      this.underwater = true;
   }
}
