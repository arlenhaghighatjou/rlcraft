package com.endertech.minecraft.forge.math;

import com.endertech.common.CommonTime;

public class Combustion {
   private long ticksFireInfluenced = 0L;
   private long ticksBurning = 0L;
   private boolean burning = false;

   public Combustion() {
   }

   public void update(boolean fireInfluenced) {
      if (fireInfluenced) {
         ++this.ticksFireInfluenced;
      } else if (this.ticksFireInfluenced > 0L) {
         --this.ticksFireInfluenced;
      }

      if (this.isBurning()) {
         ++this.ticksBurning;
      } else {
         this.ticksBurning = 0L;
      }

   }

   public void fire() {
      this.setBurning(true);
   }

   public void extinguish() {
      this.setBurning(false);
   }

   public CommonTime.Interval getFireInfluencedTime() {
      return GameTime.fromServerTicks(this.ticksFireInfluenced);
   }

   public CommonTime.Interval getBurningTime() {
      return GameTime.fromServerTicks(this.ticksBurning);
   }

   public boolean isBurning() {
      return this.burning;
   }

   public void setBurning(boolean burning) {
      this.burning = burning;
   }
}
