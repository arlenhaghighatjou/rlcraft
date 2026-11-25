package me.lucko.spark.lib.xz;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class BasicArrayCache extends ArrayCache {
   private static final int CACHEABLE_SIZE_MIN = 32768;
   private static final int STACKS_MAX = 32;
   private static final int ELEMENTS_PER_STACK = 512;
   private final BasicArrayCache.CacheMap<byte[]> byteArrayCache = new BasicArrayCache.CacheMap<byte[]>();
   private final BasicArrayCache.CacheMap<int[]> intArrayCache = new BasicArrayCache.CacheMap<int[]>();

   public BasicArrayCache() {
   }

   public static BasicArrayCache getInstance() {
      return BasicArrayCache.LazyHolder.INSTANCE;
   }

   private static <T> T getArray(BasicArrayCache.CacheMap<T> var0, int var1) {
      if (var1 < 32768) {
         return (T)null;
      } else {
         BasicArrayCache.CyclicStack var2;
         synchronized(var0) {
            var2 = (BasicArrayCache.CyclicStack)var0.get(Integer.valueOf(var1));
         }

         if (var2 == null) {
            return (T)null;
         } else {
            Object var3;
            while(true) {
               Reference var4 = (Reference)var2.pop();
               if (var4 == null) {
                  return (T)null;
               }

               var3 = var4.get();
               if (var3 != null) {
                  break;
               }
            }

            return (T)var3;
         }
      }
   }

   private static <T> void putArray(BasicArrayCache.CacheMap<T> var0, T var1, int var2) {
      if (var2 >= 32768) {
         BasicArrayCache.CyclicStack var3;
         synchronized(var0) {
            var3 = (BasicArrayCache.CyclicStack)var0.get(Integer.valueOf(var2));
            if (var3 == null) {
               var3 = new BasicArrayCache.CyclicStack();
               var0.put(Integer.valueOf(var2), var3);
            }
         }

         var3.push(new SoftReference(var1));
      }
   }

   public byte[] getByteArray(int var1, boolean var2) {
      byte[] var3 = (byte[])getArray(this.byteArrayCache, var1);
      if (var3 == null) {
         var3 = new byte[var1];
      } else if (var2) {
         Arrays.fill(var3, (byte)0);
      }

      return var3;
   }

   public void putArray(byte[] var1) {
      putArray(this.byteArrayCache, var1, var1.length);
   }

   public int[] getIntArray(int var1, boolean var2) {
      int[] var3 = (int[])getArray(this.intArrayCache, var1);
      if (var3 == null) {
         var3 = new int[var1];
      } else if (var2) {
         Arrays.fill(var3, 0);
      }

      return var3;
   }

   public void putArray(int[] var1) {
      putArray(this.intArrayCache, var1, var1.length);
   }

   private static class CacheMap<T> extends LinkedHashMap<Integer, BasicArrayCache.CyclicStack<Reference<T>>> {
      private static final long serialVersionUID = 1L;

      public CacheMap() {
         super(64, 0.75F, true);
      }

      protected boolean removeEldestEntry(Entry<Integer, BasicArrayCache.CyclicStack<Reference<T>>> var1) {
         return this.size() > 32;
      }
   }

   private static class CyclicStack<T> {
      private final T[] elements;
      private int pos;

      private CyclicStack() {
         this.elements = (T[])(new Object[512]);
         this.pos = 0;
      }

      public synchronized T pop() {
         Object var1 = this.elements[this.pos];
         this.elements[this.pos] = null;
         this.pos = this.pos - 1 & 511;
         return (T)var1;
      }

      public synchronized void push(T var1) {
         this.pos = this.pos + 1 & 511;
         this.elements[this.pos] = var1;
      }
   }

   private static final class LazyHolder {
      static final BasicArrayCache INSTANCE = new BasicArrayCache();

      private LazyHolder() {
      }
   }
}
