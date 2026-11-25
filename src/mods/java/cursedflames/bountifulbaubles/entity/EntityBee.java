package cursedflames.bountifulbaubles.entity;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityBee extends EntityTerrariaThrowable {
   public EntityBee(World w) {
      super(w);
   }

   public EntityBee(World w, int dir) {
      super(w);
   }

   public EntityBee(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn, 1.0F);
   }

   public EntityBee(World world, EntityLivingBase thrower, double x, double y, double z) {
      super(world, x, y, z);
      this.thrower = thrower;
   }

   public EntityBee(World world, double x, double y, double z) {
      super(world, x, y, z);
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public boolean hasNoGravity() {
      return true;
   }

   @ParametersAreNonnullByDefault
   protected void onImpact(RayTraceResult result) {
      if (result.typeOfHit == Type.BLOCK) {
         if (!this.world.getBlockState(result.getBlockPos()).getMaterial().blocksMovement()) {
            return;
         }

         if (result.sideHit != EnumFacing.DOWN && result.sideHit != EnumFacing.UP) {
            if (result.sideHit != EnumFacing.EAST && result.sideHit != EnumFacing.WEST) {
               if (result.sideHit == EnumFacing.NORTH || result.sideHit == EnumFacing.SOUTH) {
                  this.motionZ = -this.motionZ;
               }
            } else {
               this.motionX = -this.motionX;
            }
         } else {
            this.motionY = -this.motionY;
         }
      } else if (result.typeOfHit == Type.ENTITY) {
         result.entityHit.attackEntityFrom(DamageSource.MAGIC, 0.5F);
      }

   }

   protected void onDeath() {
   }

   public void beeShoot(int dir, double speed) {
      this.motionX = -1.0D;
      this.motionY = -1.0D;
      this.motionZ = -1.0D;
      if (dir > 4) {
         this.motionX = 1.0D;
      }

      if (dir % 4 > 2) {
         this.motionY = 1.0D;
      }

      if (dir % 2 == 1) {
         this.motionZ = 1.0D;
      }

      this.motionX *= speed;
      this.motionY *= speed;
      this.motionZ *= speed;
   }
}
