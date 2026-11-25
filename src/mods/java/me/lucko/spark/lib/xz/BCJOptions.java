package me.lucko.spark.lib.xz;

abstract class BCJOptions extends FilterOptions {
   private final int alignment;
   int startOffset = 0;

   BCJOptions(int var1) {
      this.alignment = var1;
   }

   public void setStartOffset(int var1) throws UnsupportedOptionsException {
      if ((var1 & this.alignment - 1) != 0) {
         throw new UnsupportedOptionsException("Start offset must be a multiple of " + this.alignment);
      } else {
         this.startOffset = var1;
      }
   }

   public int getStartOffset() {
      return this.startOffset;
   }

   public int getEncoderMemoryUsage() {
      return SimpleOutputStream.getMemoryUsage();
   }

   public int getDecoderMemoryUsage() {
      return SimpleInputStream.getMemoryUsage();
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         assert false;

         throw new RuntimeException();
      }
   }
}
