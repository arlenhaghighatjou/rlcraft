package xzeroair.trinkets.entity;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.Serializable;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.particles.EffectsRenderPacket;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;

public class MovingThrownProjectile extends EntityFireball implements IProjectile {
   private ItemStack stack;
   private MovingThrownProjectile.HitResult hitResult;
   private int xTile;
   private int yTile;
   private int zTile;
   private Block inTile;
   private int inData;
   protected boolean inGround;
   protected int timeInGround;
   private int ticksInGround;
   private int ticksInAir;
   private double damage;
   private int knockbackStrength;
   public int throwableShake;
   public Entity ignoreEntity;
   private int ignoreTime;
   int color;
   private boolean ignoreBlocks = false;
   int frame = 0;

   public MovingThrownProjectile(World worldIn) {
      super(worldIn);
      this.color = 12582912;
   }

   public MovingThrownProjectile(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ, int color) {
      super(worldIn, shooter, accelX, accelY, accelZ);
      this.color = color;
      float f = -MathHelper.sin(shooter.rotationYaw * 0.017453292F) * MathHelper.cos(shooter.rotationPitch * 0.017453292F);
      float f1 = -MathHelper.sin((shooter.rotationPitch + 0.0F) * 0.017453292F);
      float f2 = MathHelper.cos(shooter.rotationYaw * 0.017453292F) * MathHelper.cos(shooter.rotationPitch * 0.017453292F);
      this.damage = (double)TrinketsConfig.SERVER.races.dragon.breath_damage;
      this.setSize(0.5F, 0.5F);
      double d0 = (double)MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
      this.accelerationX = accelX / d0 * 0.1D * (double)(this.isFlying(shooter) ? 4 : 1);
      this.accelerationY = accelY / d0 * 0.1D * (double)(this.isFlying(shooter) ? 4 : 1);
      this.accelerationZ = accelZ / d0 * 0.1D * (double)(this.isFlying(shooter) ? 4 : 1);
   }

   private boolean isFlying(EntityLivingBase entity) {
      return entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isFlying;
   }

