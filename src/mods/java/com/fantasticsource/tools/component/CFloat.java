package com.fantasticsource.tools.component;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CFloat extends Component {
   public float value;

   public CFloat() {
   }

   public CFloat set(float value) {
      this.value = value;
      return this;
   }

   public CFloat write(ByteBuf buf) {
      buf.writeFloat(this.value);
      return this;
   }

   public CFloat read(ByteBuf buf) {
      this.value = buf.readFloat();
      return this;
   }

   public CFloat save(OutputStream stream) {
      try {
         int i = Float.floatToRawIntBits(this.value);
         stream.write(new byte[]{(byte)(i >>> 24), (byte)(i >>> 16), (byte)(i >>> 8), (byte)i});
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CFloat load(InputStream stream) {
      try {
         byte[] bytes = new byte[4];
         if (stream.read(bytes) < 4) {
            throw new IOException("Reached end of file while reading!");
         } else {
            this.value = Float.intBitsToFloat((bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255);
            return this;
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
