package meldexun.renderlib.util.timer;

import java.text.DecimalFormat;
import java.util.stream.LongStream;

public interface ITimer {
   DecimalFormat FORMAT = new DecimalFormat("0.0");

   String getName();

   void update();

   void start();

   void stop();

   LongStream results();

   default String avgString() {
      return FORMAT.format(this.avg()) + "ms";
   }

   default String minString() {
      return FORMAT.format(this.min()) + "ms";
   }

   default String maxString() {
      return FORMAT.format(this.max()) + "ms";
   }

   default double avg() {
      return this.results().average().orElse(0.0D) / 1000000.0D;
   }

   default double min() {
      return (double)this.results().min().orElse(0L) / 1000000.0D;
   }

   default double max() {
      return (double)this.results().max().orElse(0L) / 1000000.0D;
   }
}
