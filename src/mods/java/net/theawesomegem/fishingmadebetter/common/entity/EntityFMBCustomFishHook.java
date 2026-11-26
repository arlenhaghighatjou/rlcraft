package net.theawesomegem.fishingmadebetter.common.entity;

import java.util.List;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;

public class EntityFMBCustomFishHook extends EntityFishHook {
   private int ticksInGround;
   private int ticksInAir;
   private float fishApproachingAngle;
   private int lureSpeed;
   private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.createKey(EntityFishHook.class, DataSerializers.VARINT);

   @SideOnly(Side.CLIENT)
   public EntityFMBCustomFishHook(World worldIn, EntityPlayer p_i47290_2_, double x, double y, double z) {
      super(worldIn, p_i47290_2_, x, y, z);
   }

   public EntityFMBCustomFishHook(World worldIn, EntityPlayer fishingPlayer) {
      super(worldIn, fishingPlayer);
   }

   public EntityFMBCustomFishHook(World worldIn) {
      super(worldIn, worldIn.getPlayerEntityByUUID(Minecraft.getMinecraft().getSession().getProfile().getId()));
   }

   public float getLiquidHeight(World worldIn, BlockPos blockpos) {
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      return iblockstate.getMaterial() == MaterialLiquid.WATER ? BlockLiquid.getBlockLiquidHeight(iblockstate, worldIn, blockpos) : 0.0F;
   }

   public double getLiquidMotion() {
      return 0.2D;
   }

   public SoundEvent getSoundEvent() {
      return SoundEvents.ENTITY_BOBBER_SPLASH;
   }

   public EnumParticleTypes getBubbleParticle() {
      return EnumParticleTypes.WATER_BUBBLE;
   }

   public EnumParticleTypes getWakeParticle() {
      return EnumParticleTypes.WATER_WAKE;
   }

   public EnumParticleTypes getSplashParticle() {
      return EnumParticleTypes.WATER_SPLASH;
   }

   public void setLureSpeed(int p_191516_1_) {
      this.lureSpeed = p_191516_1_;
   }

   protected void entityInit() {
      this.getDataManager().register(DATA_HOOKED_ENTITY, Integer.valueOf(0));
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (DATA_HOOKED_ENTITY.equals(key)) {
         int i = ((Integer)this.getDataManager().get(DATA_HOOKED_ENTITY)).intValue();
         this.caughtEntity = i > 0 ? this.world.getEntityByID(i - 1) : null;
      }

      super.notifyDataManagerChange(key);
   }

   public void onUpdate() {
      if (!this.world.isRemote) {
         this.setFlag(6, this.isGlowing());
      }

      this.onEntityUpdate();
      if (this.getAngler() == null) {
         this.setDead();
      } else if (this.world.isRemote || !this.shouldStopFishing()) {
         if (this.getInGround(this).booleanValue()) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 1200) {
               this.setDead();
               return;
            }
         }

         BlockPos blockpos = new BlockPos(this);
         float f = this.getLiquidHeight(this.world, blockpos);
         if (this.getHookState(this) == EntityFMBCustomFishHook.State.FLYING) {
            if (this.caughtEntity != null) {
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
               this.setHookState(this, EntityFishHook.State.HOOKED_IN_ENTITY);
               return;
            }

            if (f > 0.0F) {
               this.motionX *= 0.3D;
               this.motionY *= 0.2D;
               this.motionZ *= 0.3D;
               this.setHookState(this, EntityFishHook.State.BOBBING);
               return;
            }

            if (!this.world.isRemote) {
               this.checkHookCollision();
            }

            if (!this.getInGround(this).booleanValue() && !this.onGround && !this.collidedHorizontally) {
               ++this.ticksInAir;
            } else {
               this.ticksInAir = 0;
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
            }
         } else {
            if (this.getHookState(this) == EntityFMBCustomFishHook.State.HOOKED_IN_ENTITY) {
               if (this.caughtEntity != null) {
                  if (this.caughtEntity.isDead) {
                     this.caughtEntity = null;
                     this.setHookState(this, EntityFishHook.State.FLYING);
                  } else {
                     this.posX = this.caughtEntity.posX;
                     double d2 = (double)this.caughtEntity.height;
                     this.posY = this.caughtEntity.getEntityBoundingBox().minY + d2 * 0.8D;
                     this.posZ = this.caughtEntity.posZ;
                     this.setPosition(this.posX, this.posY, this.posZ);
                  }
               }

               return;
            }

            if (this.getHookState(this) == EntityFMBCustomFishHook.State.BOBBING) {
               this.motionX *= 0.9D;
               this.motionZ *= 0.9D;
               double d0 = this.posY + this.motionY - (double)blockpos.getY() - (double)f;
               if (Math.abs(d0) < 0.1D) {
                  d0 += Math.signum(d0) * 0.1D;
               }

               this.motionY -= d0 * (double)this.rand.nextFloat() * 0.2D;
               if (!this.world.isRemote && f > 0.0F) {
                  this.catchingFish(blockpos);
               }
            }
         }

