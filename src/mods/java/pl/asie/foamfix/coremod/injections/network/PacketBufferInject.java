package pl.asie.foamfix.coremod.injections.network;

import com.google.common.base.Utf8;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.EncoderException;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.PacketBuffer;

public class PacketBufferInject extends PacketBuffer {
   public PacketBufferInject(ByteBuf wrapped) {
      super(wrapped);
   }

   public PacketBuffer writeString(String string) {
      int length = Utf8.encodedLength(string);
      if (length > 32767) {
         throw new EncoderException("String too big (was " + length + " bytes encoded, max " + 32767 + ")");
      } else {
         this.writeVarInt(length);
         this.writeCharSequence(string, StandardCharsets.UTF_8);
         return this;
      }
   }
}
