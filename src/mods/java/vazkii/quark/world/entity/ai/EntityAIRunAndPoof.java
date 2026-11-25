package vazkii.quark.world.entity.ai;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.entity.EntityStoneling;

public class EntityAIRunAndPoof<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> canBeSeenSelector;
   protected EntityStoneling entity;
   private final double farSpeed;
   private final double nearSpeed;
   protected T closestLivingEntity;
   private final float avoidDistance;
   private Path path;
   private final PathNavigate navigation;
   private final Class<T> classToAvoid;
   private final Predicate<T> avoidTargetSelector;

   public EntityAIRunAndPoof(EntityStoneling entity, Class<T> classToAvoid, float avoidDistance, double farSpeed, double nearSpeed) {
      this(entity, classToAvoid, (t) -> {
         return (void)true;
      }, avoidDistance, farSpeed, nearSpeed);
   }

   public EntityAIRunAndPoof(EntityStoneling entity, Class<T> classToAvoid, Predicate<T> avoidTargetSelector, float avoidDistance, double farSpeed, double nearSpeed) {
      this.canBeSeenSelector = (target) -> {
         return (void)(target != null && target.isEntityAlive() && entity.getEntitySenses().canSee(target) && !entity.isOnSameTeam(target));
      };
      this.entity = entity;
      this.classToAvoid = classToAvoid;
      this.avoidTargetSelector = avoidTargetSelector;
      this.avoidDistance = avoidDistance;
      this.farSpeed = farSpeed;
      this.nearSpeed = nearSpeed;
      this.navigation = entity.getNavigator();
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      if (this.entity.isPlayerMade()) {
         return false;
      } else {
         List<T> entities = this.entity.world.getEntitiesWithinAABB(this.classToAvoid, this.entity.getEntityBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), (entity) -> {
            return EntitySelectors.CAN_AI_TARGET.apply(entity) && this.canBeSeenSelector.test(entity) && this.avoidTargetSelector.test(entity);
         });
         if (entities.isEmpty()) {
            return false;
         } else {
            this.closestLivingEntity = entities.get(0);
            Vec3d target = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, this.closestLivingEntity.getPositionVector());
            if (target != null && this.closestLivingEntity.getDistanceSq(target.x, target.y, target.z) < this.closestLivingEntity.getDistanceSq(this.entity)) {
               return false;
            } else {
               if (target != null) {
                  this.path = this.navigation.getPathToXYZ(target.x, target.y, target.z);
               }

               return target == null || this.path != null;
            }
         }
      }
   }

   public boolean shouldContinueExecuting() {
      if (this.path != null && !this.navigation.noPath()) {
         PooledMutableBlockPos pos = PooledMutableBlockPos.retain();

         for(int i = 0; i < 8; ++i) {
            int j = MathHelper.floor(this.entity.posY + (double)(((float)(i % 2) - 0.5F) * 0.1F) + (double)this.entity.getEyeHeight());
            int k = MathHelper.floor(this.entity.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * this.entity.width * 0.8F));
            int l = MathHelper.floor(this.entity.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * this.entity.width * 0.8F));
            if (pos.getX() != k || pos.getY() != j || pos.getZ() != l) {
               pos.setPos(k, j, l);
               if (this.entity.world.getBlockState(pos).getMaterial().blocksMovement()) {
                  pos.release();
                  return false;
               }
            }
         }

         pos.release();
         return true;
      } else {
         return false;
      }
   }

   public void startExecuting() {
      if (this.path != null) {
         this.navigation.setPath(this.path, this.farSpeed);
      }

      this.entity.world.playSound((EntityPlayer)null, this.entity.posX, this.entity.posY, this.entity.posZ, QuarkSounds.ENTITY_STONELING_MEEP, SoundCategory.NEUTRAL, 1.0F, 1.0F);
   }

   public void resetTask() {
      this.closestLivingEntity = null;
      World world = this.entity.world;
      if (world instanceof WorldServer) {
         WorldServer ws = (WorldServer)world;
         ws.spawnParticle(EnumParticleTypes.CLOUD, this.entity.posX, this.entity.posY, this.entity.posZ, 40, 0.5D, 0.5D, 0.5D, 0.1D, new int[0]);
         ws.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.entity.posX, this.entity.posY, this.entity.posZ, 20, 0.5D, 0.5D, 0.5D, 0.0D, new int[0]);
      }

      for(Entity passenger : this.entity.getRecursivePassengers()) {
         if (!(passenger instanceof EntityPlayer)) {
            passenger.setDead();
         }
      }

      this.entity.setDead();
   }

   public void updateTask() {
      if (this.entity.getDistanceSq(this.closestLivingEntity) < 49.0D) {
         this.entity.getNavigator().setSpeed(this.nearSpeed);
      } else {
         this.entity.getNavigator().setSpeed(this.farSpeed);
      }

   }
}
