package me.lucko.spark.lib.xz.check;

public class CRC64 extends Check {
   private static final long poly = -3932672073523589310L;
   private static final long[] crcTable = new long[256];
   private long crc = -1L;

   public CRC64() {
      this.size = 8;
      this.name = "CRC64";
   }

   public void update(byte[] var1, int var2, int var3) {
      for(int var4 = var2 + var3; var2 < var4; this.crc = crcTable[(var1[var2++] ^ (int)this.crc) & 255] ^ this.crc >>> 8) {
         ;
      }

   }

   public byte[] finish() {
      long var1 = ~this.crc;
      this.crc = -1L;
      byte[] var3 = new byte[8];

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var3[var4] = (byte)((int)(var1 >> var4 * 8));
      }

      return var3;
   }

   static {
      for(int var0 = 0; var0 < crcTable.length; ++var0) {
         long var1 = (long)var0;

         for(int var3 = 0; var3 < 8; ++var3) {
            if ((var1 & 1L) == 1L) {
               var1 = var1 >>> 1 ^ -3932672073523589310L;
            } else {
               var1 >>>= 1;
            }
         }

         crcTable[var0] = var1;
      }

   }
}
