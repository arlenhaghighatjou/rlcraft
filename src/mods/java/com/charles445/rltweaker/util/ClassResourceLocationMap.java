package com.charles445.rltweaker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import net.minecraft.util.ResourceLocation;

public class ClassResourceLocationMap<K, V> {
   private final Map<String, V> namespace2valueMap = new HashMap();
   private final Map<String, V> resourceLocation2valueMap = new HashMap();
   private final Map<Class<? extends K>, V> class2valueMap = new HashMap();
   private final Function<Class<K>, ResourceLocation> resourceLocationFunc;
   private final V defaultValue;
   private final Function<String[], V> valueParser;

   public ClassResourceLocationMap(Function<Class<K>, ResourceLocation> func, V defaultValue, Function<String[], V> valueParser) {
      this.resourceLocationFunc = func;
      this.defaultValue = defaultValue;
      this.valueParser = valueParser;
   }

   public V get(K t) {
      return (V)(this.namespace2valueMap.isEmpty() && this.resourceLocation2valueMap.isEmpty() ? this.defaultValue : this.class2valueMap.computeIfAbsent(t.getClass(), (k) -> {
         ResourceLocation resourceLocation = (ResourceLocation)this.resourceLocationFunc.apply(k);
         if (resourceLocation == null) {
            return this.defaultValue;
         } else {
            V value = (V)this.resourceLocation2valueMap.get(resourceLocation.toString());
            if (value != null) {
               return value;
            } else {
               value = (V)this.namespace2valueMap.get(resourceLocation.getResourceDomain());
               return (V)(value != null ? value : this.defaultValue);
            }
         }
      }));
   }

   public void load(String[] data) {
      this.namespace2valueMap.clear();
      this.resourceLocation2valueMap.clear();
      this.class2valueMap.clear();

      for(String entry : data) {
         int indexKeyDelimiter = entry.indexOf(58);
         int indexEntryDelimiter = entry.indexOf(61);
         String key = indexEntryDelimiter != -1 ? entry.substring(0, indexEntryDelimiter) : entry;
         String[] rawValue;
         if (indexEntryDelimiter != -1) {
            rawValue = (String[])Arrays.stream(entry.substring(indexEntryDelimiter + 1).split(",")).map(String::trim).filter((s) -> {
               return !s.isEmpty();
            }).toArray((x$0) -> {
               return new String[x$0];
            });
         } else {
            rawValue = new String[0];
         }

         V value = (V)this.valueParser.apply(rawValue);
         if (value != null) {
            if (indexKeyDelimiter != -1 && (indexEntryDelimiter == -1 || indexKeyDelimiter <= indexEntryDelimiter)) {
               this.resourceLocation2valueMap.put(key, value);
            } else {
               this.namespace2valueMap.put(key, value);
            }
         }
      }

   }

   public static class Builder<A> {
      private final List<BiConsumer<A, String[]>> valueParsers = new ArrayList();
      private int index;

      public Builder() {
      }

      public <T> ClassResourceLocationMap.Builder<A> requiredValue(Function<String, T> parser, BiConsumer<A, T> accumulator) {
         int i = this.index++;
         this.valueParsers.add((v, s) -> {
            accumulator.accept(v, parser.apply(s[i]));
         });
         return this;
      }

      public <T> ClassResourceLocationMap.Builder<A> optionalValue(Function<String, T> parser, BiConsumer<A, T> accumulator, T defaultValue) {
         int i = this.index++;
         this.valueParsers.add((v, s) -> {
            T t = (T)null;

            try {
               t = (T)parser.apply(s[i]);
            } catch (RuntimeException var8) {
               ;
            }

            if (t == null) {
               t = (T)defaultValue;
            }

            accumulator.accept(v, t);
         });
         return this;
      }

      public <K> ClassResourceLocationMap<K, A> build(Function<Class<K>, ResourceLocation> resourceLocationParser, A defaultValue, Supplier<A> supplier) {
         return this.<K, A>build(resourceLocationParser, defaultValue, supplier, Function.identity());
      }

      public <K, V> ClassResourceLocationMap<K, V> build(Function<Class<K>, ResourceLocation> resourceLocationParser, V defaultValue, Supplier<A> supplier, Function<A, V> finisher) {
         return new ClassResourceLocationMap<K, V>(resourceLocationParser, defaultValue, (s) -> {
            try {
               return (ClassResourceLocationMap<K, V>)this.valueParsers.stream().collect(new Collector<BiConsumer<A, String[]>, A, V>() {
                  public Supplier<A> supplier() {
                     return supplier;
                  }

                  public BiConsumer<A, BiConsumer<A, String[]>> accumulator() {
                     return (a, p) -> {
                        p.accept(a, s);
                     };
                  }

                  public BinaryOperator<A> combiner() {
                     return null;
                  }

                  public Function<A, V> finisher() {
                     return finisher;
                  }

                  public Set<Characteristics> characteristics() {
                     return Collections.emptySet();
                  }
               });
            } catch (Exception var5) {
               return null;
            }
         });
      }
   }
}
