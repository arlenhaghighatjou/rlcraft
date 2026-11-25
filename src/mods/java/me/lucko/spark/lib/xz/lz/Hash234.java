package me.lucko.spark.lib.xz.lz;

import me.lucko.spark.lib.xz.ArrayCache;

final class Hash234 extends CRC32Hash {
   private static final int HASH_2_SIZE = 1024;
   private static final int HASH_2_MASK = 1023;
   private static final int HASH_3_SIZE = 65536;
   private static final int HASH_3_MASK = 65535;
   private final int hash4Mask;
   private final int[] hash2Table;
   private final int[] hash3Table;
   private final int[] hash4Table;
   private final int hash4Size;
   private int hash2Value = 0;
   private int hash3Value = 0;
   private int hash4Value = 0;

   static int getHash4Size(int var0) {
      int var1 = var0 - 1;
      var1 = var1 | var1 >>> 1;
      var1 = var1 | var1 >>> 2;
      var1 = var1 | var1 >>> 4;
      var1 = var1 | var1 >>> 8;
      var1 = var1 >>> 1;
      var1 = var1 | '\uffff';
      if (var1 > 16777216) {
         var1 >>>= 1;
      }

      return var1 + 1;
   }

   static int getMemoryUsage(int var0) {
      return (66560 + getHash4Size(var0)) / 256 + 4;
   }

   Hash234(int var1, ArrayCache var2) {
      this.hash2Table = var2.getIntArray(1024, true);
      this.hash3Table = var2.getIntArray(65536, true);
      this.hash4Size = getHash4Size(var1);
      this.hash4Table = var2.getIntArray(this.hash4Size, true);
      this.hash4Mask = this.hash4Size - 1;
   }

   void putArraysToCache(ArrayCache var1) {
      var1.putArray(this.hash4Table);
      var1.putArray(this.hash3Table);
      var1.putArray(this.hash2Table);
   }

   void calcHashes(byte[] var1, int var2) {
      int var3 = crcTable[var1[var2] & 255] ^ var1[var2 + 1] & 255;
      this.hash2Value = var3 & 1023;
      var3 = var3 ^ (var1[var2 + 2] & 255) << 8;
      this.hash3Value = var3 & '\uffff';
      var3 = var3 ^ crcTable[var1[var2 + 3] & 255] << 5;
      this.hash4Value = var3 & this.hash4Mask;
   }

   int getHash2Pos() {
      return this.hash2Table[this.hash2Value];
   }

   int getHash3Pos() {
      return this.hash3Table[this.hash3Value];
   }

   int getHash4Pos() {
      return this.hash4Table[this.hash4Value];
   }

   void updateTables(int var1) {
      this.hash2Table[this.hash2Value] = var1;
      this.hash3Table[this.hash3Value] = var1;
      this.hash4Table[this.hash4Value] = var1;
   }

   void normalize(int var1) {
      LZEncoder.normalize(this.hash2Table, 1024, var1);
      LZEncoder.normalize(this.hash3Table, 65536, var1);
      LZEncoder.normalize(this.hash4Table, this.hash4Size, var1);
   }
}
