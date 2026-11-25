package meldexun.matrixutil;

import java.util.function.DoubleUnaryOperator;

public class MathUtil {
   private static DoubleUnaryOperator SIN = Math::sin;
   private static DoubleUnaryOperator COS = Math::cos;

   public MathUtil() {
   }

   public static void setSinFunc(DoubleUnaryOperator sinFunc) {
      if (sinFunc == null) {
         throw new IllegalArgumentException();
      } else {
         SIN = sinFunc;
      }
   }

   public static void setCosFunc(DoubleUnaryOperator cosFunc) {
      if (cosFunc == null) {
         throw new IllegalArgumentException();
      } else {
         COS = cosFunc;
      }
   }

   public static double sin(double a) {
      return SIN.applyAsDouble(a);
   }

   public static double cos(double a) {
      return COS.applyAsDouble(a);
   }
}
