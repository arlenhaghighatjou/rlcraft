package meldexun.reachfix.hook.client;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import meldexun.reachfix.config.ReachFixConfig;
import meldexun.reachfix.util.BoundingBoxUtil;
import meldexun.reachfix.util.ReachFixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public class EntityRendererHook {
   public EntityRendererHook() {
   }

   public static void getMouseOver(float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      Entity viewEntity = mc.getRenderViewEntity();
      if (viewEntity != null) {
         if (mc.player != null) {
            if (mc.world != null) {
               mc.mcProfiler.startSection("pick");
               mc.objectMouseOver = pointedObject(viewEntity, mc.player, EnumHand.MAIN_HAND, mc.world, partialTicks);
               mc.entityRenderer.pointedEntity = mc.objectMouseOver.entityHit;
               mc.pointedEntity = mc.objectMouseOver.entityHit;
               mc.mcProfiler.endSection();
            }
         }
      }
   }

   public static RayTraceResult pointedObject(Entity viewEntity, EntityPlayer player, EnumHand hand, World world, float partialTicks) {
      Vec3d start = viewEntity.getPositionEyes(partialTicks);
      Vec3d look = viewEntity.getLook(partialTicks);
      double blockReach = ReachFixUtil.getBlockReach(player, hand);
      double entityReach = ReachFixUtil.getEntityReach(player, hand);
      Vec3d end = start.add(look.scale(Math.max(blockReach, entityReach)));
      RayTraceResult pointedBlock = world.rayTraceBlocks(start, end, false, false, false);
      RayTraceResult pointedEntity = getPointedEntity(viewEntity, world, start, end, partialTicks);
      if (!isNullOrMiss(pointedBlock)) {
         if (!isNullOrMiss(pointedEntity)) {
            double distBlock = start.squareDistanceTo(pointedBlock.hitVec);
            double distEntity = start.squareDistanceTo(pointedEntity.hitVec);
            if (distBlock < distEntity) {
               if (distBlock < blockReach * blockReach) {
                  return pointedBlock;
               }
            } else if (distEntity < entityReach * entityReach) {
               return pointedEntity;
            }
         } else if (start.squareDistanceTo(pointedBlock.hitVec) < blockReach * blockReach) {
            return pointedBlock;
         }
      } else if (!isNullOrMiss(pointedEntity) && start.squareDistanceTo(pointedEntity.hitVec) < entityReach * entityReach) {
         return pointedEntity;
      }

      return new RayTraceResult(Type.MISS, end, (EnumFacing)null, new BlockPos(end));
   }

   @Nullable
   private static RayTraceResult getPointedEntity(Entity viewEntity, World world, Vec3d start, Vec3d end, float partialTicks) {
      AxisAlignedBB aabb = (new AxisAlignedBB(start, end)).grow(1.0D);
      Entity lowestRidingEntity = viewEntity.getLowestRidingEntity();
      List<Entity> possibleEntities = world.getEntitiesInAABBexcluding(viewEntity, aabb, (entityx) -> {
         return !EntitySelectors.NOT_SPECTATING.apply(entity) ? false : entity.canBeCollidedWith();
      });
      RayTraceResult result = null;
      Entity pointedEntity = null;
      double min = Double.MAX_VALUE;
      Iterator var12 = possibleEntities.iterator();

      Entity entity;
      while(true) {
         if (!var12.hasNext()) {
            if (isNullOrMiss(result)) {
               return null;
            }

            return new RayTraceResult(pointedEntity, result.hitVec);
         }

         entity = (Entity)var12.next();
         AxisAlignedBB entityAabb = BoundingBoxUtil.getInteractionBoundingBox(entity, partialTicks);
         if (lowestRidingEntity == entity.getLowestRidingEntity() && !entity.canRiderInteract()) {
            if (ReachFixConfig.getInstance().forceInteractionInsideVehicles && entityAabb.contains(start)) {
               break;
            }
         } else {
            if (entityAabb.contains(start)) {
               return new RayTraceResult(entity, start);
            }

            RayTraceResult rtr = entityAabb.calculateIntercept(start, end);
            if (!isNullOrMiss(rtr)) {
               double dist = start.squareDistanceTo(rtr.hitVec);
               if (dist < min) {
                  result = rtr;
                  pointedEntity = entity;
                  min = dist;
               }
            }
         }
      }

      return new RayTraceResult(entity, start);
   }

   private static boolean isNullOrMiss(RayTraceResult rayTraceResult) {
      return rayTraceResult == null || rayTraceResult.typeOfHit == Type.MISS;
   }
}
