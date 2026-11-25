package com.TominoCZ.FBP.util;

public class FBPMathUtil {
   public FBPMathUtil() {
   }

   public static double add(double d, double add) {
      return d < 0.0D ? d - add : d + add;
   }

   public static double round(double d, int decimals) {
      int i = (int)Math.round(d * Math.pow(10.0D, (double)decimals));
      return (double)i / Math.pow(10.0D, (double)decimals);
   }
}
