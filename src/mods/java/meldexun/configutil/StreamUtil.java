package meldexun.configutil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public class StreamUtil {
   public StreamUtil() {
   }

   public static Stream<Class<?>> streamClassHierarchy(Class<?> type) {
      return streamClassHierarchy(Stream.builder(), type);
   }

   private static Stream<Class<?>> streamClassHierarchy(Builder<Class<?>> builder, Class<?> type) {
      return type == Object.class ? builder.build() : streamClassHierarchy(builder.add(type), type.getSuperclass());
   }

   public static Stream<Field> streamFields(Class<?> type) {
      return streamClassHierarchy(type).map(Class::getFields).flatMap(Arrays::stream);
   }

   public static Stream<Field> streamDeclaredFields(Class<?> type) {
      return streamClassHierarchy(type).map(Class::getDeclaredFields).flatMap(Arrays::stream);
   }

   public static <T, E extends Throwable> void forEachUnchecked(Stream<T> stream, StreamUtil.UncheckedConsumer<T, E> action, Class<E> errorType) throws E {
      try {
         stream.forEach((t) -> {
            try {
               action.accept(t);
            } catch (Throwable var4) {
               if (!errorType.isInstance(var4)) {
                  throw new StreamUtil.UnknownException(var4);
               } else {
                  throw new StreamUtil.KnownException(var4);
               }
            }
         });
      } catch (StreamUtil.KnownException var4) {
         throw var4.getCause();
      }
   }

   private static class KnownException extends RuntimeException {
      public KnownException(Throwable cause) {
         super(cause);
      }
   }

   public interface UncheckedConsumer<T, E extends Throwable> {
      void accept(T var1) throws E;
   }

   private static class UnknownException extends RuntimeException {
      public UnknownException(Throwable cause) {
         super(cause);
      }
   }
}
