package com.endertech.minecraft.forge.math;

import com.endertech.common.CommonMath;

public class Rotation {
   public static final Rotation ZERO = new Rotation(0.0F, 0.0F);
   public final float pitch;
   public final float yaw;

   public Rotation(float pitch, float yaw) {
      this.pitch = pitch;
      this.yaw = yaw;
   }

   public Rotation add(Rotation rotation) {
      return rotation != null ? new Rotation(this.pitch + rotation.pitch, this.yaw + rotation.yaw) : this;
   }

   public Rotation approx(float factor, Rotation rotation) {
      if (rotation != null) {
         float pitch = CommonMath.Approx.straight(factor, this.pitch, rotation.pitch);
         float yaw = CommonMath.Approx.straight(factor, this.yaw, rotation.yaw);
         return new Rotation(pitch, yaw);
      } else {
         return this;
      }
   }

   public Rotation substract(Rotation rotation) {
      return rotation != null ? new Rotation(this.pitch - rotation.pitch, this.yaw - rotation.yaw) : this;
   }
}
