package jaredbgreat.dldungeons.util.math;

import java.util.Random;

public class Vec3D {
   private static final double P2 = 6.283185307179586D;
   double x;
   double y;
   double z;

   public Vec3D(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vec3D(Random rand) {
      this.x = rand.nextDouble() * 2.0D - 1.0D;
      this.y = rand.nextDouble() * 2.0D - 1.0D;
      this.z = rand.nextDouble() * 2.0D - 1.0D;
   }

   public Vec3D(SpatialHash random, int px, int py, int pz, int pt) {
      this.x = random.doubleFor(px, py, pz, pt) * 2.0D - 1.0D;
      this.y = random.doubleFor(px, py, pz, pt + 1) * 2.0D - 1.0D;
      this.z = random.doubleFor(px, py, pz, pt + 2) * 2.0D - 1.0D;
   }

   public static double dot(Vec3D a, Vec3D b) {
      return a.x * b.x + a.y * b.y + a.z * b.z;
   }
}
