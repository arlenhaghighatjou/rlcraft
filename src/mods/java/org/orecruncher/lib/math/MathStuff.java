package org.orecruncher.lib.math;

import javax.annotation.Nonnull;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathStuff {
   private static final int SIN_BITS = 12;
   private static final int SIN_MASK = 4095;
   private static final int SIN_COUNT = 4096;
   private static final float RAD_FULL = 6.2831855F;
   private static final float RAD_TO_INDEX = 651.8986F;
   private static final float DEG_FULL = 360.0F;
   private static final float DEG_TO_INDEX = 11.377778F;
   private static final float COS_TO_SIN = 1.5707964F;
   private static final float[] SIN_TABLE = new float[4096];
   private static final int ATAN2_BITS = 7;
   private static final int ATAN2_BITS2 = 14;
   private static final int ATAN2_MASK = 16383;
   private static final int ATAN2_COUNT = 16384;
   private static final int ATAN2_DIM = (int)Math.sqrt(16384.0D);
   private static final float ATAN2_DIM_MINUS_1 = (float)(ATAN2_DIM - 1);
   private static final float[] ATAN2_TABLE = new float[16384];
   private static final float RAD_TO_DEG = 57.29578F;
   private static final float DEG_TO_RAD = 0.017453292F;
   public static final float PI_F = 3.1415927F;
   public static final float E_F = 2.7182817F;

   public MathStuff() {
   }

   public static final float sin(float rad) {
      return SIN_TABLE[(int)(rad * 651.8986F) & 4095];
   }

   public static final float cos(float rad) {
      return SIN_TABLE[(int)((rad + 1.5707964F) * 651.8986F) & 4095];
   }

   public static final float tan(float rad) {
      return sin(rad) / cos(rad);
   }

   public static final double sin(double rad) {
      float tmp = (float)rad;
      return (double)SIN_TABLE[(int)(tmp * 651.8986F) & 4095];
   }

   public static final double cos(double rad) {
      float tmp = (float)rad;
      return (double)SIN_TABLE[(int)((tmp + 1.5707964F) * 651.8986F) & 4095];
   }

   public static final double tan(double rad) {
      return (double)tan((float)rad);
   }

   public static final float atan2(float y, float x) {
      float add;
      float mul;
      if (x < 0.0F) {
         if (y < 0.0F) {
            x = -x;
            y = -y;
            mul = 1.0F;
         } else {
            x = -x;
            mul = -1.0F;
         }

         add = -3.1415927F;
      } else {
         if (y < 0.0F) {
            y = -y;
            mul = -1.0F;
         } else {
            mul = 1.0F;
         }

         add = 0.0F;
      }

      float invDiv = ATAN2_DIM_MINUS_1 / (x < y ? y : x);
      int xi = (int)(x * invDiv);
      int yi = (int)(y * invDiv);
      return (ATAN2_TABLE[yi * ATAN2_DIM + xi] + add) * mul;
   }

   public static final double atan2(double y, double x) {
      float tmp1 = (float)y;
      float tmp2 = (float)x;
      return (double)atan2(tmp1, tmp2);
   }

   public static final float toRadians(float degrees) {
      return degrees * 0.017453292F;
   }

   public static final float toDegrees(float radians) {
      return radians * 57.29578F;
   }

   public static float wrapDegrees(float value) {
      value = value % 360.0F;
      if (value >= 180.0F) {
         value -= 360.0F;
      }

      if (value < -180.0F) {
         value += 360.0F;
      }

      return value;
   }

   public static double wrapDegrees(double value) {
      value = value % 360.0D;
      if (value >= 180.0D) {
         value -= 360.0D;
      }

      if (value < -180.0D) {
         value += 360.0D;
      }

      return value;
   }

   public static final float abs(float val) {
      return val < 0.0F ? -val : val;
   }

   public static final double abs(double val) {
      return val < 0.0D ? -val : val;
   }

   public static final long abs(long val) {
      return val < 1L ? -val : val;
   }

   public static final int abs(int val) {
      return val < 1 ? -val : val;
   }

   public static float sqrt(float value) {
      return (float)Math.sqrt((double)value);
   }

   public static double sqrt(double value) {
      return (double)((float)Math.sqrt(value));
   }

   public static int floor(double value) {
      int i = (int)value;
      return value < (double)i ? i - 1 : i;
   }

   public static int floor(float value) {
      int i = (int)value;
      return value < (float)i ? i - 1 : i;
   }

   public static double log(double value) {
      return value < 0.03D ? Math.log(value) : 6.0D * (value - 1.0D) / (value + 1.0D + 4.0D * Math.sqrt(value));
   }

   public static float clamp(float num, float min, float max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static double clamp(double num, double min, double max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static int clamp(int num, int min, int max) {
      return num < min ? min : (num > max ? max : num);
   }

   public static Vec2f rotateScale(@Nonnull Vec2f coord, float radians, float scale) {
      float f = cos(radians);
      float f1 = sin(radians);
      float d0 = coord.x * f + coord.y * f1;
      float d1 = coord.y * f - coord.x * f1;
      return new Vec2f(d0 * scale, d1 * scale);
   }

   public static Vec2f rotate(@Nonnull Vec2f coord, float radians) {
      return rotateScale(coord, radians, 1.0F);
   }

   public static final Vec3d getVectorForRotation(float pitch, float yaw) {
      float f = cos(-yaw * 0.017453292F - 3.1415927F);
      float f1 = sin(-yaw * 0.017453292F - 3.1415927F);
      float f2 = -cos(-pitch * 0.017453292F);
      float f3 = sin(-pitch * 0.017453292F);
      return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
   }

   static {
      for(int i = 0; i < 4096; ++i) {
         SIN_TABLE[i] = (float)Math.sin((double)(((float)i + 0.5F) / 4096.0F * 6.2831855F));
      }

      for(int i = 0; i < 360; i += 90) {
         SIN_TABLE[(int)((float)i * 11.377778F) & 4095] = (float)Math.sin((double)i * 3.141592653589793D / 180.0D);
      }

      for(int i = 0; i < ATAN2_DIM; ++i) {
         for(int j = 0; j < ATAN2_DIM; ++j) {
            float x0 = (float)i / (float)ATAN2_DIM;
            float y0 = (float)j / (float)ATAN2_DIM;
            ATAN2_TABLE[j * ATAN2_DIM + i] = (float)Math.atan2((double)y0, (double)x0);
         }
      }

   }
}
