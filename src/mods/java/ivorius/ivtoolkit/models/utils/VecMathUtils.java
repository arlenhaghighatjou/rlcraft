package ivorius.ivtoolkit.models.utils;

public class VecMathUtils {
   public VecMathUtils() {
   }

   public static double lengthSQ(double[] vector) {
      double max = 0.0D;

      for(double aValue : vector) {
         max += aValue * aValue;
      }

      return max;
   }

   public static double length(double[] vector) {
      return MathUtils.sqrt(lengthSQ(vector));
   }

   public static double distanceSQ(double[] pos1, double[] pos2) {
      double distanceSQ = 0.0D;

      for(int i = 0; i < pos1.length; ++i) {
         distanceSQ += (pos1[i] - pos2[i]) * (pos1[i] - pos2[i]);
      }

      return distanceSQ;
   }

   public static double distance(double[] pos1, double[] pos2) {
      return MathUtils.sqrt(distanceSQ(pos1, pos2));
   }

   public static double[] mix(double[] pos1, double[] pos2, double progress) {
      double[] result = new double[pos1.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = MathUtils.mix(pos1[i], pos2[i], progress);
      }

      return result;
   }

   public static double[] cubicMix(double[] pos1, double[] pos2, double[] pos3, double[] pos4, double progress) {
      double[] result = new double[pos1.length];

      for(int i = 0; i < result.length; ++i) {
         result[i] = MathUtils.cubicMix(pos1[i], pos2[i], pos3[i], pos4[i], progress);
      }

      return result;
   }

   public static double[] normalize(double[] vector) {
      double max = length(vector);
      double[] resultVector = new double[vector.length];

      for(int i = 0; i < vector.length; ++i) {
         resultVector[i] = vector[i] / max;
      }

      return resultVector;
   }

   public static double dotProduct(double[] pos1, double[] pos2) {
      double dotProduct = 0.0D;

      for(int i = 0; i < pos1.length; ++i) {
         dotProduct += pos1[i] * pos2[i];
      }

      return dotProduct;
   }

   public static double[] perpendicularVector(double[] pos1, double[] premiseVector) {
      double[] resultVector = new double[pos1.length];
      double dotProduct = 0.0D;

      for(int i = 0; i < pos1.length - 1; ++i) {
         dotProduct += pos1[i] * premiseVector[i];
         resultVector[i] = premiseVector[i];
      }

      resultVector[pos1.length - 1] = -(dotProduct / pos1[pos1.length - 1]);
      return normalize(resultVector);
   }

   public static double[] crossProduct(double[] pos1, double[] pos2) {
      double[] resultVector = new double[pos1.length];
      resultVector[0] = pos1[1] * pos2[2] - pos1[2] * pos2[1];
      resultVector[1] = pos1[2] * pos2[0] - pos1[0] * pos2[2];
      resultVector[2] = pos1[0] * pos2[1] - pos1[1] * pos2[0];
      return normalize(resultVector);
   }

   public static double[] cartesianFromSpherical(double[] vector) {
      double sinInclination = (double)MathUtils.sin((float)vector[1]);
      double x = vector[2] * sinInclination * (double)MathUtils.cos((float)vector[0]);
      double y = vector[2] * (double)MathUtils.cos((float)vector[1]);
      double z = vector[2] * sinInclination * (double)MathUtils.sin((float)vector[0]);
      return new double[]{x, y, z};
   }

   public static double[] sphericalFromCartesian(double[] vector) {
      double r = length(vector);
      double inclination = Math.acos(vector[1] / r);
      double azimuth = Math.atan2(vector[2], vector[0]);
      return new double[]{azimuth, inclination, r};
   }

   public static int[] add(int[]... vectors) {
      int[] result = new int[vectors[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(int[] vector : vectors) {
            result[i] += vector[i];
         }
      }

      return result;
   }

   public static double[] add(double[]... vectors) {
      double[] result = new double[vectors[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(double[] vector : vectors) {
            result[i] += vector[i];
         }
      }

      return result;
   }

   public static int[] sub(int[] vector, int[]... subVectors) {
      int[] result = (int[])vector.clone();

      for(int i = 0; i < result.length; ++i) {
         for(int[] subVector : subVectors) {
            result[i] -= subVector[i];
         }
      }

      return result;
   }

   public static double[] sub(double[] vector, double[]... subVectors) {
      double[] result = (double[])vector.clone();

      for(int i = 0; i < result.length; ++i) {
         for(double[] subVector : subVectors) {
            result[i] -= subVector[i];
         }
      }

      return result;
   }
}
