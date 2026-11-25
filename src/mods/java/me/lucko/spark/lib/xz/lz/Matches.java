package me.lucko.spark.lib.xz.lz;

public final class Matches {
   public final int[] len;
   public final int[] dist;
   public int count = 0;

   Matches(int var1) {
      this.len = new int[var1];
      this.dist = new int[var1];
   }
}
