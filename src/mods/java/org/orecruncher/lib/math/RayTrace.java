package org.orecruncher.lib.math;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.chunk.IBlockAccessEx;

public final class RayTrace {
   private RayTrace() {
   }

   public static RayTraceResult trace(@Nonnull EntityLivingBase entity) {
      double range;
      if (entity instanceof EntityPlayer) {
         range = entity.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
      } else {
         range = (double)(entity.width * 2.0F + 0.6F);
      }

      World world = entity.getEntityWorld();
      Vec3d eyes = entity.getPositionEyes(1.0F);
      Vec3d look = entity.getLook(1.0F);
      Vec3d rangedLook = eyes.addVector(look.x * range, look.y * range, look.z * range);
      RayTraceResult traceResult = rayTraceBlocks(world, eyes, rangedLook, false, false, true);
      Entity pointedEntity = null;
      boolean flag = false;
      double range1 = range;
      if (range > 3.0D) {
         flag = true;
      }

      if (traceResult != null) {
         range1 = traceResult.hitVec.distanceTo(eyes);
      }

      Vec3d hitLocation = null;
      List<Entity> list = world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(look.x * range, look.y * range, look.z * range).grow(1.0D, 1.0D, 1.0D), Predicates.and(EntitySelectors.NOT_SPECTATING, (e) -> {
         return e != null && e.canBeCollidedWith();
      }));
      double d2 = range1;

      for(int j = 0; j < list.size(); ++j) {
         Entity entity1 = (Entity)list.get(j);
         AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double)entity1.getCollisionBorderSize());
         RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(eyes, rangedLook);
         if (axisalignedbb.contains(eyes)) {
            if (d2 >= 0.0D) {
               pointedEntity = entity1;
               hitLocation = raytraceresult == null ? eyes : raytraceresult.hitVec;
               d2 = 0.0D;
            }
         } else if (raytraceresult != null) {
            double d3 = eyes.distanceTo(raytraceresult.hitVec);
            if (d3 < d2 || d2 == 0.0D) {
               if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract()) {
                  if (d2 == 0.0D) {
                     pointedEntity = entity1;
                     hitLocation = raytraceresult.hitVec;
                  }
               } else {
                  pointedEntity = entity1;
                  hitLocation = raytraceresult.hitVec;
                  d2 = d3;
               }
            }
         }
      }

      if (pointedEntity != null && flag && eyes.distanceTo(hitLocation) > 3.0D) {
         pointedEntity = null;
         traceResult = new RayTraceResult(Type.MISS, hitLocation, (EnumFacing)null, new BlockPos(hitLocation));
      }

      if (pointedEntity != null && (d2 < range1 || traceResult == null)) {
         traceResult = new RayTraceResult(pointedEntity, hitLocation);
      }

      return traceResult;
   }

   @Nullable
   public static RayTraceResult rayTraceBlocks(@Nonnull World world, Vec3d eyePosition, Vec3d rangePosition, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
      MutableBlockPos blockpos = new MutableBlockPos();
      IBlockAccessEx provider = WorldUtils.getDefaultBlockStateProvider();
      if (!Double.isNaN(eyePosition.x) && !Double.isNaN(eyePosition.y) && !Double.isNaN(eyePosition.z)) {
         if (!Double.isNaN(rangePosition.x) && !Double.isNaN(rangePosition.y) && !Double.isNaN(rangePosition.z)) {
            int i = MathStuff.floor(rangePosition.x);
            int j = MathStuff.floor(rangePosition.y);
            int k = MathStuff.floor(rangePosition.z);
            int l = MathStuff.floor(eyePosition.x);
            int i1 = MathStuff.floor(eyePosition.y);
            int j1 = MathStuff.floor(eyePosition.z);
            blockpos.setPos(l, i1, j1);
            IBlockState iblockstate = provider.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
               RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, eyePosition, rangePosition);
               if (raytraceresult != null) {
                  return raytraceresult;
               }
            }

            RayTraceResult raytraceresult2 = null;
            int k1 = 200;

            while(k1-- >= 0) {
               if (Double.isNaN(eyePosition.x) || Double.isNaN(eyePosition.y) || Double.isNaN(eyePosition.z)) {
                  return null;
               }

               if (l == i && i1 == j && j1 == k) {
                  return returnLastUncollidableBlock ? raytraceresult2 : null;
               }

               boolean flag2 = true;
               boolean flag = true;
               boolean flag1 = true;
               double d0 = 999.0D;
               double d1 = 999.0D;
               double d2 = 999.0D;
               if (i > l) {
                  d0 = (double)l + 1.0D;
               } else if (i < l) {
                  d0 = (double)l + 0.0D;
               } else {
                  flag2 = false;
               }

               if (j > i1) {
                  d1 = (double)i1 + 1.0D;
               } else if (j < i1) {
                  d1 = (double)i1 + 0.0D;
               } else {
                  flag = false;
               }

               if (k > j1) {
                  d2 = (double)j1 + 1.0D;
               } else if (k < j1) {
                  d2 = (double)j1 + 0.0D;
               } else {
                  flag1 = false;
               }

               double d3 = 999.0D;
               double d4 = 999.0D;
               double d5 = 999.0D;
               double d6 = rangePosition.x - eyePosition.x;
               double d7 = rangePosition.y - eyePosition.y;
               double d8 = rangePosition.z - eyePosition.z;
               if (flag2) {
                  d3 = (d0 - eyePosition.x) / d6;
               }

               if (flag) {
                  d4 = (d1 - eyePosition.y) / d7;
               }

               if (flag1) {
                  d5 = (d2 - eyePosition.z) / d8;
               }

               if (d3 == -0.0D) {
                  d3 = -1.0E-4D;
               }

               if (d4 == -0.0D) {
                  d4 = -1.0E-4D;
               }

               if (d5 == -0.0D) {
                  d5 = -1.0E-4D;
               }

               EnumFacing enumfacing;
               if (d3 < d4 && d3 < d5) {
                  enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                  eyePosition = new Vec3d(d0, eyePosition.y + d7 * d3, eyePosition.z + d8 * d3);
               } else if (d4 < d5) {
                  enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                  eyePosition = new Vec3d(eyePosition.x + d6 * d4, d1, eyePosition.z + d8 * d4);
               } else {
                  enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                  eyePosition = new Vec3d(eyePosition.x + d6 * d5, eyePosition.y + d7 * d5, d2);
               }

               l = MathStuff.floor(eyePosition.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
               i1 = MathStuff.floor(eyePosition.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
               j1 = MathStuff.floor(eyePosition.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
               blockpos.setPos(l, i1, j1);
               iblockstate = provider.getBlockState(blockpos);
               block = iblockstate.getBlock();
               if (!ignoreBlockWithoutBoundingBox || iblockstate.getMaterial() == Material.PORTAL || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
                  if (block.canCollideCheck(iblockstate, stopOnLiquid)) {
                     RayTraceResult raytraceresult1 = iblockstate.collisionRayTrace(world, blockpos, eyePosition, rangePosition);
                     if (raytraceresult1 != null) {
                        return raytraceresult1;
                     }
                  } else {
                     raytraceresult2 = new RayTraceResult(Type.MISS, eyePosition, enumfacing, blockpos);
                  }
               }
            }

            return returnLastUncollidableBlock ? raytraceresult2 : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }
}
