package me.ichun.mods.ichunutil.common.module.worldportals.common.portal;

import javax.vecmath.Matrix3f;

public class Quaternion {
   public float x;
   public float y;
   public float z;
   public float w;

   public Quaternion() {
   }

   public Quaternion(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
   }

   public Quaternion set(float x, float y, float z, float w) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.w = w;
      return this;
   }

   public Quaternion conjugate() {
      Quaternion q = new Quaternion();
      q.x = -this.x;
      q.y = -this.y;
      q.z = -this.z;
      q.w = this.w;
      return q;
   }

   public Quaternion mul(Quaternion right) {
      Quaternion q = new Quaternion();
      q.set(this.x * right.w + this.w * right.x + this.y * right.z - this.z * right.y, this.y * right.w + this.w * right.y + this.z * right.x - this.x * right.z, this.z * right.w + this.w * right.z + this.x * right.y - this.y * right.x, this.w * right.w - this.x * right.x - this.y * right.y - this.z * right.z);
      return q;
   }

   public Quaternion setFromMatrix(Matrix3f m) {
      float tr = m.m00 + m.m11 + m.m22;
      if ((double)tr >= 0.0D) {
         float mag = (float)Math.sqrt((double)tr + 1.0D);
         this.w = mag * 0.5F;
         mag = 0.5F / mag;
         this.x = (m.m21 - m.m12) * mag;
         this.y = (m.m02 - m.m20) * mag;
         this.z = (m.m10 - m.m01) * mag;
      } else {
         float max = Math.max(Math.max(m.m00, m.m11), m.m22);
         if (max == m.m00) {
            float mag = (float)Math.sqrt((double)(m.m00 - (m.m11 + m.m22)) + 1.0D);
            this.x = mag * 0.5F;
            mag = 0.5F / mag;
            this.y = (m.m01 + m.m10) * mag;
            this.z = (m.m20 + m.m02) * mag;
            this.w = (m.m21 - m.m12) * mag;
         } else if (max == m.m11) {
            float mag = (float)Math.sqrt((double)(m.m11 - (m.m22 + m.m00)) + 1.0D);
            this.y = mag * 0.5F;
            mag = 0.5F / mag;
            this.z = (m.m12 + m.m21) * mag;
            this.x = (m.m01 + m.m10) * mag;
            this.w = (m.m02 - m.m20) * mag;
         } else {
            float mag = (float)Math.sqrt((double)(m.m22 - (m.m00 + m.m11)) + 1.0D);
            this.z = mag * 0.5F;
            mag = 0.5F / mag;
            this.x = (m.m20 + m.m02) * mag;
            this.y = (m.m12 + m.m21) * mag;
            this.w = (m.m10 - m.m01) * mag;
         }
      }

      return this;
   }
}
