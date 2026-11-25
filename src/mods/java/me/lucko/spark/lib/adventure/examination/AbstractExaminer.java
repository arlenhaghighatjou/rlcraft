package me.lucko.spark.lib.adventure.examination;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractExaminer<R> implements Examiner<R> {
   public AbstractExaminer() {
   }

   @NonNull
   public R examine(@Nullable Object value) {
      if (value == null) {
         return (R)this.nil();
      } else if (value instanceof String) {
         return (R)this.examine((String)value);
      } else if (value instanceof Examinable) {
         return (R)this.examine((Examinable)value);
      } else if (value instanceof Collection) {
         return (R)this.collection((Collection)value);
      } else if (value instanceof Map) {
         return (R)this.map((Map)value);
      } else if (value.getClass().isArray()) {
         Class<?> type = value.getClass().getComponentType();
         if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
               return (R)this.examine((boolean[])value);
            }

            if (type == Byte.TYPE) {
               return (R)this.examine((byte[])value);
            }

            if (type == Character.TYPE) {
               return (R)this.examine((char[])value);
            }

            if (type == Double.TYPE) {
               return (R)this.examine((double[])value);
            }

            if (type == Float.TYPE) {
               return (R)this.examine((float[])value);
            }

            if (type == Integer.TYPE) {
               return (R)this.examine((int[])value);
            }

            if (type == Long.TYPE) {
               return (R)this.examine((long[])value);
            }

            if (type == Short.TYPE) {
               return (R)this.examine((short[])value);
            }
         }

         return (R)this.array(value);
      } else if (value instanceof Boolean) {
         return (R)this.examine(((Boolean)value).booleanValue());
      } else if (value instanceof Character) {
         return (R)this.examine(((Character)value).charValue());
      } else {
         if (value instanceof Number) {
            if (value instanceof Byte) {
               return (R)this.examine(((Byte)value).byteValue());
            }

            if (value instanceof Double) {
               return (R)this.examine(((Double)value).doubleValue());
            }

            if (value instanceof Float) {
               return (R)this.examine(((Float)value).floatValue());
            }

            if (value instanceof Integer) {
               return (R)this.examine(((Integer)value).intValue());
            }

            if (value instanceof Long) {
               return (R)this.examine(((Long)value).longValue());
            }

            if (value instanceof Short) {
               return (R)this.examine(((Short)value).shortValue());
            }
         } else if (value instanceof BaseStream) {
            if (value instanceof Stream) {
               return (R)this.stream((Stream)value);
            }

            if (value instanceof DoubleStream) {
               return (R)this.stream((DoubleStream)value);
            }

            if (value instanceof IntStream) {
               return (R)this.stream((IntStream)value);
            }

            if (value instanceof LongStream) {
               return (R)this.stream((LongStream)value);
            }
         }

         return (R)this.scalar(value);
      }
   }

   @NonNull
   private <E> R array(E[] array) {
      return (R)this.array(array, Arrays.stream(array).map(this::examine));
   }

   @NonNull
   protected abstract <E> R array(E[] var1, @NonNull Stream<R> var2);

   @NonNull
   private <E> R collection(@NonNull Collection<E> collection) {
      return (R)this.collection(collection, collection.stream().map(this::examine));
   }

   @NonNull
   protected abstract <E> R collection(@NonNull Collection<E> var1, @NonNull Stream<R> var2);

   @NonNull
   public R examine(@NonNull String name, @NonNull Stream<? extends ExaminableProperty> properties) {
      return (R)this.examinable(name, properties.map((property) -> {
         return (R)(new SimpleImmutableEntry(property.name(), property.examine(this)));
      }));
   }

   @NonNull
   protected abstract R examinable(@NonNull String var1, @NonNull Stream<Entry<String, R>> var2);

   @NonNull
   private <K, V> R map(@NonNull Map<K, V> map) {
      return (R)this.map(map, map.entrySet().stream().map((entry) -> {
         return (R)(new SimpleImmutableEntry(this.examine(entry.getKey()), this.examine(entry.getValue())));
      }));
   }

   @NonNull
   protected abstract <K, V> R map(@NonNull Map<K, V> var1, @NonNull Stream<Entry<R, R>> var2);

   @NonNull
   protected abstract R nil();

   @NonNull
   protected abstract R scalar(@NonNull Object var1);

   @NonNull
   protected abstract <T> R stream(@NonNull Stream<T> var1);

   @NonNull
   protected abstract R stream(@NonNull DoubleStream var1);

   @NonNull
   protected abstract R stream(@NonNull IntStream var1);

   @NonNull
   protected abstract R stream(@NonNull LongStream var1);
}
