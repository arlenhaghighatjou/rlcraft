package mcjty.lostcities.varia;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class QualityRandom extends EaglercraftRandom {
   private long u;
   private long v;
   private long w;

   public QualityRandom() {
      this(System.nanoTime());
   }

   public QualityRandom(long seed) {
      this.v = 4101842887655102017L;
      this.w = 1L;
      this.u = seed ^ this.v;
      this.nextLong();
      this.v = this.u;
      this.nextLong();
      this.w = this.v;
      this.nextLong();
   }

   public long nextLong() {
      this.u = this.u * 2862933555777941757L + 7046029254386353087L;
      this.v ^= this.v >>> 17;
      this.v ^= this.v << 31;
      this.v ^= this.v >>> 8;
      this.w = 4294957665L * (this.w & -1L) + (this.w >>> 32);
      long x = this.u ^ this.u << 21;
      x = x ^ x >>> 35;
      x = x ^ x << 4;
      long ret = x + this.v ^ this.w;
      return ret;
   }

   protected int next(int bits) {
      return (int)(this.nextLong() >>> 64 - bits);
   }
}
