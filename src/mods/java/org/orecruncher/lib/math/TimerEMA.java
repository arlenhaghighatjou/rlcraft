package org.orecruncher.lib.math;

import javax.annotation.Nonnull;

public class TimerEMA extends EMA {
   public TimerEMA(@Nonnull String name) {
      super(name);
   }

   public double getMSecs() {
      return super.get() / 1000000.0D;
   }

   public String toString() {
      return String.format("%s:%7.3fms", this.name(), this.getMSecs());
   }
}
