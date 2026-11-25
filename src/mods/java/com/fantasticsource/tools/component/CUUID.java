package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class CUUID extends Component {
   public UUID value;

   public CUUID() {
   }

   public CUUID set(UUID value) {
      this.value = value;
      return this;
   }

   public CUUID write(ByteBuf buf) {
      buf.writeLong(this.value.getMostSignificantBits());
      buf.writeLong(this.value.getLeastSignificantBits());
      return this;
   }

   public CUUID read(ByteBuf buf) {
      this.value = new UUID(buf.readLong(), buf.readLong());
      return this;
   }

   public CUUID save(OutputStream stream) {
      try {
         long l = this.value.getMostSignificantBits();
         stream.write(new byte[]{(byte)((int)(l >>> 56)), (byte)((int)(l >>> 48)), (byte)((int)(l >>> 40)), (byte)((int)(l >>> 32)), (byte)((int)(l >>> 24)), (byte)((int)(l >>> 16)), (byte)((int)(l >>> 8)), (byte)((int)l)});
         l = this.value.getLeastSignificantBits();
         stream.write(new byte[]{(byte)((int)(l >>> 56)), (byte)((int)(l >>> 48)), (byte)((int)(l >>> 40)), (byte)((int)(l >>> 32)), (byte)((int)(l >>> 24)), (byte)((int)(l >>> 16)), (byte)((int)(l >>> 8)), (byte)((int)l)});
         return this;
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public CUUID load(InputStream stream) {
      try {
         byte[] bytes = new byte[16];
         if (stream.read(bytes) < 16) {
            throw new IOException("Reached end of file while reading!");
         } else {
            int upper = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            int lower = (bytes[4] & 255) << 24 | (bytes[5] & 255) << 16 | (bytes[6] & 255) << 8 | bytes[7] & 255;
            long mostSig = ((long)upper & 4294967295L) << 32 | (long)lower & 4294967295L;
            upper = (bytes[8] & 255) << 24 | (bytes[9] & 255) << 16 | (bytes[10] & 255) << 8 | bytes[11] & 255;
            lower = (bytes[12] & 255) << 24 | (bytes[13] & 255) << 16 | (bytes[14] & 255) << 8 | bytes[15] & 255;
            this.value = new UUID(mostSig, ((long)upper & 4294967295L) << 32 | (long)lower & 4294967295L);
            return this;
         }
      } catch (IOException var7) {
         var7.printStackTrace();
         return null;
      }
   }
}
