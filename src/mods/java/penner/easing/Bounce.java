package penner.easing;

public class Bounce {
   public Bounce() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      return c - easeOut(d - t, 0.0F, c, d) + b;
   }

   public static float easeOut(float t, float b, float c, float d) {
      if ((t = t / d) < 0.36363637F) {
         return c * 7.5625F * t * t + b;
      } else if (t < 0.72727275F) {
         float var7;
         return c * (7.5625F * (var7 = t - 0.54545456F) * var7 + 0.75F) + b;
      } else {
         float var5;
         float var6;
         return (double)t < 0.9090909090909091D ? c * (7.5625F * (var5 = t - 0.8181818F) * var5 + 0.9375F) + b : c * (7.5625F * (var6 = t - 0.95454544F) * var6 + 0.984375F) + b;
      }
   }

   public static float easeInOut(float t, float b, float c, float d) {
      return t < d / 2.0F ? easeIn(t * 2.0F, 0.0F, c, d) * 0.5F + b : easeOut(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;
   }
}
