package com.ferreusveritas.dynamictrees.systems.poissondisc;

public class PoissonDiscMathHelper {
   public PoissonDiscMathHelper() {
   }

   public static int wrap(int i, int max) {
      while(i < 0) {
         i += max;
      }

      return i % max;
   }

   public static double wrapAngle(double angle) {
      double TwoPi = 6.283185307179586D;
      angle = angle % 6.283185307179586D;
      return angle + (angle < 0.0D ? 6.283185307179586D : 0.0D);
   }

   public static float radiansToTurns(double angle) {
      return (float)(wrapAngle(angle) / 6.283185307179586D);
   }

   public static double deltaAngle(double alpha, double beta) {
      double phi = Math.abs(beta - alpha) % 6.283185307179586D;
      double distance = phi > 3.141592653589793D ? 6.283185307179586D - phi : phi;
      return distance;
   }
}
