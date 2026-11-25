package jaredbgreat.dldungeons.util.math;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class Vec2D {
   private static final double P2 = 6.283185307179586D;
   double x;
   double y;

   public Vec2D(double x, double y) {
      this.x = x;
      this.y = y;
   }

   public Vec2D(EaglercraftRandom rand) {
      this.x = rand.nextDouble() * 2.0D - 1.0D;
      this.y = rand.nextDouble() * 2.0D - 1.0D;
   }

   public Vec2D(SpatialHash random, int px, int py, int pz) {
      this.x = random.doubleFor(px, py, pz) * 2.0D - 1.0D;
      this.y = random.doubleFor(px, py, pz + 1) * 2.0D - 1.0D;
   }

   public static double dot(Vec2D a, Vec2D b) {
      return a.x * b.x + a.y * b.y;
   }
}
