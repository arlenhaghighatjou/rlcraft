package me.lucko.spark.lib.xz.delta;

public class DeltaDecoder extends DeltaCoder {
   public DeltaDecoder(int var1) {
      super(var1);
   }

   public void decode(byte[] var1, int var2, int var3) {
      int var4 = var2 + var3;

      for(int var5 = var2; var5 < var4; ++var5) {
         var1[var5] += this.history[this.distance + this.pos & 255];
         this.history[this.pos-- & 255] = var1[var5];
      }

   }
}
