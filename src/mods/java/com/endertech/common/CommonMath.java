package com.endertech.common;

import java.util.List;
import javax.annotation.Nullable;

public final class CommonMath {
   public static final double ALMOST_ZERO = 1.0E-4D;
   public static final CommonMath.Random RANDOM = new CommonMath.Random();

   public CommonMath() {
   }

   public static boolean isAlmostZero(double value) {
      return Math.abs(value) < 1.0E-4D;
   }

   public static boolean notZero(double value) {
      return !isAlmostZero(value);
   }

   public static double getAverage(double a, double b) {
      return (a + b) / 2.0D;
   }

   public static double getAverage(double a, double b, double c) {
      return (a + b + c) / 3.0D;
   }

   public static long getFloor(double a) {
      long i = (long)a;
      return (double)i > a ? i - 1L : i;
   }

   public static int getFloor(float a) {
      int i = (int)a;
      return (float)i > a ? i - 1 : i;
   }

   public static long getRounded(double a) {
      return Math.round(a);
   }

   public static int getRounded(float a) {
      return Math.round(a);
   }

   public static double raiseToPower(double value, double power) {
      return Math.pow(value, power);
   }

   public static double roundTo(double value, int powerOf10) {
      double raise = raiseToPower(10.0D, (double)(-powerOf10));
      double mult = value * raise;
      long round = getRounded(mult);
      double result = (double)round / raise;
      return result;
   }

   public static double getMinByAbs(double a, double b) {
      return Math.abs(a) <= Math.abs(b) ? a : b;
   }

   public static double getMaxByAbs(double a, double b) {
      return Math.abs(a) <= Math.abs(b) ? a : b;
   }

   public static double getDegrees(double radians) {
      return radians * 180.0D / 3.141592653589793D;
   }

   public static double getRadians(double degrees) {
      return degrees * 3.141592653589793D / 180.0D;
   }

   public static float getMirrored(float value, FloatBounds bounds) {
      return bounds != null ? bounds.getMax().floatValue() - value + bounds.getMin().floatValue() : value;
   }

   public static class Angle {
      public static final CommonMath.Angle ZERO = fromRadians(0.0D);
      protected static final double HALF_REVOLUTION = 3.141592653589793D;
      protected static final double FULL_REVOLUTION = 6.283185307179586D;
      private final double radians;

      protected Angle(double radians) {
         this.radians = radians;
      }

      public static CommonMath.Angle fromDegrees(double degrees) {
         return fromRadians(degrees * 3.141592653589793D / 180.0D);
      }

      public static CommonMath.Angle fromRadians(double radians) {
         return new CommonMath.Angle(radians % 6.283185307179586D);
      }

      public static CommonMath.Angle fromCos(double cos) {
         return fromRadians(Math.acos(cos));
      }

      public static CommonMath.Angle fromSin(double sin) {
         return fromRadians(Math.asin(sin));
      }

      public static CommonMath.Angle fromTan(double tan) {
         return fromRadians(Math.atan(tan));
      }

      public static CommonMath.Angle random() {
         return fromRadians(CommonMath.Random.between(0.0D, 6.283185307179586D));
      }

      public double getRadians() {
         return this.radians;
      }

      public double getDegrees() {
         return this.getRadians() * 180.0D / 3.141592653589793D;
      }

      public CommonMath.Angle add(CommonMath.Angle angle) {
         return fromRadians(this.getRadians() + angle.getRadians());
      }

      public CommonMath.Angle substract(CommonMath.Angle angle) {
         return fromRadians(this.getRadians() - angle.getRadians());
      }

      public CommonMath.Angle negative() {
         return fromRadians(-this.getRadians());
      }

      public double cos() {
         return Math.cos(this.getRadians());
      }

      public double sin() {
         return Math.sin(this.getRadians());
      }

      public double tan() {
         return Math.tan(this.getRadians());
      }
   }

   public static class Approx<T extends Number> {
      public final IBounds<T> source;
      public final IBounds<T> dest;

      public Approx(IBounds<T> source, IBounds<T> dest) {
         this.source = source;
         this.dest = dest;
      }

      public T straight(T value) {
         float factor = this.source.approxFactor(value);
         return this.dest.approxUp(factor);
      }

      public T backward(T value) {
         float factor = this.source.approxFactor(value);
         return this.dest.approxDown(factor);
      }

      public static float straight(float factor, float begin, float end) {
         return begin + (end - begin) * factor;
      }

      public static float backward(float factor, float begin, float end) {
         return end - (end - begin) * factor;
      }

