package com.fantasticsource.tools.component;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class CStringUTF8 extends Component {
   public String value;

   public CStringUTF8() {
   }

   public CStringUTF8 set(String value) {
      this.value = value;
      return this;
   }

   public CStringUTF8 write(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.value);
      return this;
   }

   public CStringUTF8 read(ByteBuf buf) {
      this.value = ByteBufUtils.readUTF8String(buf);
      return this;
   }

   public CStringUTF8 save(OutputStream stream) {
      try {
         byte[] bytes = this.value.getBytes(StandardCharsets.UTF_8);
         int length = bytes.length;
         stream.write(new byte[]{(byte)(length >>> 24), (byte)(length >>> 16), (byte)(length >>> 8), (byte)length});
         stream.write(bytes);
         return this;
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public CStringUTF8 load(InputStream stream) {
      try {
         byte[] bytes = new byte[4];
         if (stream.read(bytes) < 4) {
            throw new IOException("Reached end of file while reading!");
         } else {
            int length = (bytes[0] & 255) << 24 | (bytes[1] & 255) << 16 | (bytes[2] & 255) << 8 | bytes[3] & 255;
            bytes = new byte[length];
            if (stream.read(bytes) < length) {
               throw new IOException("Reached end of file while reading!");
            } else {
               this.value = new String(bytes, StandardCharsets.UTF_8);
               return this;
            }
         }
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }
}
