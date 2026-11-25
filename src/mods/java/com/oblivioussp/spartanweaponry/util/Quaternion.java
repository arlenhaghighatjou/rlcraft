package com.oblivioussp.spartanweaponry.util;

import net.minecraft.util.math.Vec3d;

public class Quaternion {
   private float x;
   private float y;
   private float z;
   private float w;

   public Quaternion(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Quaternion(Quaternion quat) {
      this.x = quat.x;
      this.y = quat.y;
      this.z = quat.z;
      this.w = quat.w;
   }

   public Quaternion(Vec3d vector, float angle, boolean degrees) {
      if (degrees) {
         angle = (float)((double)angle * 0.017453292519943295D);
      }

      float mag = (float)Math.sin((double)angle / 2.0D);
      this.x = (float)vector.x * mag;
      this.y = (float)vector.y * mag;
      this.z = (float)vector.z * mag;
      this.w = (float)Math.cos((double)angle / 2.0D);
   }

   public void multiply(Quaternion quat) {
      float xr = this.w * quat.x + this.x * quat.w + this.y * quat.z - this.z * quat.y;
      float yr = this.w * quat.y - this.x * quat.z + this.y * quat.w + this.z * quat.x;
      float zr = this.w * quat.z + this.x * quat.y - this.y * quat.x + this.z * quat.w;
      float wr = this.w * quat.w - this.x * quat.x - this.y * quat.y - this.z * quat.z;
      this.x = xr;
      this.y = yr;
      this.z = zr;
      this.w = wr;
   }

   public void conjugate() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
   }

   public Vec3d transformVector(Vec3d vector) {
      Quaternion resultQuat = new Quaternion(this);
      Quaternion quatVec = new Quaternion((float)vector.x, (float)vector.y, (float)vector.z, 0.0F);
      resultQuat.multiply(quatVec);
      Quaternion conjQuat = new Quaternion(this);
      conjQuat.conjugate();
      resultQuat.multiply(conjQuat);
      return new Vec3d((double)resultQuat.x, (double)resultQuat.y, (double)resultQuat.z);
   }
}