      public static float up(float factor, FloatBounds bounds) {
         return straight(factor, bounds.getMin().floatValue(), bounds.getMax().floatValue());
      }

      public static float down(float factor, FloatBounds bounds) {
         return backward(factor, bounds.getMin().floatValue(), bounds.getMax().floatValue());
      }

      public static int up(float factor, IntBounds bounds) {
         return Math.round(straight(factor, (float)bounds.getMin().intValue(), (float)bounds.getMax().intValue()));
      }

      public static int down(float factor, IntBounds bounds) {
         return Math.round(backward(factor, (float)bounds.getMin().intValue(), (float)bounds.getMax().intValue()));
      }

      public static float strictUp(float factor, FloatBounds bounds) {
         return CommonMath.Range.enclose(up(factor, bounds), bounds);
      }

      public static int strictUp(float factor, IntBounds bounds) {
         return CommonMath.Range.enclose(up(factor, bounds), bounds);
      }

      public static float strictDown(float factor, FloatBounds bounds) {
         return CommonMath.Range.enclose(down(factor, bounds), bounds);
      }

      public static int strictDown(float factor, IntBounds bounds) {
         return CommonMath.Range.enclose(down(factor, bounds), bounds);
      }
   }

   public static class Random extends java.util.Random {
      private static final long serialVersionUID = -5036832263594314436L;

      public Random() {
      }

      public static double around(double value, double maxDeviation) {
         return between(value - maxDeviation, value + maxDeviation);
      }

      public static float around(float value, float maxDeviation) {
         return between(value - maxDeviation, value + maxDeviation);
      }

      public static double between(double first, double second) {
         return CommonMath.RANDOM.nextDouble() == 0.0D ? second : CommonMath.RANDOM.nextDouble() * (second - first) + first;
      }

      public static float between(float first, float second) {
         return CommonMath.RANDOM.nextFloat() == 0.0F ? second : CommonMath.RANDOM.nextFloat() * (second - first) + first;
      }

      public static int between(int first, int second) {
         return first < second ? CommonMath.RANDOM.nextInt(second - first + 1) + first : CommonMath.RANDOM.nextInt(first - second + 1) + second;
      }

      @Nullable
      public static <T> T from(List<T> list) {
         if (list != null && list.size() > 0) {
            int index = between(0, list.size() - 1);
            return (T)list.get(index);
         } else {
            return (T)null;
         }
      }

      @Nullable
      public static <T> T from(T[] array) {
         if (array != null && array.length > 0) {
            int index = between(0, array.length - 1);
            return (T)array[index];
         } else {
            return (T)null;
         }
      }

      @Nullable
      public static <E extends Enum<?>> E from(Class<E> enumClass) {
         return (E)(from(enumClass.getEnumConstants()));
      }

      public static boolean result(float probability) {
         return CommonMath.RANDOM.nextFloat() < probability;
      }

      public static int sign() {
         return getBoolean() ? 1 : -1;
      }

      public static boolean getBoolean() {
         return CommonMath.RANDOM.nextBoolean();
      }

      public static float getFloat() {
         return CommonMath.RANDOM.nextFloat();
      }

      public static double getDouble() {
         return CommonMath.RANDOM.nextDouble();
      }

      public static int getInt() {
         return CommonMath.RANDOM.nextInt();
      }
   }

   public static class Range {
      public Range() {
      }

      public static double enclose(double value, double bound1, double bound2) {
         double min = Math.min(bound1, bound2);
         double max = Math.max(bound1, bound2);
         if (value < min) {
            value = min;
         }

         if (value > max) {
            value = max;
         }

         return value;
      }

      public static float enclose(float value, float bound1, float bound2) {
         float min = Math.min(bound1, bound2);
         float max = Math.max(bound1, bound2);
         if (value < min) {
            value = min;
         }

         if (value > max) {
            value = max;
         }

         return value;
      }

      public static int enclose(int value, int bound1, int bound2) {
         int min = Math.min(bound1, bound2);
         int max = Math.max(bound1, bound2);
         if (value < min) {
            value = min;
         }

         if (value > max) {
            value = max;
         }

         return value;
      }

      public static float enclose(float value, FloatBounds bounds) {
         return bounds != null ? enclose(value, bounds.getMin().floatValue(), bounds.getMax().floatValue()) : value;
      }

      public static int enclose(int value, IntBounds bounds) {
         return bounds != null ? enclose(value, bounds.getMin().intValue(), bounds.getMax().intValue()) : value;
      }
   }
}
