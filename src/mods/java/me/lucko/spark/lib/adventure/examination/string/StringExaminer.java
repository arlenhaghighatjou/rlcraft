package me.lucko.spark.lib.adventure.examination.string;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import me.lucko.spark.lib.adventure.examination.AbstractExaminer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class StringExaminer extends AbstractExaminer<String> {
   private static final Function<String, String> DEFAULT_ESCAPER = (string) -> {
      return (void)string.replace("\"", "\\\"").replace("\\", "\\\\").replace("\b", "\\b").replace("\f", "\\f").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
   };
   private static final Collector<CharSequence, ?, String> COMMA_CURLY = Collectors.joining(", ", "{", "}");
   private static final Collector<CharSequence, ?, String> COMMA_SQUARE = Collectors.joining(", ", "[", "]");
   private final Function<String, String> escaper;

   @NonNull
   public static StringExaminer simpleEscaping() {
      return StringExaminer.Instances.SIMPLE_ESCAPING;
   }

   public StringExaminer(@NonNull Function<String, String> escaper) {
      this.escaper = escaper;
   }

   @NonNull
   protected <E> String array(E[] array, @NonNull Stream<String> elements) {
      return (String)elements.collect(COMMA_SQUARE);
   }

   @NonNull
   protected <E> String collection(@NonNull Collection<E> collection, @NonNull Stream<String> elements) {
      return (String)elements.collect(COMMA_SQUARE);
   }

   @NonNull
   protected String examinable(@NonNull String name, @NonNull Stream<Entry<String, String>> properties) {
      return name + (String)properties.map((property) -> {
         return (String)property.getKey() + '=' + (String)property.getValue();
      }).collect(COMMA_CURLY);
   }

   @NonNull
   protected <K, V> String map(@NonNull Map<K, V> map, @NonNull Stream<Entry<String, String>> entries) {
      return (String)entries.map((entry) -> {
         return (String)entry.getKey() + '=' + (String)entry.getValue();
      }).collect(COMMA_CURLY);
   }

   @NonNull
   protected String nil() {
      return "null";
   }

   @NonNull
   protected String scalar(@NonNull Object value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(boolean value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(@Nullable boolean[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(byte value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(@Nullable byte[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(char value) {
      return '\'' + (String)this.escaper.apply(String.valueOf(value)) + '\'';
   }

   @NonNull
   public String examine(@Nullable char[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(double value) {
      return withSuffix(String.valueOf(value), 'd');
   }

   @NonNull
   public String examine(@Nullable double[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(float value) {
      return withSuffix(String.valueOf(value), 'f');
   }

   @NonNull
   public String examine(@Nullable float[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(int value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(@Nullable int[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(long value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(@Nullable long[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   public String examine(short value) {
      return String.valueOf(value);
   }

   @NonNull
   public String examine(@Nullable short[] values) {
      return values == null ? this.nil() : array(values.length, (index) -> {
         return this.examine(values[index]);
      });
   }

   @NonNull
   protected <T> String stream(@NonNull Stream<T> stream) {
      return (String)stream.map(this::examine).collect(COMMA_SQUARE);
   }

   @NonNull
   protected String stream(@NonNull DoubleStream stream) {
      return (String)stream.mapToObj(this::examine).collect(COMMA_SQUARE);
   }

   @NonNull
   protected String stream(@NonNull IntStream stream) {
      return (String)stream.mapToObj(this::examine).collect(COMMA_SQUARE);
   }

   @NonNull
   protected String stream(@NonNull LongStream stream) {
      return (String)stream.mapToObj(this::examine).collect(COMMA_SQUARE);
   }

   @NonNull
   public String examine(@Nullable String value) {
      return value == null ? this.nil() : '"' + (String)this.escaper.apply(value) + '"';
   }

   @NonNull
   private static String withSuffix(String string, char suffix) {
      return string + suffix;
   }

   @NonNull
   private static String array(int length, IntFunction<String> value) {
      StringBuilder sb = new StringBuilder();
      sb.append('[');

      for(int i = 0; i < length; ++i) {
         sb.append((String)value.apply(i));
         if (i + 1 < length) {
            sb.append(", ");
         }
      }

      sb.append(']');
      return sb.toString();
   }

   private static final class Instances {
      static final StringExaminer SIMPLE_ESCAPING = new StringExaminer(StringExaminer.DEFAULT_ESCAPER);

      private Instances() {
      }
   }
}
