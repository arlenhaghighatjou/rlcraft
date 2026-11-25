package goblinbob.mobends.core.math.physics;

import goblinbob.mobends.core.math.vector.IVec3fRead;
import goblinbob.mobends.core.math.vector.Vec3f;
import goblinbob.mobends.core.math.vector.VectorUtils;
import javax.annotation.Nullable;

public class Physics {
   public Physics() {
   }

   @Nullable
   public static RayHitInfo intersect(Ray ray, Plane plane) {
      IVec3fRead rayOrigin = ray.getPosition();
      IVec3fRead rayDirection = ray.getDirection();
      IVec3fRead planeOrigin = plane.getPosition();
      IVec3fRead planeNormal = plane.getNormal();
      Vec3f rayToPlane = VectorUtils.subtract(planeOrigin, rayOrigin);
      float rayToPlaneDot = VectorUtils.dot(rayToPlane, planeNormal);
      float rayToEndDot = VectorUtils.dot(rayDirection, planeNormal);
      if (rayToEndDot != 0.0F) {
         float ratio = rayToPlaneDot / rayToEndDot;
         if (ratio > 0.0F) {
            return new RayHitInfo(rayOrigin.getX() + rayDirection.getX() * ratio, rayOrigin.getY() + rayDirection.getY() * ratio, rayOrigin.getZ() + rayDirection.getZ() * ratio);
         }
      }

      return null;
   }

   public static RayHitInfo intersect(Ray ray, AABBox box) {
      float rayX = ray.position.getX();
      float rayY = ray.position.getY();
      float rayZ = ray.position.getZ();
      float dirX = ray.direction.getX();
      float dirY = ray.direction.getY();
      float dirZ = ray.direction.getZ();
      float dirInvX = 1.0F / dirX;
      float dirInvY = 1.0F / dirY;
      float dirInvZ = 1.0F / dirZ;
      float tMin = ((dirInvX < 0.0F ? box.max.getX() : box.min.getX()) - rayX) * dirInvX;
      float tMax = ((dirInvX < 0.0F ? box.min.getX() : box.max.getX()) - rayX) * dirInvX;
      float min = ((dirInvY < 0.0F ? box.max.getY() : box.min.getY()) - rayY) * dirInvY;
      float max = ((dirInvY < 0.0F ? box.min.getY() : box.max.getY()) - rayY) * dirInvY;
      if (max >= tMin && min <= tMax) {
         if (min > tMin) {
            tMin = min;
         }

         if (max < tMax) {
            tMax = max;
         }

         min = ((dirInvZ < 0.0F ? box.max.getZ() : box.min.getZ()) - rayZ) * dirInvZ;
         max = ((dirInvZ < 0.0F ? box.min.getZ() : box.max.getZ()) - rayZ) * dirInvZ;
         if (max >= tMin && min <= tMax) {
            if (min > tMin) {
               tMin = min;
            }

            if (max < tMax) {
               ;
            }

            return new RayHitInfo(rayX + dirX * tMin, rayY + dirY * tMin, rayZ + dirZ * tMin);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static RayHitInfo intersect(Ray ray, OBBox box) {
      double[] fields = box.transform.getFields();
      float rayX = ray.position.getX();
      float rayY = ray.position.getY();
      float rayZ = ray.position.getZ();
      float boxX = (float)fields[12];
      float boxY = (float)fields[13];
      float boxZ = (float)fields[14];
      Vec3f right = new Vec3f((float)fields[0], (float)fields[1], (float)fields[2]);
      Vec3f up = new Vec3f((float)fields[4], (float)fields[5], (float)fields[6]);
      Vec3f forward = new Vec3f((float)fields[8], (float)fields[9], (float)fields[10]);
      float scaleX = right.length();
      float scaleY = up.length();
      float scaleZ = forward.length();
      VectorUtils.normalize(right);
      VectorUtils.normalize(up);
      VectorUtils.normalize(forward);
      Vec3f rayToBox = new Vec3f(boxX - rayX, boxY - rayY, boxZ - rayZ);
      float tMin = 0.0F;
      float tMax = Float.POSITIVE_INFINITY;
      float nomLen = VectorUtils.dot(right, rayToBox);
      float denomLen = VectorUtils.dot(ray.direction, right);
      if ((double)Math.abs(denomLen) > 1.0E-5D) {
         float min = (nomLen + box.min.getX() * scaleX) / denomLen;
         float max = (nomLen + box.max.getX() * scaleX) / denomLen;
         if (min > max) {
            float t = min;
            min = max;
            max = t;
         }

         if (max < tMax) {
            tMax = max;
         }

         if (min > tMin) {
            tMin = min;
         }

         if (tMax < tMin) {
            return null;
         }
      } else if (-nomLen + box.min.getX() > 0.0F || -nomLen + box.max.getX() < 0.0F) {
         return null;
      }

      nomLen = VectorUtils.dot(up, rayToBox);
      denomLen = VectorUtils.dot(ray.direction, up);
      if ((double)Math.abs(denomLen) > 1.0E-5D) {
         float min = (nomLen + box.min.getY() * scaleY) / denomLen;
         float max = (nomLen + box.max.getY() * scaleY) / denomLen;
         if (min > max) {
            float t = min;
            min = max;
            max = t;
         }

         if (max < tMax) {
            tMax = max;
         }

         if (min > tMin) {
            tMin = min;
         }

         if (tMax < tMin) {
            return null;
         }
      } else if (-nomLen + box.min.getY() > 0.0F || -nomLen + box.max.getY() < 0.0F) {
         return null;
      }

      nomLen = VectorUtils.dot(forward, rayToBox);
      denomLen = VectorUtils.dot(ray.direction, forward);
      if ((double)Math.abs(denomLen) > 1.0E-5D) {
         float min = (nomLen + box.min.getZ() * scaleZ) / denomLen;
         float max = (nomLen + box.max.getZ() * scaleZ) / denomLen;
         if (min > max) {
            float t = min;
            min = max;
            max = t;
         }

         if (max < tMax) {
            tMax = max;
         }

         if (min > tMin) {
            tMin = min;
         }

         if (tMax < tMin) {
            return null;
         }
      } else if (-nomLen + box.min.getZ() > 0.0F || -nomLen + box.max.getZ() < 0.0F) {
         return null;
      }

      return new RayHitInfo(rayX + ray.direction.getX() * tMin, rayY + ray.direction.getY() * tMin, rayZ + ray.direction.getZ() * tMin);
   }
}
