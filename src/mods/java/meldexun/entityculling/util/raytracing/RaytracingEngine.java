package meldexun.entityculling.util.raytracing;

import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import meldexun.entityculling.config.EntityCullingConfig;
import meldexun.entityculling.util.MathUtil;

public class RaytracingEngine {
   private double camX;
   private double camY;
   private double camZ;
   private int camBlockX;
   private int camBlockY;
   private int camBlockZ;
   private final IRaytracingCache resultCache = new RaytracingMapCache();
   private final IRaytracingCache opacityCache;
   private final Int2BoolTriFunction isOpaqueFunction;
   private final IntSupplier renderDistSupplier;

   public RaytracingEngine(int cacheSize, Int2BoolTriFunction isOpaqueFunction, IntSupplier renderDistSupplier) {
      this.opacityCache = new RaytracingArrayCache(cacheSize);
      this.isOpaqueFunction = isOpaqueFunction;
      this.renderDistSupplier = renderDistSupplier;
   }

   public void setup(double x, double y, double z) {
      this.camX = x;
      this.camY = y;
      this.camZ = z;
      this.camBlockX = MathUtil.floor(x);
      this.camBlockY = MathUtil.floor(y);
      this.camBlockZ = MathUtil.floor(z);
   }

   public void clearCache() {
      this.resultCache.clearCache();
      this.opacityCache.clearCache();
   }

   public boolean raytraceCachedThreshold(int endX, int endY, int endZ, double threshold) {
      return this.isTooFarAway((double)endX, (double)endY, (double)endZ) ? true : this.resultCache.getOrSetCachedValue(endX - this.camBlockX, endY - this.camBlockY, endZ - this.camBlockZ, () -> {
         return this.raytraceUncachedThreshold((double)endX, (double)endY, (double)endZ, threshold);
      });
   }

   public boolean raytraceCached(int endX, int endY, int endZ) {
      return this.isTooFarAway((double)endX, (double)endY, (double)endZ) ? true : this.resultCache.getOrSetCachedValue(endX - this.camBlockX, endY - this.camBlockY, endZ - this.camBlockZ, () -> {
         return this.raytraceUncached((double)endX, (double)endY, (double)endZ);
      });
   }

   public boolean raytraceUncachedThreshold(double endX, double endY, double endZ, double threshold) {
      return this.isTooFarAway(endX, endY, endZ) ? true : this.raytraceThreshold(this.camX, this.camY, this.camZ, endX, endY, endZ, threshold);
   }

   public boolean raytraceUncached(double endX, double endY, double endZ) {
      return this.isTooFarAway(endX, endY, endZ) ? true : this.raytrace(this.camX, this.camY, this.camZ, endX, endY, endZ);
   }

   private boolean isTooFarAway(double x, double y, double z) {
      double distSqr = EntityCullingConfig.raytraceDistanceCalculator.distSqr(this.camX, this.camY, this.camZ, x, y, z);
      double maxDistSqr = MathUtil.square(((double)(this.renderDistSupplier.getAsInt() << 4) + EntityCullingConfig.raytraceDistanceLimitAdder) * EntityCullingConfig.raytraceDistanceLimitMultiplier);
      return distSqr > maxDistSqr;
   }