   public void shoot(Entity entityThrower, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity, float inaccuracy) {
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

   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt(x * x + y * y + z * z);
      x = x / (double)f;
      y = y / (double)f;
      z = z / (double)f;
      x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      float f1 = MathHelper.sqrt(x * x + z * z);
      this.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
      this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * 57.29577951308232D);
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.ticksInGround = 0;
   }

   @SideOnly(Side.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * 57.29577951308232D);
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   public MovingThrownProjectile setIgnoreBlocks(boolean ignoreBlocks) {
      this.ignoreBlocks = ignoreBlocks;
      return this;
   }

   public MovingThrownProjectile setColor(int color) {
      this.color = color;
      return this;
   }

   public void setSizes(float width, float height) {
      this.setSize(width, height);
   }

   protected boolean isFireballFiery() {
      return false;
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.onEntityUpdate();
      if (this.throwableShake > 0) {
         --this.throwableShake;
      }

      if (this.inGround) {
         if (this.world.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
            ++this.ticksInGround;
            if (this.ticksInGround == 1200) {
               this.setDead();
            }

            return;
         }

         this.inGround = false;
         this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
         this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
         this.ticksInGround = 0;
         this.ticksInAir = 0;
      } else {
         ++this.ticksInAir;
      }

      Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1);
      vec3d = new Vec3d(this.posX, this.posY, this.posZ);
      vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      if (raytraceresult != null) {
         vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
      }

      Entity entity = null;
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
      double d0 = 0.0D;
      boolean flag = false;

      for(Entity entity1 : list) {
         if (entity1.canBeCollidedWith()) {
            if (entity1 == this.ignoreEntity) {
               flag = true;
            } else if (this.shootingEntity != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
               this.ignoreEntity = entity1;
               flag = true;
            } else {
               flag = false;
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
      }

      if (this.ignoreEntity != null) {
         if (flag) {
            this.ignoreTime = 2;
         } else if (this.ignoreTime-- <= 0) {
            this.ignoreEntity = null;
         }
      }

      if (entity != null) {
         raytraceresult = new RayTraceResult(entity);
      }

      if (raytraceresult != null) {
         if (raytraceresult.typeOfHit == Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
            this.setPortal(raytraceresult.getBlockPos());
         } else if (!ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }
      }

      this.posX += this.motionX;
      this.posY += this.motionY;
      this.posZ += this.motionZ;
      float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 57.29577951308232D);

      for(this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * 57.29577951308232D); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
      float f2 = 0.03F;
      if (this.isInWater()) {
         for(int j = 0; j < 4; ++j) {
            float f3 = 0.25F;
            this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
         }

         f1 = 0.8F;
      }

      this.motionX *= (double)f1;
      this.motionY *= (double)f1;
      this.motionZ *= (double)f1;
      if (!this.hasNoGravity()) {
         this.motionY -= 0.029999999329447746D;
      }

      int life = 30;
      this.setPosition(this.posX, this.posY, this.posZ);
      this.spawnParticle();
      if (this.ticksExisted >= 30) {
         this.setDead();
      }

      if (this.isInWater()) {
         this.setDead();
      }

   }

   public void spawnParticle() {
      try {
         EaglercraftRandom random = Reference.random;
         double d0 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(random.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(random.nextFloat() * 2.0F - 1.0F);
         if (d0 * d0 + d1 * d1 + d2 * d2 <= 1.0D) {
            double d3 = this.posX + d0 * 1.0D / 4.0D;
            double d4 = this.posY + 0.5D + d1 * 1.0D / 4.0D;
            double d5 = this.posZ + d2 * 1.0D / 4.0D;
            if (this.world instanceof WorldServer) {
               NetworkHandler.sendToClients((WorldServer)this.world, this.getPosition(), new EffectsRenderPacket(this, d3, d4, d5, d0, d1 + 0.2D, d2, this.color, 4, 1.0F, 1.0F));
            }
         }
      } catch (Exception var14) {
         var14.printStackTrace();
      }

   }

   protected void onImpact(RayTraceResult movingObject) {
      boolean flag = this.world.getGameRules().getBoolean("mobGriefing");
      Entity hit = movingObject.entityHit;
      if (!this.world.isRemote) {
         if (hit != null && hit instanceof MovingThrownProjectile) {
            return;
         }

         if (this.shootingEntity != null && hit != null) {
            boolean pvpEnabled = false;

            try {
               if (this.shootingEntity instanceof EntityPlayerMP) {
                  pvpEnabled = ((EntityPlayerMP)this.shootingEntity).getServer().isPVPEnabled();
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }

            AxisAlignedBB bb1 = this.getEntityBoundingBox().grow(1.0D);
            Predicate<Entity> Targets = Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
               return ent != null && ent.canBeCollidedWith() && ent != this.shootingEntity && !(ent instanceof MovingThrownProjectile) && !ent.isImmuneToFire();
            });

            for(Entity e : this.world.getEntitiesInAABBexcluding(this, bb1, Targets)) {
               if ((!(e instanceof EntityPlayer) || pvpEnabled) && !e.isImmuneToFire()) {
                  e.attackEntityFrom((new EntityDamageSourceIndirect(DamageSource.DRAGON_BREATH.damageType, this, this.shootingEntity)).setDamageBypassesArmor().setFireDamage().setMagicDamage(), (float)this.damage);
                  e.setFire(5);
               }
            }

            this.setDead();
         }

         if (movingObject.entityHit != null && !(movingObject.entityHit instanceof MovingThrownProjectile) && !movingObject.entityHit.isEntityEqual(this.shootingEntity)) {
            this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
            this.setDead();
         }

         if (movingObject.typeOfHit != Type.ENTITY || movingObject.entityHit != null && !(movingObject.entityHit instanceof MovingThrownProjectile)) {
            if (movingObject.typeOfHit == Type.BLOCK && flag) {
               BlockPos blockpos = movingObject.getBlockPos().offset(movingObject.sideHit);
               if (this.world.isAirBlock(blockpos)) {
                  this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
               }
            }

            this.setDead();
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   public void setAim(Entity fireball, Entity entity, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
      float f = -MathHelper.sin(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
      float f1 = -MathHelper.sin(p_184547_2_ * 0.017453292F);
      float f2 = MathHelper.cos(p_184547_3_ * 0.017453292F) * MathHelper.cos(p_184547_2_ * 0.017453292F);
      fireball.motionX = entity.motionX;
      fireball.motionZ = entity.motionZ;
      if (!entity.onGround) {
         fireball.motionY = entity.motionY;
      }

      this.setThrowableHeading(fireball, (double)f, (double)f1, (double)f2, p_184547_5_, p_184547_6_);
   }

   public void setThrowableHeading(Entity fireball, double x, double y, double z, float velocity, float inaccuracy) {
      x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      fireball.motionX = x;
      fireball.motionY = y;
      fireball.motionZ = z;
      float f1 = MathHelper.sqrt(x * x + z * z);
      fireball.rotationYaw = (float)(MathHelper.atan2(x, z) * 57.29577951308232D);
      fireball.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * 57.29577951308232D);
      fireball.prevRotationYaw = fireball.rotationYaw;
      fireball.prevRotationPitch = fireball.rotationPitch;
   }

   public float getCollisionBorderSize() {
      return 1.0F;
   }

   public boolean isInRangeToRender3d(double x, double y, double z) {
      return true;
   }

   public boolean shouldRenderInPass(int pass) {
      return true;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("BreathColor")) {
         this.color = compound.getInteger("BreathColor");
      }

      if (compound.hasKey("frame")) {
         this.frame = compound.getInteger("frame");
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("BreathColor", this.color);
      compound.setInteger("frame", this.frame);
      return super.writeToNBT(compound);
   }

   public interface HitResult extends Serializable {
      void onHit(MovingThrownProjectile var1, RayTraceResult var2, boolean var3);

      public static class EmptyResult implements MovingThrownProjectile.HitResult, Serializable {
         public EmptyResult() {
         }

         public void onHit(MovingThrownProjectile entity, RayTraceResult result, boolean isServer) {
         }
      }
   }
}
