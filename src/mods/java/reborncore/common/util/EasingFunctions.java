package reborncore.common.util;

public class EasingFunctions {
   public EasingFunctions() {
   }

   public double linear(double t) {
      return t;
   }

   public double easeInQuad(double t) {
      return t * t;
   }

   public double easeOutQuad(double t) {
      return t * (2.0D - t);
   }

   public double easeInOutQuad(double t) {
      return t < 0.5D ? 2.0D * t * t : -1.0D + (4.0D - 2.0D * t) * t;
   }

   public double easeInCubic(double t) {
      return t * t * t;
   }

   public double easeOutCubic(double t) {
      return --t * t * t + 1.0D;
   }

   public double easeInOutCubic(double t) {
      return t < 0.5D ? 4.0D * t * t * t : (t - 1.0D) * (2.0D * t - 2.0D) * (2.0D * t - 2.0D) + 1.0D;
   }

   public double easeInQuart(double t) {
      return t * t * t * t;
   }

   public double easeOutQuart(double t) {
      return 1.0D - --t * t * t * t;
   }

   public double easeInOutQuart(double t) {
      return t < 0.5D ? 8.0D * t * t * t * t : 1.0D - 8.0D * --t * t * t * t;
   }

   public double easeInQuint(double t) {
      return t * t * t * t * t;
   }

   public double easeOutQuint(double t) {
      return 1.0D + --t * t * t * t * t;
   }

   public double easeInOutQuint(double t) {
      return t < 0.5D ? 16.0D * t * t * t * t * t : 1.0D + 16.0D * --t * t * t * t * t;
   }
}
