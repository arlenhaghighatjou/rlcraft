package com.teamacronymcoders.base.util.collections;

import java.util.AbstractList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import net.minecraft.util.NonNullList;

public class NonnullListCollector<T> implements Collector<T, NonNullList<T>, NonNullList<T>> {
   private final Set<Characteristics> CH_ID = Collections.unmodifiableSet(EnumSet.of(Characteristics.IDENTITY_FINISH));

   public NonnullListCollector() {
   }

   public Supplier<NonNullList<T>> supplier() {
      return NonNullList::create;
   }

   public BiConsumer<NonNullList<T>, T> accumulator() {
      return AbstractList::add;
   }

   public BinaryOperator<NonNullList<T>> combiner() {
      return (left, right) -> {
         left.addAll(right);
         return left;
      };
   }

   public Function<NonNullList<T>, NonNullList<T>> finisher() {
      return (i) -> {
         return i;
      };
   }

   public Set<Characteristics> characteristics() {
      return this.CH_ID;
   }
}
