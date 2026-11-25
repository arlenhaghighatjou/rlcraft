package me.lucko.spark.lib.xz;

import java.io.IOException;
import java.io.InputStream;

public class DeltaInputStream extends InputStream {
   public static final int DISTANCE_MIN = 1;
   public static final int DISTANCE_MAX = 256;
   private InputStream in;
   private final me.lucko.spark.lib.xz.delta.DeltaDecoder delta;
   private IOException exception = null;
   private final byte[] tempBuf = new byte[1];

   public DeltaInputStream(InputStream var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         this.in = var1;
         this.delta = new me.lucko.spark.lib.xz.delta.DeltaDecoder(var2);
      }
   }

   public int read() throws IOException {
      return this.read(this.tempBuf, 0, 1) == -1 ? -1 : this.tempBuf[0] & 255;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         return 0;
      } else if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         int var4;
         try {
            var4 = this.in.read(var1, var2, var3);
         } catch (IOException var6) {
            this.exception = var6;
            throw var6;
         }

         if (var4 == -1) {
            return -1;
         } else {
            this.delta.decode(var1, var2, var4);
            return var4;
         }
      }
   }

   public int available() throws IOException {
      if (this.in == null) {
         throw new XZIOException("Stream closed");
      } else if (this.exception != null) {
         throw this.exception;
      } else {
         return this.in.available();
      }
   }

   public void close() throws IOException {
      if (this.in != null) {
         try {
            this.in.close();
         } finally {
            this.in = null;
         }
      }

   }
}
