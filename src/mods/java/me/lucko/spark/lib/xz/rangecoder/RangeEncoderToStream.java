package me.lucko.spark.lib.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class RangeEncoderToStream extends RangeEncoder {
   private final OutputStream out;

   public RangeEncoderToStream(OutputStream var1) {
      this.out = var1;
      this.reset();
   }

   void writeByte(int var1) throws IOException {
      this.out.write(var1);
   }
}
