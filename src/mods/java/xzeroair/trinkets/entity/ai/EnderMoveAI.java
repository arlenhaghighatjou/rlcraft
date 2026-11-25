package xzeroair.trinkets.entity.ai;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIFollow;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.util.TrinketsConfig;

public class EnderMoveAI extends EntityAIFollow {
   private final EntityLiving enderman;
   private final Predicate<EntityLiving> followPredicate;
   private EntityPlayer followingEntity;
   private final double speedModifier;
   private final PathNavigate navigation;
   private int timeToRecalcPath;
   private final float stopDistance;
   private float oldWaterCost;
   private final float areaSize;

   public EnderMoveAI(EntityEnderman entity) {
      super(entity, 1.0D, 6.0F, 16.0F);
      this.enderman = entity;
      this.followPredicate = (following) -> {
         return following != null && this.enderman.getClass() != following.getClass();
      };
      this.speedModifier = 1.0D;
      this.navigation = this.enderman.getNavigator();
      this.stopDistance = 6.0F;
      this.areaSize = 16.0F;
      this.setMutexBits(3);
      if (!(this.enderman.getNavigator() instanceof PathNavigateGround) && !(this.enderman.getNavigator() instanceof PathNavigateFlying)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean shouldExecute() {
      if (TrinketsConfig.SERVER.Items.ENDER_CROWN.Follow) {
         AxisAlignedBB bBox = this.enderman.getEntityBoundingBox();
         List<EntityLivingBase> list = this.enderman.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, bBox.grow(16.0D, 4.0D, 16.0D));
         if (!list.isEmpty()) {
            for(EntityLivingBase entityliving : list) {
               if (entityliving instanceof EntityPlayer) {
                  boolean tiara = TrinketHelper.AccessoryCheck(entityliving, ModItems.trinkets.TrinketEnderTiara);
                  boolean ability = TrinketHelper.entityHasAbility("ender_queen".toString(), entityliving);
                  if (!entityliving.isInvisible() && (tiara || ability)) {
                     this.followingEntity = (EntityPlayer)entityliving;
                     int count = 0;
                     if (this.followingEntity != null) {
                        for(EntityEnderman ally : this.enderman.getEntityWorld().getEntitiesWithinAABB(EntityEnderman.class, entityliving.getEntityBoundingBox().grow(8.0D, 4.0D, 8.0D))) {
                           ++count;
                        }
                     }

                     if (count < 4) {
                        return true;
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean shouldContinueExecuting() {
      int count = 0;
      if (this.followingEntity != null) {
         for(EntityEnderman ally : this.enderman.getEntityWorld().getEntitiesWithinAABB(EntityEnderman.class, this.followingEntity.getEntityBoundingBox().grow(6.0D, 4.0D, 6.0D))) {
            ++count;
         }
      }

      return this.followingEntity != null && count < 4 && !this.navigation.noPath() && this.enderman.getDistanceSq(this.followingEntity) > (double)(this.stopDistance * this.stopDistance);
   }

   public void startExecuting() {
      this.timeToRecalcPath = 0;
      this.oldWaterCost = this.enderman.getPathPriority(PathNodeType.WATER);
      this.enderman.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   public void resetTask() {
      this.followingEntity = null;
      this.navigation.clearPath();
      this.enderman.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
   }

   public void updateTask() {
      if (this.followingEntity != null && !this.enderman.getLeashed()) {
         this.enderman.getLookHelper().setLookPositionWithEntity(this.followingEntity, 10.0F, (float)this.enderman.getVerticalFaceSpeed());
         if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            double d0 = this.enderman.posX - this.followingEntity.posX;
            double d1 = this.enderman.posY - this.followingEntity.posY;
            double d2 = this.enderman.posZ - this.followingEntity.posZ;
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;
            if (d3 > (double)(this.stopDistance * this.stopDistance)) {
               this.navigation.tryMoveToEntityLiving(this.followingEntity, this.speedModifier);
            } else {
               this.navigation.clearPath();
               if (d3 <= (double)this.stopDistance) {
                  double d4 = this.followingEntity.posX - this.enderman.posX;
                  double d5 = this.followingEntity.posZ - this.enderman.posZ;
                  this.navigation.tryMoveToXYZ(this.enderman.posX - d4, this.enderman.posY, this.enderman.posZ - d5, this.speedModifier);
               }
            }
         }
      }

   }
}