         if (this.getLiquidHeight(this.world, blockpos) == 0.0F) {
            this.motionY -= 0.03D;
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.updateHookRotation();
         this.motionX *= 0.92D;
         this.motionY *= 0.92D;
         this.motionZ *= 0.92D;
         this.setPosition(this.posX, this.posY, this.posZ);
      }

   }

   private void catchingFish(BlockPos pos) {
      WorldServer worldserver = (WorldServer)this.world;
      int i = 1;
      BlockPos blockpos = pos.up();
      if (this.rand.nextFloat() < 0.25F && this.world.isRainingAt(blockpos)) {
         ++i;
      }

      if (this.rand.nextFloat() < 0.5F && !this.world.canSeeSky(blockpos)) {
         --i;
      }

      if (this.getTicksCatchable(this) > 0) {
         this.setTicksCatchable(this, this.getTicksCatchable(this) - 1);
         if (this.getTicksCatchable(this) <= 0) {
            this.setTicksCaughtDelay(this, 0);
            this.setTicksCatchableDelay(this, 0);
         } else {
            this.motionY -= this.getLiquidMotion() * (double)this.rand.nextFloat() * (double)this.rand.nextFloat();
         }
      } else if (this.getTicksCatchableDelay(this) > 0) {
         this.setTicksCatchableDelay(this, this.getTicksCatchableDelay(this) - i);
         if (this.getTicksCatchableDelay(this) > 0) {
            this.fishApproachingAngle = (float)((double)this.fishApproachingAngle + this.rand.nextGaussian() * 4.0D);
            float f = this.fishApproachingAngle * 0.017453292F;
            float f1 = MathHelper.sin(f);
            float f2 = MathHelper.cos(f);
            double d0 = this.posX + (double)(f1 * (float)this.getTicksCatchableDelay(this) * 0.1F);
            double d1 = (double)((float)MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
            double d2 = this.posZ + (double)(f2 * (float)this.getTicksCatchableDelay(this) * 0.1F);
            BlockPos newPos = new BlockPos(d0, d1 - 1.0D, d2);
            if (this.getLiquidHeight(worldserver, newPos) != 0.0F) {
               if (this.rand.nextFloat() < 0.15F) {
                  worldserver.spawnParticle(this.getBubbleParticle(), d0, d1 - 0.10000000149011612D, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D, new int[0]);
               }

               float f3 = f1 * 0.04F;
               float f4 = f2 * 0.04F;
               worldserver.spawnParticle(this.getWakeParticle(), d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D, new int[0]);
               worldserver.spawnParticle(this.getWakeParticle(), d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D, new int[0]);
            }
         } else {
            this.motionY = (double)(-0.4F * MathHelper.nextFloat(this.rand, 0.6F, 1.0F));
            this.playSound(this.getSoundEvent(), 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            double d3 = this.getEntityBoundingBox().minY + 0.5D;
            worldserver.spawnParticle(this.getBubbleParticle(), this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D, new int[0]);
            worldserver.spawnParticle(this.getWakeParticle(), this.posX, d3, this.posZ, (int)(1.0F + this.width * 20.0F), (double)this.width, 0.0D, (double)this.width, 0.20000000298023224D, new int[0]);
            this.setTicksCatchable(this, MathHelper.getInt(this.rand, 20, 40));
         }
      } else if (this.getTicksCaughtDelay(this) > 0) {
         this.setTicksCaughtDelay(this, this.getTicksCaughtDelay(this) - i);
         float f5 = 0.15F;
         if (this.getTicksCaughtDelay(this) < 20) {
            f5 = (float)((double)f5 + (double)(20 - this.getTicksCaughtDelay(this)) * 0.05D);
         } else if (this.getTicksCaughtDelay(this) < 40) {
            f5 = (float)((double)f5 + (double)(40 - this.getTicksCaughtDelay(this)) * 0.02D);
         } else if (this.getTicksCaughtDelay(this) < 60) {
            f5 = (float)((double)f5 + (double)(60 - this.getTicksCaughtDelay(this)) * 0.01D);
         }

         if (this.rand.nextFloat() < f5) {
            float f6 = MathHelper.nextFloat(this.rand, 0.0F, 360.0F) * 0.017453292F;
            float f7 = MathHelper.nextFloat(this.rand, 25.0F, 60.0F);
            double d4 = this.posX + (double)(MathHelper.sin(f6) * f7 * 0.1F);
            double d5 = (double)((float)MathHelper.floor(this.getEntityBoundingBox().minY) + 1.0F);
            double d6 = this.posZ + (double)(MathHelper.cos(f6) * f7 * 0.1F);
            BlockPos newPos = new BlockPos((int)d4, (int)d5 - 1, (int)d6);
            if (this.getLiquidHeight(worldserver, newPos) != 0.0F) {
               worldserver.spawnParticle(this.getSplashParticle(), d4, d5, d6, 2 + this.rand.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
            }
         }

         if (this.getTicksCaughtDelay(this) <= 0) {
            this.fishApproachingAngle = MathHelper.nextFloat(this.rand, 0.0F, 360.0F);
            this.setTicksCatchableDelay(this, MathHelper.getInt(this.rand, 20, 80));
         }
      } else {
         this.setTicksCaughtDelay(this, MathHelper.getInt(this.rand, 100, 600));
         this.setTicksCaughtDelay(this, this.getTicksCaughtDelay(this) - this.lureSpeed * 20 * 5);
      }

   }

   private boolean shouldStopFishing() {
      ItemStack itemstack = this.getAngler().getHeldItemMainhand();
      ItemStack itemstack1 = this.getAngler().getHeldItemOffhand();
      boolean flag = itemstack.getItem() instanceof ItemBetterFishingRod;
      boolean flag1 = itemstack1.getItem() instanceof ItemBetterFishingRod;
      if (!this.getAngler().isDead && this.getAngler().isEntityAlive() && flag != flag1 && this.getDistanceSq(this.getAngler()) <= 1024.0D) {
         return false;
      } else {
         this.setDead();
         return true;
      }
   }

   private void updateHookRotation() {
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
   }

   private void checkHookCollision() {
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
         if (this.canBeHooked(entity1) && (entity1 != this.getAngler() || this.ticksInAir >= 5)) {
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

      if (raytraceresult != null && raytraceresult.typeOfHit != Type.MISS) {
         if (raytraceresult.typeOfHit == Type.ENTITY) {
            this.caughtEntity = raytraceresult.entityHit;
            this.updateHookedEntity();
         } else {
            this.setInGround(this, Boolean.valueOf(true));
         }
      }

   }

   private void updateHookedEntity() {
      this.getDataManager().set(DATA_HOOKED_ENTITY, Integer.valueOf(this.caughtEntity.getEntityId() + 1));
   }

   private EntityFMBCustomFishHook.State getHookState(EntityFishHook hook) {
      return EntityFMBCustomFishHook.State.values()[hook.currentState.ordinal()];
   }

   private void setHookState(EntityFishHook hook, EntityFishHook.State state) {
      hook.currentState = state;
   }

   private Boolean getInGround(EntityFishHook hook) {
      return hook.inGround;
   }

   private void setInGround(EntityFishHook hook, Boolean value) {
      hook.inGround = value.booleanValue();
   }

   private void setTicksCatchable(EntityFishHook hook, int ticks) {
      hook.ticksCatchable = ticks;
   }

   private int getTicksCatchable(EntityFishHook hook) {
      return hook.ticksCatchable;
   }

   private void setTicksCatchableDelay(EntityFishHook hook, int ticks) {
      hook.ticksCatchableDelay = ticks;
   }

   private int getTicksCatchableDelay(EntityFishHook hook) {
      return hook.ticksCatchableDelay;
   }

   private void setTicksCaughtDelay(EntityFishHook hook, int ticks) {
      hook.ticksCaughtDelay = ticks;
   }

   private int getTicksCaughtDelay(EntityFishHook hook) {
      return hook.ticksCaughtDelay;
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;

      private State() {
      }
   }
}
