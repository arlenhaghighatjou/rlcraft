package mezz.jei.gui;

import com.google.common.base.Preconditions;
import mezz.jei.api.gui.ITickTimer;

public class TickTimer implements ITickTimer {
   private final int msPerCycle;
   private final int maxValue;
   private final boolean countDown;
   private final long startTime;

   public TickTimer(int ticksPerCycle, int maxValue, boolean countDown) {
      Preconditions.checkArgument(ticksPerCycle > 0, "Must have at least 1 tick per cycle.");
      Preconditions.checkArgument(maxValue > 0, "max value must be greater than 0");
      this.msPerCycle = ticksPerCycle * 50;
      this.maxValue = maxValue;
      this.countDown = countDown;
      this.startTime = System.currentTimeMillis();
   }

   public int getValue() {
      long currentTime = System.currentTimeMillis();
      return getValue(this.startTime, currentTime, this.maxValue, this.msPerCycle, this.countDown);
   }

   public int getMaxValue() {
      return this.maxValue;
   }

   public static int getValue(long startTime, long currentTime, int maxValue, int msPerCycle, boolean countDown) {
      long msPassed = (currentTime - startTime) % (long)msPerCycle;
      int value = (int)Math.floorDiv(msPassed * (long)(maxValue + 1), (long)msPerCycle);
      return countDown ? maxValue - value : value;
   }
}
