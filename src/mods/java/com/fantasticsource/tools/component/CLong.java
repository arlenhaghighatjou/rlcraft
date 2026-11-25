package com.fantasticsource.tools.component;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CLong extends Component {
   public long value;

   public CLong() {
   }

   public CLong set(long value) {
      this.value = value;
      return this;
   }

   public CLong write(ByteBuf buf) {
      buf.writeLong(this.value);
      return this;
   }

   public CLong read(ByteBuf buf) {
      this.value = buf.readLong();
      return this;
   }

   public CLong save(OutputStream stream) {
      try {
         stream.write(new byte[]{(byte)((int)(this.value >>> 56)), (byte)((int)(this.value >>> 48)), (byte)((int)(this.value >>> 40)), (byte)((int)(this.value >>> 32)), (byte)((int)(this.value >>> 24)), (byte)((int)(this.value >>> 16)), (byte)((int)(this.value >>> 8)), (byte)((int)this.value)});
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CLong load(InputStream stream) {
      try {
         byte[] bytes = new byte[8];
         if (stream.read(bytes) < 8) {
            throw new IOException("Reached end of file while reading!");
         } else {
            int upper = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            int lower = (bytes[4] & 255) << 24 | (bytes[5] & 255) << 16 | (bytes[6] & 255) << 8 | bytes[7] & 255;
            this.value = ((long)upper & 4294967295L) << 32 | (long)lower & 4294967295L;
            return this;
         }
      } catch (IOException var5) {
         var5.printStackTrace();
         return null;
      }
   }
}
