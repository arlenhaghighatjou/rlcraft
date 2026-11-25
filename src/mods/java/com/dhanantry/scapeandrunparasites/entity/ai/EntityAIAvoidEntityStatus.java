package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;

public class EntityAIAvoidEntityStatus<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> canBeSeenSelector;
   protected EntityParasiteBase entity;
   private final double farSpeed;
   protected T closestLivingEntity;
   private final float avoidDistance;
   private Path path;
   private final PathNavigate navigation;
   private final Class<T> classToAvoid;
   private final Predicate<? super T> avoidTargetSelector;

   public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, float avoidDistanceIn, double farSpeedIn) {
      this(entityIn, classToAvoidIn, Predicates.alwaysTrue(), avoidDistanceIn, farSpeedIn);
   }

   public EntityAIAvoidEntityStatus(EntityParasiteBase entityIn, Class<T> classToAvoidIn, Predicate<? super T> avoidTargetSelectorIn, float avoidDistanceIn, double farSpeedIn) {
      this.canBeSeenSelector = new Predicate<Entity>() {
         public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_.isEntityAlive() && EntityAIAvoidEntityStatus.this.entity.getEntitySenses().canSee(p_apply_1_) && !EntityAIAvoidEntityStatus.this.entity.isOnSameTeam(p_apply_1_);
         }
      };
      this.entity = entityIn;
      this.classToAvoid = classToAvoidIn;
      this.avoidTargetSelector = avoidTargetSelectorIn;
      this.avoidDistance = avoidDistanceIn;
      this.farSpeed = farSpeedIn;
      this.navigation = entityIn.getNavigator();
      this.setMutexBits(1);
   }

   public boolean shouldExecute() {
      List<T> list = this.entity.world.getEntitiesWithinAABB(this.classToAvoid, this.entity.getEntityBoundingBox().grow((double)this.avoidDistance, 3.0D, (double)this.avoidDistance), Predicates.and(new Predicate[]{EntitySelectors.CAN_AI_TARGET, this.canBeSeenSelector, this.avoidTargetSelector}));
      if (!list.isEmpty() && !this.entity.shouldWorkTask()) {
         this.closestLivingEntity = list.get(0);
         Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, new Vec3d(this.closestLivingEntity.posX, this.closestLivingEntity.posY, this.closestLivingEntity.posZ));
         if (vec3d == null) {
            return false;
         } else if (this.closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < this.closestLivingEntity.getDistanceSq(this.entity)) {
            return false;
         } else {
            this.path = this.navigation.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
            return this.path != null;
         }
      } else {
         return false;
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.navigation.noPath();
   }

   public void startExecuting() {
      this.navigation.setPath(this.path, this.farSpeed);
   }

   public void resetTask() {
      this.closestLivingEntity = null;
   }

   public void updateTask() {
      this.entity.getNavigator().setSpeed(this.farSpeed);
   }
}
