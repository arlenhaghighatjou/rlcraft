package penner.easing;

public class Back {
   public Back() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      float s = 1.70158F;
      float var5;
      return c * (var5 = t / d) * var5 * ((s + 1.0F) * var5 - s) + b;
   }

   public static float easeIn(float t, float b, float c, float d, float s) {
      float var5;
      return c * (var5 = t / d) * var5 * ((s + 1.0F) * var5 - s) + b;
   }

   public static float easeOut(float t, float b, float c, float d) {
      float s = 1.70158F;
      float var5;
      return c * ((var5 = t / d - 1.0F) * var5 * ((s + 1.0F) * var5 + s) + 1.0F) + b;
   }

   public static float easeOut(float t, float b, float c, float d, float s) {
      float var5;
      return c * ((var5 = t / d - 1.0F) * var5 * ((s + 1.0F) * var5 + s) + 1.0F) + b;
   }

   public static float easeInOut(float t, float b, float c, float d) {
      float s = 1.70158F;
      float var5;
      float var6;
      float var7;
      float var8;
      return (var5 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var5 * var5 * (((var7 = s * 1.525F) + 1.0F) * var5 - var7) + b : c / 2.0F * ((var6 = var5 - 2.0F) * var6 * (((var8 = s * 1.525F) + 1.0F) * var6 + var8) + 2.0F) + b;
   }

   public static float easeInOut(float t, float b, float c, float d, float s) {
      float var5;
      float var6;
      float var7;
      float var8;
      return (var5 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var5 * var5 * (((var7 = s * 1.525F) + 1.0F) * var5 - var7) + b : c / 2.0F * ((var6 = var5 - 2.0F) * var6 * (((var8 = s * 1.525F) + 1.0F) * var6 + var8) + 2.0F) + b;
   }
}
