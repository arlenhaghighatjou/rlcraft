package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CBoolean extends Component {
   public boolean value;

   public CBoolean() {
   }

   public CBoolean set(boolean value) {
      this.value = value;
      return this;
   }

   public CBoolean write(ByteBuf buf) {
      buf.writeBoolean(this.value);
      return this;
   }

   public CBoolean read(ByteBuf buf) {
      this.value = buf.readBoolean();
      return this;
   }

   public CBoolean save(OutputStream stream) {
      try {
         stream.write(this.value ? 1 : 0);
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CBoolean load(InputStream stream) {
      try {
         byte[] bytes = new byte[1];
         if (stream.read(bytes) < 1) {
            throw new IOException("Reached end of file while reading!");
         } else {
            this.value = bytes[0] == 1;
            return this;
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
