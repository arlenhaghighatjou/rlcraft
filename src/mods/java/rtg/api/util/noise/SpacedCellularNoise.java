package rtg.api.util.noise;

import java.awt.Point;
import java.awt.geom.Point2D.Double;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import rtg.api.util.LimitedArrayCacheMap;

public class SpacedCellularNoise implements CellularNoise {
   private static final int totalPoints = 100;
   private static final int pointsPerTorus = 25;
   private static final double minDistanceSq = 0.005D;
   private final Map<Point, Double[]> cache = new LimitedArrayCacheMap<Point, Double[]>(256);
   private final Double[] allPoints;
   private final long xSeed;
   private final long ySeed;

   public SpacedCellularNoise(long xSeed) {
      this.xSeed = xSeed;
      this.ySeed = (new EaglercraftRandom(xSeed)).nextLong();
      this.allPoints = new Double[100];
      this.setPoints();
   }

   private static double minimalToroidalDistanceSquared(Double point, Double[] existing, int count) {
      double result = 1.0D;

      for(int i = 0; i < count; ++i) {
         double distance = toroidalDistanceSquared(point, existing[i]);
         if (distance < result) {
            result = distance;
         }
      }

      return result;
   }

   private static double toroidalDistanceSquared(Double first, Double second) {
      double xDist = Math.abs(first.x - second.x);
      if (xDist > 0.5D) {
         xDist = 1.0D - xDist;
      }

      double yDist = Math.abs(first.y - second.y);
      if (yDist > 0.5D) {
         yDist = 1.0D - yDist;
      }

      return Math.pow(xDist, 2.0D) + Math.pow(yDist, 2.0D);
   }

   public VoronoiResult eval2D(double x, double y) {
      x = x / 5.0D;
      y = y / 5.0D;
      int xInt = x > 0.0D ? (int)x : (int)x - 1;
      int yInt = y > 0.0D ? (int)y : (int)y - 1;
      VoronoiResult result = new VoronoiResult();
      Point square = new Point(xInt, yInt);
      result.evaluate(this.areaPoints(square), x, y);
      double distance = y - (double)yInt;
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt, yInt - 1)), x, y);
      }

      distance = x - (double)xInt;
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt - 1, yInt)), x, y);
      }

      distance = (double)yInt - y + 1.0D;
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt, yInt + 1)), x, y);
      }

      distance = (double)xInt - x + 1.0D;
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt + 1, yInt)), x, y);
      }

      distance = Math.min(y - (double)yInt, x - (double)xInt);
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt - 1, yInt - 1)), x, y);
      }

      distance = Math.min((double)yInt - y + 1.0D, x - (double)xInt);
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt - 1, yInt + 1)), x, y);
      }

      distance = Math.min((double)yInt - y + 1.0D, (double)xInt - x + 1.0D);
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt + 1, yInt + 1)), x, y);
      }

      distance = Math.min(y - (double)yInt, (double)xInt - x + 1.0D);
      if (distance != result.getNextDistance()) {
         result.evaluate(this.areaPoints(new Point(xInt + 1, yInt - 1)), x, y);
      }

      return result;
   }

   private Double[] areaPoints(Point area) {
      Double[] ret;
      return (ret = (Double[])this.cache.get(area)) != null ? ret : this.generatedAreaPoints(area);
   }

   private Double[] generatedAreaPoints(Point area) {
      EaglercraftRandom random = new EaglercraftRandom((long)area.hashCode());
      boolean[] used = new boolean[100];
      Double[] result = new Double[25];
      int index = 0;

      for(int i = 0; i < 25; ++i) {
         int advance = random.nextInt(100);

         for(int j = 0; j < advance; ++j) {
            while(used[index]) {
               ++index;
               if (index >= 100) {
                  index = 0;
               }
            }
         }

         result[i] = new Double(this.allPoints[index].getX() + area.getX(), this.allPoints[index].getY() + area.getY());
         used[i] = true;
      }

      this.cache.put(area, result);
      return result;
   }

   private void setPoints() {
      EaglercraftRandom xRandom = new EaglercraftRandom(this.xSeed);
      EaglercraftRandom yRandom = new EaglercraftRandom(this.ySeed);
      int i = 0;

      while(i < 100) {
         Double newPoint = new Double(xRandom.nextDouble(), yRandom.nextDouble());
         if (minimalToroidalDistanceSquared(newPoint, this.allPoints, i) >= 0.005D) {
            this.allPoints[i++] = newPoint;
         }
      }

   }
}
