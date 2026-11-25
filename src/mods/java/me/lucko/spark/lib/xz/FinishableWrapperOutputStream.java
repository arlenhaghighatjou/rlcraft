package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.OutputStream;

public class FinishableWrapperOutputStream extends FinishableOutputStream {
   protected OutputStream out;

   public FinishableWrapperOutputStream(OutputStream var1) {
      this.out = var1;
   }

   public void write(int var1) throws IOException {
      this.out.write(var1);
   }

   public void write(byte[] var1) throws IOException {
      this.out.write(var1);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
   }

   public void flush() throws IOException {
      this.out.flush();
   }

   public void close() throws IOException {
      this.out.close();
   }
}
