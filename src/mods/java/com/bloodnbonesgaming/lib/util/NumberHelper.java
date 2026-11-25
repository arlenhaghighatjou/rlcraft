package com.bloodnbonesgaming.lib.util;

import net.minecraft.util.math.AxisAlignedBB;

public class NumberHelper {
   public NumberHelper() {
   }

   public static AxisAlignedBB createBoundingBox(BlockLocation corner1, BlockLocation corner2) {
      int minZ = Math.min(corner1.getZ(), corner2.getZ());
      int minX = Math.min(corner1.getX(), corner2.getX());
      int minY = Math.min(corner1.getY(), corner2.getY());
      int maxZ = Math.max(corner1.getZ(), corner2.getZ());
      int maxX = Math.max(corner1.getX(), corner2.getX());
      int maxY = Math.max(corner1.getY(), corner2.getY());
      return createBoundingBoxFromBounds((double)minX, (double)minY, (double)minZ, (double)maxX, (double)maxY, (double)maxZ);
   }

   public static AxisAlignedBB createBoundingBoxFromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
      double d0 = Math.min(x1, x2);
      double d1 = Math.min(y1, y2);
      double d2 = Math.min(z1, z2);
      double d3 = Math.max(x1, x2);
      double d4 = Math.max(y1, y2);
      double d5 = Math.max(z1, z2);
      return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
   }

   public static boolean isInteger(String str) {
      if (str == null) {
         return false;
      } else {
         int length = str.length();
         if (length == 0) {
            return false;
         } else {
            int i = 0;
            if (str.charAt(0) == '-') {
               if (length == 1) {
                  return false;
               }

               i = 1;
            }

            while(i < length) {
               char c = str.charAt(i);
               if (c <= '/' || c >= ':') {
                  return false;
               }

               ++i;
            }

            return true;
         }
      }
   }

   public static int[] range(int start, int end) {
      int[] values = new int[end - start + 1];

      for(int i = start; i <= end; values[i - start] = i++) {
         ;
      }

      return values;
   }

   public static void interpolate(double[] smallArray, double[] largeArray, int xSize, int ySize, int zSize, int xSpacing, int ySpacing, int zSpacing) {
      int realXSize = xSize - 1;
      int realZSize = zSize - 1;
      int realYSize = ySize - 1;
      int largeHeight = realYSize * ySpacing;
      int width = realXSize * xSpacing;

      for(int i = 0; i < realXSize; ++i) {
         int j = i * 5;
         int k = (i + 1) * 5;

         for(int l = 0; l < realZSize; ++l) {
            int i1 = (j + l) * 33;
            int j1 = (j + l + 1) * 33;
            int k1 = (k + l) * 33;
            int l1 = (k + l + 1) * 33;

            for(int i2 = 0; i2 < realYSize; ++i2) {
               double d0 = 0.125D;
               double d1 = smallArray[i1 + i2];
               double d2 = smallArray[j1 + i2];
               double d3 = smallArray[k1 + i2];
               double d4 = smallArray[l1 + i2];
               double d5 = (smallArray[i1 + i2 + 1] - d1) * 0.125D;
               double d6 = (smallArray[j1 + i2 + 1] - d2) * 0.125D;
               double d7 = (smallArray[k1 + i2 + 1] - d3) * 0.125D;
               double d8 = (smallArray[l1 + i2 + 1] - d4) * 0.125D;

               for(int j2 = 0; j2 < ySpacing; ++j2) {
                  double d10 = d1;
                  double d11 = d2;
                  double d12 = (d3 - d1) * 0.25D;
                  double d13 = (d4 - d2) * 0.25D;

                  for(int k2 = 0; k2 < xSpacing; ++k2) {
                     double d16 = (d11 - d10) * 0.25D;
                     double lvt_45_1_ = d10 - d16;

                     for(int l2 = 0; l2 < zSpacing; ++l2) {
                        lvt_45_1_ += d16;
                        int x = i * 4 + k2;
                        int y = i2 * 8 + j2;
                        int z = l * 4 + l2;
                        int index = (x * width + z) * largeHeight + y;
                        largeArray[index] = lvt_45_1_;
                     }

                     d10 += d12;
                     d11 += d13;
                  }

                  d1 += d5;
                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
               }
            }
         }
      }

   }
}
