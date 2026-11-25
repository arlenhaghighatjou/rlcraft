package me.lucko.spark.lib.xz.check;

public class None extends Check {
   public None() {
      this.size = 0;
      this.name = "None";
   }

   public void update(byte[] var1, int var2, int var3) {
   }

   public byte[] finish() {
      byte[] var1 = new byte[0];
      return var1;
   }
}
