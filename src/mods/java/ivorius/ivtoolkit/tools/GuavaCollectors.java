package ivorius.ivtoolkit.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList.Builder;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

public class GuavaCollectors {
   public GuavaCollectors() {
   }

   public static <T> Collector<T, Builder<T>, ImmutableList<T>> immutableList() {
      return Collector.of(Builder::new, Builder::add, (l, r) -> {
         return l.addAll(r.build());
      }, Builder::build);
   }

   public static <T> Collector<T, com.google.common.collect.ImmutableSet.Builder<T>, ImmutableSet<T>> immutableSet() {
      return Collector.of(com.google.common.collect.ImmutableSet.Builder::new, com.google.common.collect.ImmutableSet.Builder::add, (l, r) -> {
         return l.addAll(r.build());
      }, com.google.common.collect.ImmutableSet.Builder::build, Characteristics.UNORDERED);
   }

   public static <K, U> Collector<K, ?, ImmutableMap<K, U>> toMap(Function<? super K, ? extends U> valueMapper) {
      return toMap(Function.identity(), valueMapper);
   }

   public static <T, K, U> Collector<T, ?, ImmutableMap<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
      return Collector.of(com.google.common.collect.ImmutableMap.Builder::new, (r, t) -> {
         r.put(keyMapper.apply(t), valueMapper.apply(t));
      }, (l, r) -> {
         return l.putAll(r.build());
      }, com.google.common.collect.ImmutableMap.Builder::build, Characteristics.UNORDERED);
   }

   public static <K, U> Collector<K, ?, ImmutableMultimap<K, U>> toMultimap(Function<? super K, Iterable<? extends U>> valueMapper) {
      return toMultimap(Function.identity(), valueMapper);
   }

   public static <T, K, U> Collector<T, ?, ImmutableMultimap<K, U>> toMultimap(Function<? super T, ? extends K> keyMapper, Function<? super T, Iterable<? extends U>> valueMapper) {
      return Collector.of(com.google.common.collect.ImmutableMultimap.Builder::new, (r, t) -> {
         r.putAll(keyMapper.apply(t), (Iterable)valueMapper.apply(t));
      }, (l, r) -> {
         return l.putAll(r.build());
      }, com.google.common.collect.ImmutableMultimap.Builder::build, Characteristics.UNORDERED);
   }
}
