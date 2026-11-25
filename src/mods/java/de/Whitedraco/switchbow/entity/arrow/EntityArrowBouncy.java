package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.PatricleExplosionPacket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowBouncy extends EntityArrowBase implements IThrowableEntity {
   private int bound = 0;
   private double damagesetter = ArrowConfig.DamageArrowBouncy.getDoub();

   public EntityArrowBouncy(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowBouncy(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowBouncy(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowBouncy);
   }

   protected void arrowHit(EntityLivingBase living) {
   }

   protected void arrowHitGround(BlockPos blockpos) {
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
         if (this.bound < 25) {
            RayTraceResult rtr = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY, this.posZ), new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ), false, true, false);
            double reduct = -1.0D;
            if (rtr != null) {
               switch(rtr.sideHit) {
               case DOWN:
               case UP:
                  this.motionY *= reduct;
                  break;
               case NORTH:
               case EAST:
               case SOUTH:
               case WEST:
                  this.motionX *= reduct;
                  this.motionZ *= reduct;
               }
            }

            ++this.bound;
         } else {
            if (!this.world.isRemote) {
               Explosion explosion = new Explosion(this.world, this, this.posX, this.posY, this.posZ, 0.5F, false, true);
               explosion.doExplosionA();

               for(BlockPos blockpos1 : explosion.getAffectedBlockPositions()) {
                  IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
                  Block block1 = iblockstate1.getBlock();
                  double d0 = (double)((float)blockpos1.getX() + this.world.rand.nextFloat());
                  double d1 = (double)((float)blockpos1.getY() + this.world.rand.nextFloat());
                  double d2 = (double)((float)blockpos1.getZ() + this.world.rand.nextFloat());
                  double d3 = d0 - explosion.getPosition().x;
                  double d4 = d1 - explosion.getPosition().y;
                  double d5 = d2 - explosion.getPosition().z;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                  d3 = d3 / d6;
                  d4 = d4 / d6;
                  d5 = d5 / d6;
                  double d7 = 0.5D / (d6 / 3.0D + 0.1D);
                  d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                  d3 = d3 * d7;
                  d4 = d4 * d7;
                  d5 = d5 * d7;
                  Initial.network.sendToAll(new PatricleExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, d0, d1, d2, d3, d4, d5));
               }

               explosion.clearAffectedBlockPositions();
               explosion.doExplosionB(true);
            }

            this.setDead();
         }
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
         if (this.bound < 25) {
            RayTraceResult rtr = this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY, this.posZ), new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ), false, true, false);
            double reduct = -1.0D;
            if (rtr != null) {
               switch(rtr.sideHit) {
               case DOWN:
               case UP:
                  this.motionY *= reduct;
                  break;
               case NORTH:
               case EAST:
               case SOUTH:
               case WEST:
                  this.motionX *= reduct;
                  this.motionZ *= reduct;
               }
            }

            ++this.bound;
         } else {
            if (!this.world.isRemote) {
               Explosion explosion = new Explosion(this.world, this, this.posX, this.posY, this.posZ, 0.5F, false, true);
               explosion.doExplosionA();

               for(BlockPos blockpos1 : explosion.getAffectedBlockPositions()) {
                  IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
                  Block block = iblockstate1.getBlock();
                  double d0 = (double)((float)blockpos1.getX() + this.world.rand.nextFloat());
                  double d1 = (double)((float)blockpos1.getY() + this.world.rand.nextFloat());
                  double d2 = (double)((float)blockpos1.getZ() + this.world.rand.nextFloat());
                  double d3 = d0 - explosion.getPosition().x;
                  double d4 = d1 - explosion.getPosition().y;
                  double d5 = d2 - explosion.getPosition().z;
                  double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                  d3 = d3 / d6;
                  d4 = d4 / d6;
                  d5 = d5 / d6;
                  double d7 = 0.5D / (d6 / 3.0D + 0.1D);
                  d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                  d3 = d3 * d7;
                  d4 = d4 * d7;
                  d5 = d5 * d7;
                  Initial.network.sendToAll(new PatricleExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, d0, d1, d2, d3, d4, d5));
               }

               explosion.clearAffectedBlockPositions();
               explosion.doExplosionB(true);
            }

            this.setDead();
         }

         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         if (iblockstate.getMaterial() != Material.AIR) {
            this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setInteger("bound", this.bound);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.bound = compound.getInteger("bound");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
