package me.lucko.spark.lib.xz.common;

public class Util {
   public static final int STREAM_HEADER_SIZE = 12;
   public static final long BACKWARD_SIZE_MAX = 17179869184L;
   public static final int BLOCK_HEADER_SIZE_MAX = 1024;
   public static final long VLI_MAX = Long.MAX_VALUE;
   public static final int VLI_SIZE_MAX = 9;

   public Util() {
   }

   public static int getVLISize(long var0) {
      int var2 = 0;

      while(true) {
         ++var2;
         var0 >>= 7;
         if (var0 == 0L) {
            break;
         }
      }

      return var2;
   }
}
