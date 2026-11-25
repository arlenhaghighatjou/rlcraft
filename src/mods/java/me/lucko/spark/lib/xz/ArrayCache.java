package me.lucko.spark.lib.xz;

public class ArrayCache {
   private static final ArrayCache dummyCache = new ArrayCache();
   private static volatile ArrayCache defaultCache = dummyCache;

   public static ArrayCache getDummyCache() {
      return dummyCache;
   }

   public static ArrayCache getDefaultCache() {
      return defaultCache;
   }

   public static void setDefaultCache(ArrayCache var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         defaultCache = var0;
      }
   }

   public ArrayCache() {
   }

   public byte[] getByteArray(int var1, boolean var2) {
      return new byte[var1];
   }

   public void putArray(byte[] var1) {
   }

   public int[] getIntArray(int var1, boolean var2) {
      return new int[var1];
   }

   public void putArray(int[] var1) {
   }
}
