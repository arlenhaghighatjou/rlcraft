package rtg.api.util.noise;

import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;

public class VoronoiResult {
   private double shortestDistance = 3.2E7D;
   private double nextDistance = 3.2E7D;
   private double closestX = 3.2E7D;
   private double closestZ = 3.2E7D;

   public VoronoiResult() {
   }

   public final double getShortestDistance() {
      return this.shortestDistance;
   }

   public final double getNextDistance() {
      return this.nextDistance;
   }

   public final double borderValue() {
      return this.shortestDistance / this.nextDistance;
   }

   public final double interiorValue() {
      return (this.nextDistance - this.shortestDistance) / this.nextDistance;
   }

   public final Float toLength(Float toMap, float radius) {
      double distance = toMap.distance(this.closestX, this.closestZ);
      double xDist = toMap.getX() - this.closestX;
      double zDist = toMap.getY() - this.closestZ;
      xDist = xDist * ((double)radius / distance);
      zDist = zDist * ((double)radius / distance);
      return new Float((float)(this.closestX + xDist), (float)(this.closestZ + zDist));
   }

   void evaluate(Double[] points, double x, double z) {
      for(Double point : points) {
         double distance = point.distanceSq(x, z);
         if (distance < this.shortestDistance) {
            this.nextDistance = this.shortestDistance;
            this.shortestDistance = distance;
            this.closestX = point.getX();
            this.closestZ = point.getY();
         } else if (distance < this.nextDistance) {
            this.nextDistance = distance;
         }
      }

   }
}
