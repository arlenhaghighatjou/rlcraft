package org.orecruncher.lib.random;

public final class LCGRandom {
   private long v;

   public LCGRandom() {
      this(XorShiftRandom.current().nextLong());
   }

   public LCGRandom(long seed) {
      this.v = seed;
   }

   public int nextInt(int bound) {
      this.v = 2862933555777941757L * this.v + 3037000493L;
      return (int)(this.v >> 32 & 2147483647L) % bound;
   }
}
