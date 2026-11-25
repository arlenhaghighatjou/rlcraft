package me.lucko.spark.lib.xz.lzma;

final class Optimum {
   private static final int INFINITY_PRICE = 1073741824;
   final State state = new State();
   final int[] reps = new int[4];
   int price;
   int optPrev;
   int backPrev;
   boolean prev1IsLiteral;
   boolean hasPrev2;
   int optPrev2;
   int backPrev2;

   Optimum() {
   }

   void reset() {
      this.price = 1073741824;
   }

   void set1(int var1, int var2, int var3) {
      this.price = var1;
      this.optPrev = var2;
      this.backPrev = var3;
      this.prev1IsLiteral = false;
   }

   void set2(int var1, int var2, int var3) {
      this.price = var1;
      this.optPrev = var2 + 1;
      this.backPrev = var3;
      this.prev1IsLiteral = true;
      this.hasPrev2 = false;
   }

   void set3(int var1, int var2, int var3, int var4, int var5) {
      this.price = var1;
      this.optPrev = var2 + var4 + 1;
      this.backPrev = var5;
      this.prev1IsLiteral = true;
      this.hasPrev2 = true;
      this.optPrev2 = var2;
      this.backPrev2 = var3;
   }
}
