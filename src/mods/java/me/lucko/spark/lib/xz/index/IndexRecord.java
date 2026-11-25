package me.lucko.spark.lib.xz.index;

class IndexRecord {
   final long unpadded;
   final long uncompressed;

   IndexRecord(long var1, long var3) {
      this.unpadded = var1;
      this.uncompressed = var3;
   }
}
