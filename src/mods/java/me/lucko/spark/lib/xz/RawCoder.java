package me.lucko.spark.lib.xz;

class RawCoder {
   RawCoder() {
   }

   static void validate(FilterCoder[] var0) throws UnsupportedOptionsException {
      for(int var1 = 0; var1 < var0.length - 1; ++var1) {
         if (!var0[var1].nonLastOK()) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
         }
      }

      if (!var0[var0.length - 1].lastOK()) {
         throw new UnsupportedOptionsException("Unsupported XZ filter chain");
      } else {
         int var3 = 0;

         for(int var2 = 0; var2 < var0.length; ++var2) {
            if (var0[var2].changesSize()) {
               ++var3;
            }
         }

         if (var3 > 3) {
            throw new UnsupportedOptionsException("Unsupported XZ filter chain");
         }
      }
   }
}
