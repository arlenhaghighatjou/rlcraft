package me.lucko.spark.lib.xz;

import java.io.FilterInputStream;
import java.io.InputStream;

public class CloseIgnoringInputStream extends FilterInputStream {
   public CloseIgnoringInputStream(InputStream var1) {
      super(var1);
   }

   public void close() {
   }
}
