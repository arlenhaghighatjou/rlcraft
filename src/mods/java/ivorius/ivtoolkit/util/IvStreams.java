package ivorius.ivtoolkit.util;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class IvStreams {
   public IvStreams() {
   }

   public static <T> boolean visit(Stream<T> source, Predicate<T> action) {
      source.getClass();

      for(T item : source::iterator) {
         if (!action.test(item)) {
            return false;
         }
      }

      return true;
   }

   public static <T> Stream<T> streamopt(Optional<T> opt) {
      return opt.isPresent() ? Stream.of(opt.get()) : Stream.empty();
   }

   public static <T> Stream<T> flatMapToObj(IntStream stream, IntFunction<Stream<? extends T>> function) {
      return stream.mapToObj(function).flatMap(Function.identity());
   }

   public static <T> Stream<T> flatMapToObj(DoubleStream stream, DoubleFunction<Stream<? extends T>> function) {
      return stream.mapToObj(function).flatMap(Function.identity());
   }

   public static <T> Stream<T> flatMapToObj(LongStream stream, LongFunction<Stream<? extends T>> function) {
      return stream.mapToObj(function).flatMap(Function.identity());
   }

   public static byte[] toByteArray(IntStream stream) {
      return ((ByteArrayOutputStream)stream.collect(ByteArrayOutputStream::new, (baos, i) -> {
         baos.write((byte)i);
      }, (baos1, baos2) -> {
         baos1.write(baos2.toByteArray(), 0, baos2.size());
      })).toByteArray();
   }
}
