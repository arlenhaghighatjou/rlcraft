package com.creativemd.creativecore.common.utils.math.geo;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import javax.vecmath.Vector3d;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;

public class Ray3d {
   public final Vector3d origin;
   public final Vector3d direction;

   public Ray3d(Vector3d start, Vector3d end) {
      this.origin = start;
      this.direction = new Vector3d(end);
      this.direction.sub(this.origin);
      this.direction.normalize();
   }

   public Ray3d(Vector3d start, Vector3d end, boolean normalize) {
      this.origin = start;
      this.direction = new Vector3d(end);
      this.direction.sub(this.origin);
      if (normalize) {
         this.direction.normalize();
      }

   }

   public Ray3d(Vector3d origin, EnumFacing facing) {
      this.origin = origin;
      this.direction = new Vector3d();
      VectorUtils.set(this.direction, (double)facing.getAxisDirection().getOffset(), facing.getAxis());
   }

   public void set(double x, double y, double z, double x2, double y2, double z2) {
      this.origin.set(x, y, z);
      this.direction.set(x2, y2, z2);
      this.direction.sub(this.origin);
   }

   public double getT(Axis axis, double value) {
      return (value - VectorUtils.get(axis, this.origin)) / VectorUtils.get(axis, this.direction);
   }

   public Vector3d get(float t) {
      return new Vector3d(this.origin.x + this.direction.x * (double)t, this.origin.y + this.direction.y * (double)t, this.origin.z + this.direction.z * (double)t);
   }
}
