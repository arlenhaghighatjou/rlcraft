package com.endertech.minecraft.forge.math;

import com.endertech.common.CommonMath;
import com.endertech.common.CommonTime;
import net.minecraft.world.World;

public class GameTime {
   public static final int MILLISECONDS_IN_SERVER_TICK = 50;
   public static final CommonTime.IMessure SERVER_TICKS = () -> {
      return 50L;
   };
   public static final int TICKS_PER_SECOND = 20;
   protected final CommonTime.Interval interval;
   protected final long ticks;
   protected final int shift;

   public static CommonTime.Interval fromServerTicks(long ticks) {
      return CommonTime.Interval.of(SERVER_TICKS, (double)ticks);
   }

   public static long inServerTicks(CommonTime.Interval time) {
      long ticks = (long)time.in(SERVER_TICKS);
      return ticks;
   }

   protected GameTime(long ticks) {
      this.interval = fromServerTicks(ticks);
      this.ticks = ticks;
      this.shift = CommonMath.RANDOM.nextInt();
   }

   public static GameTime second() {
      return seconds(1);
   }

   public static GameTime halfSecond() {
      return ticks(10L);
   }

   public static GameTime quaterSecond() {
      return ticks(5L);
   }

   public static GameTime seconds(int seconds) {
      return ticks((long)(seconds * 20));
   }

   public static GameTime seconds(float seconds) {
      int ticks = Math.round(seconds * 20.0F);
      return ticks((long)ticks);
   }

   public static GameTime time(CommonTime.Interval time) {
      return ticks(inServerTicks(time));
   }

   public static GameTime ticks(long ticks) {
      return new GameTime(ticks);
   }

   public boolean pastIn(World world) {
      if (this.ticks == 0L) {
         return true;
      } else {
         long time = world.getTotalWorldTime();
         time = time + (long)this.shift;
         long result = time % this.ticks;
         return result == 0L;
      }
   }

   public CommonTime.Interval getInterval() {
      return this.interval;
   }

   public GameTime mult(double factor) {
      return time(this.getInterval().mult(factor));
   }
}
