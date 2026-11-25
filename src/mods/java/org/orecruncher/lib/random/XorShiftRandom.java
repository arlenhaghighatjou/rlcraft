package org.orecruncher.lib.random;

import java.util.Random;
import org.orecruncher.lib.math.MathStuff;

public final class XorShiftRandom extends Random {
   private static final long PRIME = 402653189L;
   private static final double DOUBLE_UNIT = 1.1102230246251565E-16D;
   private static final float FLOAT_UNIT = 5.9604645E-8F;
   private static final int A = 24;
   private static final int B = 16;
   private static final int C = 37;
   private long s0;
   private long s1;
   private boolean hasGaussian;
   private double nextGaussian;
   private static final ThreadLocal<XorShiftRandom> localRandom = new ThreadLocal<XorShiftRandom>() {
      public XorShiftRandom initialValue() {
         return new XorShiftRandom();
      }
   };

   public XorShiftRandom() {
      this(System.nanoTime() * 402653189L + Thread.currentThread().getId());
   }

   public XorShiftRandom(long seed) {
      super(0L);
      this.hasGaussian = false;
      this.nextGaussian = 0.0D;
      this.setSeed0(seed);
   }

   private void setSeed0(long seed) {
      this.s0 = MurmurHash3.hash(seed);
      this.s1 = MurmurHash3.hash(this.s0);
      if (this.s0 == 0L && this.s1 == 0L) {
         this.s0 = MurmurHash3.hash(3735928559L);
         this.s1 = MurmurHash3.hash(this.s0);
      }

   }

   public void setSeed(long seed) {
      if (this.s0 != 0L || this.s1 != 0L) {
         this.setSeed0(seed);
         this.hasGaussian = false;
      }
   }

   public void nextBytes(byte[] bytes) {
      int i = 0;
      int len = bytes.length;

      while(i < len) {
         long rnd = (long)this.nextInt();

         for(int n = Math.min(len - i, 8); n-- > 0; rnd >>>= 8) {
            bytes[i++] = (byte)((int)rnd);
         }
      }

   }

   public double nextDouble() {
      return (double)(this.nextLong() >>> 11) * 1.1102230246251565E-16D;
   }

   public float nextFloat() {
      return (float)(this.nextInt() >>> 8) * 5.9604645E-8F;
   }

   public int nextInt() {
      return (int)this.nextLong();
   }

   protected double genGaussian() {
      double v1;
      double v2;
      double s;
      while(true) {
         v1 = 2.0D * this.nextDouble() - 1.0D;
         v2 = 2.0D * this.nextDouble() - 1.0D;
         s = v1 * v1 + v2 * v2;
         if (s < 1.0D && s != 0.0D) {
            break;
         }
      }

      double multiplier = Math.sqrt(-2.0D * MathStuff.log(s) / s);
      this.nextGaussian = v2 * multiplier;
      this.hasGaussian = true;
      return v1 * multiplier;
   }

   public double nextGaussian() {
      if (!this.hasGaussian) {
         return this.genGaussian();
      } else {
         this.hasGaussian = false;
         return this.nextGaussian;
      }
   }

   public long nextLong() {
      long result = this.s0 + this.s1;
      long s1 = this.s1 ^ this.s0;
      this.s0 = Long.rotateLeft(this.s0, 24) ^ s1 ^ s1 << 16;
      this.s1 = Long.rotateLeft(s1, 37);
      return result;
   }

   protected int next(int bits) {
      return (int)this.nextLong() >>> 32 - bits;
   }

   public static Random current() {
      return (Random)localRandom.get();
   }
}
