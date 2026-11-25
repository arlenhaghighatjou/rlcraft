package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CChar extends Component {
   public char value;

   public CChar() {
   }

   public CChar set(char value) {
      this.value = value;
      return this;
   }

   public CChar write(ByteBuf buf) {
      buf.writeChar(this.value);
      return this;
   }

   public CChar read(ByteBuf buf) {
      this.value = buf.readChar();
      return this;
   }

   public CChar save(OutputStream stream) {
      try {
         stream.write(("" + this.value).getBytes(StandardCharsets.UTF_8));
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CChar load(InputStream stream) {
      try {
         byte[] bytes = new byte[2];
         if (stream.read(bytes) < 2) {
            throw new IOException("Reached end of file while reading!");
         } else {
            this.value = (new String(bytes, StandardCharsets.UTF_8)).charAt(0);
            return this;
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
