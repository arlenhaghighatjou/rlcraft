package com.creativemd.creativecore.common.utils.math.geo;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.vec.VectorFan;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class NormalPlane {
   public final Vector3f normal;
   public final Vector3f origin;

   public NormalPlane(Vector3f origin, Vector3f normal) {
      this.origin = origin;
      this.normal = new Vector3f(normal);
      this.normal.normalize();
   }

   public NormalPlane(Axis axis, float value, EnumFacing facing) {
      this.origin = new Vector3f();
      VectorUtils.set(this.origin, value, axis);
      this.normal = new Vector3f();
      VectorUtils.set(this.normal, (float)facing.getAxisDirection().getOffset(), facing.getAxis());
   }

   public Boolean isInFront(Vector3f vec) {
      Vector3f temp = new Vector3f(vec);
      temp.sub(this.origin);
      float result = this.normal.dot(temp);
      if (result < 0.0F) {
         if (result > -1.0E-4F) {
            return null;
         }
      } else if (result < 1.0E-4F) {
         return null;
      }

      return result > 0.0F;
   }

   public boolean cuts(VectorFan strip) {
      boolean front = false;
      boolean back = false;

      for(int i = 0; i < strip.count(); ++i) {
         Boolean result = this.isInFront(strip.get(i));
         if (result == null) {
            return true;
         }

         if (result.booleanValue()) {
            front = true;
         }

         if (!result.booleanValue()) {
            back = true;
         }

         if (front && back) {
            return true;
         }
      }

      return false;
   }

   public Vector3f intersect(Vector3f start, Vector3f end) {
      Vector3f lineDirection = new Vector3f(end);
      lineDirection.sub(start);
      lineDirection.normalize();
      if (this.normal.dot(lineDirection) == 0.0F) {
         return null;
      } else {
         float t = (this.normal.dot(this.origin) - this.normal.dot(start)) / this.normal.dot(lineDirection);
         Vector3f point = new Vector3f(lineDirection);
         point.scale(t);
         point.add(start);
         return point;
      }
   }

   public Vector3f intersect(Ray3f ray) {
      if (this.normal.dot(ray.direction) == 0.0F) {
         return null;
      } else {
         float t = (this.normal.dot(this.origin) - this.normal.dot(ray.origin)) / this.normal.dot(ray.direction);
         Vector3f point = new Vector3f(ray.direction);
         point.scale(t);
         point.add(ray.origin);
         return point;
      }
   }

   public Float project(Axis one, Axis two, Axis axis, float valueOne, float valueTwo) {
      Vector3f lineOrigin = new Vector3f();
      VectorUtils.set(lineOrigin, valueOne, one);
      VectorUtils.set(lineOrigin, valueTwo, two);
      Vector3f lineDirection = new Vector3f();
      VectorUtils.set(lineDirection, 1.0F, axis);
      if (this.normal.dot(lineDirection) == 0.0F) {
         return null;
      } else {
         float t = (this.normal.dot(this.origin) - this.normal.dot(lineOrigin)) / this.normal.dot(lineDirection);
         return VectorUtils.get(axis, lineOrigin) + VectorUtils.get(axis, lineDirection) * t;
      }
   }

   public String toString() {
      return "[o:" + this.origin + ",n:" + this.normal + "]";
   }
}
