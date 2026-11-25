package meldexun.configutil;

import io.netty.buffer.ByteBuf;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ByteBufUtil {
   private static final Map<Class<?>, ByteBufUtil.Serializer<?>> SERIALIZERS = new HashMap();

   public ByteBufUtil() {
   }

   private static void writeByte(ByteBuf buf, byte b) {
      buf.writeByte(b);
   }

   private static void writeShort(ByteBuf buf, short s) {
      buf.writeShort(s);
   }

   private static void writeChar(ByteBuf buf, char c) {
      buf.writeChar(c);
   }

   private static void writeString(ByteBuf buf, String string) {
      byte[] data = string.getBytes(StandardCharsets.UTF_8);
      buf.writeInt(data.length);
      buf.writeBytes(data);
   }

   private static String readString(ByteBuf buf) {
      return buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
   }

   @SafeVarargs
   public static <T> void registerSerializer(final BiConsumer<ByteBuf, T> writer, final Function<ByteBuf, T> reader, Class<T>... types) {
      for(Class<T> type : types) {
         SERIALIZERS.put(type, new ByteBufUtil.Serializer<T>() {
            public void write(ByteBuf buffer, T t) {
               writer.accept(buffer, t);
            }

            public T read(ByteBuf buffer) {
               return (T)reader.apply(buffer);
            }
         });
      }

   }

   private static <T> ByteBufUtil.Serializer<T> getSerializer(Class<T> type) {
      return (ByteBufUtil.Serializer)SERIALIZERS.get(type);
   }

   public static <T> void write(T src, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (src == null) {
         buffer.writeBoolean(false);
      } else {
         write(src.getClass(), src, buffer, predicate);
      }
   }

   public static <T> void write(Class<T> type, T src, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      buffer.writeBoolean(src != null);
      if (src != null) {
         ByteBufUtil.Serializer<T> serializer = getSerializer(type);
         if (serializer != null) {
            serializer.write(buffer, src);
         } else if (type.isArray()) {
            writeArray(type, src, buffer, predicate);
         } else {
            writeObject(type, src, buffer, predicate);
         }
      }
   }

   private static <T, R> void writeArray(Class<T> type, T src, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> componentType = type.getComponentType();
      int length = Array.getLength(src);
      buffer.writeInt(length);

      for(int i = 0; i < length; ++i) {
         writeComponent(componentType, src, i, buffer, predicate);
      }

   }

   private static <T, R> void writeComponent(Class<R> componentType, T src, int index, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (componentType == Boolean.TYPE) {
         buffer.writeBoolean(Array.getBoolean(src, index));
      } else if (componentType == Byte.TYPE) {
         buffer.writeByte(Array.getByte(src, index));
      } else if (componentType == Short.TYPE) {
         buffer.writeShort(Array.getShort(src, index));
      } else if (componentType == Integer.TYPE) {
         buffer.writeInt(Array.getInt(src, index));
      } else if (componentType == Long.TYPE) {
         buffer.writeLong(Array.getLong(src, index));
      } else if (componentType == Float.TYPE) {
         buffer.writeFloat(Array.getFloat(src, index));
      } else if (componentType == Double.TYPE) {
         buffer.writeDouble(Array.getDouble(src, index));
      } else if (componentType == Character.TYPE) {
         buffer.writeChar(Array.getChar(src, index));
      } else {
         write(componentType, Array.get(src, index), buffer, predicate);
      }

   }

   private static <T> void writeObject(Class<T> type, T src, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      StreamUtil.forEachUnchecked(streamFields(type, predicate), (field) -> {
         field.setAccessible(true);
         writeField(src, field, buffer, predicate);
      }, ReflectiveOperationException.class);
   }

   private static <T, R> void writeField(T src, Field field, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> fieldType = field.getType();
      if (fieldType == Boolean.TYPE) {
         buffer.writeBoolean(field.getBoolean(src));
      } else if (fieldType == Byte.TYPE) {
         buffer.writeByte(field.getByte(src));
      } else if (fieldType == Short.TYPE) {
         buffer.writeShort(field.getShort(src));
      } else if (fieldType == Integer.TYPE) {
         buffer.writeInt(field.getInt(src));
      } else if (fieldType == Long.TYPE) {
         buffer.writeLong(field.getLong(src));
      } else if (fieldType == Float.TYPE) {
         buffer.writeFloat(field.getFloat(src));
      } else if (fieldType == Double.TYPE) {
         buffer.writeDouble(field.getDouble(src));
      } else if (fieldType == Character.TYPE) {
         buffer.writeChar(field.getChar(src));
      } else {
         write(fieldType, field.get(src), buffer, predicate);
      }

   }

   public static <T> T read(Class<T> type, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      return (T)read(type, (Object)null, buffer, predicate);
   }

   public static <T> T read(T dest, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      return (T)read(dest.getClass(), dest, buffer, predicate);
   }

   public static <T> T read(Class<T> type, @Nullable T dest, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (!buffer.readBoolean()) {
         return (T)null;
      } else {
         ByteBufUtil.Serializer<T> serializer = getSerializer(type);
         if (serializer != null) {
            return serializer.read(buffer);
         } else {
            return (T)(type.isArray() ? readArray(type, buffer, predicate) : readObject(type, dest, buffer, predicate));
         }
      }
   }

   private static <T, R> T readArray(Class<T> type, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> componentType = type.getComponentType();
      int length = buffer.readInt();
      T dest = (T)Array.newInstance(componentType, length);

      for(int i = 0; i < length; ++i) {
         readComponent(componentType, dest, i, buffer, predicate);
      }

      return dest;
   }

   private static <T, R> void readComponent(Class<R> componentType, T dest, int index, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (componentType == Boolean.TYPE) {
         Array.setBoolean(dest, index, buffer.readBoolean());
      } else if (componentType == Byte.TYPE) {
         Array.setByte(dest, index, buffer.readByte());
      } else if (componentType == Short.TYPE) {
         Array.setShort(dest, index, buffer.readShort());
      } else if (componentType == Integer.TYPE) {
         Array.setInt(dest, index, buffer.readInt());
      } else if (componentType == Long.TYPE) {
         Array.setLong(dest, index, buffer.readLong());
      } else if (componentType == Float.TYPE) {
         Array.setFloat(dest, index, buffer.readFloat());
      } else if (componentType == Double.TYPE) {
         Array.setDouble(dest, index, buffer.readDouble());
      } else if (componentType == Character.TYPE) {
         Array.setChar(dest, index, buffer.readChar());
      } else {
         Array.set(dest, index, read(componentType, (Object)null, buffer, predicate));
      }

   }

   private static <T> T readObject(Class<T> type, @Nullable T dest, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      T result = (T)(dest != null ? dest : type.newInstance());
      StreamUtil.forEachUnchecked(streamFields(type, predicate), (field) -> {
         field.setAccessible(true);
         readField(result, field, buffer, predicate);
      }, ReflectiveOperationException.class);
      return result;
   }

   private static <T, R> void readField(T dest, Field field, ByteBuf buffer, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> fieldType = field.getType();
      if (fieldType == Boolean.TYPE) {
         field.setBoolean(dest, buffer.readBoolean());
      } else if (fieldType == Byte.TYPE) {
         field.setByte(dest, buffer.readByte());
      } else if (fieldType == Short.TYPE) {
         field.setShort(dest, buffer.readShort());
      } else if (fieldType == Integer.TYPE) {
         field.setInt(dest, buffer.readInt());
      } else if (fieldType == Long.TYPE) {
         field.setLong(dest, buffer.readLong());
      } else if (fieldType == Float.TYPE) {
         field.setFloat(dest, buffer.readFloat());
      } else if (fieldType == Double.TYPE) {
         field.setDouble(dest, buffer.readDouble());
      } else if (fieldType == Character.TYPE) {
         field.setChar(dest, buffer.readChar());
      } else {
         field.set(dest, read(fieldType, field.get(dest), buffer, predicate));
      }

   }

   private static Stream<Field> streamFields(Class<?> type, @Nullable Predicate<Field> predicate) {
      Stream<Field> fields = StreamUtil.streamDeclaredFields(type).filter((field) -> {
         return !Modifier.isStatic(field.getModifiers());
      });
      if (predicate != null) {
         fields = fields.filter(predicate);
      }

      fields = fields.sorted(Comparator.comparing(Field::getName));
      return fields;
   }

   static {
      registerSerializer(ByteBuf::writeBoolean, ByteBuf::readBoolean, Boolean.TYPE, Boolean.class);
      registerSerializer(ByteBufUtil::writeByte, ByteBuf::readByte, Byte.TYPE, Byte.class);
      registerSerializer(ByteBufUtil::writeShort, ByteBuf::readShort, Short.TYPE, Short.class);
      registerSerializer(ByteBuf::writeInt, ByteBuf::readInt, Integer.TYPE, Integer.class);
      registerSerializer(ByteBuf::writeLong, ByteBuf::readLong, Long.TYPE, Long.class);
      registerSerializer(ByteBuf::writeFloat, ByteBuf::readFloat, Float.TYPE, Float.class);
      registerSerializer(ByteBuf::writeDouble, ByteBuf::readDouble, Double.TYPE, Double.class);
      registerSerializer(ByteBufUtil::writeChar, ByteBuf::readChar, Character.TYPE, Character.class);
      registerSerializer(ByteBufUtil::writeString, ByteBufUtil::readString, String.class);
   }

   public interface Serializer<T> {
      void write(ByteBuf var1, T var2) throws ReflectiveOperationException;

      T read(ByteBuf var1) throws ReflectiveOperationException;
   }
}
