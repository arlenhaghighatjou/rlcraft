package ivorius.reccomplex.utils.scale;

import ivorius.ivtoolkit.gui.FloatRange;

public class Scales {
   private static Scale none = new Scale() {
      public float in(float val) {
         return val;
      }

      public float out(float val) {
         return val;
      }
   };

   public Scales() {
   }

   public static Scale none() {
      return none;
   }

   public static Scale reverse(final Scale a) {
      return new Scale() {
         public float in(float val) {
            return a.out(val);
         }

         public float out(float val) {
            return a.in(val);
         }
      };
   }

   public static Scale combine(final Scale a, final Scale b) {
      return new Scale() {
         public float in(float val) {
            return b.in(a.in(val));
         }

         public float out(float val) {
            return a.out(b.out(val));
         }
      };
   }

   public static Scale combine(final Scale... scales) {
      return new Scale() {
         public float in(float val) {
            for(Scale scale : scales) {
               val = scale.in(val);
            }

            return val;
         }

         public float out(float val) {
            for(int i = scales.length - 1; i >= 0; --i) {
               val = scales[i].out(val);
            }

            return val;
         }
      };
   }

   public static Scale shift(final float shift) {
      return new Scale() {
         public float in(float val) {
            return val + shift;
         }

         public float out(float val) {
            return val - shift;
         }
      };
   }

   public static Scale linear(final float covariant) {
      return new Scale() {
         public float in(float val) {
            return val * covariant;
         }

         public float out(float val) {
            return val / covariant;
         }
      };
   }

   public static Scale pow(final float pow) {
      return new Scale() {
         public float in(float val) {
            return (float)Math.pow((double)val, (double)pow);
         }

         public float out(float val) {
            return (float)Math.pow((double)val, (double)(1.0F / pow));
         }
      };
   }

   public static Scale exponential(final float base) {
      return new Scale() {
         public float in(float val) {
            return (float)Math.pow((double)base, (double)val);
         }

         public float out(float val) {
            return (float)(Math.log((double)val) / Math.log((double)base));
         }
      };
   }

   public static FloatRange in(Scale scale, FloatRange range) {
      return new FloatRange(scale.in(range.getMin()), scale.in(range.getMax()));
   }

   public static FloatRange out(Scale scale, FloatRange range) {
      return new FloatRange(scale.out(range.getMin()), scale.out(range.getMax()));
   }
}
