package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowoftheSearcher extends EntityArrowBase implements IThrowableEntity {
   private boolean hastarget = false;
   private Entity lasttarget = null;
   private double damagesetter = ArrowConfig.DamageArrowoftheSearcher.getDoub();

   public EntityArrowoftheSearcher(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowoftheSearcher(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowoftheSearcher(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowoftheSearcher);
   }

   protected void arrowHit(EntityLivingBase living) {
   }

   protected void arrowHitGround(BlockPos blockpos) {
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
            this.ticksInAir = 0;
            this.hastarget = false;
         }
      }

   }

   public void onUpdate() {
      super.onEntityUpdate();
      if (this.lasttarget != null && this.lasttarget.isDead) {
         this.hastarget = false;
      }

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

   public void detectinAir() {
      if (this.posY < 0.0D) {
         this.setDead();
      }

      if (!this.hastarget) {
         double Umkreis = 3.0D;
         List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX - Umkreis, this.posY - Umkreis, this.posZ - Umkreis, this.posX + Umkreis, this.posY + Umkreis, this.posZ + Umkreis));
         Mobliste.addAll(this.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(this.posX - Umkreis, this.posY - Umkreis, this.posZ - Umkreis, this.posX + Umkreis, this.posY + Umkreis, this.posZ + Umkreis)));
         if (Mobliste.size() > 0) {
            for(int a = 0; a < Mobliste.size(); ++a) {
               boolean flag = true;
               Entity target = (Entity)Mobliste.get(0);
               if (target instanceof EntityPlayer) {
                  EntityPlayer player = (EntityPlayer)target;
                  if (player.isCreative() || player.isSpectator()) {
                     flag = false;
                  }
               }

               if (target instanceof EntityEnderman) {
                  flag = false;
               }

               if (target != this.shootingEntity && target != this.lasttarget && flag && !target.isDead) {
                  double d0 = target.posX - this.posX;
                  double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - this.posY;
                  double d2 = target.posZ - this.posZ;
                  double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
                  this.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 2.0F);
                  this.hastarget = true;
                  this.lasttarget = target;
               }
            }
         }
      }

   }

   protected void particleOnFlyArrow() {
      this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX - this.motionX * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - this.motionY * 0.25D - 0.5D, this.posZ - this.motionZ * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.motionX, this.motionY, this.motionZ, new int[0]);
      this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX - this.motionX * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - this.motionY * 0.25D - 0.5D, this.posZ - this.motionZ * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.motionX, this.motionY, this.motionZ, new int[0]);
      this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX - this.motionX * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.posY - this.motionY * 0.25D - 0.5D, this.posZ - this.motionZ * 0.25D + this.rand.nextDouble() * 0.6D - 0.3D, this.motionX, this.motionY, this.motionZ, new int[0]);
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setBoolean("hastarget", this.hastarget);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.hastarget = compound.getBoolean("hastarget");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
