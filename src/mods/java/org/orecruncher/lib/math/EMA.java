package org.orecruncher.lib.math;

import javax.annotation.Nonnull;

public class EMA {
   private final String name;
   private double ema;
   private final double factor;

   public EMA() {
      this("UNNAMED");
   }

   public EMA(@Nonnull String name) {
      this(name, 100);
   }

   public EMA(@Nonnull String name, int periods) {
      this.name = name;
      this.factor = 2.0D / (double)(periods + 1);
      this.ema = Double.NaN;
   }

   public double update(double newValue) {
      if (Double.isNaN(this.ema)) {
         this.ema = newValue;
      } else {
         this.ema += (newValue - this.ema) * this.factor;
      }

      return this.ema;
   }

   public String name() {
      return this.name;
   }

   public double get() {
      return this.ema;
   }
}
