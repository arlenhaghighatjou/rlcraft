package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CInt extends Component {
   public int value;

   public CInt() {
   }

   public CInt set(int value) {
      this.value = value;
      return this;
   }

   public CInt write(ByteBuf buf) {
      buf.writeInt(this.value);
      return this;
   }

   public CInt read(ByteBuf buf) {
      this.value = buf.readInt();
      return this;
   }

   public CInt save(OutputStream stream) {
      try {
         stream.write(new byte[]{(byte)(this.value >>> 24), (byte)(this.value >>> 16), (byte)(this.value >>> 8), (byte)this.value});
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CInt load(InputStream stream) {
      try {
         byte[] bytes = new byte[4];
         if (stream.read(bytes) < 4) {
            throw new IOException("Reached end of file while reading!");
         } else {
            this.value = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            return this;
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
