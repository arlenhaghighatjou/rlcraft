package ivorius.ivtoolkit.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.function.Supplier;

public class IvPacketHelper {
   public IvPacketHelper() {
   }

   public static void maybeWrite(ByteBuf dst, Object object, Runnable writer) {
      dst.writeBoolean(object != null);
      if (object != null) {
         writer.run();
      }

   }

   public static <T> T maybeRead(ByteBuf dst, T def, Supplier<T> reader) {
      return (T)(dst.readBoolean() ? reader.get() : def);
   }

   public static void writeByteBuffer(ByteBuf dst, ByteBuf src) {
      int length = src.readableBytes();
      dst.writeInt(length).writeBytes(src, 0, length);
   }

   public static ByteBuf readByteBuffer(ByteBuf src) {
      int length = src.readInt();
      return src.readBytes(length);
   }

   public static void writeNumber(ByteBuf buffer, Number number) {
      if (number instanceof Byte) {
         buffer.writeByte(((Byte)number).byteValue());
      } else if (number instanceof Double) {
         buffer.writeDouble(((Double)number).doubleValue());
      } else if (number instanceof Float) {
         buffer.writeFloat(((Float)number).floatValue());
      } else if (number instanceof Integer) {
         buffer.writeInt(((Integer)number).intValue());
      } else if (number instanceof Long) {
         buffer.writeLong(((Long)number).longValue());
      } else {
         if (!(number instanceof Short)) {
            throw new IllegalArgumentException();
         }

         buffer.writeShort(((Short)number).shortValue());
      }

   }
}
