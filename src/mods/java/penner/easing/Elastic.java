package penner.easing;

public class Elastic {
   public Elastic() {
   }

   public static float easeIn(float t, float b, float c, float d) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / d) == 1.0F) {
         return b + c;
      } else {
         float p = d * 0.3F;
         float s = p / 4.0F;
         return -(c * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p))) + b;
      }
   }

   public static float easeIn(float t, float b, float c, float d, float a, float p) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / d) == 1.0F) {
         return b + c;
      } else {
         float s;
         if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0F;
         } else {
            s = p / 6.2831855F * (float)Math.asin((double)(c / a));
         }

         return -(a * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)(t * d - s) * 6.283185307179586D / (double)p)) + b;
      }
   }

   public static float easeOut(float t, float b, float c, float d) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / d) == 1.0F) {
         return b + c;
      } else {
         float p = d * 0.3F;
         float s = p / 4.0F;
         return c * (float)Math.pow(2.0D, (double)(-10.0F * t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + c + b;
      }
   }

   public static float easeOut(float t, float b, float c, float d, float a, float p) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / d) == 1.0F) {
         return b + c;
      } else {
         float s;
         if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0F;
         } else {
            s = p / 6.2831855F * (float)Math.asin((double)(c / a));
         }

         return a * (float)Math.pow(2.0D, (double)(-10.0F * t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + c + b;
      }
   }

   public static float easeInOut(float t, float b, float c, float d) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / (d / 2.0F)) == 2.0F) {
         return b + c;
      } else {
         float p = d * 0.45000002F;
         float s = p / 4.0F;
         return t < 1.0F ? -0.5F * c * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + b : c * (float)Math.pow(2.0D, (double)(-10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) * 0.5F + c + b;
      }
   }

   public static float easeInOut(float t, float b, float c, float d, float a, float p) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / (d / 2.0F)) == 2.0F) {
         return b + c;
      } else {
         float s;
         if (a < Math.abs(c)) {
            a = c;
            s = p / 4.0F;
         } else {
            s = p / 6.2831855F * (float)Math.asin((double)(c / a));
         }

         return t < 1.0F ? -0.5F * a * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) + b : a * (float)Math.pow(2.0D, (double)(-10.0F * --t)) * (float)Math.sin((double)((t * d - s) * 6.2831855F / p)) * 0.5F + c + b;
      }
   }
}
