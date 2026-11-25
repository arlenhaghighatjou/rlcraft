package org.orecruncher.lib.random;

public final class MurmurHash3 {
   private MurmurHash3() {
   }

   public static int hash(int k) {
      k = k ^ k >>> 16;
      k = k * -2048144789;
      k = k ^ k >>> 13;
      k = k * -1028477387;
      k = k ^ k >>> 16;
      return k;
   }

   public static long hash(long k) {
      k = k ^ k >>> 33;
      k = k * -49064778989728563L;
      k = k ^ k >>> 33;
      k = k * -4265267296055464877L;
      k = k ^ k >>> 33;
      return k;
   }
}
