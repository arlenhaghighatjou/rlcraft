package me.lucko.spark.lib.xz.check;

public class CRC32 extends Check {
   private final java.util.zip.CRC32 state = new java.util.zip.CRC32();

   public CRC32() {
      this.size = 4;
      this.name = "CRC32";
   }

   public void update(byte[] var1, int var2, int var3) {
      this.state.update(var1, var2, var3);
   }

   public byte[] finish() {
      long var1 = this.state.getValue();
      byte[] var3 = new byte[]{(byte)((int)var1), (byte)((int)(var1 >>> 8)), (byte)((int)(var1 >>> 16)), (byte)((int)(var1 >>> 24))};
      this.state.reset();
      return var3;
   }
}
