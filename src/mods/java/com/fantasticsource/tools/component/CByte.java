package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CByte extends Component {
   public byte value;

   public CByte() {
   }

   public CByte set(byte value) {
      this.value = value;
      return this;
   }

   public CByte write(ByteBuf buf) {
      buf.writeByte(this.value);
      return this;
   }

   public CByte read(ByteBuf buf) {
      this.value = buf.readByte();
      return this;
   }

   public CByte save(OutputStream stream) {
      try {
         stream.write(this.value);
         return this;
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public CByte load(InputStream stream) {
      try {
         byte[] bytes = new byte[1];
         if (stream.read(bytes) < 1) {
            throw new IOException("Reached end of file while reading!");
         } else {
            this.value = bytes[0];
            return this;
         }
      } catch (IOException var3) {
         var3.printStackTrace();
         return null;
      }
   }
}