   private boolean raytraceThreshold(double startX, double startY, double startZ, double endX, double endY, double endZ, double threshold) {
      if (threshold <= 0.0D) {
         return this.raytrace(startX, startY, startZ, endX, endY, endZ);
      } else {
         double dirX = endX - startX;
         double dirY = endY - startY;
         double dirZ = endZ - startZ;
         if (dirX * dirX + dirY * dirY + dirZ * dirZ <= threshold * threshold) {
            return true;
         } else {
            int x = MathUtil.floor(startX);
            int y = MathUtil.floor(startY);
            int z = MathUtil.floor(startZ);
            int incX = MathUtil.signum(dirX);
            int incY = MathUtil.signum(dirY);
            int incZ = MathUtil.signum(dirZ);
            double dx = incX == 0 ? Double.MAX_VALUE : (double)incX / dirX;
            double dy = incY == 0 ? Double.MAX_VALUE : (double)incY / dirY;
            double dz = incZ == 0 ? Double.MAX_VALUE : (double)incZ / dirZ;
            double percentX = dx * (incX > 0 ? 1.0D - MathUtil.frac(startX) : MathUtil.frac(startX));
            double percentY = dy * (incY > 0 ? 1.0D - MathUtil.frac(startY) : MathUtil.frac(startY));
            double percentZ = dz * (incZ > 0 ? 1.0D - MathUtil.frac(startZ) : MathUtil.frac(startZ));
            if (this.isOpaque(x, y, z)) {
               double d1 = Math.min(Math.min(Math.min(percentX, percentY), percentZ), 1.0D);
               double nextHitX = startX + dirX * d1;
               double nextHitY = startY + dirY * d1;
               double nextHitZ = startZ + dirZ * d1;
               threshold -= MathUtil.dist(startX, startY, startZ, nextHitX, nextHitY, nextHitZ);
               if (threshold <= 0.0D) {
                  return false;
               }
            }

            while(percentX <= 1.0D || percentY <= 1.0D || percentZ <= 1.0D) {
               Axis axis;
               if (percentX < percentY) {
                  if (percentX < percentZ) {
                     x += incX;
                     percentX += dx;
                     axis = Axis.X;
                  } else {
                     z += incZ;
                     percentZ += dz;
                     axis = Axis.Z;
                  }
               } else if (percentY < percentZ) {
                  y += incY;
                  percentY += dy;
                  axis = Axis.Y;
               } else {
                  z += incZ;
                  percentZ += dz;
                  axis = Axis.Z;
               }

               if (this.isOpaque(x, y, z)) {
                  double d = Math.min(axis != Axis.X ? (axis != Axis.Y ? percentZ - dz : percentY - dy) : percentX - dx, 1.0D);
                  double hitX = startX + dirX * d;
                  double hitY = startY + dirY * d;
                  double hitZ = startZ + dirZ * d;
                  double d1 = Math.min(Math.min(Math.min(percentX, percentY), percentZ), 1.0D);
                  double nextHitX = startX + dirX * d1;
                  double nextHitY = startY + dirY * d1;
                  double nextHitZ = startZ + dirZ * d1;
                  threshold -= MathUtil.dist(hitX, hitY, hitZ, nextHitX, nextHitY, nextHitZ);
                  if (threshold <= 0.0D) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private boolean raytrace(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      int x = MathUtil.floor(startX);
      int y = MathUtil.floor(startY);
      int z = MathUtil.floor(startZ);
      if (this.isOpaque(x, y, z)) {
         return false;
      } else {
         double dirX = endX - startX;
         double dirY = endY - startY;
         double dirZ = endZ - startZ;
         int incX = MathUtil.signum(dirX);
         int incY = MathUtil.signum(dirY);
         int incZ = MathUtil.signum(dirZ);
         double dx = incX == 0 ? Double.MAX_VALUE : (double)incX / dirX;
         double dy = incY == 0 ? Double.MAX_VALUE : (double)incY / dirY;
         double dz = incZ == 0 ? Double.MAX_VALUE : (double)incZ / dirZ;
         double percentX = dx * (incX > 0 ? 1.0D - MathUtil.frac(startX) : MathUtil.frac(startX));
         double percentY = dy * (incY > 0 ? 1.0D - MathUtil.frac(startY) : MathUtil.frac(startY));
         double percentZ = dz * (incZ > 0 ? 1.0D - MathUtil.frac(startZ) : MathUtil.frac(startZ));

         while(percentX <= 1.0D || percentY <= 1.0D || percentZ <= 1.0D) {
            if (percentX < percentY) {
               if (percentX < percentZ) {
                  x += incX;
                  percentX += dx;
               } else {
                  z += incZ;
                  percentZ += dz;
               }
            } else if (percentY < percentZ) {
               y += incY;
               percentY += dy;
            } else {
               z += incZ;
               percentZ += dz;
            }

            if (this.isOpaque(x, y, z)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isOpaque(int x, int y, int z) {
      return this.opacityCache.getOrSetCachedValue(x - this.camBlockX, y - this.camBlockY, z - this.camBlockZ, () -> {
         return this.isOpaqueFunction.applyAsBool(x, y, z);
      });
   }
}
