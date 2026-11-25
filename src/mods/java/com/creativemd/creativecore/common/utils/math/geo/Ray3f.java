package com.creativemd.creativecore.common.utils.math.geo;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class Ray3f {
   public final Vector3f origin;
   public final Vector3f direction;

   public Ray3f(Vector3f start, Vector3f end) {
      this.origin = start;
      this.direction = new Vector3f(end);
      this.direction.sub(this.origin);
      this.direction.normalize();
   }

   public Ray3f(Vector3f origin, EnumFacing facing) {
      this.origin = origin;
      this.direction = new Vector3f();
      VectorUtils.set(this.direction, (float)facing.getAxisDirection().getOffset(), facing.getAxis());
   }

   public void set(float x, float y, float z, float x2, float y2, float z2) {
      this.origin.set(x, y, z);
      this.direction.set(x2, y2, z2);
      this.direction.sub(this.origin);
   }

   public double getT(Axis axis, double value) {
      return (value - (double)VectorUtils.get(axis, this.origin)) / (double)VectorUtils.get(axis, this.direction);
   }

   public Vector3f get(float t) {
      return new Vector3f(this.origin.x + this.direction.x * t, this.origin.y + this.direction.y * t, this.origin.z + this.direction.z * t);
   }
}
