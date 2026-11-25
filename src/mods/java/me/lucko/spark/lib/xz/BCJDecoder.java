package me.lucko.spark.lib.xz;

import java.io.InputStream;
import me.lucko.spark.lib.xz.simple.ARM;
import me.lucko.spark.lib.xz.simple.ARMThumb;
import me.lucko.spark.lib.xz.simple.IA64;
import me.lucko.spark.lib.xz.simple.PowerPC;
import me.lucko.spark.lib.xz.simple.SPARC;
import me.lucko.spark.lib.xz.simple.SimpleFilter;
import me.lucko.spark.lib.xz.simple.X86;

class BCJDecoder extends BCJCoder implements FilterDecoder {
   private final long filterID;
   private final int startOffset;

   BCJDecoder(long var1, byte[] var3) throws UnsupportedOptionsException {
      assert isBCJFilterID(var1);

      this.filterID = var1;
      if (var3.length == 0) {
         this.startOffset = 0;
      } else {
         if (var3.length != 4) {
            throw new UnsupportedOptionsException("Unsupported BCJ filter properties");
         }

         int var4 = 0;

         for(int var5 = 0; var5 < 4; ++var5) {
            var4 |= (var3[var5] & 255) << var5 * 8;
         }

         this.startOffset = var4;
      }

   }

   public int getMemoryUsage() {
      return SimpleInputStream.getMemoryUsage();
   }

   public InputStream getInputStream(InputStream var1, ArrayCache var2) {
      Object var3 = null;
      if (this.filterID == 4L) {
         var3 = new X86(false, this.startOffset);
      } else if (this.filterID == 5L) {
         var3 = new PowerPC(false, this.startOffset);
      } else if (this.filterID == 6L) {
         var3 = new IA64(false, this.startOffset);
      } else if (this.filterID == 7L) {
         var3 = new ARM(false, this.startOffset);
      } else if (this.filterID == 8L) {
         var3 = new ARMThumb(false, this.startOffset);
      } else if (this.filterID == 9L) {
         var3 = new SPARC(false, this.startOffset);
      } else {
         assert false;
      }

      return new SimpleInputStream(var1, (SimpleFilter)var3);
   }
}
