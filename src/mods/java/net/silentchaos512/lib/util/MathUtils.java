package net.silentchaos512.lib.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3i;

public final class MathUtils {
   private static final double DOUBLES_EQUAL_PRECISION = 1.0E-9D;
   private static final EaglercraftRandom RANDOM = new EaglercraftRandom();

   private MathUtils() {
      throw new IllegalAccessError("Utility class");
   }

   public static AxisAlignedBB boundingBoxByPixels(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      return new AxisAlignedBB((double)((float)minX / 16.0F), (double)((float)minY / 16.0F), (double)((float)minZ / 16.0F), (double)((float)maxX / 16.0F), (double)((float)maxY / 16.0F), (double)((float)maxZ / 16.0F));
   }

   public static double distance(Vec3i from, Vec3i to) {
      int dx = to.getX() - from.getX();
      int dy = to.getY() - from.getY();
      int dz = to.getZ() - from.getZ();
      return Math.sqrt((double)(dx * dx + dy * dy + dz * dz));
   }

   public static double distanceSq(Vec3i from, Vec3i to) {
      int dx = to.getX() - from.getX();
      int dy = to.getY() - from.getY();
      int dz = to.getZ() - from.getZ();
      return (double)(dx * dx + dy * dy + dz * dz);
   }

   public static double distanceHorizontal(Vec3i from, Vec3i to) {
      int dx = to.getX() - from.getX();
      int dz = to.getZ() - from.getZ();
      return Math.sqrt((double)(dx * dx + dz * dz));
   }

   public static double distanceHorizontalSq(Vec3i from, Vec3i to) {
      int dx = to.getX() - from.getX();
      int dz = to.getZ() - from.getZ();
      return (double)(dx * dx + dz * dz);
   }

   public static boolean doublesEqual(double a, double b) {
      return doublesEqual(a, b, 1.0E-9D);
   }

   public static boolean doublesEqual(double a, double b, double precision) {
      return Math.abs(b - a) < precision;
   }

   public static boolean floatsEqual(float a, float b) {
      return floatsEqual(a, b, 1.0E-9F);
   }

   public static boolean floatsEqual(float a, float b, float precision) {
      return Math.abs(b - a) < precision;
   }

   public static boolean inRangeExclusive(double value, double min, double max) {
      return value < max && value > min;
   }

   public static boolean inRangeExclusive(int value, int min, int max) {
      return value < max && value > min;
   }

   public static boolean inRangeInclusive(double value, double min, double max) {
      return value <= max && value >= min;
   }

   public static boolean inRangeInclusive(int value, int min, int max) {
      return value <= max && value >= min;
   }

   public static int min(int a, int b) {
      return a < b ? a : b;
   }

   public static int min(int a, int b, int c) {
      if (b < a) {
         a = b;
      }

      if (c < a) {
         a = c;
      }

      return a;
   }

   public static int min(int a, int b, int c, int d) {
      if (b < a) {
         a = b;
      }

      if (c < a) {
         a = c;
      }

      if (d < a) {
         a = d;
      }

      return a;
   }

   public static int min(int a, int b, int c, int d, int... rest) {
      int min = min(a, b, c, d);

      for(int i : rest) {
         if (i < min) {
            min = i;
         }
      }

      return min;
   }

   public static int max(int a, int b) {
      return a > b ? a : b;
   }

   public static int max(int a, int b, int c) {
      if (b > a) {
         a = b;
      }

      if (c > a) {
         a = c;
      }

      return a;
   }

   public static int max(int a, int b, int c, int d) {
      if (b > a) {
         a = b;
      }

      if (c > a) {
         a = c;
      }

      if (d > a) {
         a = d;
      }

      return a;
   }

   public static int max(int a, int b, int c, int d, int... rest) {
      int max = max(a, b, c, d);

      for(int i : rest) {
         if (i > max) {
            max = i;
         }
      }

      return max;
   }

   public static double nextGaussian(double mean, double deviation) {
      return deviation * RANDOM.nextGaussian() + mean;
   }

   public static double nextGaussian(EaglercraftRandom random, double mean, double deviation) {
      return deviation * random.nextGaussian() + mean;
   }

   public static int nextInt(int bound) {
      return RANDOM.nextInt(bound);
   }

   public static int nextIntInclusive(int min, int max) {
      return RANDOM.nextInt(max - min + 1) + min;
   }

   public static int nextIntInclusive(EaglercraftRandom random, int min, int max) {
      return random.nextInt(max - min + 1) + min;
   }

   public static boolean tryPercentage(double percent) {
      return RANDOM.nextDouble() < percent;
   }

   public static boolean tryPercentage(EaglercraftRandom random, double percent) {
      return random.nextDouble() < percent;
   }
}
