package penner.easing;

public class Expo {
   public Expo() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      return t == 0.0F ? b : c * (float)Math.pow(2.0D, (double)(10.0F * (t / d - 1.0F))) + b;
   }

   public static float easeOut(float t, float b, float c, float d) {
      return t == d ? b + c : c * (-((float)Math.pow(2.0D, (double)(-10.0F * t / d))) + 1.0F) + b;
   }

   public static float easeInOut(float t, float b, float c, float d) {
      if (t == 0.0F) {
         return b;
      } else if (t == d) {
         return b + c;
      } else {
         float var4;
         return (var4 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * (float)Math.pow(2.0D, (double)(10.0F * (var4 - 1.0F))) + b : c / 2.0F * (-((float)Math.pow(2.0D, (double)(-10.0F * --var4))) + 2.0F) + b;
      }
   }
}
