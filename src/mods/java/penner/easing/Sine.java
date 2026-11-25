package penner.easing;

public class Sine {
   public Sine() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      return -c * (float)Math.cos((double)(t / d) * 1.5707963267948966D) + c + b;
   }

   public static float easeOut(float t, float b, float c, float d) {
      return c * (float)Math.sin((double)(t / d) * 1.5707963267948966D) + b;
   }

   public static float easeInOut(float t, float b, float c, float d) {
      return -c / 2.0F * ((float)Math.cos(3.141592653589793D * (double)t / (double)d) - 1.0F) + b;
   }
}
