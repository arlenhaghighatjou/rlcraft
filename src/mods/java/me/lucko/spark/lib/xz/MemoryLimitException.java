package me.lucko.spark.lib.xz;

public class MemoryLimitException extends XZIOException {
   private static final long serialVersionUID = 3L;
   private final int memoryNeeded;
   private final int memoryLimit;

   public MemoryLimitException(int var1, int var2) {
      super("" + var1 + " KiB of memory would be needed; limit was " + var2 + " KiB");
      this.memoryNeeded = var1;
      this.memoryLimit = var2;
   }

   public int getMemoryNeeded() {
      return this.memoryNeeded;
   }

   public int getMemoryLimit() {
      return this.memoryLimit;
   }
}
