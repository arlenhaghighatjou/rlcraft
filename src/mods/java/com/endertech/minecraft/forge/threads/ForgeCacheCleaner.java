package com.endertech.minecraft.forge.threads;

import com.endertech.common.CommonTime;

public abstract class ForgeCacheCleaner extends ForgeThread {
   private final CommonTime.Interval runInterval;
   private final CommonTime.Stamp wasRunAt;

   public ForgeCacheCleaner(CommonTime.Interval runInterval) {
      this.runInterval = runInterval;
      this.wasRunAt = CommonTime.Stamp.now();
      this.start();
   }

   public boolean isTimeToRun() {
      CommonTime.Interval passed = CommonTime.Interval.passedFrom(this.wasRunAt);
      return !this.isAlive() && passed.moreThan(this.runInterval);
   }
}
