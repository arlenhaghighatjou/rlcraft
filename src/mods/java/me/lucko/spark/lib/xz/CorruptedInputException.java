package me.lucko.spark.lib.xz;

public class CorruptedInputException extends XZIOException {
   private static final long serialVersionUID = 3L;

   public CorruptedInputException() {
      super("Compressed data is corrupt");
   }

   public CorruptedInputException(String var1) {
      super(var1);
   }
}
