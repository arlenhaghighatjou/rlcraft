package com.fantasticsource.tools;

import net.minecraft.util.text.TextFormatting;

public class Smoothing {
   public static final int LINEAR = 0;
   public static final int SINUOUS = 1;

   public Smoothing() {
   }

   public static double interpolate(double start, double end, double normalizedProgress, int type) {
      switch(type) {
      case 0:
      default:
         return start + (end - start) * normalizedProgress;
      case 1:
         return start + (end - start) * (1.0D - TrigLookupTable.TRIG_TABLE_1024.cos(3.141592653589793D * normalizedProgress)) / 2.0D;
      }
   }

   public static double balanceAlpha(int layers, double alpha) {
      return 1.0D - Math.pow(1.0D - alpha, 1.0D / (double)layers);
   }

   public static void balanceAlphaTest() {
      for(double d : new double[]{0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 0.6D, 0.7D, 0.8D, 0.9D, 1.0D}) {
         for(int i = 1; i < 11; ++i) {
            double la = balanceAlpha(i, d);
            double da = 1.0D - Math.pow(1.0D - la, (double)i);
            System.out.println(TextFormatting.AQUA + "" + i + ", " + d + ", " + la + ", " + da);
         }
      }

   }

   public static void interpolationTest(int type) {
      String result = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

      for(double d = 0.0D; d <= 1.0D; d += 0.01D) {
         if (1.0D - d < 0.002D) {
            d = 1.0D;
         }

         System.out.println(result.substring((int)((double)result.length() - (double)result.length() * interpolate(0.0D, 1.0D, d, type))));
      }

   }
}
