package vazkii.aurelienribon.tweenengine.equations;

import vazkii.aurelienribon.tweenengine.TweenEquation;

public abstract class Sine extends TweenEquation {
   private static final float PI = 3.1415927F;
   public static final Sine IN = new Sine() {
      public final float compute(float t) {
         return (float)(-Math.cos((double)(t * 1.5707964F))) + 1.0F;
      }

      public String toString() {
         return "Sine.IN";
      }
   };
   public static final Sine OUT = new Sine() {
      public final float compute(float t) {
         return (float)Math.sin((double)(t * 1.5707964F));
      }

      public String toString() {
         return "Sine.OUT";
      }
   };
   public static final Sine INOUT = new Sine() {
      public final float compute(float t) {
         return -0.5F * ((float)Math.cos((double)(3.1415927F * t)) - 1.0F);
      }

      public String toString() {
         return "Sine.INOUT";
      }
   };

   public Sine() {
   }
}
