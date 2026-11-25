package com.oblivioussp.spartanweaponry.entity.projectile;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import com.oblivioussp.spartanweaponry.util.ExplosionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityDynamite extends EntityThrowable {
   private static final Predicate<Entity> VALID_TARGETS = Predicates.and(new Predicate[]{EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
      public boolean apply(Entity input) {
         return input.canBeCollidedWith();
      }
   }});
   protected int timer;
   protected boolean stuckToSomething = false;
   protected int fuseTicks = 60;
   protected int ignoreTime = 0;

   public EntityDynamite(World worldIn) {
      super(worldIn);
      this.fuseTicks = ConfigHandler.fuseTicksDynamite;
   }

   public EntityDynamite(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.fuseTicks = ConfigHandler.fuseTicksDynamite;
   }

   public EntityDynamite(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
      this.fuseTicks = ConfigHandler.fuseTicksDynamite;
   }

   public void onUpdate() {
      this.lastTickPosX = this.posX;
      this.lastTickPosY = this.posY;
      this.lastTickPosZ = this.posZ;
      this.onEntityUpdate();
      Vec3d currentPos = new Vec3d(this.posX, this.posY, this.posZ);
      Vec3d nextPos = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
      RayTraceResult rayTrace = this.world.rayTraceBlocks(currentPos, nextPos, false, true, false);
      if (rayTrace != null) {
         nextPos = new Vec3d(rayTrace.hitVec.x, rayTrace.hitVec.y, rayTrace.hitVec.z);
      }

      Entity hitEntity = null;
      double distanceToEntity = 0.0D;
      boolean ignoreChosenEntity = false;

      for(Entity entityToCheck : this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), VALID_TARGETS)) {
         if (entityToCheck == this.ignoreEntity) {
            ignoreChosenEntity = true;
         } else if (this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
            this.ignoreEntity = entityToCheck;
            ignoreChosenEntity = true;
         } else {
            ignoreChosenEntity = false;
            AxisAlignedBB aabb = entityToCheck.getEntityBoundingBox().grow(0.3D);
            RayTraceResult interceptResult = aabb.calculateIntercept(currentPos, nextPos);
            if (interceptResult != null) {
               double sqDist = currentPos.squareDistanceTo(interceptResult.hitVec);
               if (sqDist < distanceToEntity || distanceToEntity == 0.0D) {
                  hitEntity = entityToCheck;
                  distanceToEntity = sqDist;
               }
            }
         }
      }

      if (this.ignoreEntity != null) {
         if (ignoreChosenEntity) {
            this.ignoreTime = 2;
         } else if (this.ignoreTime-- <= 0) {
            this.ignoreEntity = null;
         }
      }

      if (hitEntity != null) {
         rayTrace = new RayTraceResult(hitEntity);
      }

      if (rayTrace != null) {
         if (rayTrace.typeOfHit == Type.BLOCK && this.world.getBlockState(rayTrace.getBlockPos()).getBlock() == Blocks.PORTAL) {
            this.setPortal(rayTrace.getBlockPos());
         } else if (!ForgeEventFactory.onProjectileImpact(this, rayTrace)) {
            this.onImpact(rayTrace);
         }
      }

      this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
      if (this.stuckToSomething) {
         this.motionX = 0.0D;
         this.motionY = 0.0D;
         this.motionZ = 0.0D;
      } else if (this.onGround) {
         this.motionX *= 0.7D;
         this.motionZ *= 0.7D;
         this.motionY *= -0.5D;
      }

      if (!this.hasNoGravity() && !this.stuckToSomething) {
         this.motionY -= 0.05D;
      }

      ++this.timer;
      if (this.timer >= this.fuseTicks) {
         ExplosionHelper.explode(this, this.thrower, this.getPosition());
      } else {
         double friction = 0.99D;
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }

            friction = 0.6D;
         }

         this.motionX *= friction;
         this.motionY *= friction;
         this.motionZ *= friction;
         this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.25D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.doBlockCollisions();
   }

   protected void onImpact(RayTraceResult result) {
   }
}
