package mcjty.lostcities.varia;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class GeometryTools {
   public GeometryTools() {
   }

   public static double squaredDistanceBoxPoint(AxisAlignedBB chunkBox, BlockPos center) {
      double dmin = 0.0D;
      if ((double)center.getX() < chunkBox.minX) {
         dmin += Math.pow((double)center.getX() - chunkBox.minX, 2.0D);
      } else if ((double)center.getX() > chunkBox.maxX) {
         dmin += Math.pow((double)center.getX() - chunkBox.maxX, 2.0D);
      }

      if ((double)center.getY() < chunkBox.minY) {
         dmin += Math.pow((double)center.getY() - chunkBox.minY, 2.0D);
      } else if ((double)center.getY() > chunkBox.maxY) {
         dmin += Math.pow((double)center.getY() - chunkBox.maxY, 2.0D);
      }

      if ((double)center.getZ() < chunkBox.minZ) {
         dmin += Math.pow((double)center.getZ() - chunkBox.minZ, 2.0D);
      } else if ((double)center.getZ() > chunkBox.maxZ) {
         dmin += Math.pow((double)center.getZ() - chunkBox.maxZ, 2.0D);
      }

      return dmin;
   }

   public static double maxSquaredDistanceBoxPoint(AxisAlignedBB chunkBox, BlockPos center) {
      double dmax = 0.0D;
      if ((double)center.getX() < (chunkBox.minX + chunkBox.maxX) / 2.0D) {
         dmax = dmax + Math.pow((double)center.getX() - chunkBox.maxX, 2.0D);
      } else {
         dmax = dmax + Math.pow((double)center.getX() - chunkBox.minX, 2.0D);
      }

      if ((double)center.getY() < (chunkBox.minY + chunkBox.maxY) / 2.0D) {
         dmax = dmax + Math.pow((double)center.getY() - chunkBox.maxY, 2.0D);
      } else {
         dmax = dmax + Math.pow((double)center.getY() - chunkBox.minY, 2.0D);
      }

      if ((double)center.getZ() < (chunkBox.minZ + chunkBox.maxZ) / 2.0D) {
         dmax = dmax + Math.pow((double)center.getZ() - chunkBox.maxZ, 2.0D);
      } else {
         dmax = dmax + Math.pow((double)center.getZ() - chunkBox.minZ, 2.0D);
      }

      return dmax;
   }

   public static double squaredDistanceBoxPoint(GeometryTools.AxisAlignedBB2D chunkBox, int x, int y) {
      double dmin = 0.0D;
      if ((double)x < chunkBox.minX) {
         dmin += Math.pow((double)x - chunkBox.minX, 2.0D);
      } else if ((double)x > chunkBox.maxX) {
         dmin += Math.pow((double)x - chunkBox.maxX, 2.0D);
      }

      if ((double)y < chunkBox.minY) {
         dmin += Math.pow((double)y - chunkBox.minY, 2.0D);
      } else if ((double)y > chunkBox.maxY) {
         dmin += Math.pow((double)y - chunkBox.maxY, 2.0D);
      }

      return dmin;
   }

   public static class AxisAlignedBB2D {
      public final double minX;
      public final double minY;
      public final double maxX;
      public final double maxY;
      public int height;

      public AxisAlignedBB2D(double minX, double minY, double maxX, double maxY) {
         this.minX = minX;
         this.minY = minY;
         this.maxX = maxX;
         this.maxY = maxY;
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMinY() {
         return this.minY;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMaxY() {
         return this.maxY;
      }
   }
}
