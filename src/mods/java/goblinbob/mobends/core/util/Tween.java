package goblinbob.mobends.core.util;

public class Tween {
   public Tween() {
   }

   public static double easeIn(double a, double power) {
      return Math.pow(a, power);
   }

   public static double easeOut(double a, double power) {
      return 1.0D - Math.pow(1.0D - a, power);
   }

   public static double easeInOut(double a, double power) {
      if (a < 0.5D) {
         a = a * 2.0D;
         a = Math.pow(a, power);
         a = a / 2.0D;
      } else {
         a = 1.0D - a;
         a = a * 2.0D;
         a = Math.pow(a, power);
         a = a / 2.0D;
         a = 1.0D - a;
      }

      return a;
   }
}
