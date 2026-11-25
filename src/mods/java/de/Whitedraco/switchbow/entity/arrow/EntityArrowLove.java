package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.PotionInit;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowLove extends EntityArrowBase implements IThrowableEntity {
   public EntityArrowLove(World worldIn) {
      super(worldIn);
   }

   public EntityArrowLove(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowLove(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
   }

   protected void onHit(RayTraceResult raytraceResultIn) {
      Entity entity = raytraceResultIn.entityHit;
      if (entity != null) {
         if (this.shootingEntity == null) {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this);
         } else {
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
         }

         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            this.arrowHit(entitylivingbase);
            if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
               ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
            }
         }

         this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
         if (!(entity instanceof EntityEnderman)) {
            this.setDead();
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

   protected void arrowHit(EntityLivingBase living) {
      int sec = 110;
      if (!this.world.isRemote) {
         if (living instanceof EntityAnimal) {
            EntityAnimal animal = (EntityAnimal)living;
            if (!animal.isChild() && animal.getGrowingAge() < 4000) {
               animal.setGrowingAge(0);
               animal.setInLove((EntityPlayer)null);
            } else {
               this.world.spawnEntity(new EntityItem(this.world, living.posX, living.posY, living.posZ, new ItemStack(ItemInit.ArrowLove)));
            }
         } else if (living instanceof EntityPlayer) {
            living.addPotionEffect(new PotionEffect(PotionInit.PotionLove, sec, 0, true, true));
         } else if (living instanceof EntityLiving && !(living instanceof EntityVillager)) {
            EntityLiving mob = (EntityLiving)living;
            living.addPotionEffect(new PotionEffect(PotionInit.PotionLove, sec, 0, true, true));
         }
      }

      this.setDead();
   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   protected void particleOnFlyArrow() {
      for(int k = 0; k < 4; ++k) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.spawnParticle(EnumParticleTypes.HEART, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2, new int[0]);
      }

   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowLove);
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
