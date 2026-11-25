package penner.easing;

public class Quart {
   public Quart() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      float var4;
      return c * (var4 = t / d) * var4 * var4 * var4 + b;
   }

   public static float easeOut(float t, float b, float c, float d) {
      float var4;
      return -c * ((var4 = t / d - 1.0F) * var4 * var4 * var4 - 1.0F) + b;
   }

   public static float easeInOut(float t, float b, float c, float d) {
      float var4;
      float var5;
      return (var4 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var4 * var4 * var4 * var4 + b : -c / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 * var5 - 2.0F) + b;
   }
}
