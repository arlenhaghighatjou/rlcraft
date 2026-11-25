package meldexun.configutil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class CopyUtil {
   private static final Set<Class<?>> IMMUTABLE_CLASSES = new HashSet();

   public CopyUtil() {
   }

   public static void registerImmutableClass(Class<?> type) {
      IMMUTABLE_CLASSES.add(type);
   }

   private static boolean isImmutable(Class<?> type) {
      return type.isPrimitive() || IMMUTABLE_CLASSES.contains(type);
   }

   public static <T> T copy(@Nullable T src, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      return (T)copy(src, (Object)null, predicate);
   }

   public static <T> T copy(Class<T> type, @Nullable T src, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      return (T)copy(type, src, (Object)null, predicate);
   }

   public static <T> T copy(@Nullable T src, @Nullable T dest, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      return (T)(src == null ? null : copy(src.getClass(), src, dest, predicate));
   }

   public static <T> T copy(Class<T> type, @Nullable T src, @Nullable T dest, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (src == null) {
         return (T)null;
      } else if (isImmutable(type)) {
         return src;
      } else {
         return (T)(type.isArray() ? copyArray(type, src, predicate) : copyObject(type, src, dest, predicate));
      }
   }

   private static <T, R> T copyArray(Class<T> type, T src, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> componentType = type.getComponentType();
      int length = Array.getLength(src);
      T dest = (T)Array.newInstance(componentType, length);

      for(int i = 0; i < length; ++i) {
         copyComponent(componentType, src, dest, i, predicate);
      }

      return dest;
   }

   private static <T, R> void copyComponent(Class<R> componentType, T src, T dest, int index, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      if (componentType == Boolean.TYPE) {
         Array.setBoolean(dest, index, Array.getBoolean(src, index));
      } else if (componentType == Byte.TYPE) {
         Array.setByte(dest, index, Array.getByte(src, index));
      } else if (componentType == Short.TYPE) {
         Array.setShort(dest, index, Array.getShort(src, index));
      } else if (componentType == Integer.TYPE) {
         Array.setInt(dest, index, Array.getInt(src, index));
      } else if (componentType == Long.TYPE) {
         Array.setLong(dest, index, Array.getLong(src, index));
      } else if (componentType == Float.TYPE) {
         Array.setFloat(dest, index, Array.getFloat(src, index));
      } else if (componentType == Double.TYPE) {
         Array.setDouble(dest, index, Array.getDouble(src, index));
      } else if (componentType == Character.TYPE) {
         Array.setChar(dest, index, Array.getChar(src, index));
      } else {
         Array.set(dest, index, copy(componentType, Array.get(src, index), predicate));
      }

   }

   private static <T> T copyObject(Class<T> type, T src, @Nullable T dest, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      T result = (T)(dest != null ? dest : type.newInstance());
      StreamUtil.forEachUnchecked(streamFields(type, predicate), (field) -> {
         field.setAccessible(true);
         copyField(src, result, field, predicate);
      }, ReflectiveOperationException.class);
      return result;
   }

   private static <T, R> void copyField(T src, T dest, Field field, @Nullable Predicate<Field> predicate) throws ReflectiveOperationException {
      Class<R> fieldType = field.getType();
      if (fieldType == Boolean.TYPE) {
         field.setBoolean(dest, field.getBoolean(src));
      } else if (fieldType == Byte.TYPE) {
         field.setByte(dest, field.getByte(src));
      } else if (fieldType == Short.TYPE) {
         field.setShort(dest, field.getShort(src));
      } else if (fieldType == Integer.TYPE) {
         field.setInt(dest, field.getInt(src));
      } else if (fieldType == Long.TYPE) {
         field.setLong(dest, field.getLong(src));
      } else if (fieldType == Float.TYPE) {
         field.setFloat(dest, field.getFloat(src));
      } else if (fieldType == Double.TYPE) {
         field.setDouble(dest, field.getDouble(src));
      } else if (fieldType == Character.TYPE) {
         field.setChar(dest, field.getChar(src));
      } else {
         field.set(dest, copy(fieldType, field.get(src), field.get(dest), predicate));
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
      registerImmutableClass(Boolean.class);
      registerImmutableClass(Byte.class);
      registerImmutableClass(Short.class);
      registerImmutableClass(Integer.class);
      registerImmutableClass(Long.class);
      registerImmutableClass(Float.class);
      registerImmutableClass(Double.class);
      registerImmutableClass(Character.class);
      registerImmutableClass(String.class);
   }
}
