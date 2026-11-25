package me.lucko.spark.lib.xz.delta;

public class DeltaEncoder extends DeltaCoder {
   public DeltaEncoder(int var1) {
      super(var1);
   }

   public void encode(byte[] var1, int var2, int var3, byte[] var4) {
      for(int var5 = 0; var5 < var3; ++var5) {
         byte var6 = this.history[this.distance + this.pos & 255];
         this.history[this.pos-- & 255] = var1[var2 + var5];
         var4[var5] = (byte)(var1[var2 + var5] - var6);
      }

   }
}
