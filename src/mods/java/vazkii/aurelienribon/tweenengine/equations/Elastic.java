package vazkii.aurelienribon.tweenengine.equations;

import vazkii.aurelienribon.tweenengine.TweenEquation;

public abstract class Elastic extends TweenEquation {
   private static final float PI = 3.1415927F;
   public static final Elastic IN = new Elastic() {
      public final float compute(float t) {
         float a = this.param_a;
         float p = this.param_p;
         if (t == 0.0F) {
            return 0.0F;
         } else if (t == 1.0F) {
            return 1.0F;
         } else {
            if (!this.setP) {
               p = 0.3F;
            }

            float s;
            if (this.setA && a >= 1.0F) {
               s = p / 6.2831855F * (float)Math.asin((double)(1.0F / a));
            } else {
               a = 1.0F;
               s = p / 4.0F;
            }

            return -(a * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t - s) * 6.2831855F / p)));
         }
      }

      public String toString() {
         return "Elastic.IN";
      }
   };
   public static final Elastic OUT = new Elastic() {
      public final float compute(float t) {
         float a = this.param_a;
         float p = this.param_p;
         if (t == 0.0F) {
            return 0.0F;
         } else if (t == 1.0F) {
            return 1.0F;
         } else {
            if (!this.setP) {
               p = 0.3F;
            }

            float s;
            if (this.setA && a >= 1.0F) {
               s = p / 6.2831855F * (float)Math.asin((double)(1.0F / a));
            } else {
               a = 1.0F;
               s = p / 4.0F;
            }

            return a * (float)Math.pow(2.0D, (double)(-10.0F * t)) * (float)Math.sin((double)((t - s) * 6.2831855F / p)) + 1.0F;
         }
      }

      public String toString() {
         return "Elastic.OUT";
      }
   };
   public static final Elastic INOUT = new Elastic() {
      public final float compute(float t) {
         float a = this.param_a;
         float p = this.param_p;
         if (t == 0.0F) {
            return 0.0F;
         } else if ((t = t * 2.0F) == 2.0F) {
            return 1.0F;
         } else {
            if (!this.setP) {
               p = 0.45000002F;
            }

            float s;
            if (this.setA && a >= 1.0F) {
               s = p / 6.2831855F * (float)Math.asin((double)(1.0F / a));
            } else {
               a = 1.0F;
               s = p / 4.0F;
            }

            return t < 1.0F ? -0.5F * a * (float)Math.pow(2.0D, (double)(10.0F * --t)) * (float)Math.sin((double)((t - s) * 6.2831855F / p)) : a * (float)Math.pow(2.0D, (double)(-10.0F * --t)) * (float)Math.sin((double)((t - s) * 6.2831855F / p)) * 0.5F + 1.0F;
         }
      }

      public String toString() {
         return "Elastic.INOUT";
      }
   };
   protected float param_a;
   protected float param_p;
   protected boolean setA = false;
   protected boolean setP = false;

   public Elastic() {
   }

   public Elastic a(float a) {
      this.param_a = a;
      this.setA = true;
      return this;
   }

   public Elastic p(float p) {
      this.param_p = p;
      this.setP = true;
      return this;
   }
}
