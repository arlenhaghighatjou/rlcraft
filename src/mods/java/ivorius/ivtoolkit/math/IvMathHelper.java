package ivorius.ivtoolkit.math;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.MathHelper;

public class IvMathHelper {
   public IvMathHelper() {
   }

   public static double mix(double value1, double value2, double progress) {
      return value1 + (value2 - value1) * progress;
   }

   public static double mixEaseInOut(double value1, double value2, double progress) {
      return cubicMix(value1, value1, value2, value2, progress);
   }

   public static double easeZeroToOne(double progress) {
      return cubicMix(0.0D, 0.0D, 1.0D, 1.0D, clamp(0.0D, progress, 1.0D));
   }

   public static double quadraticMix(double value1, double value2, double value3, double progress) {
      return mix(mix(value1, value2, progress), mix(value2, value3, progress), progress);
   }

   public static double cubicMix(double value1, double value2, double value3, double value4, double progress) {
      return mix(quadraticMix(value1, value2, value3, progress), quadraticMix(value2, value3, value4, progress), progress);
   }

   public static float mix(float value1, float value2, float progress) {
      return value1 + (value2 - value1) * progress;
   }

   public static float mixEaseInOut(float value1, float value2, float progress) {
      return cubicMix(value1, value1, value2, value2, progress);
   }

   public static float easeZeroToOne(float progress) {
      return cubicMix(0.0F, 0.0F, 1.0F, 1.0F, clamp(0.0F, progress, 1.0F));
   }

   public static float quadraticMix(float value1, float value2, float value3, float progress) {
      return mix(mix(value1, value2, progress), mix(value2, value3, progress), progress);
   }

   public static float cubicMix(float value1, float value2, float value3, float value4, float progress) {
      return mix(quadraticMix(value1, value2, value3, progress), quadraticMix(value2, value3, value4, progress), progress);
   }

   public static float clamp(float min, float value, float max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static double clamp(double min, double value, double max) {
      return value < min ? min : (value > max ? max : value);
   }

   public static float nearValue(float value, float dest, float mulSpeed, float plusSpeed) {
      value = value + (dest - value) * mulSpeed;
      if (value > dest) {
         value -= plusSpeed;
         if (value < dest) {
            value = dest;
         }
      } else if (value < dest) {
         value += plusSpeed;
         if (value > dest) {
            value = dest;
         }
      }

      return value;
   }

   public static double nearValue(double value, double dest, double mulSpeed, double plusSpeed) {
      value = value + (dest - value) * mulSpeed;
      if (value > dest) {
         value -= plusSpeed;
         if (value < dest) {
            value = dest;
         }
      } else if (value < dest) {
         value += plusSpeed;
         if (value > dest) {
            value = dest;
         }
      }

      return value;
   }

   public static int randomLinearNumber(EaglercraftRandom random, float number) {
      return MathHelper.floor(number) + (random.nextFloat() < number % 1.0F ? 1 : 0);
   }

   public static float zeroToOne(float value, float min, float max) {
      return clamp(0.0F, (value - min) / (max - min), 1.0F);
   }

   public static double zeroToOne(double value, double min, double max) {
      return clamp(0.0D, (value - min) / (max - min), 1.0D);
   }
}
