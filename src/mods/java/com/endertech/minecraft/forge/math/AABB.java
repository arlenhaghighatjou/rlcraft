package com.endertech.minecraft.forge.math;

import com.endertech.minecraft.forge.world.ForgeWorld;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.AxisAlignedBB;

public class AABB {
   public static final AxisAlignedBB ZERO = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   public static final AxisAlignedBB BLOCK = Block.FULL_BLOCK_AABB;
   public static final int BLOCK_SIZE = 1;

   public AABB() {
   }

   public static List<AxisAlignedBB> getEdges(float thickness) {
      float d1 = 1.0F - thickness;
      AxisAlignedBB X = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, (double)thickness, (double)thickness);
      AxisAlignedBB Y = new AxisAlignedBB(0.0D, 0.0D, 0.0D, (double)thickness, 1.0D, (double)thickness);
      AxisAlignedBB Z = new AxisAlignedBB(0.0D, 0.0D, 0.0D, (double)thickness, (double)thickness, 1.0D);
      AxisAlignedBB[] AABB_X = new AxisAlignedBB[]{X.offset(0.0D, 0.0D, 0.0D), X.offset(0.0D, (double)d1, 0.0D), X.offset(0.0D, 0.0D, (double)d1), X.offset(0.0D, (double)d1, (double)d1)};
      AxisAlignedBB[] AABB_Y = new AxisAlignedBB[]{Y.offset(0.0D, 0.0D, 0.0D), Y.offset((double)d1, 0.0D, 0.0D), Y.offset(0.0D, 0.0D, (double)d1), Y.offset((double)d1, 0.0D, (double)d1)};
      AxisAlignedBB[] AABB_Z = new AxisAlignedBB[]{Z.offset(0.0D, 0.0D, 0.0D), Z.offset(0.0D, (double)d1, 0.0D), Z.offset((double)d1, 0.0D, 0.0D), Z.offset((double)d1, (double)d1, 0.0D)};
      List<AxisAlignedBB> edges = new ArrayList();
      Collections.addAll(edges, AABB_X);
      Collections.addAll(edges, AABB_Y);
      Collections.addAll(edges, AABB_Z);
      return edges;
   }

   public static AxisAlignedBB getFace(float thickness, EnumFacing facing) {
      float d1 = 1.0F - thickness;
      switch(facing) {
      case DOWN:
         return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, (double)thickness, 1.0D);
      case UP:
         return new AxisAlignedBB(0.0D, (double)d1, 0.0D, 1.0D, 1.0D, 1.0D);
      case EAST:
         return new AxisAlignedBB((double)d1, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
      case NORTH:
         return new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, (double)thickness);
      case SOUTH:
         return new AxisAlignedBB(0.0D, 0.0D, (double)d1, 1.0D, 1.0D, 1.0D);
      case WEST:
         return new AxisAlignedBB(0.0D, 0.0D, 0.0D, (double)thickness, 1.0D, 1.0D);
      default:
         return ZERO;
      }
   }

   public static List<AxisAlignedBB> getFaces(float thickness, EnumFacing... facings) {
      List<AxisAlignedBB> list = new ArrayList();

      for(EnumFacing facing : facings) {
         list.add(getFace(thickness, facing));
      }

      return list;
   }

   public static List<AxisAlignedBB> getWalls(float thickness) {
      return getFaces(thickness, ForgeWorld.Direction.CLOCKWISE_HORIZONTALS);
   }

   public static boolean isFullBlockingPane(AxisAlignedBB aabb, Axis axis) {
      if (aabb == null) {
         return false;
      } else {
         switch(axis) {
         case X:
            if (aabb.maxY - aabb.minY >= 1.0D && aabb.maxZ - aabb.minZ >= 1.0D) {
               return true;
            }
            break;
         case Y:
            if (aabb.maxX - aabb.minX >= 1.0D && aabb.maxZ - aabb.minZ >= 1.0D) {
               return true;
            }
            break;
         case Z:
            if (aabb.maxY - aabb.minY >= 1.0D && aabb.maxX - aabb.minX >= 1.0D) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isFullBlockingFace(AxisAlignedBB aabb, EnumFacing facing) {
      if (isFullBlockingPane(aabb, facing.getAxis())) {
         boolean neg = facing.getAxisDirection() == AxisDirection.NEGATIVE;
         switch(facing.getAxis()) {
         case X:
            return neg ? aabb.minX == 0.0D : aabb.maxX == 1.0D;
         case Y:
            return neg ? aabb.minY == 0.0D : aabb.maxY == 1.0D;
         case Z:
            return neg ? aabb.minZ == 0.0D : aabb.maxZ == 1.0D;
         }
      }

      return false;
   }
}
