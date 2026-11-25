package me.lucko.spark.lib.xz;

import java.util.ArrayList;
import java.util.List;

public class ResettableArrayCache extends ArrayCache {
   private final ArrayCache arrayCache;
   private final List<byte[]> byteArrays;
   private final List<int[]> intArrays;

   public ResettableArrayCache(ArrayCache var1) {
      this.arrayCache = var1;
      if (var1 == ArrayCache.getDummyCache()) {
         this.byteArrays = null;
         this.intArrays = null;
      } else {
         this.byteArrays = new ArrayList();
         this.intArrays = new ArrayList();
      }

   }

   public byte[] getByteArray(int var1, boolean var2) {
      byte[] var3 = this.arrayCache.getByteArray(var1, var2);
      if (this.byteArrays != null) {
         synchronized(this.byteArrays) {
            this.byteArrays.add(var3);
         }
      }

      return var3;
   }

   public void putArray(byte[] var1) {
      if (this.byteArrays != null) {
         synchronized(this.byteArrays) {
            int var3 = this.byteArrays.lastIndexOf(var1);
            if (var3 != -1) {
               this.byteArrays.remove(var3);
            }
         }

         this.arrayCache.putArray(var1);
      }

   }

   public int[] getIntArray(int var1, boolean var2) {
      int[] var3 = this.arrayCache.getIntArray(var1, var2);
      if (this.intArrays != null) {
         synchronized(this.intArrays) {
            this.intArrays.add(var3);
         }
      }

      return var3;
   }

   public void putArray(int[] var1) {
      if (this.intArrays != null) {
         synchronized(this.intArrays) {
            int var3 = this.intArrays.lastIndexOf(var1);
            if (var3 != -1) {
               this.intArrays.remove(var3);
            }
         }

         this.arrayCache.putArray(var1);
      }

   }

   public void reset() {
      if (this.byteArrays != null) {
         synchronized(this.byteArrays) {
            for(int var2 = this.byteArrays.size() - 1; var2 >= 0; --var2) {
               this.arrayCache.putArray((byte[])this.byteArrays.get(var2));
            }

            this.byteArrays.clear();
         }

         synchronized(this.intArrays) {
            for(int var8 = this.intArrays.size() - 1; var8 >= 0; --var8) {
               this.arrayCache.putArray((int[])this.intArrays.get(var8));
            }

            this.intArrays.clear();
         }
      }

   }
}
