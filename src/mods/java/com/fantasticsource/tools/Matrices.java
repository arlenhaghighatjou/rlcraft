package com.fantasticsource.tools;

public class Matrices {
   public Matrices() {
   }

   public static float[] mult(float[]... matrices) {
      if (matrices[0].length != 16) {
         throw new IllegalArgumentException("All matrices must be of length 16 (4x4 matrices; argument 0 was not)");
      } else {
         float[] result = (float[])matrices[0].clone();

         for(int i = 1; i < matrices.length; ++i) {
            if (matrices[i].length != 16) {
               throw new IllegalArgumentException("All matrices must be of length 16 (4x4 matrices; argument " + i + " was not)");
            }

            result[0] = result[0] * matrices[i][0] + result[4] * matrices[i][1] + result[8] * matrices[i][2] + result[12] * matrices[i][3];
            result[1] = result[1] * matrices[i][0] + result[5] * matrices[i][1] + result[9] * matrices[i][2] + result[13] * matrices[i][3];
            result[2] = result[2] * matrices[i][0] + result[6] * matrices[i][1] + result[10] * matrices[i][2] + result[14] * matrices[i][3];
            result[3] = result[3] * matrices[i][0] + result[7] * matrices[i][1] + result[11] * matrices[i][2] + result[15] * matrices[i][3];
            result[4] = result[0] * matrices[i][4] + result[4] * matrices[i][5] + result[8] * matrices[i][6] + result[12] * matrices[i][7];
            result[5] = result[1] * matrices[i][4] + result[5] * matrices[i][5] + result[9] * matrices[i][6] + result[13] * matrices[i][7];
            result[6] = result[2] * matrices[i][4] + result[6] * matrices[i][5] + result[10] * matrices[i][6] + result[14] * matrices[i][7];
            result[7] = result[3] * matrices[i][4] + result[7] * matrices[i][5] + result[11] * matrices[i][6] + result[15] * matrices[i][7];
            result[8] = result[0] * matrices[i][8] + result[4] * matrices[i][9] + result[8] * matrices[i][10] + result[12] * matrices[i][11];
            result[9] = result[1] * matrices[i][8] + result[5] * matrices[i][9] + result[9] * matrices[i][10] + result[13] * matrices[i][11];
            result[10] = result[2] * matrices[i][8] + result[6] * matrices[i][9] + result[10] * matrices[i][10] + result[14] * matrices[i][11];
            result[11] = result[3] * matrices[i][8] + result[7] * matrices[i][9] + result[11] * matrices[i][10] + result[15] * matrices[i][11];
            result[12] = result[0] * matrices[i][12] + result[4] * matrices[i][13] + result[8] * matrices[i][14] + result[12] * matrices[i][15];
            result[13] = result[1] * matrices[i][12] + result[5] * matrices[i][13] + result[9] * matrices[i][14] + result[13] * matrices[i][15];
            result[14] = result[2] * matrices[i][12] + result[6] * matrices[i][13] + result[10] * matrices[i][14] + result[14] * matrices[i][15];
            result[15] = result[3] * matrices[i][12] + result[7] * matrices[i][13] + result[11] * matrices[i][14] + result[15] * matrices[i][15];
         }

         return result;
      }
   }

   public static float[] translate(float[] matrix, float x, float y, float z) {
      return mult(matrix, translate(x, y, z));
   }

   public static float[] translate(float x, float y, float z) {
      return new float[]{1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, x, y, z, 1.0F};
   }

   public static float[] scale(float[] matrix, float x, float y, float z) {
      return mult(matrix, scale(x, y, z));
   }

   public static float[] scale(float x, float y, float z) {
      return new float[]{x, 0.0F, 0.0F, 0.0F, 0.0F, y, 0.0F, 0.0F, 0.0F, 0.0F, z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
   }

   public static float[] frustumBetter(float left, float right, float top, float bottom, float zStart, float zDepth, float xScale, float yScale, float zScale) {
      zScale = zScale * -2.0E-4F * Math.min(Math.abs(left - right), Math.abs(top - bottom));
      int STATIC_ZNEAR = 100;
      if (zDepth <= 0.0F) {
         zDepth = 1.0E-6F;
      }

      return scale(translate(frustum(left, right, top, bottom, 100.0F, 100.0F + zDepth), 0.0F, 0.0F, zStart - 100.0F), xScale, yScale, zScale);
   }

   public static float[] frustum(float left, float right, float top, float bottom, float zNear, float zFar) {
      return new float[]{2.0F * zNear / (right - left), 0.0F, 0.0F, 0.0F, 0.0F, 2.0F * zNear / (top - bottom), 0.0F, 0.0F, (right + left) / (right - left), (top + bottom) / (top - bottom), -(zFar + zNear) / (zFar - zNear), -1.0F, 0.0F, 0.0F, -2.0F * zFar * zNear / (zFar - zNear), 0.0F};
   }
}
