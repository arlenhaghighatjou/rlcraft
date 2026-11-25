package meldexun.randomutil;

import java.util.Random;

public class FastRandom extends Random {
   private static final long serialVersionUID = 5655469615636521736L;
   private long seed;
   private static final long multiplier = 25214903917L;
   private static final long addend = 11L;
   private static final long mask = 281474976710655L;
   private static long seedUniquifier = 8682522807148012L;
   private double nextNextGaussian;
   private boolean haveNextNextGaussian;

   public FastRandom() {
      this(seedUniquifier() ^ System.nanoTime());
   }

   private static long seedUniquifier() {
      return seedUniquifier *= 1181783497276652981L;
   }

   public FastRandom(long seed) {
      super(seed);
   }

   private static long initialScramble(long seed) {
      return (seed ^ 25214903917L) & 281474976710655L;
   }

   public void setSeed(long seed) {
      this.seed = initialScramble(seed);
      this.haveNextNextGaussian = false;
   }

   protected int next(int bits) {
      return (int)((this.seed = this.seed * 25214903917L + 11L & 281474976710655L) >>> 48 - bits);
   }

   public double nextGaussian() {
      // $FF: Couldn't be decompiled
   }
}
