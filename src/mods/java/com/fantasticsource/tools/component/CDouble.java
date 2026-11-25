package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CDouble extends Component {
   public double value;

   public CDouble() {
   }

   public CDouble set(double value) {
      this.value = value;
      return this;
   }

   public CDouble write(ByteBuf buf) {
      buf.writeDouble(this.value);
      return this;
   }

   public CDouble read(ByteBuf buf) {
      this.value = buf.readDouble();
      return this;
   }

   public CDouble save(OutputStream stream) {
      try {
         long l = Double.doubleToRawLongBits(this.value);
         stream.write(new byte[]{(byte)((int)(l >>> 56)), (byte)((int)(l >>> 48)), (byte)((int)(l >>> 40)), (byte)((int)(l >>> 32)), (byte)((int)(l >>> 24)), (byte)((int)(l >>> 16)), (byte)((int)(l >>> 8)), (byte)((int)l)});
         return this;
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public CDouble load(InputStream stream) {
      try {
         byte[] bytes = new byte[8];
         if (stream.read(bytes) < 8) {
            throw new IOException("Reached end of file while reading!");
         } else {
            int upper = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            int lower = (bytes[4] & 255) << 24 | (bytes[5] & 255) << 16 | (bytes[6] & 255) << 8 | bytes[7] & 255;
            this.value = Double.longBitsToDouble(((long)upper & 4294967295L) << 32 | (long)lower & 4294967295L);
            return this;
         }
      } catch (IOException var5) {
         var5.printStackTrace();
         return null;
      }
   }
}
